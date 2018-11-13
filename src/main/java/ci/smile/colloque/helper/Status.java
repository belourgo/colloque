
/*
 * Created on 26 oct. 2018 ( Time 19:07:10 )
 * Generator tool : Telosys Tools Generator ( version 2.1.1 )
 * Copyright 2017 Savoir Faire Linux. All Rights Reserved.
 */

package ci.smile.colloque.helper;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Status
 * 
 * @author SFL Back-End developper
 *
 */
@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class Status {
	
	private String	code;
	private String	message;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
