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

import ci.smile.colloque.dao.entity.Universite;
import ci.smile.colloque.dao.repository.UniversiteRepository;
import ci.smile.colloque.helper.ExceptionUtils;
import ci.smile.colloque.helper.FunctionalError;
import ci.smile.colloque.helper.Utilities;
import ci.smile.colloque.helper.Validate;
import ci.smile.colloque.helper.contract.IBasicBusiness;
import ci.smile.colloque.helper.contract.Request;
import ci.smile.colloque.helper.contract.Response;
import ci.smile.colloque.helper.dto.UniversiteDto;
import ci.smile.colloque.helper.dto.transformer.UniversiteTransformer;
import ci.smile.colloque.helper.extra.classes.CountColloqueByUniv;
import ci.smile.colloque.helper.extra.repository.NativeQueryByJDBC;

@Component
public class UniversiteBusiness implements IBasicBusiness<Request<UniversiteDto>,Response<UniversiteDto>> {
	
	private Response<UniversiteDto> response;
	

	
	@Autowired
	private FunctionalError functionalError;
	@Autowired
	private UniversiteRepository universiteRepository;
	@Autowired
	private ExceptionUtils exceptionUtils;

	private Logger slf4jLogger;
	
	@PersistenceContext
	 private EntityManager em;
	
	@Autowired
	private NativeQueryByJDBC nativeQueryByJDBC;
	
	

	public  UniversiteBusiness( ) { 
		slf4jLogger = LoggerFactory.getLogger(getClass());
	}
	
	
	
