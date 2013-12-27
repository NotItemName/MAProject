package com.epam.preprod.ma.service.interf;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.epam.preprod.ma.dao.entity.Question;
import com.epam.preprod.ma.dao.entity.Survey;
import com.epam.preprod.ma.dao.entity.SurveyStatus;
import com.epam.preprod.ma.dao.entity.SurveyText;
import com.epam.preprod.ma.dao.impl.SurveyDao;
import com.epam.preprod.ma.service.IService;

/**
 * 
 * Interface of service for survey
 * 
 * @author Yevhen Lobazov, Mykola Zalyayev
 * 
 * @version 1.0
 */
public interface ISurveyService extends IService<Survey> {

	/**
	 * Must check all surveys that with SurveyStatus.SCHEDULED and
	 * SurveyStatus.IN_PROGRESS statuses and change status of the survey
	 * depending on current date
	 * 
	 * @see SurveyStatus
	 */
	void checkScheduledAndInProgressSurveys();

	/**
	 * Find survey which specified by link by calling special method in
	 * {@linkplain SurveyDao} class.
	 * 
	 * @param link
	 *            - survey link to search.
	 * 
	 * @return founded survey.
	 * 
	 * @author Mykola Zalyayev
	 */
	Survey readSurveyByLink(String link);

	/**
	 * Stop survey. Change survey status to FINISHED
	 * 
	 * @param surveyId
	 *            - {@linkplain Survey} id to search for.
	 * 
	 * @return {@linkplain Survey} status.
	 * 
	 * @author Oleksandr Lagoda
	 */
	SurveyStatus stopSurveyById(Long surveyId);

	/**
	 * 
	 * @author Alexandra Martyntseva
	 * @param userId
	 * @return
	 */
	Survey readCurrentSurveyForRM(Long userId);

	/**
	 * Schedule the specified survey
	 * 
	 * @param startDate
	 * @param endDate
	 * @param description
	 * @param rmId
	 * @param contextPath
	 * @return
	 */
	Survey scheduleSurvey(Date startDate, Date endDate,
			Map<Locale, SurveyText> description, Long rmId,
			List<Question> questions);

	/**
	 * Find finished survey what created by RM id calling by special method in
	 * {@linkplain SurveyDao} class.
	 * 
	 * @param userId
	 *            - user id to search.
	 * 
	 * @return founded surveys.
	 * 
	 * @author Mykola Zalyayev
	 */
	List<Survey> readFinishedSurveysByRM(Long userId);

}
