package ci.smile.colloque.rest.api;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ci.smile.colloque.business.InstitutionBusiness;
import ci.smile.colloque.helper.ExceptionUtils;
import ci.smile.colloque.helper.FunctionalError;
import ci.smile.colloque.helper.StatusCode;
import ci.smile.colloque.helper.StatusMessage;
import ci.smile.colloque.helper.Validate;
import ci.smile.colloque.helper.contract.Request;
import ci.smile.colloque.helper.contract.Response;
import ci.smile.colloque.helper.dto.InstitutionDto;

@CrossOrigin("*")
@RestController
@RequestMapping(value="/institution/")
public class InstitutionController {

	@Autowired
	private InstitutionBusiness institutionBusiness;

	@Autowired
	private FunctionalError functionalError;

	@Autowired
	private ExceptionUtils			exceptionUtils;

	private Logger slf4jLogger = LoggerFactory.getLogger(getClass());

	@Autowired
	private HttpServletRequest requestBasic;

	@RequestMapping(value="/create",method=RequestMethod.POST,consumes = {"application/json"},produces={"application/json"})
    public Response<InstitutionDto> create(@RequestBody Request<InstitutionDto> request) {
    	slf4jLogger.info("start method /institution/create");
        Response<InstitutionDto> response = new Response<InstitutionDto>();

        String languageID = (String)requestBasic.getAttribute("CURRENT_LANGUAGE_IDENTIFIER");
        Locale locale = new Locale(languageID, "");

        try {
            
           	//response = Validate.validateList(request, response, functionalError, locale);
           	if(!response.isHasError()){
                  response = institutionBusiness.create(request, locale);
        	}else{
        	   slf4jLogger.info("Erreur| code: {} -  message: {}", response.getStatus().getCode(), response.getStatus().getMessage());
        	   return response;
        	}

        	if(!response.isHasError()){
				response.setStatus(functionalError.SUCCESS("", locale));
        	    slf4jLogger.info("end method create");
          	    slf4jLogger.info("code: {} -  message: {}", StatusCode.SUCCESS, StatusMessage.SUCCESS);
            }else{
             	slf4jLogger.info("Erreur| code: {} -  message: {}", response.getStatus().getCode(), response.getStatus().getMessage());
            }

        } catch (CannotCreateTransactionException e) {
			exceptionUtils.CANNOT_CREATE_TRANSACTION_EXCEPTION(response, locale, e);
		} catch (TransactionSystemException e) {
			exceptionUtils.TRANSACTION_SYSTEM_EXCEPTION(response, locale, e);
		} catch (RuntimeException e) {
			exceptionUtils.RUNTIME_EXCEPTION(response, locale, e);
		} catch (Exception e) {
			exceptionUtils.EXCEPTION(response, locale, e);
		}
		slf4jLogger.info("end method /institution/create");
        return response;
    }
	
	
	
	//-----------------------------------
		// Method UPDATE
		//---------------------------------------
		
