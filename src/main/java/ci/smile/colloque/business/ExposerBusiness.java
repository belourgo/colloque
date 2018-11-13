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

import ci.smile.colloque.dao.entity.Exposer;
import ci.smile.colloque.dao.repository.ExposerRepository;
import ci.smile.colloque.helper.ExceptionUtils;
import ci.smile.colloque.helper.FunctionalError;
import ci.smile.colloque.helper.Utilities;
import ci.smile.colloque.helper.Validate;
import ci.smile.colloque.helper.contract.IBasicBusiness;
import ci.smile.colloque.helper.contract.Request;
import ci.smile.colloque.helper.contract.Response;
import ci.smile.colloque.helper.dto.ExposerDto;
import ci.smile.colloque.helper.dto.transformer.ExposerTransformer;

@Component
public class ExposerBusiness implements IBasicBusiness<Request<ExposerDto>, Response<ExposerDto>>{

	private Response<ExposerDto> response ;
	
	//Le Repository
	@Autowired
	private ExposerRepository exposerRepository;
	
	
	
	
	// Necessaire pour reporter les Erreurs 
	@Autowired
	private FunctionalError functionalError; 
	
	// Pour les execption
	@Autowired
	private ExceptionUtils exceptionUtils;
	
	
	//-----
	// Slf4Logger
	//-------------
	
	private Logger slf4jLogger;
	
	
	//------------------
	//Entity Manager
	//---------------------
	@PersistenceContext
	 private EntityManager em;
	
	public  ExposerBusiness( ) { 
		slf4jLogger = LoggerFactory.getLogger(getClass());
	}
	
	
	
	//-------------
	//------ Method CRUD
	//--------------------
	
	
	//--------------------
	// Method Create
	//-------------
	
