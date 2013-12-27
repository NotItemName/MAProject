package com.epam.preprod.ma.controller;

import javax.servlet.http.HttpServletRequest;

public class ControllerUtils {

	/**
	 * Reconstructs the URL the client used to make the request. The returned
	 * URL contains a protocol, server name, port number, application name and
	 * "/" character
	 * 
	 * @param request
	 *            - http request to get information from
	 * @return string that contains application url
	 * 
	 * @author Leonid Polyakov
	 */
	public static String getApplicationURL(HttpServletRequest request) {
		String servletPath = request.getServletPath();
		StringBuffer requestURL = request.getRequestURL();
		return requestURL.substring(0,
				requestURL.length() - servletPath.length() + 1);
	}
}