		@RequestMapping(value="/update",method=RequestMethod.POST,consumes = {"application/json"},produces={"application/json"})
	    public Response<InstitutionDto> update(@RequestBody Request<InstitutionDto> request) {
	    	slf4jLogger.info("start method /institution/update");
	        Response<InstitutionDto> response = new Response<InstitutionDto>();

	        String languageID = (String)requestBasic.getAttribute("CURRENT_LANGUAGE_IDENTIFIER");
	        Locale locale = new Locale(languageID, "");

	        try {
	            
	           	response = Validate.validateList(request, response, functionalError, locale);
	           	if(!response.isHasError()){
	                  response = institutionBusiness.update(request, locale);
	        	}else{
	        	   slf4jLogger.info("Erreur| code: {} -  message: {}", response.getStatus().getCode(), response.getStatus().getMessage());
	        	   return response;
	        	}

	        	if(!response.isHasError()){
					response.setStatus(functionalError.SUCCESS("", locale));
	        	    slf4jLogger.info("end method update");
	          	    slf4jLogger.info("code: {} -  message: {}", StatusCode.SUCCESS, StatusMessage.SUCCESS);
	            }else{
	             	slf4jLogger.info("Erreur| code: {} -  message: {}", response.getStatus().getCode(), response.getStatus().getMessage());
	            }

	        } catch (CannotCreateTransactionException e) {
				exceptionUtils.CANNOT_CREATE_TRANSACTION_EXCEPTION(response, locale, e);
			} catch (TransactionSystemException e) {
				exceptionUtils.TRANSACTION_SYSTEM_EXCEPTION(response, locale, e);
			} catch (RuntimeException e) {
				exceptionUtils.RUNTIME_EXCEPTION(response, locale, e);
			} catch (Exception e) {
				exceptionUtils.EXCEPTION(response, locale, e);
			}
			slf4jLogger.info("end method /institution/update");
	        return response;
	    }
		
		
		
		
		//-----------------------------------
		// Method Delete
		//---------------------------------------
		
		
		
		
		@RequestMapping(value="/delete",method=RequestMethod.POST,consumes = {"application/json"},produces={"application/json"})
	    public Response<InstitutionDto> delete(@RequestBody Request<InstitutionDto> request) {
	    	slf4jLogger.info("start method /institution/delete");
	        Response<InstitutionDto> response = new Response<InstitutionDto>();

	        String languageID = (String)requestBasic.getAttribute("CURRENT_LANGUAGE_IDENTIFIER");
	        Locale locale = new Locale(languageID, "");

	        try {
	            
	           	response = Validate.validateList(request, response, functionalError, locale);
	           	if(!response.isHasError()){
	                  response = institutionBusiness.delete(request, locale);
	        	}else{
	        	   slf4jLogger.info("Erreur| code: {} -  message: {}", response.getStatus().getCode(), response.getStatus().getMessage());
	        	   return response;
	        	}

	        	if(!response.isHasError()){
					response.setStatus(functionalError.SUCCESS("", locale));
	        	    slf4jLogger.info("end method delete");
	          	    slf4jLogger.info("code: {} -  message: {}", StatusCode.SUCCESS, StatusMessage.SUCCESS);
	            }else{
	             	slf4jLogger.info("Erreur| code: {} -  message: {}", response.getStatus().getCode(), response.getStatus().getMessage());
	            }

	        } catch (CannotCreateTransactionException e) {
				exceptionUtils.CANNOT_CREATE_TRANSACTION_EXCEPTION(response, locale, e);
			} catch (TransactionSystemException e) {
				exceptionUtils.TRANSACTION_SYSTEM_EXCEPTION(response, locale, e);
			} catch (RuntimeException e) {
				exceptionUtils.RUNTIME_EXCEPTION(response, locale, e);
			} catch (Exception e) {
				exceptionUtils.EXCEPTION(response, locale, e);
			}
			slf4jLogger.info("end method /institution/delete");
	        return response;
	    }
		
		
		//-----------------------------------
		// Method getByCriteria
		//---------------------------------------
		
		
		
		
		@RequestMapping(value="/getByCriteria",method=RequestMethod.POST,consumes = {"application/json"},produces={"application/json"})
	    public Response<InstitutionDto> getByCriteria(@RequestBody Request<InstitutionDto> request) {
	    	slf4jLogger.info("start method /institution/getByCriteria");
	        Response<InstitutionDto> response = new Response<InstitutionDto>();

	        String languageID = (String)requestBasic.getAttribute("CURRENT_LANGUAGE_IDENTIFIER");
	        Locale locale = new Locale(languageID, "");

	        try {
	            
	           	response = Validate.validateObject(request, response, functionalError, locale);
	           	if(!response.isHasError()){
	                  response = institutionBusiness.getByCriteria(request, locale);
	        	}else{
	        	   slf4jLogger.info("Erreur| code: {} -  message: {}", response.getStatus().getCode(), response.getStatus().getMessage());
	        	   return response;
	        	}

	        	if(!response.isHasError()){
					response.setStatus(functionalError.SUCCESS("", locale));
	        	    slf4jLogger.info("end method getByCriteria");
	          	    slf4jLogger.info("code: {} -  message: {}", StatusCode.SUCCESS, StatusMessage.SUCCESS);
	            }else{
	             	slf4jLogger.info("Erreur| code: {} -  message: {}", response.getStatus().getCode(), response.getStatus().getMessage());
	            }

	        } catch (CannotCreateTransactionException e) {
				exceptionUtils.CANNOT_CREATE_TRANSACTION_EXCEPTION(response, locale, e);
			} catch (TransactionSystemException e) {
				exceptionUtils.TRANSACTION_SYSTEM_EXCEPTION(response, locale, e);
			} catch (RuntimeException e) {
				exceptionUtils.RUNTIME_EXCEPTION(response, locale, e);
			} catch (Exception e) {
				exceptionUtils.EXCEPTION(response, locale, e);
			}
			slf4jLogger.info("end method /institution/getByCriteria");
	        return response;
	    }
		
	
}
