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
import ci.smile.colloque.dao.entity.Universite;
import ci.smile.colloque.dao.repository.ColloqueRepository;
import ci.smile.colloque.dao.repository.UniversiteRepository;
import ci.smile.colloque.helper.ExceptionUtils;
import ci.smile.colloque.helper.FunctionalError;
import ci.smile.colloque.helper.Utilities;
import ci.smile.colloque.helper.Validate;
import ci.smile.colloque.helper.contract.IBasicBusiness;
import ci.smile.colloque.helper.contract.Request;
import ci.smile.colloque.helper.contract.Response;
import ci.smile.colloque.helper.dto.ColloqueDto;
import ci.smile.colloque.helper.dto.transformer.ColloqueTransformer;
import ci.smile.colloque.helper.extra.classes.CountParticipantByColloque;
import ci.smile.colloque.helper.extra.classes.ParticipantExtra;
import ci.smile.colloque.helper.extra.repository.NativeQueryByJDBC;

@Component
public class ColloqueBusiness implements IBasicBusiness<Request<ColloqueDto>,Response<ColloqueDto>> {
 

	private Response<ColloqueDto> response ;

	//-----------------------------------------------------
	//Variable dont l'utilisation est necessaire / NB: On instancie pas les interfaces
	//---------------------------------------------
			
	@Autowired
	private ColloqueRepository colloqueRepository;
	@Autowired
	private UniversiteRepository universiteRepository ;
	@Autowired
	private FunctionalError functionalError;
	
	
	
	@Autowired
	private ExceptionUtils exceptionUtils;

	private Logger slf4jLogger;
	
	@PersistenceContext
	 private EntityManager em;
	
	//------------------------------------------
	//ACCESS TO THE JDBC DB
	//------------------------------------------
	@Autowired
	private NativeQueryByJDBC nativeQueryByJDBC;
	
	
	

	public  ColloqueBusiness( ) { 
		slf4jLogger = LoggerFactory.getLogger(getClass());
	}

	
	
	//-----------------------------------------
	// Create
	//------------------------------------------------
	
