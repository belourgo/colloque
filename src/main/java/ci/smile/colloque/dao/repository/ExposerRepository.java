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

import ci.smile.colloque.dao.entity.Exposer;
import ci.smile.colloque.helper.CriteriaUtils;
import ci.smile.colloque.helper.Utilities;
import ci.smile.colloque.helper.contract.Request;
import ci.smile.colloque.helper.dto.ExposerDto;

@Repository
public interface ExposerRepository extends JpaRepository<Exposer,Integer> {

	
	@Query("select e from Exposer e where e.titre = :titre")
	Exposer findByTitre(@Param("titre") String titre);
	
	
	
	/**
	 * Finds List of Exposer by using ExposerDto as a search criteria.
	 *
	 * @param request, em
	 * @return A List of Exposer
	 * @throws DataAccessException,ParseException
	 */
	public default List<Exposer> getByCriteria(Request<ExposerDto> request, EntityManager em, Locale locale) throws DataAccessException, Exception {
		String req = "select e from Exposer e where e IS NOT NULL";
		HashMap<String, Object> param = new HashMap<String, Object>();
		req += getWhereExpression(request, param, locale);
		req += " order by e.id desc";
		TypedQuery<Exposer> query = em.createQuery(req, Exposer.class);
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
	 * Finds count of Exposer by using ExposerDto as a search criteria.
	 *
	 * @param request, em
	 * @return Number of Exposer
	 *
	 */
	public default Long count(Request<ExposerDto> request, EntityManager em, Locale locale) throws DataAccessException, Exception  {
		String req = "select count(e.id) from Exposer e where e IS NOT NULL";
		HashMap<String, Object> param = new HashMap<String, Object>();
		req += getWhereExpression(request, param, locale);
		req += " order by  e.id desc";
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
	default String getWhereExpression(Request<ExposerDto> request, HashMap<String, Object> param, Locale locale) throws Exception {
		// main query
		ExposerDto dto = request.getData() != null ? request.getData() : new ExposerDto();
		String mainReq = generateCriteria(dto, param, 0, locale);
		// others query
		String othersReq = "";
		if (request.getDatas() != null && !request.getDatas().isEmpty()) {
			Integer index = 1;
			for (ExposerDto elt : request.getDatas()) {
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
	default String generateCriteria(ExposerDto dto, HashMap<String, Object> param, Integer index,  Locale locale) throws Exception{
		List<String> listOfQuery = new ArrayList<String>();
		if (dto != null) {
			if (dto.getId()!= null && dto.getId() > 0) {
				listOfQuery.add(CriteriaUtils.generateCriteria("id", dto.getId(), "e.id", "Integer", dto.getIdParam(), param, index, locale));
			}
			if (Utilities.notBlank(dto.getTitre())) {
				listOfQuery.add(CriteriaUtils.generateCriteria("titre", dto.getTitre(), "e.titre", "String", dto.getTitreParam(), param, index, locale));
			}
			if (Utilities.notBlank(dto.getResumer())) {
				listOfQuery.add(CriteriaUtils.generateCriteria("resumer", dto.getResumer(), "e.resumer", "String", dto.getResumerParam(), param, index, locale));
			}
			
//			List<String> listOfCustomQuery = generateCriteria(dto, param, index, locale);
//			if (Utilities.isNotEmpty(listOfCustomQuery)) {
//				listOfQuery.addAll(listOfCustomQuery);
//			}
			
//			if (!listOfCustomQuery.isEmpty() && listOfCustomQuery != null) {
//			listOfQuery.add(listOfCustomQuery);
//		}
		}
		return CriteriaUtils.getCriteriaByListOfQuery(listOfQuery);
	}
}
