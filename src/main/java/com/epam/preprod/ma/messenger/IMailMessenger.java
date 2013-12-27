package com.epam.preprod.ma.messenger;

import java.util.List;

import com.epam.preprod.ma.dao.entity.Result;
import com.epam.preprod.ma.dao.entity.Survey;
import com.epam.preprod.ma.dao.entity.User;

/**
 * 
 * @author Mykola_Zalyayev
 * 
 */
public interface IMailMessenger {

	/**
	 * Send mail to RM about change survey status.
	 * 
	 * @param survey
	 *            - survey that change status
	 * 
	 * @param user
	 *            - RM to send
	 * 
	 * @author Mykola Zalyayev
	 */
	void sendSurveyStatusChangedMail(Survey survey, User user);

	/**
	 * Send mail to user with result.
	 * 
	 * @param user
	 *            - user to send.
	 * 
	 * @param result
	 *            - result of users.
	 * 
	 * @author Mykola Zalyayev
	 */
	void sendSurveyResultMail(User user, List<Result> result);

	/**
	 * Send mail with survey link to employees.
	 * 
	 * @param survey
	 *            - survey.
	 * 
	 * @param users
	 *            - list of users to send.
	 * 
	 * @param url
	 *            - url to send.
	 * 
	 * @author Mykola Zalyayev
	 */
	void sendSurveyLnkToEmployee(Survey survey, List<User> users, String url);

}
