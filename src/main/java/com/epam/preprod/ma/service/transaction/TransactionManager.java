package com.epam.preprod.ma.service.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.epam.preprod.ma.exception.ServiceException;

/**
 * Implements {@linkplain ITransactionManager} interface using
 * {@linkplain ThreadLocalStorage}.
 * 
 * <p>
 * Transaction level is set in properties file with
 * "transactionManager.transactionLevel" key.
 * </p>
 * 
 * @author Oleksandr Lagoda, Leonid Polyakov.
 * 
 * @version 1.0
 * 
 * @see DataSource
 * 
 */
@Service
public class TransactionManager implements ITransactionManager {

	private static final Logger LOGGER = Logger
			.getLogger(TransactionManager.class);

	@Value("${transactionManager.transactionLevel}")
	private int transactionLevel;

	@Autowired
	private DataSource dataSource;

	public <E> E executeOperation(ITransactionOperation<E> operation)
			throws ServiceException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(transactionLevel);

			ThreadLocalStorage.setConnection(connection);
			E entity = operation.execute();
			connection.commit();
			return entity;
		} catch (Exception exception) {
			try {
				// added null validation, since code
				// "connection = dataSource.getConnection();"
				// can throw Exception when DB is unavailable
				//
				// thus connection == null and it will cause
				// NullPointerException without any info at all in
				// "connection.close();" code in finally statement
				if (connection != null) {
					connection.rollback();
				}
			} catch (SQLException rollbackException) {
				throwServiceException("Can't rollback connection",
						rollbackException);
			}
			throwServiceException("Transaction operation error occured",
					exception);
		} finally {
			ThreadLocalStorage.removeConnection();
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException sqlException) {
				throwServiceException("Can't close connection", sqlException);
			}
		}
		return null;
	}

	private void throwServiceException(String message, Exception cause)
			throws ServiceException {
		LOGGER.error(message, cause);
		throw new ServiceException(message, cause);
	}

}