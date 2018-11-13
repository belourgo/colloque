package ci.smile.colloque.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Component;

import ci.smile.colloque.dao.entity.Institution;
import ci.smile.colloque.dao.repository.InstitutionRepository;
import ci.smile.colloque.helper.ExceptionUtils;
import ci.smile.colloque.helper.FunctionalError;
import ci.smile.colloque.helper.Validate;
import ci.smile.colloque.helper.contract.IBasicBusiness;
import ci.smile.colloque.helper.contract.Request;
import ci.smile.colloque.helper.contract.Response;
import ci.smile.colloque.helper.dto.InstitutionDto;
import ci.smile.colloque.helper.dto.transformer.InstitutionTransformer;

@Component
public class InstitutionBusiness implements IBasicBusiness<Request<InstitutionDto>, Response<InstitutionDto>>  {

	private Response<InstitutionDto> response ; 
	
	//--------------
	// Institution Repository
	//--------------------
	
	@Autowired
	private InstitutionRepository institutionRepository;
	
	//-------------------------------
	//  Necessaire pour reporter les Erreurs 
	//--------------------------------------
	
	@Autowired
	private FunctionalError functionalError; 
	
	@Autowired
	private ExceptionUtils exceptionUtils; // Pour les execption
	
	@PersistenceContext
	 private EntityManager em;
	
	
	
	//--------------------
	// Slf4Logger
	//-----------------------------
	
	private Logger slf4jLogger;
	
	public  InstitutionBusiness( ) { 
		slf4jLogger = LoggerFactory.getLogger(getClass());
	}
	
	
	
	@Override
	public Response<InstitutionDto> create(Request<InstitutionDto> request, Locale locale) {
		// TODO Auto-generated method stub
		
		// Obliger a renseigner les champs obligatoires
		// Utiliser la fonction Validate pour verifier que le champs en bon
		
		
		//Verifier si l'information existe dans la base de donnée 
		// Verifier si l'identifiant unique existe dans la BD
		
		// Ajouter si conforme
		
		response = new Response<InstitutionDto>();
		
		
		try {
			
			List<Institution> items = new ArrayList<>();
			for(InstitutionDto dto : request.getDatas() ) {
				
				//-----------------------------------
				//----------Forcer à enter une valeur
				//------------------------------------
				
				Map<String,Object> fieldsToVerify = new HashMap<>();	
				fieldsToVerify.put("Nom",dto.getNom());
				
				//Fonction deja écrite
				if (!Validate.RequiredValue(fieldsToVerify).isGood()) {
					response.setHasError(true);
					response.setStatus(functionalError.FIELD_EMPTY(Validate.getValidate().getField(), locale));
					return response;
				}
					
				    
//					Institution existingInstitution = institutionRepository.findbyNom(dto.getNom());					
//					
//					if(existingInstitution != null ) {
//						response.setHasError(true);
//						response.setStatus(functionalError.DATA_EXIST("nom :"+dto.getNom(), locale));
//					    return response;
//					}
					
					//unicité du nom de l'université
				
						Institution existingInstitution = institutionRepository.findByNom(dto.getNom());
					if(existingInstitution != null){
						response.setHasError(true);
						response.setStatus(functionalError.DATA_EXIST("nom :"+dto.getNom(), locale));
					    return response;
					}
							
					
					
		//Tranformer Step
					
		Institution institutionTosave = InstitutionTransformer.INSTANCE.toEntity(dto);
		
		items.add(institutionTosave);
				
			}
			
//			if( items.isEmpty() || items==null ) {
//				response.setHasError(true);
//				response.setStatus(functionalError.DATA_EMPTY("Items DATA is Empty", locale));
//				return response;
//			}
//			
//			List<Institution> itemsaved = institutionRepository.saveAll(items);
//			if( itemsaved==null ) {
//				response.setHasError(true);
//				response.setStatus(functionalError.SAVE_FAIL("The save "
//						+ "has failled", locale));
//				return response;
//			}
			
			
			
			
			if(items != null && !items.isEmpty()) { 
				//inserer dans la base de données
				List<Institution> itemsSaved=institutionRepository.saveAll(items);
				if (itemsSaved == null) {
					response.setHasError(true);
					response.setStatus(functionalError.SAVE_FAIL("institution", locale));
					return response;
						
				}
				//transformation en dto des données insérées en bdd 
				//et retour au front
				response.setHasError(false);
				response.setStatus(functionalError.SUCCESS("create institution", locale));
				//response.setItems(InstitutionTransformer.INSTANCE.todtos(itemsSaved));
				response.setItems(InstitutionTransformer.INSTANCE.toDtos(itemsSaved));
			}
			
		}
		catch(PermissionDeniedDataAccessException e){
			 exceptionUtils.PERMISSION_DENIED_DATA_ACCESS_EXCEPTION(response, locale, e);
		  } catch (DataAccessResourceFailureException e) {
		   exceptionUtils.DATA_ACCESS_RESOURCE_FAILURE_EXCEPTION(response, locale, e);
		  } catch (DataAccessException e) {
		   exceptionUtils.DATA_ACCESS_EXCEPTION(response, locale, e);
		  } catch (RuntimeException e) {
		   exceptionUtils.RUNTIME_EXCEPTION(response, locale, e);
		  } catch (Exception e) {
		   exceptionUtils.EXCEPTION(response, locale, e);
		  } finally {
		   if (response.isHasError() && response.getStatus() != null) {
		    slf4jLogger.info("Erreur| code: {} -  message: {}", response.getStatus().getCode(), response.getStatus().getMessage());
		    throw new RuntimeException(response.getStatus().getCode() + ";" + response.getStatus().getMessage());
		   }
			
		}
		return response;
	}


	
	
	
	
