package com.epam.preprod.ma.service.transaction;

import com.epam.preprod.ma.exception.DaoException;

/**
 * Wraps transaction logic.
 * 
 * @author Oleksandr Lagoda, Leonid Polyakov.
 * 
 * @version 1.0
 * 
 * @param <E>
 *            - data type which is returned as a result of transaction.
 */
public interface ITransactionOperation<E> {
	E execute() throws DaoException;
}