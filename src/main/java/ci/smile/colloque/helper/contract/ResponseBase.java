
/*
 * Created on 26 oct. 2018 ( Time 19:07:11 )
 * Generator tool : Telosys Tools Generator ( version 2.1.1 )
 * Copyright 2017 Savoir Faire Linux. All Rights Reserved.
 */

package ci.smile.colloque.helper.contract;

import ci.smile.colloque.helper.Status;

/**
 * Response Base
 * 
 * @author SFL Back-End developper
 *
 */
public class ResponseBase {

	protected Status	status;
	protected boolean	hasError;
	protected String	sessionUser;
	protected Long		count;

	public ResponseBase() {
		status = new Status();
	}

	public String getSessionUser() {
		return sessionUser;
	}

	public void setSessionUser(String sessionUser) {
		this.sessionUser = sessionUser;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public boolean isHasError() {
		return hasError;
	}

	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}
}
