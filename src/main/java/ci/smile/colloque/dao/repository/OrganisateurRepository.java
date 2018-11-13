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
import ci.smile.colloque.dao.entity.Organisateur;
import ci.smile.colloque.dao.entity.Participant;
import ci.smile.colloque.helper.CriteriaUtils;
import ci.smile.colloque.helper.contract.Request;
import ci.smile.colloque.helper.dto.OrganisateurDto;

public interface OrganisateurRepository extends JpaRepository<Organisateur, Integer>{

	
	@Query("select o from Organisateur o where o.participant = :participant"
			+ "  and o.colloque = :colloque "
			)
	Organisateur findByOrganisateur(
			@Param("participant") Participant participant,
			@Param("colloque") Colloque conferencier
			);
	
	
	/**
	 * Finds List of Organisateur by using OrganisateurDto as a search criteria.
	 *
	 * @param request, em
	 * @return A List of Organisateur
	 * @throws DataAccessException,ParseException
	 */
	public default List<Organisateur> getByCriteria(Request<OrganisateurDto> request, EntityManager em, Locale locale) throws DataAccessException, Exception {
		String req = "select u from Organisateur u where u IS NOT NULL";
		HashMap<String, Object> param = new HashMap<String, Object>();
		req += getWhereExpression(request, param, locale);
		req += " order by u.id desc";
		TypedQuery<Organisateur> query = em.createQuery(req, Organisateur.class);
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
	 * Finds count of Organisateur by using OrganisateurDto as a search criteria.
	 *
	 * @param request, em
	 * @return Number of Organisateur
	 *
	 */
	public default Long count(Request<OrganisateurDto> request, EntityManager em, Locale locale) throws DataAccessException, Exception  {
		String req = "select count(u.id) from Organisateur u where u IS NOT NULL";
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
	default String getWhereExpression(Request<OrganisateurDto> request, HashMap<String, Object> param, Locale locale) throws Exception {
		// main query
		OrganisateurDto dto = request.getData() != null ? request.getData() : new OrganisateurDto();
		String mainReq = generateCriteria(dto, param, 0, locale);
		// others query
		String othersReq = "";
		if (request.getDatas() != null && !request.getDatas().isEmpty()) {
			Integer index = 1;
			for (OrganisateurDto elt : request.getDatas()) {
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
	default String generateCriteria(OrganisateurDto dto, HashMap<String, Object> param, Integer index,  Locale locale) throws Exception{
		List<String> listOfQuery = new ArrayList<String>();
		if (dto != null) {
			if (dto.getId()!= null && dto.getId() > 0) {
				listOfQuery.add(CriteriaUtils.generateCriteria("id", dto.getId(), "u.id", "Integer", dto.getIdParam(), param, index, locale));
			}
			if (dto.getParticipantId()!= null && dto.getParticipantId() > 0) {
				listOfQuery.add(CriteriaUtils.generateCriteria("participantId", dto.getParticipantId(), "u.participant", "Integer", dto.getParticipantIdParam(), param, index, locale));
			}
			if (dto.getColloqueId()!= null && dto.getColloqueId() > 0) {
				listOfQuery.add(CriteriaUtils.generateCriteria("colloqueId", dto.getParticipantId(), "u.colloque", "Integer", dto.getColloqueIdParam(), param, index, locale));
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
