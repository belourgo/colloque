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

import ci.smile.colloque.dao.entity.Colloque;
import ci.smile.colloque.dao.entity.Conferencier;
import ci.smile.colloque.dao.entity.Exposer;
import ci.smile.colloque.dao.entity.Presentation;
import ci.smile.colloque.dao.repository.ColloqueRepository;
import ci.smile.colloque.dao.repository.ConferencierRepository;
import ci.smile.colloque.dao.repository.ExposerRepository;
import ci.smile.colloque.dao.repository.PresentationRepository;
import ci.smile.colloque.helper.ExceptionUtils;
import ci.smile.colloque.helper.FunctionalError;
import ci.smile.colloque.helper.Validate;
import ci.smile.colloque.helper.contract.IBasicBusiness;
import ci.smile.colloque.helper.contract.Request;
import ci.smile.colloque.helper.contract.Response;
import ci.smile.colloque.helper.dto.PresentationDto;
import ci.smile.colloque.helper.dto.transformer.PresentationTransformer;

@Component
public class PresentationBusiness implements IBasicBusiness<Request<PresentationDto>,Response<PresentationDto>>{

	private Response<PresentationDto> response;
	
	
	//-----------------------------------------------------
	//Variable dont l'utilisation est necessaire / NB: On instancie pas les interfaces
	//---------------------------------------------
			
	@Autowired
	private PresentationRepository presentationRepository;
	@Autowired
	private ColloqueRepository colloqueRepository ;
	@Autowired
	private ConferencierRepository conferencierRepository ;
	@Autowired
	private ExposerRepository exposerRepository ;
	
	@Autowired
	private FunctionalError functionalError;
	
	@Autowired
	private ExceptionUtils exceptionUtils;

	private Logger slf4jLogger;
	
	@PersistenceContext
	private EntityManager em;
	

