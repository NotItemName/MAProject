package com.epam.preprod.ma.dao.interf;

import java.util.List;

import com.epam.preprod.ma.dao.IDao;
import com.epam.preprod.ma.dao.entity.Question;
import com.epam.preprod.ma.exception.DaoException;

/**
 * Extension of {@linkplain IDao} interface for {@linkplain Question} entity.
 * 
 * @author Mykola Zalyayev, Oleksandr Lagoda
 * 
 * @version 1.0
 * 
 */
public interface IQuestionDao extends IDao<Question> {

	/**
	 * Read all questions which belong to specified survey link.
	 * 
	 * @param surveyId
	 *            - survey id to search.
	 * 
	 * @return List of founded questions.
	 * 
	 * @throws DaoException
	 *             if a database access error occurs.
	 * 
	 * @author Mykola Zalyayev
	 */
	List<Question> readSurveyQuestionsById(Long surveyId) throws DaoException;

	/**
	 * Read all questions which belong to specified user id.
	 * 
	 * @param userId
	 *            - id of questions owner.
	 * 
	 * @return List of founded questions.
	 * 
	 * @throws DaoException
	 *             if a database access error occurs.
	 * 
	 */
	List<Question> readUserQuestionsById(Long userId) throws DaoException;

	/**
	 * check if question belongs to existing surveys
	 * 
	 * @param questionId
	 *            - id of question
	 * 
	 * @return boolean
	 * 
	 * @throws DaoException
	 *             if a database access error occurs.
	 */
	boolean isQuestionBelongToSurveys(Long questionId) throws DaoException;

	/**
	 * Delete question from survey that never started before
	 * 
	 * @param questionId
	 *            - id of question
	 * 
	 * 
	 * @throws DaoException
	 *             if a database access error occurs.
	 */
	void deleteCurrentQuestion(Long questionId) throws DaoException;

	/**
	 * Binds question with given ID to user with given ID.
	 * 
	 * @author Alexandra Martyntseva
	 * 
	 * @param questionId
	 *            - id of already existing question
	 * @param userId
	 *            - user ID
	 * @throws DaoException
	 *             if a database access error occurs.
	 */
	void bindQuestionToUser(Long questionId, Long userId) throws DaoException;

	/**
	 * @author Alexandra Martyntseva
	 * 
	 * @param question
	 *            Question object with QuestionText objects inside.
	 * @throws DaoException
	 *             if given Question object does not exist
	 */
	void addQuestionText(Question question) throws DaoException;

	/**
	 * Update question from survey that never launch before
	 * 
	 * @param question
	 *            to update
	 * 
	 * 
	 * 
	 * @throws DaoException
	 *             if a database access error occurs.
	 */
	void updateQuestionText(Question question) throws DaoException;

	/**
	 * check if question belongs to finished survey
	 * 
	 * @param questionId
	 *            - id of question
	 * 
	 * @return boolean
	 * 
	 * @throws DaoException
	 *             if a database access error occurs.
	 */
	boolean belongsToFinishedSurveys(Long questionId) throws DaoException;

	/**
	 * check if question belongs to active survey
	 * 
	 * @param questionId
	 *            - id of question
	 * 
	 * @return boolean
	 * 
	 * @throws DaoException
	 *             if a database access error occurs.
	 */
	boolean belongsToActiveSurveys(Long questionId) throws DaoException;

}