	//--------------------------------------------------
	//Method Update
	//--------------------------------------------------
	
	public Response<InstitutionDto> update(Request<InstitutionDto> request, Locale locale){
		response=new Response<>();
		try {

			List<Institution> items = new ArrayList<>();
			
			for (InstitutionDto dto : request.getDatas()) {
				
				// retrouver l'enregistrement dont on veut modifier les valeurs,
				// id obligatoire
				// the function witch is coming need maps
				Map<String, Object> fieldsToVerify= new HashMap<>();
				fieldsToVerify.put("id",dto.getId());
				
				// The map is now in function fieldToVerify
				if (!Validate.RequiredValue(fieldsToVerify).isGood()) {
					response.setHasError(true);
					response.setStatus(functionalError.FIELD_EMPTY(Validate.getValidate().getField(), locale));
					return response;
				}
				
				
				//on retrouve l'enregistrement à partir de l'id fourni
												
						
				
				//Institution institution = institutionRepository.findById(dto.getId()); 
				Optional<Institution> institution = institutionRepository.findById(dto.getId());

				// isPresent retourne True si institution contient une valeur
				
				if( !institution.isPresent() ) {
					response.setHasError(true);
					response.setStatus(functionalError.DATA_NOT_EXIST("institution id--> "+dto.getId(), locale));
			        return response;
				}
				
				//on applique les modifications demandées
				if(dto.getNom() != null && !dto.getNom().isEmpty()
						&& !dto.getNom().equals(institution.get().getNom())) {
					//unicité du nom de l'université
					Institution existingInstitution= institutionRepository.findByNom(dto.getNom());
					
					if(existingInstitution != null && existingInstitution.getId() != institution.get().getId()){
						response.setHasError(true);
						response.setStatus(functionalError.DATA_EXIST("nom :"+dto.getNom(), locale));
					    return response;
					}

					institution.get().setNom(dto.getNom());
				}
				
				
				items.add(institution.get());
				// enregistrer dans la bdd
				
				
				// on ramene au front les nouvelles valeurs
				
			}
			if(items != null && !items.isEmpty()) { 
				//inserer dans la base de données
				List<Institution> itemsSaved=institutionRepository.saveAll(items);
				if (itemsSaved == null) {
					response.setHasError(true);
					response.setStatus(functionalError.SAVE_FAIL("institution", locale));
					return response;
					
				}
				//transformation en dto des données insérées en bdd 
				//et retour au front
				response.setHasError(false);
				response.setStatus(functionalError.SUCCESS("update institution", locale));
				response.setItems(InstitutionTransformer.INSTANCE.toDtos(itemsSaved));
			}
		} catch (PermissionDeniedDataAccessException e) {
			   exceptionUtils.PERMISSION_DENIED_DATA_ACCESS_EXCEPTION(response, locale, e);
		  } catch (DataAccessResourceFailureException e) {
		   exceptionUtils.DATA_ACCESS_RESOURCE_FAILURE_EXCEPTION(response, locale, e);
		  } catch (DataAccessException e) {
		   exceptionUtils.DATA_ACCESS_EXCEPTION(response, locale, e);
		  } catch (RuntimeException e) {
		   exceptionUtils.RUNTIME_EXCEPTION(response, locale, e);
		  } catch (Exception e) {
		   exceptionUtils.EXCEPTION(response, locale, e);
		  } finally {
		   if (response.isHasError() && response.getStatus() != null) {
		    slf4jLogger.info("Erreur| code: {} -  message: {}", response.getStatus().getCode(), response.getStatus().getMessage());
		    throw new RuntimeException(response.getStatus().getCode() + ";" + response.getStatus().getMessage());
		   }
		  }
		return response;
	}
	
	
	
	
	
	
	//--------------------------------------------------
	//Method Delete
	//--------------------------------------------------
	

	
	
	
	public Response<InstitutionDto> delete(Request<InstitutionDto> request,Locale locale){

		// retrouver l'enregistrement à supprimer,
		// id obligatoire
		
		//Recuperer et emmagasiner
		
		// on supprime 
		response=new Response<>();
		
		
		try {
			
			
			List<Institution> items = new ArrayList<>();
			
			for (InstitutionDto dto : request.getDatas()) {
				
				//--------------------
				// Fisrt step:id obligatoire
				//---------------------
				
				Map<String, Object> fieldsToVerify = new HashMap<>();
				fieldsToVerify.put("id", dto.getId());
				
				if (!Validate.RequiredValue(fieldsToVerify).isGood()) {
					response.setHasError(true);
					response.setStatus(functionalError.FIELD_EMPTY(Validate.getValidate().getField(), locale));
					return response;
				}
				
				//--------------------
				// Second step:    Retrouver l'enregistrement à supprimer
				//---------------------
				
				Optional<Institution> institution = institutionRepository.findById(dto.getId());
			
				
				//---------------
				//1-Erreur si institution ne retourne rien
				//2-Erreur si le nom renseigné ne correspond pas au nom en BD
				//------------------------
				
				
				if(!institution.isPresent() && dto.getNom() != institution.get().getNom()) {	
					response.setHasError(true);
					response.setStatus(functionalError.DATA_NOT_EXIST("L'institution avec ID " +
					dto.getId() + 	"n'existe pas", locale)) ;
					return response;
				}
				
				//--------------------
				// Fird step:    Recuperer et Emmagasiner
				//---------------------
				
				
				items.add(institution.get());
				
			}
			
			if(  items != null  &&  !items.isEmpty()  ) {
				
				// -----------------------------
				// Delete
				//------------------------------
				
				institutionRepository.deleteInBatch(items);      // Method return void
				
				//Verifier que les elements ont ete supprime  ______ Pas encore fait ___
				
				
				//---------------------
				// Retourner reponse pour ce qui a ete surprime 
				//-------------------------------
				response.setHasError(false);
				response.setStatus(functionalError.SUCCESS("deleted institution", locale));
				response.setItems(InstitutionTransformer.INSTANCE.toDtos(items));
				
			}
				
			}	
			
		catch (PermissionDeniedDataAccessException e) {
			   exceptionUtils.PERMISSION_DENIED_DATA_ACCESS_EXCEPTION(response, locale, e);
		  } catch (DataAccessResourceFailureException e) {
		   exceptionUtils.DATA_ACCESS_RESOURCE_FAILURE_EXCEPTION(response, locale, e);
		  } catch (DataAccessException e) {
		   exceptionUtils.DATA_ACCESS_EXCEPTION(response, locale, e);
		  } catch (RuntimeException e) {
		   exceptionUtils.RUNTIME_EXCEPTION(response, locale, e);
		  } catch (Exception e) {
		   exceptionUtils.EXCEPTION(response, locale, e);
		  } finally {
		   if (response.isHasError() && response.getStatus() != null) {
		    slf4jLogger.info("Erreur| code: {} -  message: {}", response.getStatus().getCode(), response.getStatus().getMessage());
		    throw new RuntimeException(response.getStatus().getCode() + ";" + response.getStatus().getMessage());
		   }
		  }
		return response;
	}





	
	
	
	  @Override
	  public Response<InstitutionDto> getByCriteria(Request<InstitutionDto> request, Locale locale) {
	    slf4jLogger.info("----begin get Institution-----");

	    response = new Response<InstitutionDto>();

	    try {
	      List<Institution> items = null;
	      items = institutionRepository.getByCriteria(request, em, locale);
	      if (items != null && !items.isEmpty()) {
	        List<InstitutionDto> itemsDto = new ArrayList<InstitutionDto>();
	        for (Institution entity : items) {
	          InstitutionDto dto = getFullInfos(entity, items.size(), locale);
	          if (dto == null) continue;
	          itemsDto.add(dto);
	        }
	        response.setItems(itemsDto);
	        response.setCount(institutionRepository.count(request, em, locale));
	        response.setHasError(false);
	      } else {
	        response.setStatus(functionalError.DATA_EMPTY("Institution", locale));
	        response.setHasError(false);
	        return response;
	      }

	      slf4jLogger.info("----end get Institution-----");
	    } catch (PermissionDeniedDataAccessException e) {
	      exceptionUtils.PERMISSION_DENIED_DATA_ACCESS_EXCEPTION(response, locale, e);
	    } catch (DataAccessResourceFailureException e) {
	      exceptionUtils.DATA_ACCESS_RESOURCE_FAILURE_EXCEPTION(response, locale, e);
	    } catch (DataAccessException e) {
	      exceptionUtils.DATA_ACCESS_EXCEPTION(response, locale, e);
	    } catch (RuntimeException e) {
	      exceptionUtils.RUNTIME_EXCEPTION(response, locale, e);
	    } catch (Exception e) {
	      exceptionUtils.EXCEPTION(response, locale, e);
	    } finally {
	      if (response.isHasError() && response.getStatus() != null) {
	        slf4jLogger.info("Erreur| code: {} -  message: {}", response.getStatus().getCode(), response.getStatus().getMessage());
	        throw new RuntimeException(response.getStatus().getCode() + ";" + response.getStatus().getMessage());
	      }
	    }
	    return response;
	  }
	  
	  
	  private InstitutionDto getFullInfos(Institution entity, Integer size, Locale locale) throws Exception {
		    InstitutionDto dto = InstitutionTransformer.INSTANCE.toDto(entity);
		    if (dto == null){
		      return null;
		    }
		    if (size > 1) {
		      return dto;
		    }

		    return dto;
		  }



}
