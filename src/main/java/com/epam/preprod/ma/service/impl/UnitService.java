package com.epam.preprod.ma.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epam.preprod.ma.dao.IDao;
import com.epam.preprod.ma.dao.entity.Unit;
import com.epam.preprod.ma.dao.entity.User;
import com.epam.preprod.ma.dao.interf.IUnitDao;
import com.epam.preprod.ma.exception.DaoException;
import com.epam.preprod.ma.exception.ServiceException;
import com.epam.preprod.ma.service.AbstractService;
import com.epam.preprod.ma.service.interf.IUnitService;
import com.epam.preprod.ma.service.transaction.ITransactionOperation;

/**
 * Implementation of {@link IUnitService}.
 * 
 * @author Oleksandr Lagoda
 * 
 * @version 1.0
 * 
 */
@Service
public class UnitService extends AbstractService<Unit> implements IUnitService {

	@Autowired
	private IUnitDao unitDao;

	@Override
	public List<Unit> readAllUnits() {
		ITransactionOperation<List<Unit>> operation = new ITransactionOperation<List<Unit>>() {
			@Override
			public List<Unit> execute() throws DaoException {
				return unitDao.readAllUnits();
			}
		};
		return manager.executeOperation(operation);
	}

	@Override
	protected IDao<Unit> getDao() {
		return this.unitDao;
	}

	@Override
	public Long create(Unit newEntity) throws ServiceException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Unit readById(Long id) throws ServiceException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void update(Unit entityToUpdate) throws ServiceException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(Long id) throws ServiceException {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Unit> readSubordinateUnitsByUser(final User user) {
		ITransactionOperation<List<Unit>> operation = new ITransactionOperation<List<Unit>>() {
			@Override
			public List<Unit> execute() throws DaoException {
				return unitDao.readSubordinateUnitsByUser(user);
			}
		};
		return manager.executeOperation(operation);
	}

	@Override
	public List<Unit> readSubordinateUnitsByUnit(final Unit unit) {
		ITransactionOperation<List<Unit>> operation = new ITransactionOperation<List<Unit>>() {
			@Override
			public List<Unit> execute() throws DaoException {
				return unitDao.readSubordinateUnitsByUnit(unit);
			}
		};
		return manager.executeOperation(operation);
	}
}