	@Override
	public Response<ExposerDto> create(Request<ExposerDto> request, Locale locale) {
		// TODO Auto-generated method stub
		response = new Response<>(); 
		try {
			List<Exposer> items = new ArrayList<>();
			
		//forcer a renseigner des champs dans la requete
		for (ExposerDto dto : request.getDatas()) {
			
			// Mise des éléments dans un Map
			Map<String, Object> fieldToVerify= new HashMap<>();
			fieldToVerify.put("Titre",dto.getTitre());
			
			
			//Fonction deja écrite
			if (!Validate.RequiredValue(fieldToVerify).isGood()) {
				response.setHasError(true);
				response.setStatus(functionalError.FIELD_EMPTY(Validate.getValidate().getField(), locale));
				return response;
			}
			
			//Unicité du Titre de l'exposer
			
			Exposer existingExposer = exposerRepository.findByTitre(dto.getTitre());
			
			if( existingExposer !=null) {
				response.setHasError(true);
				response.setStatus(functionalError.DATA_EXIST(" Titre : "+ existingExposer.getTitre() , locale));
				return response;
			}
			
			//Transformation en Entite des dto reçu
			Exposer exposerTosave = ExposerTransformer.INSTANCE.toEntity(dto);
			
			//accumulation des entites a enregistrer
			
			items.add(exposerTosave);			
			
		}
		
		
		if( items != null && !items.isEmpty() ) {
			
			
			//Inserer dans la BD
			List<Exposer> itemsaved = exposerRepository.saveAll(items);
			
			if (itemsaved==null) {
				
				response.setHasError(true);
				response.setStatus(functionalError.SAVE_FAIL("Exposer", locale));
				return response;
			}
			
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

		return this.response;
	}

	


	//--------------------------------------------------
	//Method Update
	//--------------------------------------------------
	
	public Response<ExposerDto> update(Request<ExposerDto> request, Locale locale){
		response=new Response<>();
		try {

			List<Exposer> items = new ArrayList<>();
			
			for (ExposerDto dto : request.getDatas()) {
				
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
												
						
				
				//Exposer exposer = exposerRepository.findById(dto.getId()); 
				Optional<Exposer> exposer = exposerRepository.findById(dto.getId());

				// isPresent retourne True si exposer contient une valeur
				
				if( !exposer.isPresent() ) {
					response.setHasError(true);
					response.setStatus(functionalError.DATA_NOT_EXIST("exposer id--> "+dto.getId(), locale));
			        return response;
				}
				
				//on applique les modifications demandées
				if(Utilities.notBlank(dto.getTitre())
						&& !dto.getTitre().equals(exposer.get().getTitre())) {
					//unicité du nom de l'université
					Exposer existingExposer= exposerRepository.findByTitre(dto.getTitre());
					
					if(existingExposer != null && existingExposer.getId() != exposer.get().getId()){
						response.setHasError(true);
						response.setStatus(functionalError.DATA_EXIST("nom :"+dto.getTitre(), locale));
					    return response;
					}

					exposer.get().setTitre(dto.getTitre());
				}
				
				//------------------------------
				//Ajout du Resumer
				//--------------------------------------
				
				
				if(Utilities.notBlank(dto.getResumer())
						&& !dto.getResumer().equals(exposer.get().getResumer())) {
					exposer.get().setResumer(dto.getResumer());
				}
				
				items.add(exposer.get());
				// enregistrer dans la bdd
				
				
				// on ramene au front les nouvelles valeurs
				
			}
			if(items != null && !items.isEmpty()) { 
				//inserer dans la base de données
				List<Exposer> itemsSaved=exposerRepository.saveAll(items);
				if (itemsSaved == null) {
					response.setHasError(true);
					response.setStatus(functionalError.SAVE_FAIL("exposer", locale));
					return response;
					
				}
				//transformation en dto des données insérées en bdd 
				//et retour au front
				response.setHasError(false);
				response.setStatus(functionalError.SUCCESS("update exposer", locale));
				response.setItems(ExposerTransformer.INSTANCE.toDtos(itemsSaved));
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
	
	
	
	public Response<ExposerDto> delete(Request<ExposerDto> request,Locale locale){

		// retrouver l'enregistrement à supprimer,
		// id obligatoire
		
		//Recuperer et emmagasiner
		
		// on supprime 
		response=new Response<>();
		
		
		try {
			
			
			List<Exposer> items = new ArrayList<>();
			
			for (ExposerDto dto : request.getDatas()) {
				
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
				
				Optional<Exposer> exposer = exposerRepository.findById(dto.getId());
			
				
				//---------------
				//1-Erreur si exposer ne retourne rien
				//2-Erreur si le nom renseigné ne correspond pas au nom en BD
				//------------------------
				if(!exposer.isPresent() && dto.getTitre() != exposer.get().getTitre()) {	
					response.setHasError(true);
					response.setStatus(functionalError.DATA_NOT_EXIST("L'exposer avec ID " +
					dto.getId() + 	"n'existe pas", locale)) ;
					return response;
				}
				
				//--------------------
				// Fird step:    Recuperer et Emmagasiner
				//---------------------
				
				
				items.add(exposer.get());
				
			}
			
			if(  items != null  &&  !items.isEmpty()  ) {
				
				// -----------------------------
				// Delete
				//------------------------------
				
				exposerRepository.deleteInBatch(items);      // Method return void
				
				//Verifier que les elements ont ete supprime  ______ Pas encore fait ___
				
				
				//---------------------
				// Retourner reponse pour ce qui a ete surprime 
				//-------------------------------
				response.setHasError(false);
				response.setStatus(functionalError.SUCCESS("deleted exposer", locale));
				response.setItems(ExposerTransformer.INSTANCE.toDtos(items));
				
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
	public Response<ExposerDto> getByCriteria(Request<ExposerDto> request, Locale locale) {
	    slf4jLogger.info("----begin get Exposer-----");

	    response = new Response<ExposerDto>();

	    try {
	      List<Exposer> items = null;
	      items = exposerRepository.getByCriteria(request, em, locale);
	      if (items != null && !items.isEmpty()) {
	        List<ExposerDto> itemsDto = new ArrayList<ExposerDto>();
	        for (Exposer entity : items) {
	          ExposerDto dto = getFullInfos(entity, items.size(), locale);
	          if (dto == null) continue;
	          itemsDto.add(dto);
	        }
	        response.setItems(itemsDto);
	        response.setCount(exposerRepository.count(request, em, locale));
	        response.setHasError(false);
	      } else {
	        response.setStatus(functionalError.DATA_EMPTY("Exposer", locale));
	        response.setHasError(false);
	        return response;
	      }

	      slf4jLogger.info("----end get Exposer-----");
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
	  
	  
	  private ExposerDto getFullInfos(Exposer entity, Integer size, Locale locale) throws Exception {
		    ExposerDto dto = ExposerTransformer.INSTANCE.toDto(entity);
		    if (dto == null){
		      return null;
		    }
		    if (size > 1) {
		      return dto;
		    }

		    return dto;
		  }

	
	
}
