package com.epam.preprod.ma.service.transaction;

/**
 * Defines the method that allows an application server to manage transactions.
 * 
 * @author Oleksandr Lagoda, Leonid Polyakov
 * 
 * @version 1.0
 * 
 */
public interface ITransactionManager {

	<E> E executeOperation(ITransactionOperation<E> operation);

}