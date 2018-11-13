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
import ci.smile.colloque.dao.entity.Exposer;
import ci.smile.colloque.dao.entity.Institution;
import ci.smile.colloque.dao.entity.Organisateur;
import ci.smile.colloque.dao.entity.Participant;
import ci.smile.colloque.dao.repository.ColloqueRepository;
import ci.smile.colloque.dao.repository.OrganisateurRepository;
import ci.smile.colloque.dao.repository.ParticipantRepository;
import ci.smile.colloque.helper.ExceptionUtils;
import ci.smile.colloque.helper.FunctionalError;
import ci.smile.colloque.helper.Utilities;
import ci.smile.colloque.helper.Validate;
import ci.smile.colloque.helper.contract.IBasicBusiness;
import ci.smile.colloque.helper.contract.Request;
import ci.smile.colloque.helper.contract.Response;
import ci.smile.colloque.helper.dto.InscriptionDto;
import ci.smile.colloque.helper.dto.OrganisateurDto;
import ci.smile.colloque.helper.dto.PresentationDto;
import ci.smile.colloque.helper.dto.transformer.OrganisateurTransformer;

@Component
public class OrganisateurBusiness implements IBasicBusiness<Request<OrganisateurDto>, Response<OrganisateurDto>>{

	private Response<OrganisateurDto> response;
	
	
	//-----------------------------------------------------
	//Variable dont l'utilisation est necessaire / NB: On instancie pas les interfaces
	//---------------------------------------------
			
	@Autowired
	private OrganisateurRepository organisateurRepository;
	@Autowired
	private ParticipantRepository participantRepository ;
	@Autowired
	private ColloqueRepository colloqueRepository ;
	
	@Autowired
	private FunctionalError functionalError;
	
	@Autowired
	private ExceptionUtils exceptionUtils;

	private Logger slf4jLogger;
	
	@PersistenceContext
	 private EntityManager em;
	

	public  OrganisateurBusiness( ) { 
		slf4jLogger = LoggerFactory.getLogger(getClass());
	}

	
	
	

	
	
	
	//-----------------------------------------
	// Create
	//------------------------------------------------
	
