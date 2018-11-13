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

import ci.smile.colloque.dao.entity.Conferencier;
import ci.smile.colloque.helper.CriteriaUtils;
import ci.smile.colloque.helper.contract.Request;
import ci.smile.colloque.helper.dto.ConferencierDto;

public interface ConferencierRepository extends JpaRepository<Conferencier,Integer> {

	
	
	
	/**
	 * Finds List of Conferencier by using ConferencierDto as a search criteria.
	 *
	 * @param request, em
	 * @return A List of Conferencier
	 * @throws DataAccessException,ParseException
	 */
	public default List<Conferencier> getByCriteria(Request<ConferencierDto> request, EntityManager em, Locale locale) throws DataAccessException, Exception {
		String req = "select u from Conferencier u where u IS NOT NULL";
		HashMap<String, Object> param = new HashMap<String, Object>();
		req += getWhereExpression(request, param, locale);
		req += " order by u.id desc";
		TypedQuery<Conferencier> query = em.createQuery(req, Conferencier.class);
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
	 * Finds count of Conferencier by using ConferencierDto as a search criteria.
	 *
	 * @param request, em
	 * @return Number of Conferencier
	 *
	 */
	public default Long count(Request<ConferencierDto> request, EntityManager em, Locale locale) throws DataAccessException, Exception  {
		String req = "select count(u.id) from Conferencier u where u IS NOT NULL";
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
	default String getWhereExpression(Request<ConferencierDto> request, HashMap<String, Object> param, Locale locale) throws Exception {
		// main query
		ConferencierDto dto = request.getData() != null ? request.getData() : new ConferencierDto();
		String mainReq = generateCriteria(dto, param, 0, locale);
		// others query
		String othersReq = "";
		if (request.getDatas() != null && !request.getDatas().isEmpty()) {
			Integer index = 1;
			for (ConferencierDto elt : request.getDatas()) {
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
	default String generateCriteria(ConferencierDto dto, HashMap<String, Object> param, Integer index,  Locale locale) throws Exception{
		List<String> listOfQuery = new ArrayList<String>();
		if (dto != null) {
			if (dto.getParticipantId()!= null && dto.getParticipantId() > 0) {
				listOfQuery.add(CriteriaUtils.generateCriteria("participantId", dto.getParticipantId(), "u.participantId", "Integer", dto.getParticipantIdParam(), param, index, locale));
			}
			if (dto.getInstitutionId()!= null && dto.getInstitutionId() > 0) {
				listOfQuery.add(CriteriaUtils.generateCriteria("institutionId", dto.getInstitutionId(), "u.institution", "Integer", dto.getInstitutionIdParam(), param, index, locale));
			}
//			Boolean.valueOf(dto.getOrganisateur());
//			if (Boolean.TRUE || Boolean.FALSE) {
			if (dto.getOrganisateur() != null) {
				listOfQuery.add(CriteriaUtils.generateCriteria("organisateur", dto.getOrganisateur(), "u.organisateur", "Boolean", dto.getOrganisateurParam(), param, index, locale));
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
