package com.epam.preprod.ma.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epam.preprod.ma.dao.IDao;
import com.epam.preprod.ma.dao.entity.Result;
import com.epam.preprod.ma.dao.entity.Survey;
import com.epam.preprod.ma.dao.entity.User;
import com.epam.preprod.ma.dao.interf.IResultDao;
import com.epam.preprod.ma.exception.DaoException;
import com.epam.preprod.ma.exception.ServiceException;
import com.epam.preprod.ma.messenger.MailMessenger;
import com.epam.preprod.ma.service.AbstractService;
import com.epam.preprod.ma.service.interf.IResultService;
import com.epam.preprod.ma.service.transaction.ITransactionOperation;

/**
 * Implementation of {@link IResultService}
 * 
 * @author Leonid Polyakov
 * 
 * @version 1.0
 * 
 */
@Service
public class ResultService extends AbstractService<Result> implements
		IResultService {

	@Autowired
	private IResultDao resultDao;

	@Autowired
	private MailMessenger mailMessenger;

	/**
	 * @author Leonid Polyakov
	 */
	@Override
	public void saveSurveyResults(final List<Result> results) {
		if (results.size() == 0) {
			throw new ServiceException("Can't save empty results");
		}
		User user = results.get(0).getUser();
		Survey survey = results.get(0).getSurvey();
		for (Result result : results) {
			if (!result.getUser().equals(user)) {
				throw new ServiceException("All results must have same user");
			}
			if (!result.getSurvey().equals(survey)) {
				throw new ServiceException("All results must have same survey");
			}
		}
		ITransactionOperation<Void> operation = new ITransactionOperation<Void>() {
			@Override
			public Void execute() throws DaoException {
				for (Result result : results) {
					resultDao.create(result);
				}
				mailMessenger.sendSurveyResultMail(results.get(0).getUser(),
						results);
				return null;
			}
		};
		manager.executeOperation(operation);
	}

	/**
	 * @author Leonid Polyakov
	 */
	@Override
	protected IDao<Result> getDao() {
		return resultDao;
	}

	/**
	 * @author Leonid Polyakov
	 */
	@Override
	public boolean hasResultsOnSurvey(final User user, final Survey survey) {
		ITransactionOperation<Boolean> operation = new ITransactionOperation<Boolean>() {
			@Override
			public Boolean execute() throws DaoException {
				return resultDao.countResultsOnSurvey(user, survey) > 0;
			}
		};
		return manager.executeOperation(operation);
	}

}