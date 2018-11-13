
/*
 * Created on 26 oct. 2018 ( Time 19:07:11 )
 * Generator tool : Telosys Tools Generator ( version 2.1.1 )
 * Copyright 2017 Savoir Faire Linux. All Rights Reserved.
 */

package ci.smile.colloque.helper.contract;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Response
 * 
 * @author SFL Back-End developper
 *
 */
@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class Response<T> extends ResponseBase {

	protected List<T> items;

	// ----------------------------------------------------------------------
	// GETTER(S) & SETTER(S) FOR DATA FIELDS
	// ----------------------------------------------------------------------

	/**
	 * Get the "items" field value
	 * 
	 * @return the field value
	 */
	public List<T> getItems() {
		return items;
	}

	/**
	 * Set the "items" field value
	 * 
	 * @param items
	 */
	public void setItems(List<T> items) {
		this.items = items;
	}

}
