
/*
 * Created on 26 oct. 2018 ( Time 19:07:10 )
 * Generator tool : Telosys Tools Generator ( version 2.1.1 )
 * Copyright 2017 Savoir Faire Linux. All Rights Reserved.
 */

package ci.smile.colloque.helper;


/**
 * Math Utils
 * 
 * @author SFL Back-End developper
 *
 */
public class MathUtils {
	/**
	 * 
	 * @param percentage
	 * @param totalPrice
	 * @return
	 */
	public static Double getValueByPercentage(Integer percentage, Double totalPrice) {
		return getValueByPercentage(Double.parseDouble(percentage.toString()), totalPrice);
	}

	/**
	 * 
	 * @param percentage
	 * @param totalPrice
	 * @return
	 */
	public static Double getValueByPercentage(Double percentage, Double totalPrice) {
		Double value = 0d;
		Double pe = ((double) percentage / 100);
		value = totalPrice * pe;
		return value;
	}

	/**
	 * 
	 * @param value
	 * @param totalPrice
	 * @return
	 */
	public static Double getPercentageByValue(Double value, Double totalPrice) {
		Double percentage = 0d;
		percentage = ((value * 100) / totalPrice);
		return percentage;
	}
}