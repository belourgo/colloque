
/*
 * Created on 26 oct. 2018 ( Time 19:07:11 )
 * Generator tool : Telosys Tools Generator ( version 2.1.1 )
 * Copyright 2017 Savoir Faire Linux. All Rights Reserved.
 */

package ci.smile.colloque.helper.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Operator Enums
 * 
 * @author SFL Back-End developper
 *
 */
public class OperatorEnum {
	public static final String	EQUAL				= "=";
	public static final String	NOT_EQUAL_1			= "<>";
	public static final String	NOT_EQUAL_2			= "!=";
	public static final String	LESS_OR_EQUAL		= "<=";
	public static final String	LESS				= "<";
	public static final String	MORE_OR_EQUAL		= ">=";
	public static final String	MORE				= ">";
	public static final String	BETWEEN				= "[]";
	public static final String	BETWEEN_OUT			= "][";
	public static final String	BETWEEN_LEFT_OUT	= "]]";
	public static final String	BETWEEN_RIGHT_OUT	= "[[";
	public static final String	CONTAINS			= "%%";
	public static final String	START_WTIH			= "_%";
	public static final String	END_WTIH			= "%_";
	
	private static final List<String> LIST_OF_BETWEEN = Arrays.asList(BETWEEN, BETWEEN_OUT, BETWEEN_LEFT_OUT, BETWEEN_RIGHT_OUT);
	
	public static final boolean	IS_BETWEEN_OPERATOR	(String operator){
		return LIST_OF_BETWEEN.stream().anyMatch(s -> operator.equals(s));
	}
}