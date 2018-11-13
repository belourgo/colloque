package ci.smile.colloque.dao.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ci.smile.colloque.dao.entity.Participant;
import ci.smile.colloque.helper.CriteriaUtils;
import ci.smile.colloque.helper.Utilities;
import ci.smile.colloque.helper.contract.Request;
import ci.smile.colloque.helper.dto.ParticipantDto;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Integer> {

	
	@Query("select p from Participant p where p.numeroDeParticipation = :numeroDeParticipation")
	Participant findByNumPar(@Param("numeroDeParticipation") String numeroDeParticipation);

//	@Query("select e from Participant e where e.nom = :nom")
//	Participant findByNumPar(@Param("nom") String nom);
	
	
	/**
	 * Finds List of Participant by using ParticipantDto as a search criteria.
	 *
	 * @param request, em
	 * @return A List of Participant
	 * @throws DataAccessException,ParseException
	 */
	public default List<Participant> getByCriteria(Request<ParticipantDto> request, EntityManager em, Locale locale) throws DataAccessException, Exception {
		String req = "select p from Participant p where p IS NOT NULL";
		HashMap<String, Object> param = new HashMap<String, Object>();
		req += getWhereExpression(request, param, locale);
		req += " order by p.id desc";
		TypedQuery<Participant> query = em.createQuery(req, Participant.class);
		for (Map.Entry<String, Object> entry : param.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		if (request.getIndex() != null && request.getSize() != null) {
			query.setFirstResult(request.getIndex() * request.getSize());
			query.setMaxResults(request.getSize());
		}
		return query.getResultList();
	}

	/**
	 * Finds count of Participant by using ParticipantDto as a search criteria.
	 *
	 * @param request, em
	 * @return Number of Participant
	 *
	 */
	public default Long count(Request<ParticipantDto> request, EntityManager em, Locale locale) throws DataAccessException, Exception  {
		String req = "select count(p.id) from Participant p where p IS NOT NULL";
		HashMap<String, Object> param = new HashMap<String, Object>();
		req += getWhereExpression(request, param, locale);
		req += " order by  p.id desc";
		javax.persistence.Query query = em.createQuery(req);
		for (Map.Entry<String, Object> entry : param.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		Long count = (Long) query.getResultList().get(0);
		return count;
	}

	/**
	 * get where expression
	 * @param request
	 * @param param
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	default String getWhereExpression(Request<ParticipantDto> request, HashMap<String, Object> param, Locale locale) throws Exception {
		// main query
		ParticipantDto dto = request.getData() != null ? request.getData() : new ParticipantDto();
		String mainReq = generateCriteria(dto, param, 0, locale);
		// others query
		String othersReq = "";
		if (request.getDatas() != null && !request.getDatas().isEmpty()) {
			Integer index = 1;
			for (ParticipantDto elt : request.getDatas()) {
				String eltReq = generateCriteria(elt, param, index, locale);
				if (request.getIsAnd() != null && request.getIsAnd()) {
					othersReq += "and (" + eltReq + ") ";
				} else {
					othersReq += "or (" + eltReq + ") ";
				}
				index++;
			}
		}
		String req = "";
		if (!mainReq.isEmpty()) {
			req += " and (" + mainReq + ") ";
		}
		req += othersReq;
		return req;
	}

	/**
	 * generate sql query for dto
	 * @param dto
	 * @param req
	 * @param param
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	default String generateCriteria(ParticipantDto dto, HashMap<String, Object> param, Integer index,  Locale locale) throws Exception{
		List<String> listOfQuery = new ArrayList<String>();
		if (dto != null) {
			if (dto.getId()!= null && dto.getId() > 0) {
				listOfQuery.add(CriteriaUtils.generateCriteria("id", dto.getId(), "p.id", "Integer", dto.getIdParam(), param, index, locale));
			}
			if (Utilities.notBlank(dto.getNom())) {
				listOfQuery.add(CriteriaUtils.generateCriteria("nom", dto.getNom(), "p.nom", "String", dto.getNomParam(), param, index, locale));
			}
			if (Utilities.notBlank(dto.getAdresse())) {
				listOfQuery.add(CriteriaUtils.generateCriteria("prenom", dto.getPrenom(), "p.prenom", "String", dto.getPrenomParam(), param, index, locale));
			}
			if (Utilities.notBlank(dto.getAdresse())) {
				listOfQuery.add(CriteriaUtils.generateCriteria("adresse", dto.getAdresse(), "p.adresse", "String", dto.getAdresseParam(), param, index, locale));
			}
			if (Utilities.notBlank(dto.getAdresse())) {
				listOfQuery.add(CriteriaUtils.generateCriteria("numeroDeParticipation", dto.getNumeroDeParticipation(), "p.prenom", "String", dto.getNumeroDeParticipationParam(), param, index, locale));
			}
			
//			List<String> listOfCustomQuery = generateCriteria(dto, param, index, locale);
//			if (Utilities.isNotEmpty(listOfCustomQuery)) {
//				listOfQuery.addAll(listOfCustomQuery);
//			}
//			
//			if (!listOfCustomQuery.isEmpty() && listOfCustomQuery != null) {
//			listOfQuery.add(listOfCustomQuery);
//		}
			
		}
		return CriteriaUtils.getCriteriaByListOfQuery(listOfQuery);
	}
}
