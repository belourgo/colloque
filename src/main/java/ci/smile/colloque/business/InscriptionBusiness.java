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
import ci.smile.colloque.dao.entity.Inscription;
import ci.smile.colloque.dao.entity.Participant;
import ci.smile.colloque.dao.repository.ColloqueRepository;
import ci.smile.colloque.dao.repository.InscriptionRepository;
import ci.smile.colloque.dao.repository.ParticipantRepository;
import ci.smile.colloque.helper.ExceptionUtils;
import ci.smile.colloque.helper.FunctionalError;
import ci.smile.colloque.helper.Utilities;
import ci.smile.colloque.helper.Validate;
import ci.smile.colloque.helper.contract.IBasicBusiness;
import ci.smile.colloque.helper.contract.Request;
import ci.smile.colloque.helper.contract.Response;
import ci.smile.colloque.helper.dto.InscriptionDto;
import ci.smile.colloque.helper.dto.ParticipantDto;
import ci.smile.colloque.helper.dto.transformer.InscriptionTransformer;

@Component
public class InscriptionBusiness implements IBasicBusiness<Request<InscriptionDto>, Response<InscriptionDto>> {

	
	private Response<InscriptionDto> response;
	
	
	
	
	//-----------------------------------------------------
	//Variable dont l'utilisation est necessaire / NB: On instancie pas les interfaces
	//---------------------------------------------
			
	@Autowired
	private InscriptionRepository inscriptionRepository;
	@Autowired
	private ColloqueRepository colloqueRepository ;
	@Autowired
	private ParticipantRepository participantRepository ;
	@Autowired
	private ParticipantBusiness participantBusiness ;

	
	
	
	@Autowired
	private FunctionalError functionalError;
	
	@Autowired
	private ExceptionUtils exceptionUtils;

	private Logger slf4jLogger;
	
	@PersistenceContext
	private EntityManager em;
	

	public  InscriptionBusiness( ) { 
		slf4jLogger = LoggerFactory.getLogger(getClass());
	}


	
	
