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
import ci.smile.colloque.helper.CriteriaUtils;
import ci.smile.colloque.helper.Utilities;
import ci.smile.colloque.helper.contract.Request;
import ci.smile.colloque.helper.dto.ColloqueDto;

public interface ColloqueRepository extends JpaRepository<Colloque,Integer> 
{

	@Query("select e from Colloque e where e.nom = :nom")
	Colloque findByNom(@Param("nom") String nom);
	
	
	

 
    
	
//	public default List<CountColloqueByUniv> getColloqueByUniv(EntityManager em) {
//		
//		String req = "SELECT c.universite, COUNT(c.nom) FROM colloque c "
//				+ "GROUP BY c.universite";
//		Query query = (Query) em.createNativeQuery(req, "resultClass");
////		   Query query = em.createNativeQuery(req, "resultClass");
//		    @SqlResultSetMapping(name="resultClass", 
//		            entities={ 
//		                @EntityResult(entityClass=com.acme.Order.class, fields={
//		                    @FieldResult(name="id", column="order_id"),
//		                    @FieldResult(name="quantity", column="order_quantity"), 
//		                    @FieldResult(name="item", column="order_item")})},
//		            columns={
//		                @ColumnResult(name="item_name")}
//		
//	}
//	
//	
	
	
	
	
	
	
	//-------------------------------------------------------
	//REQUETE POUR AVOIR LE NMBRE DE COLLOQUES PAR UNIVERSITE
	//-------------------------------------------------------
	
//	@Query("select c from Colloque c " +
//			" order by c.universite desc")
//	List<Colloque> getColloqueByUniversity();
//	
//	@Query(value="select c.universite_id as universite, count(c.nom) as nbreColloque from colloque c " +
//			" group by c.universite_id " , nativeQuery = true)
//	List<CountColloqueByUniv> getColloqueByUniv();
//	
//	
//	@Query("SELECT c.universite, COUNT(c.nom) FROM colloque c "
//			+ "GROUP BY c.universite")
//	Colloque getColloqueByUniversity();
	

	
	/**
	 * Finds List of Colloque by using ColloqueDto as a search criteria.
	 *
	 * @param request, em
	 * @return A List of Colloque
	 * @throws DataAccessException,ParseException
	 */
	public default List<Colloque> getByCriteria(Request<ColloqueDto> request, EntityManager em, Locale locale) throws DataAccessException, Exception {
		String req = "select u from Colloque u where u IS NOT NULL";
		HashMap<String, Object> param = new HashMap<String, Object>();
		req += getWhereExpression(request, param, locale);
		req += " order by u.id desc";
		TypedQuery<Colloque> query = em.createQuery(req, Colloque.class);
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
	 * Finds count of Colloque by using ColloqueDto as a search criteria.
	 *
	 * @param request, em
	 * @return Number of Colloque
	 *
	 */
	public default Long count(Request<ColloqueDto> request, EntityManager em, Locale locale) throws DataAccessException, Exception  {
		String req = "select count(u.id) from Colloque u where u IS NOT NULL";
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
	default String getWhereExpression(Request<ColloqueDto> request, HashMap<String, Object> param, Locale locale) throws Exception {
		// main query
		ColloqueDto dto = request.getData() != null ? request.getData() : new ColloqueDto();
		String mainReq = generateCriteria(dto, param, 0, locale);
		// others query
		String othersReq = "";
		if (request.getDatas() != null && !request.getDatas().isEmpty()) {
			Integer index = 1;
			for (ColloqueDto elt : request.getDatas()) {
				String eltReq = generateCriteria(elt, param, index, locale);
				if (request.getIsAnd() != null 	&& request.getIsAnd()) {
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
	default String generateCriteria(ColloqueDto dto, HashMap<String, Object> param, Integer index,  Locale locale) throws Exception{
		List<String> listOfQuery = new ArrayList<String>();
		if (dto != null) {
			if (dto.getId()!= null && dto.getId() > 0) {
				listOfQuery.add(CriteriaUtils.generateCriteria("id", dto.getId(), "u.id", "Integer", dto.getIdParam(), param, index, locale));
			}
			if (Utilities.notBlank(dto.getNom())) {
				listOfQuery.add(CriteriaUtils.generateCriteria("nom", dto.getNom(), "u.nom", "String", dto.getNomParam(), param, index, locale));
			}
			if (dto.getUniversiteId()!= null && dto.getUniversiteId() > 0) {
				listOfQuery.add(CriteriaUtils.generateCriteria("universiteId", dto.getUniversiteId(), "u.universite", "String", dto.getUniversiteIdParam(), param, index, locale));
			}
			if (Utilities.notBlank(dto.getDate())) {
				listOfQuery.add(CriteriaUtils.generateCriteria("date", dto.getDate(), "u.date", "String", dto.getDateParam(), param, index, locale));
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