	@Override
	public Response<ColloqueDto> create(Request<ColloqueDto> request, Locale locale){
		response=new Response<>();
		
		
		slf4jLogger.info("-----debut create colloque------");
		try {
			List<Colloque> items = new ArrayList<>();
		for (ColloqueDto dto : request.getDatas()) {
			
			
			//-----------------------------------------------------
			//Verifier que les champs obligatoires ont été saisie
			//---------------------------------------------
			
			
			// the function witch is coming need maps
			Map<String, Object> fieldsToVerify1= new HashMap<>();
			Map<String, Object> fieldsToVerify2= new HashMap<>();
			fieldsToVerify1.put("InstitutionId",dto.getNom());
			fieldsToVerify2.put("ParticipantId",dto.getUniversiteId());
			
			// The map is now in function fieldToVerify
			
			if (!Validate.RequiredValue(fieldsToVerify1).isGood() ) {
				response.setHasError(true);
				response.setStatus(functionalError.FIELD_EMPTY(Validate.getValidate().getField(), locale));
				return response;
			}
			
			if ( !Validate.RequiredValue(fieldsToVerify2).isGood() ) {
				response.setHasError(true);
				response.setStatus(functionalError.FIELD_EMPTY(Validate.getValidate().getField(), locale));
				return response;
			}
			
			
			//----------------------------------------
			//Unicite par l'utilisation de participantId dans Organisation
			//---------------------------------------------------
			
			
			Colloque existingColloque= colloqueRepository.findByNom(dto.getNom());
			Optional<Universite> existingUniversite= universiteRepository.findById(dto.getUniversiteId());

			if( existingColloque!=null  ){
				response.setHasError(true);
				response.setStatus(functionalError.DATA_NOT_EXIST("Cet Id d'universite n'existe pas  :"+
				dto.getUniversiteId() , locale));
			    return response;
			}
			
			
			
			//----------------------------------------
			//Verifier que UniversiteId existe dans la table Universite 
			//---------------------------------------------------
			if( !existingUniversite.isPresent() ){
				response.setHasError(true);
				response.setStatus(functionalError.DATA_NOT_EXIST("Cet Id d'universite n'existe pas  :"+
				dto.getUniversiteId() , locale));
			    return response;
			}
		
			
			//----------------------------------------------------
			//TRANSFORMATION DES ENTITES EN DTO ( REQUEST )
			//----------------------------------------------------
			
			//---------------------------------
			//AJOUT DE LA DATE AY TRANSFORMER
			//---------------------------------
			
			//------------------------------------
			//S'ASSURER QUE LA DATE N'EST PAS VIDE
			//------------------------------------
			
//			if ( dto.getDate().isEmpty() || dto.getDate() == null) {
//				
//				dto.setDate("01/01/2000");
//				
//			}
			
			Colloque colloqueToSave=ColloqueTransformer.INSTANCE.toEntity(dto, existingUniversite.get(), dto.getDate() );
			
			//----------------------------------
			//accumulation des entités à enregistrer
			//----------------------------------------------
			
			items.add(colloqueToSave);
	
		}
		if(items != null && !items.isEmpty()) { 
			
			//-----------------------------------------------
			//INSERER DANS LA BASE DE DONNEE
			//------------------------------------------------
			
			List<Colloque> itemsSaved= colloqueRepository.saveAll(items);
			if (itemsSaved == null) {
				response.setHasError(true);
				response.setStatus(functionalError.SAVE_FAIL("colloque", locale));
				return response;
				
			}
			//transformation en dto des données insérées en bdd 
			//et retour au front
			response.setHasError(false);
			response.setStatus(functionalError.SUCCESS("create colloque", locale));
			response.setItems(ColloqueTransformer.INSTANCE.toDtos(itemsSaved));
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

		slf4jLogger.info("-----fin create Colloque------");
		return response;
	}



	
	
	
	
	
	
	//--------------------------------------------------
	//Method Update
	//--------------------------------------------------
	@Override
	public Response<ColloqueDto> update(Request<ColloqueDto> request, Locale locale){
		response=new Response<>();
		try {

			List<Colloque> items = new ArrayList<>();
			
			for (ColloqueDto dto : request.getDatas()) {
				
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
												
						
				
				//Colloque colloque = colloqueRepository.findById(dto.getId()); 
				Optional<Colloque> colloque = colloqueRepository.findById(dto.getId());

				// isPresent retourne True si colloque contient une valeur
				
				if( !colloque.isPresent() ) {
					response.setHasError(true);
					response.setStatus(functionalError.DATA_NOT_EXIST("colloque id--> "+dto.getId(), locale));
			        return response;
				}
				
				//on applique les modifications demandées
				if(Utilities.notBlank(dto.getNom()) 
						&& !dto.getNom().equals(colloque.get().getNom())) {
					//unicité du nom de l'université
					Colloque existingColloque= colloqueRepository.findByNom(dto.getNom());
					
					if(existingColloque != null && existingColloque.getId() != colloque.get().getId()){
						response.setHasError(true);
						response.setStatus(functionalError.DATA_EXIST("nom :"+dto.getNom(), locale));
					    return response;
					}

					colloque.get().setNom(dto.getNom());
				}
				
				
				//-----------------------------------------------------------------------
				//Verifie que l'universite existe bien en 
				//base de donnée Universite avant le Update
				//-----------------------------------------------------------------------
				
				Optional<Universite> existingUniversite = 
						universiteRepository.findById(dto.getUniversiteId()) ;

				
				if( !existingUniversite.isPresent()) {
							response.setHasError(true);
							response.setStatus(functionalError.DATA_NOT_EXIST(" L'universite avec id --->"
									+ dto.getUniversiteId() + "n'existe pas", locale)) ;
							return response;			
						}
						
				colloque.get().setUniversite(existingUniversite.get());
				
			

				
				
				//-----------------------------------------------------------	
				// TRANSFORMER DTO en ENTITY pour recuperer la date en entity
				//-----------------------------------------------------------
					
				Colloque colloqueToSave=ColloqueTransformer.INSTANCE.toEntity(dto, existingUniversite.get(), dto.getDate() );
				
				//---------------------------------------------------------	
				// MODIFIER LA DATE
				//-----------------------------------------------------------								
				
				if(dto.getDate()!=null && !dto.getDate().isEmpty() ) {
				

				colloque.get().setDate(colloqueToSave.getDate());
					
				}
				
				
				//---------------------------------------------------------	
				// ENREGISTRER DANS LA BASE DE DONNEE
				//-----------------------------------------------------------
					
					items.add(colloque.get());
				
				// on ramene au front les nouvelles valeurs
				
			}
			if(items != null && !items.isEmpty()) { 
				//inserer dans la base de données
				List<Colloque> itemsSaved=colloqueRepository.saveAll(items);
				if (itemsSaved == null) {
					response.setHasError(true);
					response.setStatus(functionalError.SAVE_FAIL("colloque", locale));
					return response;
					
				}
				//transformation en dto des données insérées en bdd 
				//et retour au front
				response.setHasError(false);
				response.setStatus(functionalError.SUCCESS("update colloque", locale));
				response.setItems(ColloqueTransformer.INSTANCE.toDtos(itemsSaved));
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
	public Response<ColloqueDto> delete(Request<ColloqueDto> request,Locale locale){

		// retrouver l'enregistrement à supprimer,
		// id obligatoire
		
		//Recuperer et emmagasiner
		
		// on supprime 
		response=new Response<>();
		
		
		try {
			
			
			List<Colloque> items = new ArrayList<>();
			
			for (ColloqueDto dto : request.getDatas()) {
				
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
				
				Optional<Colloque> colloque = colloqueRepository.findById(dto.getId());
			
				
				//------------------------------------------------------------
				//1-Erreur si colloque ne retourne rien
				//2-Erreur si le nom renseigné ne correspond pas au nom en BD
				//------------------------------------------------------------
				
				if(  !colloque.isPresent() && dto.getNom() != colloque.get().getNom()) {	
					
					response.setHasError(true);
					
					if(!colloque.isPresent()) {
					response.setStatus(functionalError.DATA_NOT_EXIST("Le colloque avec ID " +
					dto.getId() + 	"n'existe pas", locale)) ;
					return response;
					}
					
					if( dto.getNom() != colloque.get().getNom()) {
					response.setStatus(functionalError.DATA_NOT_EXIST("Le nom a " +
					dto.getNom() + 	" n'existe pas", locale)) ;
					return response;
					}
				}
				
				
				//------------------------------------------
				// FIRD STEP:    Recuperer et Emmagasiner
				//------------------------------------------
				
				items.add(colloque.get());
				
			}
			
			if(  items != null  &&  !items.isEmpty()  ) {
				
				// -----------------------------
				// Delete
				//------------------------------
				
				colloqueRepository.deleteInBatch(items);      // Method return void
				
				//Verifier que les elements ont ete supprime  ______ Pas encore fait ___
				
				
				//---------------------
				// Retourner reponse pour ce qui a ete surprime 
				//-------------------------------
				response.setHasError(false);
				response.setStatus(functionalError.SUCCESS("deleted colloque", locale));
				response.setItems(ColloqueTransformer.INSTANCE.toDtos(items));
				
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
	public Response<ColloqueDto> getByCriteria(Request<ColloqueDto> request, Locale locale) {
	    slf4jLogger.info("----begin get Colloque-----");

	    response = new Response<ColloqueDto>();

	    try {
	      List<Colloque> items = null;
	      items = colloqueRepository.getByCriteria(request, em, locale);
	      if (items != null && !items.isEmpty()) {
	        List<ColloqueDto> itemsDto = new ArrayList<ColloqueDto>();
	        for (Colloque entity : items) {
	          ColloqueDto dto = getFullInfos(entity, items.size(), locale);
	          if (dto == null) continue;
	          itemsDto.add(dto);
	        }
	        response.setItems(itemsDto);
	        response.setCount(colloqueRepository.count(request, em, locale));
	        response.setHasError(false);
	      } else {
	        response.setStatus(functionalError.DATA_EMPTY("Colloque", locale));
	        response.setHasError(false);
	        return response;
	      }

	      slf4jLogger.info("----end get Colloque-----");
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
	  
	  
	  private ColloqueDto getFullInfos(Colloque entity, Integer size, Locale locale) throws Exception {
		    ColloqueDto dto = ColloqueTransformer.INSTANCE.toDto(entity);
		    if (dto == null){
		      return null;
		    }
		    if (size > 1) {
		      return dto;
		    }

		    return dto;
		  }
	  
	  
	  
	  public Response<ColloqueDto> getNbreParticipant(Locale locale){
		  
		  response = new Response<>();
		  List<ColloqueDto> items = new ArrayList<>();
		  List<CountParticipantByColloque>  countColloqueParticipantList    =   nativeQueryByJDBC.getNbreParticipantByColloque()  ;
		  
		  try {		  
			  
			  for ( CountParticipantByColloque countParticipantByColloque : countColloqueParticipantList ) {
				  
				  ColloqueDto colloqueDto = new ColloqueDto() ;
				  colloqueDto.setCountParticipantByColloque(countParticipantByColloque);
				  items.add(colloqueDto);		
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
	  
	  
	  public Response<ColloqueDto> getColDetails( Request<ColloqueDto> request, Locale locale){
		  
		  
		  
		  response = new Response<>();
		  List<ColloqueDto> items = new ArrayList<>();		
		  	
		  
		  
		  try {		  
			  
			  for ( ColloqueDto dto : request.getDatas() ) {
				  
				  List<CountParticipantByColloque>  nbreColloqueParticipant    =   nativeQueryByJDBC.getNbrePart(dto.getNom())  ;
				  List<ParticipantExtra>  conferencierList    =   nativeQueryByJDBC.getConfParticipant(dto.getNom())  ;
				  List<ParticipantExtra>  organisateurList    =   nativeQueryByJDBC.getOrgParticipant(dto.getNom()) ;

				  ;	

				  
				  ColloqueDto colloqueDto = new ColloqueDto() ;
				  colloqueDto.setNbreColloqueParticipant(nbreColloqueParticipant.get(0));
				
				  colloqueDto.setConferencierList(conferencierList);
				  colloqueDto.setOrganisateurList(organisateurList);
				  items.add(colloqueDto);		
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
