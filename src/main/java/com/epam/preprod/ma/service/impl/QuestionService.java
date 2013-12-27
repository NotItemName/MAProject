package com.epam.preprod.ma.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epam.preprod.ma.dao.IDao;
import com.epam.preprod.ma.dao.entity.Question;
import com.epam.preprod.ma.dao.interf.IQuestionDao;
import com.epam.preprod.ma.exception.DaoException;
import com.epam.preprod.ma.exception.ServiceException;
import com.epam.preprod.ma.service.AbstractService;
import com.epam.preprod.ma.service.interf.IQuestionService;
import com.epam.preprod.ma.service.transaction.ITransactionOperation;

/**
 * Class question service extends abstract service, implements interface
 * question service.
 * 
 * @author Mykola Zalyayev, Oleksandr Lagoda, Stanislav_Gaponenko
 * 
 * @version 1.0
 * 
 */
@Service
public class QuestionService extends AbstractService<Question> implements
		IQuestionService {

	@Autowired
	private IQuestionDao dao;

	@Override
	public List<Question> readSurveyQuestionsById(final Long surveyId) {
		ITransactionOperation<List<Question>> operation = new ITransactionOperation<List<Question>>() {
			@Override
			public List<Question> execute() throws DaoException {
				return dao.readSurveyQuestionsById(surveyId);
			}
		};
		return manager.executeOperation(operation);
	}

	@Override
	public List<Question> readUserQuestionsById(final Long userId) {
		ITransactionOperation<List<Question>> operation = new ITransactionOperation<List<Question>>() {
			@Override
			public List<Question> execute() throws DaoException {

				return dao.readUserQuestionsById(userId);
			}
		};
		return manager.executeOperation(operation);
	}

	/**
	 * Always throws UnsupportedOperationException. Use
	 * {@link #createNewQuestion(Question)} instead.
	 * 
	 * @author Alexandra Martyntseva
	 */
	@Override
	public Long create(Question newEntity) throws ServiceException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @author Alexandra Martyntseva
	 */
	@Override
	public Long createNewQuestion(final Question question, final Long userId) {
		ITransactionOperation<Long> operation = new ITransactionOperation<Long>() {

			@Override
			public Long execute() throws DaoException {
				Long result = dao.create(question);
				question.setId(result);
				dao.bindQuestionToUser(result, userId);
				dao.addQuestionText(question);
				return result;
			}
		};
		return manager.executeOperation(operation);
	}

	/**
	 * @author Stanislav_Gaponenko
	 */
	@Override
	public boolean deleteQuestionFromSurvey(final Long questionId) {
		ITransactionOperation<Boolean> operation = new ITransactionOperation<Boolean>() {
			@Override
			public Boolean execute() throws DaoException {
				if (!dao.belongsToActiveSurveys(questionId)) {
					if (dao.belongsToFinishedSurveys(questionId)) {
						dao.deleteCurrentQuestion(questionId);
						return true;
					} else {
						dao.delete(questionId);
						return true;
					}
				} else {
					return false;
				}

			}
		};
		return manager.executeOperation(operation);

	}

	@Override
	public void update(final Question question) {

		ITransactionOperation<Void> operation = new ITransactionOperation<Void>() {
			@Override
			public Void execute() throws DaoException {
				dao.updateQuestionText(question);
				return null;
			}
		};

		manager.executeOperation(operation);

	}

	@Override
	protected IDao<Question> getDao() {
		return dao;
	}

	@Override
	public boolean belongsToFinishedSurveys(final Question question) {
		ITransactionOperation<Boolean> operation = new ITransactionOperation<Boolean>() {
			@Override
			public Boolean execute() throws DaoException {
				return dao.belongsToFinishedSurveys(question.getId());
			}
		};
		return manager.executeOperation(operation);
	}

}
