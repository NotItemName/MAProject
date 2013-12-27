package com.epam.preprod.ma.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epam.preprod.ma.dao.IDao;
import com.epam.preprod.ma.exception.DaoException;
import com.epam.preprod.ma.exception.ServiceException;
import com.epam.preprod.ma.service.transaction.ITransactionManager;
import com.epam.preprod.ma.service.transaction.ITransactionOperation;

/**
 * Passes calls of CRUD operations to @Autowired {@linkplain IDao} class.
 * 
 * @author Oleksandr Lagoda, Leonid Polyakov
 * 
 * @version 1.0
 * 
 * @param <D>
 *            - {@link IDao} bean class. Can be defined by @Repository
 *            annotation.
 * @param <E>
 *            - entity with which {@linkplain IService} works.
 */
@Service
public abstract class AbstractService<E> implements IService<E> {

	@Autowired
	protected ITransactionManager manager;

	protected abstract IDao<E> getDao();

	@Override
	public Long create(final E newEntity) throws ServiceException {
		ITransactionOperation<Long> operation = new ITransactionOperation<Long>() {
			@Override
			public Long execute() throws DaoException {
				return getDao().create(newEntity);
			}
		};
		return manager.executeOperation(operation);
	}

	@Override
	public E readById(final Long id) throws ServiceException {
		ITransactionOperation<E> operation = new ITransactionOperation<E>() {
			@Override
			public E execute() throws DaoException {
				return getDao().readById(id);
			}
		};
		return manager.executeOperation(operation);
	}

	@Override
	public void update(final E entityToUpdate) throws ServiceException {
		ITransactionOperation<Void> operation = new ITransactionOperation<Void>() {
			@Override
			public Void execute() throws DaoException {
				getDao().update(entityToUpdate);
				return null;
			}
		};
		manager.executeOperation(operation);
	}

	@Override
	public void delete(final Long id) throws ServiceException {
		ITransactionOperation<Void> operation = new ITransactionOperation<Void>() {
			@Override
			public Void execute() throws DaoException {
				getDao().delete(id);
				return null;
			}
		};
		manager.executeOperation(operation);
	}

}