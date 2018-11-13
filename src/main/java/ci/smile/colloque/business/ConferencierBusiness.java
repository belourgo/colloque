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
import ci.smile.colloque.dao.entity.Institution;
import ci.smile.colloque.dao.entity.Participant;
import ci.smile.colloque.dao.repository.ColloqueRepository;
import ci.smile.colloque.dao.repository.ConferencierRepository;
import ci.smile.colloque.dao.repository.ExposerRepository;
import ci.smile.colloque.dao.repository.InstitutionRepository;
import ci.smile.colloque.dao.repository.ParticipantRepository;
import ci.smile.colloque.helper.ExceptionUtils;
import ci.smile.colloque.helper.FunctionalError;
import ci.smile.colloque.helper.Validate;
import ci.smile.colloque.helper.contract.IBasicBusiness;
import ci.smile.colloque.helper.contract.Request;
import ci.smile.colloque.helper.contract.Response;
import ci.smile.colloque.helper.dto.ConferencierDto;
import ci.smile.colloque.helper.dto.InscriptionDto;
import ci.smile.colloque.helper.dto.PresentationDto;
import ci.smile.colloque.helper.dto.transformer.ConferencierTransformer;

@Component
public class ConferencierBusiness implements IBasicBusiness<Request<ConferencierDto>,Response<ConferencierDto>> {

	
	private Response<ConferencierDto> response;
	
	@Autowired
	private FunctionalError functionalError;
	
	//-----------------------------------------------------
	//Variable dont l'utilisation est necessaire / NB: On instancie pas les interfaces
	//---------------------------------------------
			
	@Autowired
	private ConferencierRepository conferencierRepository;
	@Autowired
	private ParticipantRepository participantRepository ;
	@Autowired
	private InstitutionRepository institutionRepository ;
	@Autowired
	private ColloqueRepository colloqueRepository;
	@Autowired
	private ExposerRepository exposerRepository;
	@Autowired
	InscriptionBusiness inscriptionBusiness ;
	@Autowired
	private PresentationBusiness presentationBusiness ;

	
	
	
	
	@Autowired
	private ExceptionUtils exceptionUtils;

	private Logger slf4jLogger;
	
	@PersistenceContext
	 private EntityManager em;
	

	public  ConferencierBusiness( ) { 
		slf4jLogger = LoggerFactory.getLogger(getClass());
	}

	
	//-----------------------------------------
	// Create
	//------------------------------------------------
	
