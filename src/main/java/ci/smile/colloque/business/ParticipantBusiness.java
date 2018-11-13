package ci.smile.colloque.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Component;

import ci.smile.colloque.dao.entity.Participant;
import ci.smile.colloque.dao.repository.ParticipantRepository;
import ci.smile.colloque.helper.ExceptionUtils;
import ci.smile.colloque.helper.FunctionalError;
import ci.smile.colloque.helper.Utilities;
import ci.smile.colloque.helper.Validate;
import ci.smile.colloque.helper.contract.IBasicBusiness;
import ci.smile.colloque.helper.contract.Request;
import ci.smile.colloque.helper.contract.Response;
import ci.smile.colloque.helper.dto.ParticipantDto;
import ci.smile.colloque.helper.dto.transformer.ParticipantTransformer;

@Component
public class ParticipantBusiness implements IBasicBusiness<Request<ParticipantDto>,Response<ParticipantDto>> {
	
	private Response<ParticipantDto> response;
	
	
	@Autowired
	private FunctionalError functionalError;
	@Autowired
	private ParticipantRepository participantRepository;
	@Autowired
	private ExceptionUtils exceptionUtils;

	private Logger slf4jLogger;
	
	@PersistenceContext
	private EntityManager em;
	

	
	

	public  ParticipantBusiness( ) { 
		slf4jLogger = LoggerFactory.getLogger(getClass());
	}
	
	
	
