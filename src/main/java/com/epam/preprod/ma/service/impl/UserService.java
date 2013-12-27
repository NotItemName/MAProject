package com.epam.preprod.ma.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epam.preprod.ma.dao.IDao;
import com.epam.preprod.ma.dao.entity.Survey;
import com.epam.preprod.ma.dao.entity.User;
import com.epam.preprod.ma.dao.interf.IResultDao;
import com.epam.preprod.ma.dao.interf.IUserDao;
import com.epam.preprod.ma.exception.DaoException;
import com.epam.preprod.ma.exception.ServiceException;
import com.epam.preprod.ma.service.AbstractService;
import com.epam.preprod.ma.service.interf.IUserService;
import com.epam.preprod.ma.service.transaction.ITransactionOperation;

/**
 * Implementation of {@link IUserService}.
 * 
 * @author Oleksandr Lagoda
 * 
 * @version 1.0
 * 
 */
@Service
public class UserService extends AbstractService<User> implements IUserService {

	@Autowired
	private IUserDao userDao;

	@Autowired
	private IResultDao resultDao;

	@Override
	public List<User> readAllUsers() {
		ITransactionOperation<List<User>> operation = new ITransactionOperation<List<User>>() {
			@Override
			public List<User> execute() throws DaoException {
				return userDao.readAllUsers();
			}
		};
		return manager.executeOperation(operation);
	}

	@Override
	protected IDao<User> getDao() {
		return userDao;
	}

	@Override
	public Long create(User newEntity) throws ServiceException {
		throw new UnsupportedOperationException();
	}

	@Override
	public User readById(Long id) throws ServiceException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void update(User entityToUpdate) throws ServiceException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(Long id) throws ServiceException {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<User> readUsersWithResultOnSurveysByUnit(final Long unitId,
			final Long[] surveysId) {
		ITransactionOperation<List<User>> operation = new ITransactionOperation<List<User>>() {
			@Override
			public List<User> execute() throws DaoException {
				List<User> usersByUnit = new ArrayList<>();
				for (User user : userDao.readUsersByUnit(unitId)) {
					for (Long surveyId : surveysId) {
						Survey survey = new Survey();
						survey.setId(surveyId);
						if (resultDao.countResultsOnSurvey(user, survey) > 0) {
							usersByUnit.add(user);
							break;
						}
					}

				}
				return usersByUnit;
			}
		};
		return manager.executeOperation(operation);
	}

	@Override
	public User readUserByLogin(final String login) {

		ITransactionOperation<User> operation = new ITransactionOperation<User>() {
			@Override
			public User execute() throws DaoException {
				return userDao.readUserByLogin(login);
			}
		};
		return manager.executeOperation(operation);

	}

	@Override
	public List<User> readUsersByUnit(final Long unitId) {
		ITransactionOperation<List<User>> operation = new ITransactionOperation<List<User>>() {
			@Override
			public List<User> execute() throws DaoException {
				return userDao.readUsersByUnit(unitId);
			}
		};
		return manager.executeOperation(operation);
	}

	@Override
	public List<User> readUsersByIds(final Long[] usersId) {
		ITransactionOperation<List<User>> operation = new ITransactionOperation<List<User>>() {
			@Override
			public List<User> execute() throws DaoException {
				List<User> usersList = new ArrayList<>();
				for (Long userId : usersId) {
					usersList.add(userDao.readById(userId));
				}
				return usersList;
			}
		};
		return manager.executeOperation(operation);
	}
}
