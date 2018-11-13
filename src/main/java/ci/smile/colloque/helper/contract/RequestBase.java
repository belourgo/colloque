
/*
 * Created on 26 oct. 2018 ( Time 19:07:11 )
 * Generator tool : Telosys Tools Generator ( version 2.1.1 )
 * Copyright 2017 Savoir Faire Linux. All Rights Reserved.
 */

package ci.smile.colloque.helper.contract;

/**
 * Request Base
 * 
 * @author SFL Back-End developper
 *
 */
public class RequestBase {

	protected String	sessionUser;
	protected Integer	size;
	protected Integer	index;
	protected String	lang;
	protected String	businessLineCode;
	protected String	caseEngine;
	protected Boolean	isAnd;

	public String getBusinessLineCode() {
		return businessLineCode;
	}

	public void setBusinessLineCode(String businessLineCode) {
		this.businessLineCode = businessLineCode;
	}

	public String getCaseEngine() {
		return caseEngine;
	}

	public void setCaseEngine(String caseEngine) {
		this.caseEngine = caseEngine;
	}

	public String getSessionUser() {
		return sessionUser;
	}

	public void setSessionUser(String sessionUser) {
		this.sessionUser = sessionUser;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Boolean getIsAnd() {
		return isAnd;
	}

	public void setIsAnd(Boolean isAnd) {
		this.isAnd = isAnd;
	}

}