	public  PresentationBusiness( ) { 
		slf4jLogger = LoggerFactory.getLogger(getClass());
	}

	
	
	
	
	
	//-------------------------------------------------------------------------------
	// Create
	//-------------------------------------------------------------------------------
	
	
	public Response<PresentationDto> create(Request<PresentationDto> request, Locale locale){
		response=new Response<>();
		
		
		slf4jLogger.info("-----debut create presentation ------");
		try {
			List<Presentation> items = new ArrayList<>();
		for (PresentationDto dto : request.getDatas()) {
			
			
			
			//-----------------------------------------------------
			//VERIFIER LES CHAMPS OBLIGATOIRES
			//---------------------------------------------
			//ICI LE CHAMPS Numero de Telephone EST NECESSAIRE
			//------------------------------
			// the function witch is coming need maps
			
			Map<String, Object> fieldsToVerify= new HashMap<>();
			fieldsToVerify.put("ColloqueId",dto.getColloqueId());
			fieldsToVerify.put("ConferencierId",dto.getConferencierId());
			fieldsToVerify.put("ExposerId",dto.getExposerId());
			fieldsToVerify.put("Resumer",dto.getResumer());
			
			
			// The map is now in function fieldToVerify
			
			if (!Validate.RequiredValue(fieldsToVerify).isGood() ) {
				response.setHasError(true);
				response.setStatus(functionalError.FIELD_EMPTY(Validate.getValidate().getField(), locale));
				return response;
			}
			

			
			

		
			
			//----------------------------------------
			//EXISTANCE DE participantId ET DE ColloqueId DANS LES TABLES
			// Colloque ET Participant
			//---------------------------------------------------
			
			
			Optional<Exposer> existingExposer= exposerRepository.findById(dto.getExposerId());
			Optional<Colloque> existingColloque= colloqueRepository.findById(dto.getColloqueId());
			Optional<Conferencier> existingConferencier= conferencierRepository.findById(dto.getConferencierId());
		


			
			if( !existingExposer.isPresent() ||
					!existingColloque.isPresent() ||
					!existingConferencier.isPresent()
					){
				response.setHasError(true);
				
				if(!existingExposer.isPresent() ) {
					response.setStatus(functionalError.DATA_NOT_EXIST("L'exposer n'existe pas "
							+ " dans la base de donnée ", locale));
				}
				
				if(!existingColloque.isPresent()  ) {
					response.setStatus(functionalError.DATA_NOT_EXIST(" Le colloque n'existe pas "
							+ " dans la base de donnée ", locale));
				}
				
				if(!existingConferencier.isPresent()  ) {
					response.setStatus(functionalError.DATA_NOT_EXIST(" Le Conferencier n'existe pas "
							+ " dans la base de donnée ", locale));
				}
			    return response;
			}
			
			
			//----------------------------------------
			//EXISTANCE DE Presentation DANS LA TABLE Presentation 
			//------------------------------------------------------
			//ICI FAIRE ATTENTION AU TYPE DE PRSENTATION, CAR IL RECOIT LES TYPES ENTITY
			//---------------------------------------------------------------------------

				Presentation existingPresentation= presentationRepository.findByPresentation(existingExposer.get(),
						existingConferencier.get(), existingColloque.get());
				
			if( existingPresentation != null 
					){
				response.setHasError(true);
				response.setStatus(functionalError.DATA_EXIST(" La Presentation avec ID --->:"+ 
						existingPresentation.getId() +
				"     existe deja dans la base de donnée    ", locale));
			    return response;
			}
			
	
					
			
			//----------------------------------------
			//TRANSFORMATION DES DTO RECU EN ENTITE 
			//-------------------------------------
			
			Presentation presentationToSave=PresentationTransformer.INSTANCE.toEntity(dto,
					existingConferencier.get(), existingColloque.get(), existingExposer.get());
			
			if( presentationToSave == null) {
				
				response.setHasError(true);
				response.setStatus(functionalError.SAVE_FAIL("Echec de sauvegarde d'une"
						+ "Presentation", locale));
			    return response;
								
			}
			
			//--------------------------------------------------
			//ACCUMULATION DES ENTITES A ENREGISTRER
			//------------------------------------------------
			items.add(presentationToSave);
	
		}
		if(items != null && !items.isEmpty()) { 
			
			//---------------------------------------------
			//INSERTION DANS  LA  BASE DE DONNEE
			//---------------------------------------------
			List<Presentation> itemsSaved=presentationRepository.saveAll(items);
			if (itemsSaved == null) {
				response.setHasError(true);
				response.setStatus(functionalError.SAVE_FAIL("presentation", locale));
				return response;
				
			}
			
			//----------------------------------------------------
			//TRANSFORMATION EN DTO DES ENTITES ET RETOUR AU FRONT 
			//----------------------------------------------------
			response.setHasError(false);
			response.setStatus(functionalError.SUCCESS("create presentation", locale));
			response.setItems(PresentationTransformer.INSTANCE.toDtos(itemsSaved));
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

		slf4jLogger.info("-----fin create presentation ------");
		return response;
	}
	
	
	
	
	
	//--------------------------------------------------
	//Method Update
	//--------------------------------------------------
	
	public Response<PresentationDto> update(Request<PresentationDto> request, Locale locale){
		response=new Response<>();
		try {

			List<Presentation> items = new ArrayList<>();
			
			for (PresentationDto dto : request.getDatas()) {
				
				//-----------------------------------------------------
				//VERIFIER LES CHAMPS OBLIGATOIRES
				//---------------------------------------------
				//ICI LE CHAMPS Numero de Telephone EST NECESSAIRE
				//------------------------------
				// the function witch is coming need maps
				
				Map<String, Object> fieldsToVerify1= new HashMap<>();


				fieldsToVerify1.put("Id",dto.getId());
				
				
				
				
				// The map is now in function fieldToVerify
				
				if (!Validate.RequiredValue(fieldsToVerify1).isGood() ) {
					response.setHasError(true);
					response.setStatus(functionalError.FIELD_EMPTY(Validate.getValidate().getField(), locale));
					return response;
				}
				
				
				
				//--------------------------------------------------------
				//Verifier que le Presentation existe par le participantId
				// Ce qu'on peut modifier c'est Organisation et le colloque
				//--------------------------------------------------------											
		
				
				Optional<Presentation> presentation = presentationRepository.findById(dto.getId());
				
				if( !presentation.isPresent() ) {
					response.setHasError(true);
					response.setStatus(functionalError.DATA_NOT_EXIST("presentation id--> "+dto.getId(), locale));
			        return response;
				}
				

				
				//---------------------------------------------------------
				//SE RASSURER QU'ON RECOIT BIEN DES ENTIERS
				//-----------------------------------------------------------
				
				if ( 
					dto.getColloqueId() == (int) dto.getColloqueId() || 
					dto.getConferencierId() ==(int) dto.getConferencierId() ||
					dto.getExposerId() == (int) dto.getExposerId()
					)
						
				{
				   
				//-----------------------------------------------------------------------
				//VERIFIER QUE Colloque EXISTE BIEN DANS LA BASE DE DONNEE 
				//-----------------------------------------------------------------------
				
				Optional<Colloque> existingColloque = colloqueRepository.findById(dto.getColloqueId()) ;

				
				if( !existingColloque.isPresent()) {
							response.setHasError(true);
							response.setStatus(functionalError.DATA_NOT_EXIST(" L'colloque avec id --->"
									+ dto.getColloqueId() +"n'existe pas", locale)) ;
							return response;			
						}
						
				presentation.get().setColloque(existingColloque.get());
				
				
				//-----------------------------------------------------------------------
				//VERIFIER QUE Conferencier EXISTE BIEN DANS LA BASE DE DONNEE 
				//-----------------------------------------------------------------------
			
				
				Optional<Conferencier> existingConferencier = conferencierRepository.findById(dto.getConferencierId()) ;

				
				if( !existingConferencier.isPresent()) {
							response.setHasError(true);
							response.setStatus(functionalError.DATA_NOT_EXIST("  Le participant avec id --->   "
									+ dto.getConferencierId() +"    n'existe pas     ", locale)) ;
							return response;			
						}
						
				presentation.get().setConferencier(existingConferencier.get());

				
				//-----------------------------------------------------------------------
				//VERIFIER QUE Exposer EXISTE BIEN DANS LA BASE DE DONNEE 
				//-----------------------------------------------------------------------
				
				Optional<Exposer> existingExposer = exposerRepository.findById(dto.getExposerId()) ;

				
				
				if( !existingExposer.isPresent()) {
							response.setHasError(true);
							response.setStatus(functionalError.DATA_NOT_EXIST("  Le exposer avec id --->   "
									+ dto.getExposerId() +"    n'existe pas     ", locale)) ;
							return response;			
						}
						
				presentation.get().setExposer(existingExposer.get());

				
				}
				
				//---------------------------------------------------------	
				// AJOUT  DU NUMERO DANS LA  BASE DE DONNEE	S'IL N'EST PAS VIDE OU NUL
				//-----------------------------------------------------------
				
				if ( !dto.getResumer().isEmpty() 
						&& dto.getResumer() != null &&
						!dto.getResumer().equals(presentation.get().getResumer())) {
					
					presentation.get().setResumer(dto.getResumer());
				}
				
				
			//---------------------------------------------------------	
			// ACCUMULATION DES ENTITES	
			//-----------------------------------------------------------
						
				items.add(presentation.get());
			
				
				
				// on ramene au front les nouvelles valeurs
				
			}
			if(items != null && !items.isEmpty()) { 
				//inserer dans la base de données
				List<Presentation> itemsSaved=presentationRepository.saveAll(items);
				if (itemsSaved == null) {
					response.setHasError(true);
					response.setStatus(functionalError.SAVE_FAIL("Presentation", locale));
					return response;
					
				}
				//transformation en dto des données insérées en bdd 
				//et retour au front
				response.setHasError(false);
				response.setStatus(functionalError.SUCCESS("update presentation", locale));
				response.setItems(PresentationTransformer.INSTANCE.toDtos(itemsSaved));
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
	

	
	





	//-----------------------------------------------
	// DELETE 
	//-----------------------------------------------
	// deleteby Id | deleteby nom
	//-----------------------------------------------
	
	
	
	public Response<PresentationDto> delete(Request<PresentationDto> request,Locale locale){

		// retrouver l'enregistrement à supprimer,
		// id obligatoire
		
		//Recuperer et emmagasiner
		
		// on supprime 
		response=new Response<>();
		
		
		try {
			
			
			List<Presentation> items = new ArrayList<>();
			
			for (PresentationDto dto : request.getDatas()) {
				
				//--------------------
				// FIRST STEP:id obligatoire
				//---------------------
				
				Map<String, Object> fieldsToVerify = new HashMap<>();
				fieldsToVerify.put("id", dto.getId());
				
				if (!Validate.RequiredValue(fieldsToVerify).isGood()) {
					response.setHasError(true);
					response.setStatus(functionalError.FIELD_EMPTY(Validate.getValidate().getField(), locale));
					return response;
				}
				
				//--------------------
				// SECOND STEP:    Retrouver l'enregistrement à supprimer
				//---------------------
				
				Optional<Presentation> presentation = presentationRepository.findById(dto.getId());
			
				
				//------------------------------------------------------------
				//1-Erreur si presentation ne retourne rien
				//------------------------------------------------------------
				
				if(!presentation.isPresent() ) {	
					
					response.setHasError(true);
					
					if(!presentation.isPresent()) {
					response.setStatus(functionalError.DATA_NOT_EXIST("Le presentation avec ID " +
					dto.getId() + 	"n'existe pas", locale)) ;
					return response;
					}
					
			
				}

				
				
				//------------------------------------------
				// FIRD STEP:    Recuperer et Emmagasiner
				//------------------------------------------
				
				items.add(presentation.get());
				
			}
			
			if(  items != null  &&  !items.isEmpty()  ) {
				
				// -----------------------------
				// Delete
				//------------------------------
				
				presentationRepository.deleteInBatch(items);      // Method return void
				
				//Verifier que les elements ont ete supprime  ______ Pas encore fait ___
				
				
				//---------------------
				// Retourner reponse pour ce qui a ete surprime 
				//-------------------------------
				response.setHasError(false);
				response.setStatus(functionalError.SUCCESS("deleted presentation", locale));
				response.setItems(PresentationTransformer.INSTANCE.toDtos(items));
				
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
	public Response<PresentationDto> getByCriteria(Request<PresentationDto> request, Locale locale) {
	    slf4jLogger.info("----begin get Presentation-----");

	    response = new Response<PresentationDto>();

	    try {
	      List<Presentation> items = null;
	      items = presentationRepository.getByCriteria(request, em, locale);
	      if (items != null && !items.isEmpty()) {
	        List<PresentationDto> itemsDto = new ArrayList<PresentationDto>();
	        for (Presentation entity : items) {
	          PresentationDto dto = getFullInfos(entity, items.size(), locale);
	          if (dto == null) continue;
	          itemsDto.add(dto);
	        }
	        response.setItems(itemsDto);
	        response.setCount(presentationRepository.count(request, em, locale));
	        response.setHasError(false);
	      } else {
	        response.setStatus(functionalError.DATA_EMPTY("Presentation", locale));
	        response.setHasError(false);
	        return response;
	      }

	      slf4jLogger.info("----end get Presentation-----");
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
	  
	  
	  private PresentationDto getFullInfos(Presentation entity, Integer size, Locale locale) throws Exception {
		    PresentationDto dto = PresentationTransformer.INSTANCE.toDto(entity);
		    if (dto == null){
		      return null;
		    }
		    if (size > 1) {
		      return dto;
		    }

		    return dto;
		  }
	

}
