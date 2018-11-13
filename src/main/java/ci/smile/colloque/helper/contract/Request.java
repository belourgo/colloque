
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
 * Request
 * 
 * @author SFL Back-End developper
 *
 */
@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class Request<T> extends RequestBase {

	protected T			data;
	protected List<T>	datas;

	// ----------------------------------------------------------------------
	// GETTER(S) & SETTER(S) FOR DATA FIELDS
	// ----------------------------------------------------------------------
	/**
	 * Get the "data" field value
	 * 
	 * @return the field value
	 */
	public T getData() {
		return data;
	}

	/**
	 * Set the "data" field value
	 * 
	 * @param data
	 */
	public void setData(T data) {
		this.data = data;
	}

	/**
	 * Get the "datas" field value
	 * 
	 * @return the field value
	 */
	public List<T> getDatas() {
		return datas;
	}

	/**
	 * Set the "datas" field value
	 * 
	 * @param datas
	 */
	public void setDatas(List<T> datas) {
		this.datas = datas;
	}
}