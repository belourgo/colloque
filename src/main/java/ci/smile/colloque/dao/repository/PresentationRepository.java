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
import ci.smile.colloque.dao.entity.Conferencier;
import ci.smile.colloque.dao.entity.Exposer;
import ci.smile.colloque.dao.entity.Presentation;
import ci.smile.colloque.helper.CriteriaUtils;
import ci.smile.colloque.helper.Utilities;
import ci.smile.colloque.helper.contract.Request;
import ci.smile.colloque.helper.dto.PresentationDto;

public interface PresentationRepository extends JpaRepository<Presentation, Integer> {

	
	@Query("select p from Presentation p where p.exposer = :exposer"
			+ "  and p.conferencier = :conferencier "
			+ "  and p.colloque = :colloque "
			)
	Presentation findByPresentation(
			@Param("exposer") Exposer exposer,
			@Param("conferencier") Conferencier conferencier,
			@Param("colloque") Colloque colloque 
			);
	
	
	
	/**
	 * Finds List of Presentation by using ColloqueDto as a search criteria.
	 *
	 * @param request, em
	 * @return A List of Presentation
	 * @throws DataAccessException,ParseException
	 */
	public default List<Presentation> getByCriteria(Request<PresentationDto> request, EntityManager em, Locale locale) throws DataAccessException, Exception {
		String req = "select u from Presentation u where u IS NOT NULL";
		HashMap<String, Object> param = new HashMap<String, Object>();
		req += getWhereExpression(request, param, locale);
		req += " order by u.id desc";
		TypedQuery<Presentation> query = em.createQuery(req, Presentation.class);
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
	 * Finds count of Presentation by using PresentationDto as a search criteria.
	 *
	 * @param request, em
	 * @return Number of Presentation
	 *
	 */
	public default Long count(Request<PresentationDto> request, EntityManager em, Locale locale) throws DataAccessException, Exception  {
		String req = "select count(u.id) from Presentation u where u IS NOT NULL";
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
	default String getWhereExpression(Request<PresentationDto> request, HashMap<String, Object> param, Locale locale) throws Exception {
		// main query
		PresentationDto dto = request.getData() != null ? request.getData() : new PresentationDto();
		String mainReq = generateCriteria(dto, param, 0, locale);
		// others query
		String othersReq = "";
		if (request.getDatas() != null && !request.getDatas().isEmpty()) {
			Integer index = 1;
			for (PresentationDto elt : request.getDatas()) {
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
	default String generateCriteria(PresentationDto dto, HashMap<String, Object> param, Integer index,  Locale locale) throws Exception{
		List<String> listOfQuery = new ArrayList<String>();
		if (dto != null) {
			if (dto.getId()!= null && dto.getId() > 0) {
				listOfQuery.add(CriteriaUtils.generateCriteria("id", dto.getId(), "u.id", "Integer", dto.getIdParam(), param, index, locale));
			}
			if (dto.getConferencierId()!= null && dto.getConferencierId() > 0) {
				listOfQuery.add(CriteriaUtils.generateCriteria("conferencierId", dto.getConferencierId(), "u.conferencierId", "Integer", dto.getConferencierIdParam(), param, index, locale));
			}
			if (dto.getColloqueId()!= null && dto.getColloqueId() > 0) {
				listOfQuery.add(CriteriaUtils.generateCriteria("colloqueId", dto.getColloqueId(), "u.colloqueId", "Integer", dto.getColloqueIdParam(), param, index, locale));
			}
			if (dto.getExposerId()!= null && dto.getExposerId() > 0) {
				listOfQuery.add(CriteriaUtils.generateCriteria("exposerId", dto.getExposerId(), "u.exposerId", "Integer", dto.getExposerIdParam(), param, index, locale));
			}
			if (Utilities.notBlank(dto.getResumer())) {
				listOfQuery.add(CriteriaUtils.generateCriteria("resumer", dto.getResumer(), "u.resumer", "String", dto.getResumerParam(), param, index, locale));
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