	public Response<UniversiteDto> create(Request<UniversiteDto> request, Locale locale){
		response=new Response<>();
		slf4jLogger.info("-----debut create universite ------");
		try {
			List<Universite> items = new ArrayList<>();
		for (UniversiteDto dto : request.getDatas()) {
			// exiger la saisie des champs obligatoires
			
			// the function witch is coming need maps
			Map<String, Object> fieldsToVerify= new HashMap<>();
			fieldsToVerify.put("Nom",dto.getNom());
			
			// The map is now in function fieldToVerify
			
			if (!Validate.RequiredValue(fieldsToVerify).isGood()) {
				response.setHasError(true);
				response.setStatus(functionalError.FIELD_EMPTY(Validate.getValidate().getField(), locale));
				return response;
			}
			
			//unicité du nom de l'université
			Universite existingUniversite= universiteRepository.findByNom(dto.getNom());
			if(existingUniversite != null){
				response.setHasError(true);
				response.setStatus(functionalError.DATA_EXIST("nom :"+dto.getNom(), locale));
			    return response;
			}

			
			//transformation en entité des dto reçus (request)
			Universite universiteToSave=UniversiteTransformer.INSTANCE.toEntity(dto);
			
			
			//accumulation des entités à enregistrer
			items.add(universiteToSave);
	
		}
		if(items != null && !items.isEmpty()) { 
			//inserer dans la base de données
			
			List<Universite> itemsSaved=universiteRepository.saveAll(items);
			if (itemsSaved == null) {
				response.setHasError(true);
				response.setStatus(functionalError.SAVE_FAIL("universite", locale));
				return response;
				
			}
			//transformation en dto des données insérées en bdd 
			//et retour au front
			response.setHasError(false);
			response.setStatus(functionalError.SUCCESS("create universite", locale));
			response.setItems(UniversiteTransformer.INSTANCE.todtos(itemsSaved));
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

		slf4jLogger.info("-----fin create universite ------");
		return response;
	}



	
	//--------------------------------------------------
	//Method Update
	//--------------------------------------------------
	
	public Response<UniversiteDto> update(Request<UniversiteDto> request, Locale locale){
		response=new Response<>();
		try {

			List<Universite> items = new ArrayList<>();
			
			for (UniversiteDto dto : request.getDatas()) {
				
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
												
						
				
				//Universite universite = universiteRepository.findById(dto.getId()); 
				Optional<Universite> universite = universiteRepository.findById(dto.getId());

				// isPresent retourne True si universite contient une valeur
				
				if( !universite.isPresent() ) {
					response.setHasError(true);
					response.setStatus(functionalError.DATA_NOT_EXIST("universite id--> "+dto.getId(), locale));
			        return response;
				}
				
				//on applique les modifications demandées
				//-------------------------------------------------------
				//Utilities.notBlank can be use similar to this condition
				//dto.getNom() != null && !dto.getNom().isEmpty() 
				//It permit to check also if the entry is a string or not
				//-------------------------------------------------------
				//dto.getNom() != null && !dto.getNom().isEmpty() &&
				//-------------------------------------------------------
				
				if(
						Utilities.notBlank(dto.getNom()) &&
						!dto.getNom().equals(universite.get().getNom())) {
					//unicité du nom de l'université
					Universite existingUniversite= universiteRepository.findByNom(dto.getNom());
					
					if(existingUniversite != null && existingUniversite.getId() != universite.get().getId()){
						response.setHasError(true);
						response.setStatus(functionalError.DATA_EXIST("nom :"+dto.getNom(), locale));
					    return response;
					}

					universite.get().setNom(dto.getNom());
				}
				if(        Utilities.notBlank(dto.getNom()) &&
						 !dto.getAdresse().equals(universite.get().getAdresse())) {
					universite.get().setAdresse(dto.getAdresse());
				}
				
				items.add(universite.get());
				// enregistrer dans la bdd
				
				
				// on ramene au front les nouvelles valeurs
				
			}
			if(items != null && !items.isEmpty()) { 
				//inserer dans la base de données
				List<Universite> itemsSaved=universiteRepository.saveAll(items);
				if (itemsSaved == null) {
					response.setHasError(true);
					response.setStatus(functionalError.SAVE_FAIL("universite", locale));
					return response;
					
				}
				//transformation en dto des données insérées en bdd 
				//et retour au front
				response.setHasError(false);
				response.setStatus(functionalError.SUCCESS("update universite", locale));
				response.setItems(UniversiteTransformer.INSTANCE.todtos(itemsSaved));
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
	
	public Response<UniversiteDto> delete(Request<UniversiteDto> request,Locale locale){

		// retrouver l'enregistrement à supprimer,
		// id obligatoire
		
		//Recuperer et emmagasiner
		
		// on supprime 
		response=new Response<>();
		
		
		try {
			
			
			List<Universite> items = new ArrayList<>();
			
			for (UniversiteDto dto : request.getDatas()) {
				
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
				
				Optional<Universite> universite = universiteRepository.findById(dto.getId());
			
				
				//---------------
				//1-Erreur si universite ne retourne rien
				//2-Erreur si le nom renseigné ne correspond pas au nom en BD
				//------------------------
				if(!universite.isPresent() && dto.getNom() != universite.get().getNom()) {	
					response.setHasError(true);
					response.setStatus(functionalError.DATA_NOT_EXIST("L'universite avec ID " +
					dto.getId() + 	"n'existe pas", locale)) ;
					return response;
				}
				
				//--------------------
				// Fird step:    Recuperer et Emmagasiner
				//---------------------
				
				
				items.add(universite.get());
				
			}
			
			if(  items != null  &&  !items.isEmpty()  ) {
				
				// -----------------------------
				// Delete
				//------------------------------
				
				universiteRepository.deleteInBatch(items);      // Method return void
				
				//Verifier que les elements ont ete supprime  ______ Pas encore fait ___
				
				
				//---------------------
				// Retourner reponse pour ce qui a ete surprime 
				//-------------------------------
				response.setHasError(false);
				response.setStatus(functionalError.SUCCESS("deleted universite", locale));
				response.setItems(UniversiteTransformer.INSTANCE.todtos(items));
				
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




	
	
	//--------------------------------------------------
	//Method GetByCriteria
	//--------------------------------------------------
	
	
	  @Override
	public Response<UniversiteDto> getByCriteria(Request<UniversiteDto> request, Locale locale) {
	    slf4jLogger.info("----begin get Universite-----");

	    response = new Response<UniversiteDto>();

	    try {
	      List<Universite> items = null;
	      items = universiteRepository.getByCriteria(request, em, locale);
	      if (items != null && !items.isEmpty()) {
	        List<UniversiteDto> itemsDto = new ArrayList<UniversiteDto>();
	        for (Universite entity : items) {
	          UniversiteDto dto = getFullInfos(entity, items.size(), locale);
	          if (dto == null) continue;
	          itemsDto.add(dto);
	        }
	        response.setItems(itemsDto);
	        response.setCount(universiteRepository.count(request, em, locale));
	        response.setHasError(false);
	      } else {
	        response.setStatus(functionalError.DATA_EMPTY("Universite", locale));
	        response.setHasError(false);
	        return response;
	      }

	      slf4jLogger.info("----end get Universite-----");
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
	  
	  
	  private UniversiteDto getFullInfos(Universite entity, Integer size, Locale locale) throws Exception {
		    UniversiteDto dto = UniversiteTransformer.INSTANCE.todto(entity);
		    if (dto == null){
		      return null;
		    }
		    if (size > 1) {
		      return dto;
		    }

		    return dto;
		  }

	  
	 


public Response<UniversiteDto> getNbreCol(Locale locale){
	  
	  response = new Response<>();
	  List<UniversiteDto> items = new ArrayList<>();

	  List<CountColloqueByUniv>  countUnivColList    =   nativeQueryByJDBC.getNbreColloqueByUniversity()  ;
	  
	  try {		  
		  
		  for ( CountColloqueByUniv countUnivCol : countUnivColList ) {
			  
			  UniversiteDto universiteDto = new UniversiteDto() ;
			  universiteDto.setCountColloqueByUniv(countUnivCol);
			  items.add(universiteDto);		
			  
		  }
		  
		  if ( !items.isEmpty() && items != null ) {
			  
				 
				//----------------------
				//ADD Items IF ALL IS OK 
				//----------------------
			  
			  	response.setItems(items);
		        response.setHasError(false);
		        response.setStatus(functionalError.SUCCESS("", locale));
		        
		        slf4jLogger.info("----Ca Marche-----");
		  }	
		  else {
			  response.setHasError(true);
			  slf4jLogger.info("----Erreur-----");
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




	

}
	