	//-------------------------------------------------------------------------------
	// Create
	//-------------------------------------------------------------------------------
	
	
	public Response<InscriptionDto> create(Request<InscriptionDto> request, Locale locale){
		response=new Response<>();
	
		//-----------------------------------
		//GENERE LES VALEURS ENTRE 1 ET 10000
		//-----------------------------------
		
//		Random r = new Random();
//		int value = 1 + r.nextInt(1 - 10000);
		
		slf4jLogger.info("-----debut create inscription ------");
		try {
			List<Inscription> items = new ArrayList<>();
		for (InscriptionDto dto : request.getDatas()) {
			
			
			
			//-----------------------------------------------------
			//VERIFIER LES CHAMPS OBLIGATOIRES
			//---------------------------------------------
			//ICI LE CHAMPS Numero de Telephone EST NECESSAIRE
			//------------------------------
			// the function witch is coming need maps
			
			Map<String, Object> fieldsToVerify= new HashMap<>();
			

			fieldsToVerify.put("ColloqueId",dto.getColloqueId());
			fieldsToVerify.put("ParticipantId",dto.getParticipantId());

			
			
			// The map is now in function fieldToVerify
			
			if (!Validate.RequiredValue(fieldsToVerify).isGood() ) {
				response.setHasError(true);
				response.setStatus(functionalError.FIELD_EMPTY(Validate.getValidate().getField(), locale));
				return response;
			}
			


		
			
			//----------------------------------------
			// EXISTANCE DE participantId ET DE ColloqueId DANS LES TABLES
			// COLLOQUE ET Participant
			//---------------------------------------------------
			
			Optional<Colloque> existingColloque= colloqueRepository.findById(dto.getColloqueId());
			Optional<Participant> existingParticipant= participantRepository.findById(dto.getParticipantId());
		


			
			if( 	!existingColloque.isPresent() ||
					!existingParticipant.isPresent()
					){
				response.setHasError(true);
				
				
				if(!existingColloque.isPresent()  ) {
					response.setStatus(functionalError.DATA_NOT_EXIST(" Le colloque n'existe pas "
							+ " dans la base de donnée ", locale));
				}
				
				if(!existingParticipant.isPresent()  ) {
					response.setStatus(functionalError.DATA_NOT_EXIST(" Le Participant n'existe pas "
							+ " dans la base de donnée ", locale));
				}
			    return response;
			}
			
			
			//----------------------------------------
			//EXISTANCE DE Inscription DANS LA TABLE Inscription 
			//------------------------------------------------------
			//ICI FAIRE ATTENTION AU TYPE DE PRSENTATION, CAR IL RECOIT LES TYPES ENTITY
			//---------------------------------------------------------------------------

				Inscription existingInscription= inscriptionRepository.findByInscription(existingColloque.get(),
						existingParticipant.get());
				
			if( existingInscription != null 
					){
				response.setHasError(true);
				response.setStatus(functionalError.DATA_EXIST(" La Inscription avec ID --->:"+ 
						existingInscription.getId() +
				"     existe deja dans la base de donnée    ", locale));
			    return response;
			}
			
	
					
			
			//----------------------------------------
			//TRANSFORMATION DES DTO RECU EN ENTITE 
			//-------------------------------------
			
			Inscription inscriptionToSave=InscriptionTransformer.INSTANCE.toEntity(dto, 
					existingParticipant.get(), existingColloque.get(), dto.getDate());
			
			if( inscriptionToSave == null) {
				
				response.setHasError(true);
				response.setStatus(functionalError.SAVE_FAIL("Echec de sauvegarde d'une"
						+ "Inscription", locale));
			    return response;
								
			}
			
			//--------------------------------------------------
			//ACCUMULATION DES ENTITES A ENREGISTRER
			//------------------------------------------------
			items.add(inscriptionToSave);
	
		}
		if(items != null && !items.isEmpty()) { 
			
			//---------------------------------------------
			//INSERTION DANS  LA  BASE DE DONNEE
			//---------------------------------------------
			List<Inscription> itemsSaved=inscriptionRepository.saveAll(items);
			if (itemsSaved == null) {
				response.setHasError(true);
				response.setStatus(functionalError.SAVE_FAIL("inscription", locale));
				return response;
				
			}
			
			//----------------------------------------------------
			//TRANSFORMATION EN DTO DES ENTITES ET RETOUR AU FRONT 
			//----------------------------------------------------
			response.setHasError(false);
			response.setStatus(functionalError.SUCCESS("create inscription", locale));
			response.setItems(InscriptionTransformer.INSTANCE.toDtos(itemsSaved));
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

		slf4jLogger.info("-----fin create inscription ------");
		return response;
	}
	
	
	
	
	
	//--------------------------------------------------
	//Method Update
	//--------------------------------------------------
	
	public Response<InscriptionDto> update(Request<InscriptionDto> request, Locale locale){
		response=new Response<>();
		
		//Ajouter le participant
		

		
	
		
		try {

			List<Inscription> items = new ArrayList<>();
			
			for (InscriptionDto dto : request.getDatas()) {
							
				
				//-----------------------------------------------------
				//VERIFIER LES CHAMPS OBLIGATOIRES
				//---------------------------------------------
				//ICI LE CHAMPS Numero de Telephone EST NECESSAIRE
				//------------------------------
				// the function witch is coming need maps
				
				Map<String, Object> fieldsToVerify= new HashMap<>();


				fieldsToVerify.put("Id",dto.getId());
				
				
				
				
				// The map is now in function fieldToVerify
				
				if (!Validate.RequiredValue(fieldsToVerify).isGood() ) {
					response.setHasError(true);
					response.setStatus(functionalError.FIELD_EMPTY(Validate.getValidate().getField(), locale));
					return response;
				}
				
				
				
				//--------------------------------------------------------
				//Verifier que le Inscription existe par le participantId
				// Ce qu'on peut modifier c'est Organisation et le colloque
				//--------------------------------------------------------											
		
				
				Optional<Inscription> inscription = inscriptionRepository.findById(dto.getId());
				
				if( !inscription.isPresent() ) {
					response.setHasError(true);
					response.setStatus(functionalError.DATA_NOT_EXIST("inscription id--> "+dto.getId(), locale));
			        return response;
				}
				

				
				//---------------------------------------------------------
				//SE RASSURER QU'ON RECOIT BIEN DES ENTIERS
				//-----------------------------------------------------------
				
				if ( 
					dto.getColloqueId() == (int) dto.getColloqueId() || 
					dto.getParticipantId() ==(int) dto.getParticipantId() 
					)
						
				{
				   
				//-----------------------------------------------------------------------
				//VERIFIER QUE Colloque EXISTE BIEN DANS LA BASE DE DONNEE 
				//-----------------------------------------------------------------------
				
				Optional<Colloque> existingColloque = colloqueRepository.findById(dto.getColloqueId()) ;

				
				if( !existingColloque.isPresent()) {
							response.setHasError(true);
							response.setStatus(functionalError.DATA_NOT_EXIST(" L'colloque avec id --->"
									+ dto.getColloqueId() +"  n'existe pas    ", locale)) ;
							return response;			
						}
						
				inscription.get().setColloque(existingColloque.get());
				
				
				//-----------------------------------------------------------------------
				//VERIFIER QUE Conferencier EXISTE BIEN DANS LA BASE DE DONNEE 
				//-----------------------------------------------------------------------
			
				
				Optional<Participant> existingParticipant = participantRepository.findById(dto.getParticipantId()) ;
		
				
				if( !existingParticipant.isPresent()) {
							response.setHasError(true);
							response.setStatus(functionalError.DATA_NOT_EXIST("  Le participant avec id --->   "
									+ dto.getParticipantId() +"    n'existe pas     ", locale)) ;
							return response;			
						}
						
				inscription.get().setParticipant(existingParticipant.get());
				
				
				//-----------------------------------------------------------	
				// TRANSFORMER DTO en ENTITY pour recuperer la date en entity
				//-----------------------------------------------------------
				
				Inscription inscriptionToSave = InscriptionTransformer.INSTANCE.toEntity(dto, 
						existingParticipant.get(), existingColloque.get(), dto.getDate());
				
				
				
				
                //---------------------------------------------------------	
				// AJOUT  DE LA DATE DANS LA  BASE DE DONNEE	S'IL N'EST PAS VIDE OU NUL
				//-----------------------------------------------------------
				
				if ( !dto.getDate().isEmpty() 
						&& dto.getDate() != null ) {
					
					
					inscription.get().setDate(inscriptionToSave.getDate());
					
				}
				
				}
				
				
				//---------------------------------------------------------	
				// AJOUT  DU NUMERO DANS LA  BASE DE DONNEE	S'IL N'EST PAS VIDE OU NUL
				//-----------------------------------------------------------
				
				
				if ( Utilities.notBlank(dto.getNumeroInscription()) &&
						!dto.getNumeroInscription().equals(inscription.get().getNumeroInscription())) {
					
					inscription.get().setNumeroInscription(dto.getNumeroInscription());
				}
				
				//---------------------------------------------------------	
				// GESTION DE LA PRESENCE
				//-----------------------------------------------------------
			
				Boolean.valueOf(dto.getPresence() );
				if(dto.getPresence() != null &&
						(Boolean.TRUE || 
						Boolean.FALSE )
						) {
					
					  inscription.get().setPresence(dto.getPresence());
					
				}
				
				

				
				
			//---------------------------------------------------------	
			// ACCUMULATION DES ENTITES	
			//-----------------------------------------------------------
						
				items.add(inscription.get());
			
				
				// on ramene au front les nouvelles valeurs
				
			}
			if(items != null && !items.isEmpty()) { 
				//inserer dans la base de données
				List<Inscription> itemsSaved=inscriptionRepository.saveAll(items);
				if (itemsSaved == null) {
					response.setHasError(true);
					response.setStatus(functionalError.SAVE_FAIL("Inscription", locale));
					return response;
					
				}
				//transformation en dto des données insérées en bdd 
				//et retour au front
				response.setHasError(false);
				response.setStatus(functionalError.SUCCESS("update inscription", locale));
				response.setItems(InscriptionTransformer.INSTANCE.toDtos(itemsSaved));
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
	
	
	
	public Response<InscriptionDto> delete(Request<InscriptionDto> request,Locale locale){

		// retrouver l'enregistrement à supprimer,
		// id obligatoire
		
		//Recuperer et emmagasiner
		
		// on supprime 
		response=new Response<>();
		
		
		try {
			
			
			List<Inscription> items = new ArrayList<>();
			
			for (InscriptionDto dto : request.getDatas()) {
				
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
				
				Optional<Inscription> inscription = inscriptionRepository.findById(dto.getId());
			
				
				//------------------------------------------------------------
				//1-Erreur si inscription ne retourne rien
				//------------------------------------------------------------
				
				if(!inscription.isPresent() ) {	
					
					response.setHasError(true);
					
					if(!inscription.isPresent()) {
					response.setStatus(functionalError.DATA_NOT_EXIST("Le inscription avec ID " +
					dto.getId() + 	"n'existe pas", locale)) ;
					return response;
					}
					
			
				}

				
				
				//------------------------------------------
				// FIRD STEP:    Recuperer et Emmagasiner
				//------------------------------------------
				
				items.add(inscription.get());
				
			}
			
			if(  items != null  &&  !items.isEmpty()  ) {
				
				// -----------------------------
				// Delete
				//------------------------------
				
				inscriptionRepository.deleteInBatch(items);      // Method return void
				
				//Verifier que les elements ont ete supprime  ______ Pas encore fait ___
				
				
				//---------------------
				// Retourner reponse pour ce qui a ete surprime 
				//-------------------------------
				response.setHasError(false);
				response.setStatus(functionalError.SUCCESS("deleted inscription", locale));
				response.setItems(InscriptionTransformer.INSTANCE.toDtos(items));
				
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
	public Response<InscriptionDto> getByCriteria(Request<InscriptionDto> request, Locale locale) {
	    slf4jLogger.info("----begin get Inscription-----");

	    response = new Response<InscriptionDto>();

	    try {
	      List<Inscription> items = null;
	      items = inscriptionRepository.getByCriteria(request, em, locale);
	      if (items != null && !items.isEmpty()) {
	        List<InscriptionDto> itemsDto = new ArrayList<InscriptionDto>();
	        for (Inscription entity : items) {
	          InscriptionDto dto = getFullInfos(entity, items.size(), locale);
	          if (dto == null) continue;
	          itemsDto.add(dto);
	        }
	        response.setItems(itemsDto);
	        response.setCount(inscriptionRepository.count(request, em, locale));
	        response.setHasError(false);
	      } else {
	        response.setStatus(functionalError.DATA_EMPTY("Inscription", locale));
	        response.setHasError(false);
	        return response;
	      }

	      slf4jLogger.info("----end get Inscription-----");
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
	  
	  
	  private InscriptionDto getFullInfos(Inscription entity, Integer size, Locale locale) throws Exception {
		    InscriptionDto dto = InscriptionTransformer.INSTANCE.toDto(entity);
		    if (dto == null){
		      return null;
		    }
		    if (size > 1) {
		      return dto;
		    }

		    return dto;
		  }

	  
	  
	  
	  
	  
	  
	  	//-------------------------------------------------------------------------------
		// CREATE INSCRIT COMPLET
		//-------------------------------------------------------------------------------
		
		
		public Response<InscriptionDto> createInscrit(Request<InscriptionDto> request, Locale locale){
			response=new Response<>();
		
		    //--------------------------------------------------- 
			// 
			// POUR LA CREATION DE PARTICIPANT
			//
			//---------------------------------------------------
			//RECUPERER LES DTO LA REQUETE ET LES METTRE DANS ParticipantDto 
			//---------------------------------------------------
			
			
			Request<ParticipantDto> requestParticipant= new Request<>() ;
			
			
			try {
				List<ParticipantDto> itemsParticipant = new ArrayList<>();
				List<Integer> itemsColloqueId = new ArrayList<>();
			
				
				
				for( InscriptionDto  dto : request.getDatas()) {
				
					//----------------------------------
					//CHAMPS OBLIGATOIRE DU PARTICIPANT
					//----------------------------------
					Map<String, Object> fieldsToVerify= new HashMap<>();
					
					fieldsToVerify.put("nom",dto.getParticipantDto().getNom());
					
					
					//----------------------------------
					//Champs Obligatoires du Colloque
					//----------------------------------
					fieldsToVerify.put("nom", dto.getColloqueName());
						
					// The map is now in function fieldToVerify
					
					if (!Validate.RequiredValue(fieldsToVerify).isGood() ) {
						response.setHasError(true);
						response.setStatus(functionalError.FIELD_EMPTY(Validate.getValidate().getField(), locale));
						return response;
					}
					
					
					Colloque existingColloque=null;
					 existingColloque = colloqueRepository.findByNom(dto.getColloqueName()) ;
				
					if( existingColloque == null  ) {
						
						response.setHasError(true);
						response.setStatus(functionalError.DATA_NOT_EXIST(" Le colloque n'existe pas "
								+ " dans la base de donnée ", locale));
					}
					
					
					itemsColloqueId.add(existingColloque.getId());
					itemsParticipant.add(dto.getParticipantDto());
					
				}
		
				//------------------------------------------------------
				//CREATION D UN PARTICIPANT
				//------------------------------------------------------
				
				if(!itemsParticipant.isEmpty() && itemsParticipant !=null &&
				   !itemsColloqueId.isEmpty() && itemsColloqueId != null) {
					requestParticipant.setDatas(itemsParticipant);
					Response<ParticipantDto> responseParticipantDto = participantBusiness.create(requestParticipant, locale);
				 
			if ( responseParticipantDto.getItems() != null 
					&&  !responseParticipantDto.getItems().isEmpty() ) {
			
		
			slf4jLogger.info("-----debut create inscription ------");
		
			List<InscriptionDto> itemsInscriptionDto = new ArrayList<>();
			
			for (InscriptionDto dto : request.getDatas()) {
				
				//---------------------------------
				// RECUPERER LES Id DU PARTICIPANT
				// ET LES Id DU COLLOQUE
			    //---------------------------------
				
				Colloque existingColloque=null;
				Participant existingParticipant=null;
				 existingColloque = colloqueRepository.findByNom(dto.getColloqueName()) ;
				 existingParticipant= participantRepository.findByNumPar(dto.getParticipantDto().getNumeroDeParticipation());
			
				if( existingColloque == null || existingParticipant ==null ) {
					
					response.setHasError(true);
					response.setStatus(functionalError.DATA_NOT_EXIST(" Le colloque n'existe pas "
							+ " dans la base de donnée ", locale));
				}
				
				
				//------------------------------------------
				//CELUI QUI S'INSCRIT EST PRESENT PAR DEFAUT
				//------------------------------------------
				dto.setPresence(true);
				
				dto.setColloqueId(existingColloque.getId()); 
				dto.setParticipantId(existingParticipant.getId());
				
				//-------------------------------------------------------
				//LE NUMERO D'INSCRIPTION EST LE NUMERO DE PARTICIPANTION
				//-------------------------------------------------------
				dto.setNumeroInscription(dto.getParticipantDto().getNumeroDeParticipation());
				
		
					
				itemsInscriptionDto.add(dto);
				
				
							
				}
			
			//-----------------------------------------
			//CREATE INSCRIT
			//------------------------------------------
			
			request= new Request<>();
			request.setDatas(itemsInscriptionDto);
			if(!itemsInscriptionDto.isEmpty() && itemsInscriptionDto!= null) {
				
			create(request, locale);
			}
			
			response.setHasError(false);
			response.setStatus(functionalError.SUCCESS("created inscription", locale));
			response.setItems(itemsInscriptionDto);
				
				}
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

			slf4jLogger.info("-----fin create inscription ------");
			return response;
		}
	  
}
