package com.epam.preprod.ma.service.interf;

import java.util.List;

import com.epam.preprod.ma.dao.entity.Result;
import com.epam.preprod.ma.dao.entity.Survey;
import com.epam.preprod.ma.dao.entity.User;
import com.epam.preprod.ma.service.IService;

/**
 * Extension of {@linkplain IService} interface for {@linkplain Result} entity.
 * 
 * @author Leonid Polyakov
 * 
 * @version 1.0
 * 
 */
public interface IResultService extends IService<Result> {

	/**
	 * Saves survey results.
	 * 
	 * @param results
	 *            - list of results, every element must have same survey and
	 *            user as others
	 * 
	 * @author Leonid Polyakov
	 */
	void saveSurveyResults(List<Result> results);

	/**
	 * Finds out if user has already passed the survey.
	 * 
	 * @param user
	 *            - user to check
	 * @param survey
	 *            - survey to check
	 * @return true if there is at least one result from the user for the survey
	 */
	boolean hasResultsOnSurvey(User user, Survey survey);

}