	@Override
	public Response<OrganisateurDto> create(Request<OrganisateurDto> request, Locale locale){
		response=new Response<>();
		
		
		slf4jLogger.info("-----debut create organisateur ------");
		try {
			List<Organisateur> items = new ArrayList<>();
		for (OrganisateurDto dto : request.getDatas()) {
			
			
			
			//-----------------------------------------------------
			//VERIFIER LES CHAMPS OBLIGATOIRES
			//---------------------------------------------
			//ICI LE CHAMPS Numero de Telephone EST NECESSAIRE
			//------------------------------
			// the function witch is coming need maps
			
			Map<String, Object> fieldsToVerify= new HashMap<>();
			fieldsToVerify.put("ColloqueId",dto.getColloqueId());
			fieldsToVerify.put("ParticipantId",dto.getParticipantId());
			fieldsToVerify.put("NumeroDeTelephone",dto.getNumeroDeTelephone());
			
			
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
			
			Optional<Participant> existingParticipant= participantRepository.findById(dto.getParticipantId());
			Optional<Colloque> existingColloque= colloqueRepository.findById(dto.getColloqueId());

			
			if( !existingParticipant.isPresent() ||
					!existingColloque.isPresent() 
					){
				response.setHasError(true);
				
				if(!existingParticipant.isPresent() ) {
					response.setStatus(functionalError.DATA_NOT_EXIST("Le participant n'existe pas "
							+ " dans la base de donnée ", locale));
				}
				
				if(!existingColloque.isPresent()  ) {
					response.setStatus(functionalError.DATA_NOT_EXIST(" Le colloque n'existe pas "
							+ " dans la base de donnée ", locale));
				}
				
			    return response;
			}
			
			
			
			//----------------------------------------
			//EXISTANCE DE Presentation DANS LA TABLE Presentation 
			//------------------------------------------------------
			//ICI FAIRE ATTENTION AU TYPE DE PRSENTATION, CAR IL RECOIT LES TYPES ENTITY
			//---------------------------------------------------------------------------

				Organisateur existingOrganisateur= organisateurRepository.findByOrganisateur(existingParticipant.get(),
						existingColloque.get());
				
			if( existingOrganisateur != null 
					){
				response.setHasError(true);
				response.setStatus(functionalError.DATA_EXIST(" La Presentation avec ID --->:"+ 
						existingOrganisateur.getId() +
				"     existe deja dans la base de donnée    ", locale));
				return response;
			}
			
			
			
//			//----------------------------------------
//			//EXISTANCE DE ORAGANISATEUR DANS LA TABLE ORGANISATEUR 
//			//---------------------------------------------------
//			
//			
//			Optional<Organisateur> existingOrganisateur= organisateurRepository.findById(dto.getId());
//
//			if( existingOrganisateur.isPresent()
//					){
//				response.setHasError(true);
//				response.setStatus(functionalError.DATA_EXIST(" L'Organisateur ayant pour ID  :"+dto.getId() +
//						"     existe deja dans la base de donnée    ", locale));
//			    return response;
//			}
			
		
			

			
			
					
			
			//-------------------------------------------------
			//TRANSFORMATION DES DTO RECU EN ENTITE 
			//-------------------------------------------------
			
			Organisateur organisateurToSave=OrganisateurTransformer.INSTANCE.toEntity(dto, existingParticipant.get(), existingColloque.get());
			
			if( organisateurToSave == null) {
				
				response.setHasError(true);
				response.setStatus(functionalError.SAVE_FAIL("Echec de sauvegarde d'un"
						+ "Organisateur", locale));
			    return response;
								
			}
			
			//--------------------------------------------------
			//ACCUMULATION DES ENTITES A ENREGISTRER
			//------------------------------------------------
			items.add(organisateurToSave);
	
		}
		if(items != null && !items.isEmpty()) { 
			
			//---------------------------------------------
			//INSERTION DANS  LA  BASE DE DONNEE
			//---------------------------------------------
			List<Organisateur> itemsSaved=organisateurRepository.saveAll(items);
			if (itemsSaved == null) {
				response.setHasError(true);
				response.setStatus(functionalError.SAVE_FAIL("organisateur", locale));
				return response;
				
			}
			
			//----------------------------------------------------
			//TRANSFORMATION EN DTO DES ENTITES ET RETOUR AU FRONT 
			//----------------------------------------------------
			response.setHasError(false);
			response.setStatus(functionalError.SUCCESS("create organisateur", locale));
			response.setItems(OrganisateurTransformer.INSTANCE.toDtos(itemsSaved));
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

		slf4jLogger.info("-----fin create organisateur ------");
		return response;
	}
	
	
	
	
	
	
	//--------------------------------------------------
	//Method Update
	//--------------------------------------------------
	@Override
	public Response<OrganisateurDto> update(Request<OrganisateurDto> request, Locale locale){
		response=new Response<>();
		try {

			List<Organisateur> items = new ArrayList<>();
			
			for (OrganisateurDto dto : request.getDatas()) {
				
				//-----------------------------------------------------
				//VERIFIER LES CHAMPS OBLIGATOIRES
				//---------------------------------------------
				//ICI LE CHAMPS Numero de Telephone EST NECESSAIRE
				//------------------------------
				// the function witch is coming need maps
				
				Map<String, Object> fieldsToVerify= new HashMap<>();
				

				fieldsToVerify.put("Id",dto.getId());
				fieldsToVerify.put("ColloqueId",dto.getColloqueId());
				fieldsToVerify.put("ParticipantId",dto.getParticipantId());
				
				
				
				// The map is now in function fieldToVerify
				
				if (!Validate.RequiredValue(fieldsToVerify).isGood() ) {
					response.setHasError(true);
					response.setStatus(functionalError.FIELD_EMPTY(Validate.getValidate().getField(), locale));
					return response;
				}
				
				
				
				
				//--------------------------------------------------------
				//Verifier que le Organisateur existe par le participantId
				// Ce qu'on peut modifier c'est Organisation et le colloque
				//--------------------------------------------------------											
		
				
				Optional<Organisateur> organisateur = organisateurRepository.findById(dto.getId());
				
				if( !organisateur.isPresent() ) {
					response.setHasError(true);
					response.setStatus(functionalError.DATA_NOT_EXIST("organisateur id--> "+dto.getId(), locale));
			        return response;
				}
				

				
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
						
				organisateur.get().setColloque(existingColloque.get());
				
				
				//-----------------------------------------------------------------------
				//VERIFIER QUE Participant EXISTE BIEN DANS LA BASE DE DONNEE 
				//-----------------------------------------------------------------------
				
				Optional<Participant> existingParticipant = participantRepository.findById(dto.getParticipantId()) ;

				
				if( !existingParticipant.isPresent()) {
							response.setHasError(true);
							response.setStatus(functionalError.DATA_NOT_EXIST("  Le participant avec id --->   "
									+ dto.getParticipantId() +"    n'existe pas     ", locale)) ;
							return response;			
						}
						
				organisateur.get().setParticipant(existingParticipant.get());

				
				//---------------------------------------------------------	
				// AJOUT  DU NUMERO DANS LA  BASE DE DONNEE	S'IL N'EST PAS VIDE OU NUL
				//-----------------------------------------------------------
				
				if (  Utilities.notBlank(dto.getNumeroDeTelephone()) &&
						!dto.getNumeroDeTelephone().equals(organisateur.get().getNumeroDeTelephone())) {
					
					organisateur.get().setNumeroDeTelephone(dto.getNumeroDeTelephone());
				}
				
				
			//---------------------------------------------------------	
			// ACCUMULATION DES ENTITES	
			//-----------------------------------------------------------
						
				items.add(organisateur.get());
			
				
				
				// on ramene au front les nouvelles valeurs
				
			}
			if(items != null && !items.isEmpty()) { 
				//inserer dans la base de données
				List<Organisateur> itemsSaved=organisateurRepository.saveAll(items);
				if (itemsSaved == null) {
					response.setHasError(true);
					response.setStatus(functionalError.SAVE_FAIL("organisateur", locale));
					return response;
					
				}
				//transformation en dto des données insérées en bdd 
				//et retour au front
				response.setHasError(false);
				response.setStatus(functionalError.SUCCESS("update organisateur", locale));
				response.setItems(OrganisateurTransformer.INSTANCE.toDtos(itemsSaved));
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
	
	
	@Override
	public Response<OrganisateurDto> delete(Request<OrganisateurDto> request,Locale locale){

		// retrouver l'enregistrement à supprimer,
		// id obligatoire
		
		//Recuperer et emmagasiner
		
		// on supprime 
		response=new Response<>();
		
		
		try {
			
			
			List<Organisateur> items = new ArrayList<>();
			
			for (OrganisateurDto dto : request.getDatas()) {
				
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
				
				Optional<Organisateur> organisateur = organisateurRepository.findById(dto.getId());
			
				
				//------------------------------------------------------------
				//1-Erreur si organisateur ne retourne rien
				//------------------------------------------------------------
				
				if(!organisateur.isPresent() ) {	
					
					response.setHasError(true);
					
					if(!organisateur.isPresent()) {
					response.setStatus(functionalError.DATA_NOT_EXIST("Le organisateur avec ID " +
					dto.getId() + 	"n'existe pas", locale)) ;
					return response;
					}
					
			
				}
				
				
				//------------------------------------------
				// FIRD STEP:    Recuperer et Emmagasiner
				//------------------------------------------
				
				items.add(organisateur.get());
				
			}
			
			if(  items != null  &&  !items.isEmpty()  ) {
				
				// -----------------------------
				// Delete
				//------------------------------
				
				organisateurRepository.deleteInBatch(items);      // Method return void
				
				//Verifier que les elements ont ete supprime  ______ Pas encore fait ___
				
				
				//----------------------------
				// RETURN RESPONSE THE DELETED 
				//----------------------------
				response.setHasError(false);
				response.setStatus(functionalError.SUCCESS("deleted organisateur", locale));
				response.setItems(OrganisateurTransformer.INSTANCE.toDtos(items));
				
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
	public Response<OrganisateurDto> getByCriteria(Request<OrganisateurDto> request, Locale locale) {
	    slf4jLogger.info("----begin get Organisateur-----");

	    response = new Response<OrganisateurDto>();

	    try {
	      List<Organisateur> items = null;
	      items = organisateurRepository.getByCriteria(request, em, locale);
	      if (items != null && !items.isEmpty()) {
	        List<OrganisateurDto> itemsDto = new ArrayList<OrganisateurDto>();
	        for (Organisateur entity : items) {
	          OrganisateurDto dto = getFullInfos(entity, items.size(), locale);
	          if (dto == null) continue;
	          itemsDto.add(dto);
	        }
	        response.setItems(itemsDto);
	        response.setCount(organisateurRepository.count(request, em, locale));
	        response.setHasError(false);
	      } else {
	        response.setStatus(functionalError.DATA_EMPTY("Organisateur", locale));
	        response.setHasError(false);
	        return response;
	      }

	      slf4jLogger.info("----end get Organisateur-----");
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
	  
	  
	  private OrganisateurDto getFullInfos(Organisateur entity, Integer size, Locale locale) throws Exception {
		    OrganisateurDto dto = OrganisateurTransformer.INSTANCE.toDto(entity);
		    if (dto == null){
		      return null;
		    }
		    if (size > 1) {
		      return dto;
		    }

		    return dto;
		  }
	  
	  
	  
	  
	  public Response<OrganisateurDto> createOrganisateur(Request<OrganisateurDto> request, Locale locale){
		  
		  
			
			
		    //--------------------------------------------------- 
			// 
			// POUR LA CREATION DE INSCRIPTION
			//
			//--------------------------------------------------------------
			//RECUPERER LES DTO LA REQUETE ET LES METTRE DANS InscriptionDto 
			//--------------------------------------------------------------
			
		   
			Request<InscriptionDto> requestInscription= new Request<>() ;
			
		  
		  try {

				List<InscriptionDto> itemsInscritption = new ArrayList<>();
				
				
				PresentationDto presentationDto = new PresentationDto();
			  
			  for( OrganisateurDto dto : request.getDatas()) {
					//----------------------------------
					//CHAMPS OBLIGATOIRE DU PARTICIPANT
					//----------------------------------
					Map<String, Object> fieldsToVerify= new HashMap<>();					
				
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
				
			
				List<OrganisateurDto> itemsOrganisateurDto = new ArrayList<>();
				
				for (OrganisateurDto dto : request.getDatas()) {
				
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
					presentationDto.setOrganisateurId(existingParticipant.getId());
					itemsOrganisateurDto.add(dto);
					
					//--------------------------------------
					// AJOUT DES ELEMENTS DE LA PRESENTATION
					//--------------------------------------
					presentationDto.setColloqueId(existingColloque.getId());
					presentationDto.setExposerId(existingExposer.getId());
					
					presentationDto.setResumer(existingExposer.getResumer());
						
					
					itemsPresenation.add(presentationDto);
					
					}
				//-----------------------------------------
				//CREATE Organisateur 
				//------------------------------------------
				
				
				
				if(!itemsOrganisateurDto.isEmpty() && itemsOrganisateurDto!= null) {
				request= new Request<>();
				request.setDatas(itemsOrganisateurDto);	
				Response<OrganisateurDto> responseOrganisateurDto = create(request, locale);
				
				
				if ( responseOrganisateurDto.getItems() != null ||
						!responseOrganisateurDto.getItems().isEmpty()) {
				
				 requestPresentation= new Request<>();
				 requestPresentation.setDatas(itemsPresenation);;
				 presentationBusiness.create(requestPresentation, locale);
			
				}
				
				}
				
				response.setHasError(false);
				response.setStatus(functionalError.SUCCESS("Created Organisateur", locale));
				response.setItems(itemsOrganisateurDto);					
			
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
