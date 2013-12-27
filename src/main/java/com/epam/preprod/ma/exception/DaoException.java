package com.epam.preprod.ma.exception;

/**
 * An exception that provides information on a database access error.
 * 
 * @author Oleksandr Lagoda, Leonid Polyakov
 * 
 */
public class DaoException extends Exception {

	private static final long serialVersionUID = 3985779316306015355L;

	public DaoException() {
		super();
	}

	public DaoException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DaoException(String message, Throwable cause) {
		super(message, cause);
	}

	public DaoException(String message) {
		super(message);
	}

	public DaoException(Throwable cause) {
		super(cause);
	}

}