	@Override
	public Response<ConferencierDto> create(Request<ConferencierDto> request, Locale locale){
		response=new Response<>();
		
		
		slf4jLogger.info("-----debut create conferencier ------");
		try {
			List<Conferencier> items = new ArrayList<>();
		for (ConferencierDto dto : request.getDatas()) {
			
			
			//-----------------------------------------------------
			//Verifier que les champs obligatoires ont été saisie
			//---------------------------------------------
			
			
			// the function witch is coming need maps
			Map<String, Object> fieldsToVerify= new HashMap<>();
			
			fieldsToVerify.put("InstitutionId",dto.getInstitutionId());
			fieldsToVerify.put("ParticipantId",dto.getParticipantId());
			
			// The map is now in function fieldToVerify
			
			if (!Validate.RequiredValue(fieldsToVerify).isGood() ) {
				response.setHasError(true);
				response.setStatus(functionalError.FIELD_EMPTY(Validate.getValidate().getField(), locale));
				return response;
			}
			
			
			
			//----------------------------------------
			//Unicite par l'utilisation de participantId dans Organisation
			//---------------------------------------------------
			
			
			Optional<Conferencier> existingConferencier= conferencierRepository.findById(dto.getParticipantId());
			Optional<Participant> existingParticipant= participantRepository.findById(dto.getParticipantId());
			Optional<Institution> existingInstitution= institutionRepository.findById(dto.getInstitutionId());

			if( existingConferencier.isPresent()
					){
				response.setHasError(true);
				response.setStatus(functionalError.DATA_EXIST("Le conferncier ayant pour ID  :"+dto.getParticipantId()+
						"existe deja dabs la base de donnée", locale));
			    return response;
			}
			
		
			
			if( !existingParticipant.isPresent() ||
					!existingInstitution.isPresent() 
					){
				response.setHasError(true);
				
				if(!existingParticipant.isPresent() ) {
					response.setStatus(functionalError.DATA_NOT_EXIST("Le participant n'existe pas "
							+ " dans la base de donnée ", locale));
				}
				
				if(!existingInstitution.isPresent()  ) {
					response.setStatus(functionalError.DATA_NOT_EXIST(" L'institution n'existe pas "
							+ " dans la base de donnée ", locale));
				}
				
			    return response;
			}
			
			
			
			
			
			//----------------------------------------
			//Existence de participantId et de InstitutionId dans les tables
			// Institution et Participant
			//---------------------------------------------------
			
			
			//transformation en entité des dto reçus (request)
			Conferencier conferencierToSave=ConferencierTransformer.INSTANCE.toEntity(dto, existingParticipant.get(), existingInstitution.get());
			
			
			//accumulation des entités à enregistrer
			items.add(conferencierToSave);
	
		}
		if(items != null && !items.isEmpty()) { 
			//inserer dans la base de données
			List<Conferencier> itemsSaved=conferencierRepository.saveAll(items);
			if (itemsSaved == null) {
				response.setHasError(true);
				response.setStatus(functionalError.SAVE_FAIL("conferencier", locale));
				return response;
				
			}
			//transformation en dto des données insérées en bdd 
			//et retour au front
			response.setHasError(false);
			response.setStatus(functionalError.SUCCESS("create conferencier", locale));
			response.setItems(ConferencierTransformer.INSTANCE.toDtos(itemsSaved));
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

		slf4jLogger.info("-----fin create conferencier ------");
		return response;
	}
	
	






	
	//--------------------------------------------------
	//Method Update
	//--------------------------------------------------
	
	public Response<ConferencierDto> update(Request<ConferencierDto> request, Locale locale){
		response=new Response<>();
		try {

			List<Conferencier> items = new ArrayList<>();
			
			for (ConferencierDto dto : request.getDatas()) {
				
				// retrouver l'enregistrement dont on veut modifier les valeurs,
				// id obligatoire
				// the function witch is coming need maps

				
				//-----------------------------------------------------
				//Verifier que les champs obligatoires ont été saisie
				//---------------------------------------------
				
				
				// the function witch is coming need maps
				Map<String, Object> fieldsToVerify= new HashMap<>();
				fieldsToVerify.put("InstitutionId",dto.getInstitutionId());
				fieldsToVerify.put("ParticipantId",dto.getParticipantId());
				
				// The map is now in function fieldToVerify
				
				if (!Validate.RequiredValue(fieldsToVerify).isGood() ) {
					response.setHasError(true);
					response.setStatus(functionalError.FIELD_EMPTY(Validate.getValidate().getField(), locale));
					return response;
				}
				

				
				
				//--------------------------------------------------------
				//Verifier que le conferencier existe par le participantId
				// Ce qu'on peut modifier c'est Organisation et l'institution
				//--------------------------------------------------------											
		
				
				Optional<Conferencier> conferencier = conferencierRepository.findById(dto.getParticipantId());
				
				if( !conferencier.isPresent() ) {
					response.setHasError(true);
					response.setStatus(functionalError.DATA_NOT_EXIST("conferencier id--> "+dto.getParticipantId(), locale));
			        return response;
				}
				

				
				//-----------------------------------------------------------------------
				//Verifie que l'institution existe bien en 
				//base de donnée Institution avant le Update
				//-----------------------------------------------------------------------
				
				Optional<Institution> existingInstitution = institutionRepository.findById(dto.getInstitutionId()) ;

				
				if( !existingInstitution.isPresent()) {
							response.setHasError(true);
							response.setStatus(functionalError.DATA_NOT_EXIST(" L'institution avec id --->"
									+ dto.getInstitutionId() +"n'existe pas", locale)) ;
							return response;			
						}
						
				conferencier.get().setInstitution(existingInstitution.get());
				
				
			//---------------------------
			//Verifier si un champ est un boolean
			//-----------------------------
				
				if(dto.getOrganisateur() != null) {
					
					  conferencier.get().setOrganisateur(dto.getOrganisateur());
					
				}
				
			//---------------------------------------------------------	
			// enregistrer dans la bdd	
			//-----------------------------------------------------------
				
				items.add(conferencier.get());
			
				
				
				// on ramene au front les nouvelles valeurs
				
			}
			if(items != null && !items.isEmpty()) { 
				//inserer dans la base de données
				List<Conferencier> itemsSaved=conferencierRepository.saveAll(items);
				if (itemsSaved == null) {
					response.setHasError(true);
					response.setStatus(functionalError.SAVE_FAIL("conferencier", locale));
					return response;
					
				}
				//transformation en dto des données insérées en bdd 
				//et retour au front
				response.setHasError(false);
				response.setStatus(functionalError.SUCCESS("update conferencier", locale));
				response.setItems(ConferencierTransformer.INSTANCE.toDtos(itemsSaved));
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
	//METHOD DELETE
	//--------------------------------------------------
	
	
	public Response<ConferencierDto> delete(Request<ConferencierDto> request,Locale locale){

		// retrouver l'enregistrement à supprimer,
		// id obligatoire
		
		//Recuperer et emmagasiner
		
		// on supprime 
		response=new Response<>();
		
		
		try {
			
			
			List<Conferencier> items = new ArrayList<>();
			
			for (ConferencierDto dto : request.getDatas()) {
				
				//--------------------
				// Fisrt step:id obligatoire
				//-------------------------------
				//Supprime a partir de Id du participant
				//---------------------
				
				Map<String, Object> fieldsToVerify = new HashMap<>();
				fieldsToVerify.put("id", dto.getParticipantId());
				
				if (!Validate.RequiredValue(fieldsToVerify).isGood()) {
					response.setHasError(true);
					response.setStatus(functionalError.FIELD_EMPTY(Validate.getValidate().getField(), locale));
					return response;
				}
				
				//--------------------
				// Second step:    Retrouver l'enregistrement à supprimer
				//---------------------
				
				Optional<Conferencier> conferencier = conferencierRepository.findById(dto.getParticipantId());
			
				
				//---------------
				//1-Erreur si conferencier ne retourne rien
				//2-Erreur si le nom renseigné ne correspond pas au nom en BD
				//------------------------
				
				
				if(!conferencier.isPresent() && dto.getParticipantId() != conferencier.get().getParticipantId()) {	
					response.setHasError(true);
					response.setStatus(functionalError.DATA_NOT_EXIST("L'conferencier avec ID " +
					dto.getParticipantId() + 	"n'existe pas", locale)) ;
					return response;
				}
				
				
				//-------------------------------------------------------------
				//1-Erreur si champ non validé
				//-------------------------------------------------------
				//2-Verifier s'il rempli le champ Institution, que la saisie est correcte
				//--------------------------------------------------------------------------
				//3-ConferencierTransformer.INSTANCE.toDto(conferencier.get()).getInstitution();
				//---------------------------------------------------------------------
				//Le champ au dessus permet de ramener un type dto
				//---------------------------------------------------------------
				
				if (dto.getInstitutionId() != null && 
						dto.getInstitutionId() == (int) dto.getInstitutionId() &&
						dto.getInstitutionId() != 
						ConferencierTransformer.INSTANCE.toDto(conferencier.get()).getInstitutionId()) {
				
					response.setHasError(true);
					response.setStatus(functionalError.DATA_NOT_DELETABLE("L'Id du  champ Institution --->"
						+ dto.getInstitutionId()	+ "   n'est pas correcte", locale)) ;
					return response;
					
					
					
				}
				
				
				
				//-----------------------------------------------------
				//S'assurer que Institution est correcte
				//------------------------------------------------------
				//Chercher a voir le cas de dto.getOrganisateur
				//------------------------------------------------------
							
				Boolean.valueOf(dto.getOrganisateur());
				if (dto.getOrganisateur() != null &&
						(Boolean.TRUE || 
						Boolean.FALSE ) &&
						dto.getOrganisateur() != 
						ConferencierTransformer.INSTANCE.toDto(conferencier.get()).getOrganisateur()
						)  {
					
					response.setHasError(true);
					response.setStatus(functionalError.DATA_NOT_DELETABLE("L'Id du  champ Organisateur --->"
						+ dto.getOrganisateur()	+ "   n'est pas correcte", locale)) ;
					return response;
					
					
				}
				
				
				//--------------------
				// Fird step:    Recuperer et Emmagasiner
				//---------------------
				
				items.add(conferencier.get());
				
			}
			
			
			
			if(  items != null  &&  !items.isEmpty()  ) {
				
				// -----------------------------
				// Delete
				//------------------------------
				
				conferencierRepository.deleteInBatch(items);      // Method return void
				
				//Verifier que les elements ont ete supprime  ______ Pas encore fait ___
				
				
				//----------------------------------
				// RETURN RESPONSE FOR DELETED ITEMS 
				//----------------------------------
				response.setHasError(false);
				response.setStatus(functionalError.SUCCESS("deleted conferencier", locale));
				response.setItems(ConferencierTransformer.INSTANCE.toDtos(items));
				
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
	public Response<ConferencierDto> getByCriteria(Request<ConferencierDto> request, Locale locale) {
	    slf4jLogger.info("----begin get Conferencier-----");

	    response = new Response<ConferencierDto>();

	    try {
	      List<Conferencier> items = null;
	      items = conferencierRepository.getByCriteria(request, em, locale);
	      if (items != null && !items.isEmpty()) {
	        List<ConferencierDto> itemsDto = new ArrayList<ConferencierDto>();
	        for (Conferencier entity : items) {
	          ConferencierDto dto = getFullInfos(entity, items.size(), locale);
	          if (dto == null) continue;
	          itemsDto.add(dto);
	        }
	        response.setItems(itemsDto);
	        response.setCount(conferencierRepository.count(request, em, locale));
	        response.setHasError(false);
	      } else {
	        response.setStatus(functionalError.DATA_EMPTY("Conferencier", locale));
	        response.setHasError(false);
	        return response;
	      }

	      slf4jLogger.info("----end get Conferencier-----");
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
	  
	  
	  private ConferencierDto getFullInfos(Conferencier entity, Integer size, Locale locale) throws Exception {
		    ConferencierDto dto = ConferencierTransformer.INSTANCE.toDto(entity);
		    if (dto == null){
		      return null;
		    }
		    if (size > 1) {
		      return dto;
		    }

		    return dto;
		  }
	  
	  
	  
	  public Response<ConferencierDto> createConferencier(Request<ConferencierDto> request, Locale locale){
		  
		  
	
			
		    //--------------------------------------------------- 
			// 
			// POUR LA CREATION DE INSCRIPTION
			//
			//--------------------------------------------------------------
			//RECUPERER LES DTO LA REQUETE ET LES METTRE DANS InscriptionDto 
			//--------------------------------------------------------------
			
		    Request<PresentationDto> requestPresentation= new Request<>() ;
			Request<InscriptionDto> requestInscription= new Request<>() ;
			
		  
		  try {
				List<Integer> itemsInstitutionId = new ArrayList<>();
				List<Integer> itemsEposerId = new ArrayList<>();
				List<InscriptionDto> itemsInscritption = new ArrayList<>();
				List<PresentationDto> itemsPresenation = new ArrayList<>();
				
				PresentationDto presentationDto = new PresentationDto();
			  
			  for( ConferencierDto dto : request.getDatas()) {
					//----------------------------------
					//CHAMPS OBLIGATOIRE DU PARTICIPANT
					//----------------------------------
					Map<String, Object> fieldsToVerify= new HashMap<>();
					
						//---------------------------------------------
						//CHAMP INSTITUTION OBLIGATOIRE
						//---------------------------------------------
					
					fieldsToVerify.put("institutionName",dto.getInstitutionName());
				
						//---------------------------------------------
						//CHAMP INSCRIPTION OBLIGATOIRE
						//---------------------------------------------
					
					fieldsToVerify.put("colloqueName",dto.getInscriptionDto().getColloqueName());
					fieldsToVerify.put("nom",dto.getInscriptionDto().getParticipantDto().getNom());
					fieldsToVerify.put("numeroDeParticipation",dto.getInscriptionDto().getParticipantDto().getNumeroDeParticipation());
					fieldsToVerify.put("nom", dto.getInscriptionDto().getColloqueName());
					
					//--------------------
					// EXPOSER OBLIGATOIRE
					//--------------------
					fieldsToVerify.put("titre", dto.getExposer().getTitre());

					
					// The map is now in function fieldToVerify
					
					if (!Validate.RequiredValue(fieldsToVerify).isGood() ) {
						response.setHasError(true);
						response.setStatus(functionalError.FIELD_EMPTY(Validate.getValidate().getField(), locale));
						return response;
					}
					
					//---------------------
					// EXISTING INSTITUTION
					//---------------------
					
					Institution existingInstitution=null;
					 existingInstitution = institutionRepository.findByNom(dto.getInstitutionName()) ;
				
					if( existingInstitution == null  ) {
						
						response.setHasError(true);
						response.setStatus(functionalError.DATA_NOT_EXIST(" L'institution n'existe pas "
								+ " dans la base de donnée ", locale));
					}

					//-----------------
					// EXISTING EXPOSER
					//-----------------
					
					Exposer existingExposer=null;
					existingExposer = exposerRepository.findByTitre(dto.getExposer().getTitre()) ;
				
					if( existingExposer == null  ) {
						
						response.setHasError(true);
						response.setStatus(functionalError.DATA_NOT_EXIST(" L'Exposer n'existe pas "
								+ " dans la base de donnée ", locale));
					}
					
					itemsInstitutionId.add(existingExposer.getId());
					itemsEposerId.add(existingInstitution.getId());
					itemsInscritption.add(dto.getInscriptionDto());		
					
				
				  
			  }
			  
			  
			  	//------------------------------------------------------
				//CREATION DES INSCRIPTIONS
				//------------------------------------------------------
				
				if(
				   !itemsInscritption.isEmpty() && itemsInscritption !=null &&		
				   !itemsInstitutionId.isEmpty() && itemsInstitutionId != null &&			   
				   !itemsEposerId.isEmpty() && itemsEposerId != null
				   ) {
					
					requestInscription.setDatas(itemsInscritption);
					Response<InscriptionDto> responseInscriptionDto = inscriptionBusiness.createInscrit(requestInscription, locale);
				 
			if ( responseInscriptionDto.getItems() != null 
					&&  !responseInscriptionDto.getItems().isEmpty() ) {
				
			
				List<ConferencierDto> itemsConferencierDto = new ArrayList<>();
				
				for (ConferencierDto dto : request.getDatas()) {
				
					//---------------------------------
					// RECUPERER LES Id DU PARTICIPANT
					// ET LES Id DU COLLOQUE
				    //---------------------------------
					
					Institution existingInstitution=null;
					Participant existingParticipant=null;
					Colloque 	existingColloque=null;
					Exposer 	existingExposer= null;
					
					existingColloque = colloqueRepository.findByNom(dto.getInscriptionDto().getColloqueName()) ;
					existingExposer = exposerRepository.findByTitre(dto.getExposer().getTitre()) ;

					
					existingInstitution = institutionRepository.findByNom(dto.getInstitutionName()) ;
					existingParticipant= participantRepository.findByNumPar(dto.getInscriptionDto().getParticipantDto().getNumeroDeParticipation());
				
					if( existingInstitution == null || existingParticipant ==null ||
						existingColloque == null || existingExposer == null) {
						
						response.setHasError(true);
						response.setStatus(functionalError.DATA_NOT_EXIST(" Le colloque n'existe pas "
								+ " dans la base de donnée ", locale));
					}
					
					//-------------------------------------------------
					// PAR DEFAUT LE PARTICIPANT N'EST PAS ORGANISATEUR
					//-------------------------------------------------
					dto.setOrganisateur(false);
					
					dto.setInstitutionId(existingInstitution.getId()); 
					dto.setParticipantId(existingParticipant.getId());
					presentationDto.setConferencierId(existingParticipant.getId());
					itemsConferencierDto.add(dto);
					
					//--------------------------------------
					// AJOUT DES ELEMENTS DE LA PRESENTATION
					//--------------------------------------
					presentationDto.setColloqueId(existingColloque.getId());
					presentationDto.setExposerId(existingExposer.getId());
					
					presentationDto.setResumer(existingExposer.getResumer());
						
					
					itemsPresenation.add(presentationDto);
					
					}
				//-----------------------------------------
				//CREATE CONFERENCIER 
				//------------------------------------------
				
				
				
				if(!itemsConferencierDto.isEmpty() && itemsConferencierDto!= null) {
				request= new Request<>();
				request.setDatas(itemsConferencierDto);	
				Response<ConferencierDto> responseConferencierDto = create(request, locale);
				
				
				if ( responseConferencierDto.getItems() != null ||
						!responseConferencierDto.getItems().isEmpty()) {
				
				 requestPresentation= new Request<>();
				 requestPresentation.setDatas(itemsPresenation);;
				 presentationBusiness.create(requestPresentation, locale);
			
				}
				
				}
				
				response.setHasError(false);
				response.setStatus(functionalError.SUCCESS("Created Conferencier", locale));
				response.setItems(itemsConferencierDto);					
			
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
		  
		  
		  return response;
	  }
	
}