	public Response<ParticipantDto> create(Request<ParticipantDto> request, Locale locale){
		response=new Response<>();
		slf4jLogger.info("-----debut create participant ------");
		try {
			List<Participant> items = new ArrayList<>();
		for (ParticipantDto dto : request.getDatas()) {
			// exiger la saisie des champs obligatoires
			
			// the function witch is coming need maps
			
			
		
			
			Map<String, Object> fieldsToVerify= new HashMap<>();
//			fieldsToVerify.put("numeroDeParticipation",dto.getNumeroDeParticipation());
			fieldsToVerify.put("nom",dto.getNom());
			
			
			// The map is now in function fieldToVerify
			
			if (!Validate.RequiredValue(fieldsToVerify).isGood()) {
				response.setHasError(true);
				response.setStatus(functionalError.FIELD_EMPTY(Validate.getValidate().getField(), locale));
				return response;
			}
			
			
			//------------------------------------------------------
			// GENERER NUMERO DE PARTRICIPATION UNIQUE AVEC LES UUID
			//------------------------------------------------------
			
			//POUR MAINTENIR LA BOUCLE TANT QUE VALEUR DEJA EXISTANTE GENEREE
				
			boolean  testIfExistValue = true ;    
			while( testIfExistValue == true) {
			String uuid = UUID.randomUUID().toString();
			
			dto.setNumeroDeParticipation(uuid.substring(0, 5));
		
			//--------------------------------------------
			//TESTER L'UNICITE DU NUMERO DE PARTICIPATION 
			//--------------------------------------------
			Participant existingParticipant =null;
			existingParticipant= participantRepository.findByNumPar(dto.getNumeroDeParticipation());
				
			if(existingParticipant != null){
				testIfExistValue = true;
			}
			else {
				testIfExistValue = false;
			}
				
			//empecher le renseignement de id par le front
			
			}
			
			//transformation en entité des dto reçus (request)
			Participant participantToSave= ParticipantTransformer.INSTANCE.toEntity(dto)   ;
			
			
			//accumulation des entités à enregistrer
			items.add(participantToSave);
	
		}
		if(items != null && !items.isEmpty()) { 
			//inserer dans la base de données
			List<Participant> itemsSaved=participantRepository.saveAll(items);
			if (itemsSaved == null) {
				response.setHasError(true);
				response.setStatus(functionalError.SAVE_FAIL("participant", locale));
				return response;
				
			}
			//transformation en dto des données insérées en bdd 
			//et retour au front
			response.setHasError(false);
			response.setStatus(functionalError.SUCCESS("create participant", locale));
			response.setItems(ParticipantTransformer.INSTANCE.toDtos(itemsSaved));
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

		slf4jLogger.info("-----fin create participant ------");
		return response;
	}



	
	//--------------------------------------------------
	//Method Update
	//--------------------------------------------------
	
	public Response<ParticipantDto> update(Request<ParticipantDto> request, Locale locale){
		response=new Response<>();
		try {

			List<Participant> items = new ArrayList<>();
			
			for (ParticipantDto dto : request.getDatas()) {
				
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
												
						
				
				Optional<Participant> participant = participantRepository.findById(dto.getId());

				// isPresent retourne True si participant contient une valeur
				
				if( !participant.isPresent() ) {
					response.setHasError(true);
					response.setStatus(functionalError.DATA_NOT_EXIST("participant id--> "+dto.getId(), locale));
			        return response;
				}
				
				//on applique les modifications demandées
				if(Utilities.notBlank(dto.getNumeroDeParticipation())
						&& !dto.getNumeroDeParticipation().equals(participant.get().getNumeroDeParticipation())) {
					//unicité du nom de l'université
					Participant existingParticipant= participantRepository.findByNumPar(dto.getNumeroDeParticipation());
					
					if(existingParticipant != null && existingParticipant.getId() != participant.get().getId()){
						response.setHasError(true);
						response.setStatus(functionalError.DATA_EXIST("nom :"+dto.getNom(), locale));
					    return response;
					}

					participant.get().setNumeroDeParticipation(dto.getNumeroDeParticipation());
				}
				
				//Update Name
				if(Utilities.notBlank(dto.getNom())
						&& !dto.getNom().equals(participant.get().getNom())) {
					participant.get().setNom(dto.getNom());
				}
				
				//Update PreNom
				if(Utilities.notBlank(dto.getPrenom())
						&& !dto.getPrenom().equals(participant.get().getPrenom())) {
					participant.get().setPrenom(dto.getPrenom());
				}
				
				
				//Update Adresse
				if(Utilities.notBlank(dto.getAdresse())
						&& !dto.getAdresse().equals(participant.get().getAdresse())) {
					participant.get().setAdresse(dto.getAdresse());
				}
				
				
				items.add(participant.get());
				// enregistrer dans la bdd
				
				
				// on ramene au front les nouvelles valeurs
				
			}
			if(items != null && !items.isEmpty()) { 
				//inserer dans la base de données
				List<Participant> itemsSaved=participantRepository.saveAll(items);
				if (itemsSaved == null) {
					response.setHasError(true);
					response.setStatus(functionalError.SAVE_FAIL("participant", locale));
					return response;
					
				}
				//transformation en dto des données insérées en bdd 
				//et retour au front
				response.setHasError(false);
				response.setStatus(functionalError.SUCCESS("update participant", locale));
				response.setItems(ParticipantTransformer.INSTANCE.toDtos(itemsSaved));
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
	
	
	
	
	
	
	
	public Response<ParticipantDto> delete(Request<ParticipantDto> request,Locale locale){

		// retrouver l'enregistrement à supprimer,
		// id obligatoire
		
		//Recuperer et emmagasiner
		
		// on supprime 
		response=new Response<>();
		
		
		try {
			
			
			List<Participant> items = new ArrayList<>();
			
			for (ParticipantDto dto : request.getDatas()) {
				
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
				
				Optional<Participant> participant = participantRepository.findById(dto.getId());
			
				
				//---------------
				//1-Erreur si participant ne retourne rien
				//2-Erreur si le nom renseigné ne correspond pas au nom en BD
				//------------------------
				if(!participant.isPresent() && dto.getNumeroDeParticipation() != participant.get().getNumeroDeParticipation()) {	
					response.setHasError(true);
					response.setStatus(functionalError.DATA_NOT_EXIST("Le participant' avec ID " +
					dto.getId() + 	"n'existe pas", locale)) ;
					return response;
				}
				
				//--------------------
				// Fird step:    Recuperer et Emmagasiner
				//---------------------
				
				
				items.add(participant.get());
				
			}
			
			if(  items != null  &&  !items.isEmpty()  ) {
				
				// -----------------------------
				// Delete
				//------------------------------
				
				participantRepository.deleteInBatch(items);      // Method return void
				
				//Verifier que les elements ont ete supprime  ______ Pas encore fait ___
				
				
				//---------------------
				// Retourner reponse pour ce qui a ete surprime 
				//-------------------------------
				response.setHasError(false);
				response.setStatus(functionalError.SUCCESS("deleted participant", locale));
				response.setItems(ParticipantTransformer.INSTANCE.toDtos(items));
				
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
	public Response<ParticipantDto> getByCriteria(Request<ParticipantDto> request, Locale locale) {
	    slf4jLogger.info("----begin get Participant-----");

	    response = new Response<ParticipantDto>();

	    try {
	      List<Participant> items = null;
	      items = participantRepository.getByCriteria(request, em, locale);
	      if (items != null && !items.isEmpty()) {
	        List<ParticipantDto> itemsDto = new ArrayList<ParticipantDto>();
	        for (Participant entity : items) {
	          ParticipantDto dto = getFullInfos(entity, items.size(), locale);
	          if (dto == null) continue;
	          itemsDto.add(dto);
	        }
	        response.setItems(itemsDto);
	        response.setCount(participantRepository.count(request, em, locale));
	        response.setHasError(false);
	      } else {
	        response.setStatus(functionalError.DATA_EMPTY("Participant", locale));
	        response.setHasError(false);
	        return response;
	      }

	      slf4jLogger.info("----end get Participant-----");
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
	  
	  
	  private ParticipantDto getFullInfos(Participant entity, Integer size, Locale locale) throws Exception {
		    ParticipantDto dto = ParticipantTransformer.INSTANCE.toDto(entity);
		    if (dto == null){
		      return null;
		    }
		    if (size > 1) {
		      return dto;
		    }

		    return dto;
		  }
	  
}
	