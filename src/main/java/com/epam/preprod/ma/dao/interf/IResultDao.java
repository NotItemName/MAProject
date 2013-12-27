package com.epam.preprod.ma.dao.interf;

import com.epam.preprod.ma.dao.IDao;
import com.epam.preprod.ma.dao.entity.Result;
import com.epam.preprod.ma.dao.entity.Survey;
import com.epam.preprod.ma.dao.entity.User;
import com.epam.preprod.ma.exception.DaoException;

/**
 * Interface for Result Dao
 * 
 * @author Leonid Polyakov
 * 
 * @version 1.0
 */

public interface IResultDao extends IDao<Result> {

	/**
	 * Counts results of user on survey which may be used to indicate whether
	 * user has already passed givn survey.
	 * 
	 * @param user
	 *            - user to check
	 * @param survey
	 *            - survey to check
	 * @return number of results of user on survey
	 * @throws DaoException
	 *             if database exception occurs while counting
	 */
	Integer countResultsOnSurvey(User user, Survey survey) throws DaoException;

}