package com.epam.preprod.ma.dao.interf;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.epam.preprod.ma.dao.IDao;
import com.epam.preprod.ma.dao.entity.Survey;
import com.epam.preprod.ma.dao.entity.SurveyStatus;
import com.epam.preprod.ma.dao.entity.SurveyText;
import com.epam.preprod.ma.exception.DaoException;

/**
 * Interface for Survey Dao
 * 
 * @author Yevhen Lobazov, Mykola Zalyayev
 * 
 * @version 1.0
 */

public interface ISurveyDao extends IDao<Survey> {

	/**
	 * Must return all survey from data base with statuses
	 * SurveyStatus.SCHEDULED and SurveyStatus.IN_PROGRESS
	 * 
	 * @return List<Survey>
	 * @see Survey
	 * @see SurveyStatus
	 * @throws DaoException
	 *             if some error occurred while reading
	 */
	List<Survey> readScheduledAndInProgressSurveys() throws DaoException;

	/**
	 * Finds survey with given link.
	 * 
	 * @param link
	 *            - survey link to search.
	 * 
	 * @return Survey.
	 * 
	 * @throws DaoException
	 *             if error occurred while reading.
	 * 
	 * @author Mykola Zalyayev
	 */
	Survey readSurveyByLink(String link) throws DaoException;

	// TODO javadoc
	void createSurveyText(Long surveyId, Integer langId, SurveyText surveyText)
			throws DaoException;

	/**
	 * Find all survey by user id.
	 * 
	 * @param userId
	 *            - user id to search.
	 * 
	 * @return List of surveys.
	 * 
	 * @author Mykola Zalyayev
	 */
	List<Survey> readFinishedSurveysByUser(Long userId) throws DaoException;

	/**
	 * @param userId
	 * @return
	 * @throws DaoException
	 * @author Alexandra Martyntseva
	 */
	Survey readCurrentSurveyForRM(Long userId) throws DaoException;

	void createSurveyQuestion(Long questionId, Long surveyId)
			throws DaoException;

	/**
	 * Reads survey text from localization tables.
	 * 
	 * @author Leonid Polyakov
	 */
	Map<Locale, SurveyText> readSurveyText(Survey survey) throws DaoException;

}
