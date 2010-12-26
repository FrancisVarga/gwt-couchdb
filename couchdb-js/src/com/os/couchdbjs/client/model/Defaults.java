package com.os.couchdbjs.client.model;

import com.google.gwt.core.client.GWT;

/**
 * Provides ability to set the default date format and service root (defaults to
 * GWT.getModuleBaseURL()).
 * 
 */
public class Defaults {
	private static String __dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	public static String getDateFormat() {
		return __dateFormat;
	}

	/**
	 * Sets the format used when encoding and decoding Dates.
	 * 
	 * @param dateFormat
	 */
	public static void setDateFormat(String dateFormat) {
		Defaults.__dateFormat = dateFormat;
	}
}