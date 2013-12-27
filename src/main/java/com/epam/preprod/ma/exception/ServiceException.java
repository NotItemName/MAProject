package com.epam.preprod.ma.exception;

import com.epam.preprod.ma.service.IService;

/**
 * The ServiceException class is the base exception class used to indicate an
 * error while using {@linkplain IService} interface.
 * 
 * @author Oleksandr Lagoda
 * 
 */
public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = -3013123321258468235L;

	public ServiceException() {
		super();
	}

	public ServiceException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}

}