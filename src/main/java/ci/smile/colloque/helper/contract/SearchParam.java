
/*
 * Created on 26 oct. 2018 ( Time 19:07:11 )
 * Generator tool : Telosys Tools Generator ( version 2.1.1 )
 * Copyright 2017 Savoir Faire Linux. All Rights Reserved.
 */

package ci.smile.colloque.helper.contract;

/**
 * Search Param
 * 
 * @author SFL Back-End developper
 *
 */
public class SearchParam<T> {

	String	operator;
	T		start;
	T		end;

	/**
	 * @return the operator
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * @return the start
	 */
	public T getStart() {
		return start;
	}

	/**
	 * @return the end
	 */
	public T getEnd() {
		return end;
	}

	/**
	 * @param operator
	 *            the operator to set
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}

	/**
	 * @param start
	 *            the start to set
	 */
	public void setStart(T start) {
		this.start = start;
	}

	/**
	 * @param end
	 *            the end to set
	 */
	public void setEnd(T end) {
		this.end = end;
	}

}
