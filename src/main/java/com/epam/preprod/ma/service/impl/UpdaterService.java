package com.epam.preprod.ma.service.impl;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epam.preprod.ma.constant.EMarker;
import com.epam.preprod.ma.dao.entity.Unit;
import com.epam.preprod.ma.dao.entity.User;
import com.epam.preprod.ma.dao.interf.IUnitDao;
import com.epam.preprod.ma.dao.interf.IUserDao;
import com.epam.preprod.ma.exception.DaoException;
import com.epam.preprod.ma.service.interf.IUpdaterService;
import com.epam.preprod.ma.service.transaction.ITransactionManager;
import com.epam.preprod.ma.service.transaction.ITransactionOperation;

/**
 * Implementation of {@link IUpdaterService}.
 * 
 * @author Oleksandr Lagoda
 * 
 * @version 1.0
 * 
 */
@Service
public class UpdaterService implements IUpdaterService {

	private static final Logger LOGGER = Logger.getLogger(UpdaterService.class);

	@Autowired
	protected ITransactionManager manager;

	@Autowired
	private IUserDao userDao;

	@Autowired
	private IUnitDao unitDao;

	@Override
	public void update(final Map<User, EMarker> users,
			final Map<Unit, EMarker> units) {
		ITransactionOperation<Void> operation = new ITransactionOperation<Void>() {

			@Override
			public Void execute() throws DaoException {
				updateUnits(units);
				updateUsers(users);
				setRMAndParentUnit();
				return null;
			}

			/**
			 * Update 'units' table in database.
			 * 
			 * @author Oleksandr Lagoda
			 * @throws DaoException
			 *             if database error occurred.
			 */
			private void updateUnits(Map<Unit, EMarker> units)
					throws DaoException {

				for (Entry<Unit, EMarker> entry : units.entrySet()) {
					Unit unit = entry.getKey();
					EMarker marker = entry.getValue();

					if (marker == EMarker.CREATE) {
						if (LOGGER.isTraceEnabled()) {
							LOGGER.trace("Creating unit [" + unit
									+ "] in database.");
						}
						unitDao.createUnitWithoutRMAndParentUnit(unit);
					} else if (marker == EMarker.UPDATE) {
						if (LOGGER.isTraceEnabled()) {
							LOGGER.trace("Updating unit [" + unit
									+ "] in database.");
						}
						unitDao.update(unit);
					}
				}
			}

			/**
			 * Update 'users' table in database.
			 * 
			 * @throws DaoException
			 *             if database error occurred.
			 * 
			 * @author Oleksandr Lagoda
			 * 
			 */
			private void updateUsers(Map<User, EMarker> users)
					throws DaoException {
				for (Entry<User, EMarker> entry : users.entrySet()) {
					User user = entry.getKey();
					EMarker marker = entry.getValue();

					if (marker == EMarker.CREATE) {
						if (LOGGER.isTraceEnabled()) {
							LOGGER.trace("Creating user [" + user
									+ "] in database.");
						}
						userDao.create(user);
					} else if (marker == EMarker.UPDATE) {
						if (LOGGER.isTraceEnabled()) {
							LOGGER.trace("Updating user [" + user
									+ "] in database.");
						}
						userDao.update(user);
					}
				}
			}

			/**
			 * Set to previously added units parent units and rm's.
			 * 
			 * @throws DaoException
			 *             if database error occurred.
			 */
			private void setRMAndParentUnit() throws DaoException {
				for (Unit unit : units.keySet()) {

					if (LOGGER.isTraceEnabled()) {
						LOGGER.trace("Updating unit: " + unit);
					}

					unitDao.updateUnitRMAndParentUnit(unit);
				}
			}

		};

		manager.executeOperation(operation);

	}
}