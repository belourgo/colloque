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

import ci.smile.colloque.business.ExposerBusiness;
import ci.smile.colloque.helper.ExceptionUtils;
import ci.smile.colloque.helper.FunctionalError;
import ci.smile.colloque.helper.StatusCode;
import ci.smile.colloque.helper.StatusMessage;
import ci.smile.colloque.helper.Validate;
import ci.smile.colloque.helper.contract.Request;
import ci.smile.colloque.helper.contract.Response;
import ci.smile.colloque.helper.dto.ExposerDto;

@CrossOrigin("*")
@RestController
@RequestMapping(value="/exposer/")
public class ExposerController {

	@Autowired
	private ExposerBusiness exposerBusiness;

	@Autowired
	private FunctionalError functionalError;

	@Autowired
	private ExceptionUtils			exceptionUtils;

	private Logger slf4jLogger = LoggerFactory.getLogger(getClass());

	@Autowired
	private HttpServletRequest requestBasic;

	@RequestMapping(value="/create",method=RequestMethod.POST,consumes = {"application/json"},produces={"application/json"})
    public Response<ExposerDto> create(@RequestBody Request<ExposerDto> request) {
    	slf4jLogger.info("start method /exposer/create");
        Response<ExposerDto> response = new Response<ExposerDto>();

        String languageID = (String)requestBasic.getAttribute("CURRENT_LANGUAGE_IDENTIFIER");
        Locale locale = new Locale(languageID, "");

        try {
            
           	//response = Validate.validateList(request, response, functionalError, locale);
           	if(!response.isHasError()){
                  response = exposerBusiness.create(request, locale);
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
		slf4jLogger.info("end method /exposer/create");
        return response;
    }
	
	
	
	    //-----------------------------------
		// Method UPDATE
		//---------------------------------------
		
		@RequestMapping(value="/update",method=RequestMethod.POST,consumes = {"application/json"},produces={"application/json"})
	    public Response<ExposerDto> update(@RequestBody Request<ExposerDto> request) {
	    	slf4jLogger.info("start method /exposer/update");
	        Response<ExposerDto> response = new Response<ExposerDto>();

	        String languageID = (String)requestBasic.getAttribute("CURRENT_LANGUAGE_IDENTIFIER");
	        Locale locale = new Locale(languageID, "");

	        try {
	            
	           	response = Validate.validateList(request, response, functionalError, locale);
	           	if(!response.isHasError()){
	                  response = exposerBusiness.update(request, locale);
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
			slf4jLogger.info("end method /exposer/update");
	        return response;
	    }
		
		
		
		
		//-----------------------------------
		// Method getByCriteria
		//---------------------------------------
		
		
		
		//@CrossOrigin("*")
		@RequestMapping(value="/getByCriteria",method=RequestMethod.POST,consumes = {"application/json"},produces={"application/json"})
	    public Response<ExposerDto> getByCriteria(@RequestBody Request<ExposerDto> request) {
	    	slf4jLogger.info("start method /exposer/getByCriteria");
	        Response<ExposerDto> response = new Response<ExposerDto>();

	        String languageID = (String)requestBasic.getAttribute("CURRENT_LANGUAGE_IDENTIFIER");
	        Locale locale = new Locale(languageID, "");

	        try {
	            
	           	response = Validate.validateObject(request, response, functionalError, locale);
	           	if(!response.isHasError()){
	                  response = exposerBusiness.getByCriteria(request, locale);
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
			slf4jLogger.info("end method /exposer/getByCriteria");
	        return response;
	    }
		
	
	
	
	
	
	
}
