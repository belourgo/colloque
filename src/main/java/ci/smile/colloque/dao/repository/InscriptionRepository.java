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

import ci.smile.colloque.dao.entity.Colloque;
import ci.smile.colloque.dao.entity.Inscription;
import ci.smile.colloque.dao.entity.Participant;
import ci.smile.colloque.helper.CriteriaUtils;
import ci.smile.colloque.helper.Utilities;
import ci.smile.colloque.helper.contract.Request;
import ci.smile.colloque.helper.dto.InscriptionDto;

public interface InscriptionRepository extends JpaRepository<Inscription,Integer> {

	@Query(" select i from Inscription i where i.colloque = :colloque "
			+ "and i.participant = :participant ")
	Inscription findByInscription(
			@Param("colloque") Colloque colloque,
			@Param("participant") Participant participant 
			);
	
	
	
	/**
	 * Finds List of Inscription by using ColloqueDto as a search criteria.
	 *
	 * @param request, em
	 * @return A List of Inscription
	 * @throws DataAccessException,ParseException
	 */
	public default List<Inscription> getByCriteria(Request<InscriptionDto> request, EntityManager em, Locale locale) throws DataAccessException, Exception {
		String req = "select u from Inscription u where u IS NOT NULL";
		HashMap<String, Object> param = new HashMap<String, Object>();
		req += getWhereExpression(request, param, locale);
		req += " order by u.id desc";
		TypedQuery<Inscription> query = em.createQuery(req, Inscription.class);
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
	 * Finds count of Inscription by using InscriptionDto as a search criteria.
	 *
	 * @param request, em
	 * @return Number of Inscription
	 *
	 */
	public default Long count(Request<InscriptionDto> request, EntityManager em, Locale locale) throws DataAccessException, Exception  {
		String req = "select count(u.id) from Inscription u where u IS NOT NULL";
		HashMap<String, Object> param = new HashMap<String, Object>();
		req += getWhereExpression(request, param, locale);
		req += " order by  u.id desc";
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
	default String getWhereExpression(Request<InscriptionDto> request, HashMap<String, Object> param, Locale locale) throws Exception {
		// main query
		InscriptionDto dto = request.getData() != null ? request.getData() : new InscriptionDto();
		String mainReq = generateCriteria(dto, param, 0, locale);
		// others query
		String othersReq = "";
		if (request.getDatas() != null && !request.getDatas().isEmpty()) {
			Integer index = 1;
			for (InscriptionDto elt : request.getDatas()) {
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
	default String generateCriteria(InscriptionDto dto, HashMap<String, Object> param, Integer index,  Locale locale) throws Exception{
		List<String> listOfQuery = new ArrayList<String>();
		if (dto != null) {
			if (dto.getId()!= null && dto.getId() > 0) {
				listOfQuery.add(CriteriaUtils.generateCriteria("id", dto.getId(), "u.id", "Integer", dto.getIdParam(), param, index, locale));
			}
			if (Utilities.notBlank(dto.getDate())) {
				listOfQuery.add(CriteriaUtils.generateCriteria("date", dto.getDate(), "u.date", "String", dto.getDateParam(), param, index, locale));
			}
			if (Utilities.notBlank(dto.getNumeroInscription())) {
				listOfQuery.add(CriteriaUtils.generateCriteria("numeroInscription", dto.getNumeroInscription(), "u.numeroInscription", "String", dto.getNumeroInscriptionParam(), param, index, locale));
			}
			if (dto.getParticipantId()!= null && dto.getParticipantId() > 0) {
				listOfQuery.add(CriteriaUtils.generateCriteria("participantId", dto.getParticipantId(), "u.participant", "Integer", dto.getParticipantIdParam(), param, index, locale));
			}
			if (dto.getColloqueId()!= null && dto.getColloqueId() > 0) {
				listOfQuery.add(CriteriaUtils.generateCriteria("colloqueId", dto.getColloqueId(), "u.colloque", "Integer", dto.getColloqueIdParam(), param, index, locale));
			}
		//	Boolean.valueOf(dto.getPresence());
		//	dto.getPresence() == true || dto.getPresence() == false
			if ( dto.getPresence() !=null ) {
				listOfQuery.add(CriteriaUtils.generateCriteria("presence", dto.getPresence(), "u.presence", "Boolean", dto.getPresenceParam(), param, index, locale));
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
