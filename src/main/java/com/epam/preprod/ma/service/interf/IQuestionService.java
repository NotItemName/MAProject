package com.epam.preprod.ma.service.interf;

import java.util.List;

import com.epam.preprod.ma.dao.entity.Question;
import com.epam.preprod.ma.dao.impl.QuestionDao;
import com.epam.preprod.ma.exception.DaoException;
import com.epam.preprod.ma.service.IService;

/**
 * Extension of {@linkplain IService} interface for {@linkplain Question}
 * entity.
 * 
 * @author Mykola Zalyayev, Oleksandr Lagoda, Stanislav Gaponenko
 * 
 * @version 1.0
 * 
 */
public interface IQuestionService extends IService<Question> {

	/**
	 * Read all questions which belong to survey with given survey id by calling
	 * special method in {@linkplain QuestionDao} class.
	 * 
	 * @param surveyId
	 *            - id to search.
	 * 
	 * @return List of founded questions.
	 * 
	 * @throws DaoException
	 *             if a database access error occurs.
	 * 
	 * @author Mykola Zalyayev
	 */
	List<Question> readSurveyQuestionsById(Long surveyId);

	/**
	 * Read all questions which belong to specified user id by calling special
	 * method in {@linkplain QuestionDao} class.
	 * 
	 * @param userId
	 *            - id to search.
	 * 
	 * @return List of founded questions.
	 * 
	 * @throws DaoException
	 *             if a database access error occurs.
	 * 
	 */
	List<Question> readUserQuestionsById(Long userId);

	/**
	 * Deletes question by id
	 * 
	 * 
	 * @param userId
	 *            - id to search.
	 * 
	 * @return boolean
	 * 
	 * @throws DaoException
	 *             if a database access error occurs.
	 * 
	 */
	boolean deleteQuestionFromSurvey(Long questionId);

	/**
	 * Substitutes {@link IService#create(Object)}.
	 * 
	 * @author Alexandra Martyntseva
	 * 
	 * @param question
	 * @return question ID
	 * 
	 */
	Long createNewQuestion(Question question, Long userId);

	/**
	 * check if question belongs to finished survey
	 * 
	 * @param questionId
	 *            - id of question
	 * 
	 * @return boolean
	 * @author Stanislav Gaponenko
	 * 
	 */
	boolean belongsToFinishedSurveys(Question question);

}
