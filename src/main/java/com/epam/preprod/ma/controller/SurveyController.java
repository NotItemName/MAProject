package com.epam.preprod.ma.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.request.WebRequest;

import com.epam.preprod.ma.bean.AnswersBean;
import com.epam.preprod.ma.constant.SessionAttribute;
import com.epam.preprod.ma.controller.validation.IAnswersValidator;
import com.epam.preprod.ma.controller.validation.PrioritizationAnswersValidator;
import com.epam.preprod.ma.controller.validation.SatisfactionAnswersValidator;
import com.epam.preprod.ma.dao.entity.Question;
import com.epam.preprod.ma.dao.entity.Result;
import com.epam.preprod.ma.dao.entity.Survey;
import com.epam.preprod.ma.dao.entity.SurveyStatus;
import com.epam.preprod.ma.dao.entity.User;
import com.epam.preprod.ma.exception.BadRequestException;
import com.epam.preprod.ma.service.interf.IQuestionService;
import com.epam.preprod.ma.service.interf.IResultService;
import com.epam.preprod.ma.service.interf.ISurveyService;

/**
 * Implements logic of passing the whole survey: from first GET request on
 * survey list till last POST request from prioritization survey page.
 * 
 * @author Leonid Polyakov, Mykola
 * 
 * @version 1.0
 * 
 */
@Controller
@SessionAttributes({ SessionAttribute.CURRENT_SURVEY_FOR_EMPLOYEE,
		SessionAttribute.SATISFACTION_ANSWERS,
		SessionAttribute.PRIORITIZATION_ANSWERS, SessionAttribute.USER })
public class SurveyController {

	private static final Logger LOGGER = Logger
			.getLogger(SurveyController.class);

	private final String SURVEY_SCHEDULED_ERROR_MESSAGE = "jsp.showSurvey.invalid.scheduled";

	private final String SURVEY_FINISHED_ERROR_MESSAGE = "jsp.showSurvey.invalid.finished";

	private final String SURVEY_INVALID_ERROR_MESSAGE = "jsp.showSurvey.invalid.invalidLink";

	private final String SURVEY_IS_ALREADY_PASSED = "jsp.showSurvey.invalid.alreadyPassed";

	private final String SATISFACTION_ANSWER = "satisfaction";

	private final String PRIORITY_ANSWER = "priority";

	private final String REDIRECT_TO_LINK = "redirect:/";

	@Autowired
	private IQuestionService questionService;

	@Autowired
	private ISurveyService surveyService;

	@Autowired
	private IResultService resultService;

	@ModelAttribute(SessionAttribute.CURRENT_SURVEY_FOR_EMPLOYEE)
	public Survey populateSurvey() {
		return new Survey();
	}

	@ModelAttribute(SessionAttribute.SATISFACTION_ANSWERS)
	public AnswersBean populateSatisfactionAnswers() {
		return new AnswersBean();
	}

	@ModelAttribute(SessionAttribute.PRIORITIZATION_ANSWERS)
	public AnswersBean populatePrioritizationAnswers() {
		return new AnswersBean();
	}

	/**
	 * Controller to show page of passing the survey
	 * 
	 * @param surveyLink
	 *            - path variable to read survey
	 * 
	 * @param survey
	 *            - survey from session
	 * 
	 * @param user
	 *            - user that passing survey
	 * 
	 * @param satisfactionAnswers
	 * 
	 * @param priorityAnswers
	 * 
	 * @param model
	 * 
	 * @return view name
	 * 
	 * @author Mykola Zalyayev
	 */
	@RequestMapping(value = "/{surveyLink}", method = RequestMethod.GET)
	public String controlSurvey(
			@PathVariable("surveyLink") String surveyLink,
			@ModelAttribute(SessionAttribute.CURRENT_SURVEY_FOR_EMPLOYEE) Survey survey,
			@ModelAttribute(SessionAttribute.USER) User user,
			@ModelAttribute(SessionAttribute.SATISFACTION_ANSWERS) AnswersBean satisfactionAnswers,
			@ModelAttribute(SessionAttribute.PRIORITIZATION_ANSWERS) AnswersBean priorityAnswers,
			ModelMap model) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Got survey from session --> " + survey);
		}

		String viewName = null;

		// this block checks if user has gone to another survey or is passing
		// survey for the first time
		if (survey.getLink() == null || !survey.getLink().equals(surveyLink)) {
			survey = surveyService.readSurveyByLink(surveyLink);
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Read survey from database --> " + survey);
			}
			// this block means that there is no survey with given link
			if (survey == null) {
				model.put("invalidSurveyMessage", SURVEY_INVALID_ERROR_MESSAGE);
				return "invalidSurveyStatus";
			}
			// this code means that new survey was found and we need to put it
			// into session, overriding old one and old answers
			model.put(SessionAttribute.CURRENT_SURVEY_FOR_EMPLOYEE, survey);
			// these two calls repopulate beans in session
			satisfactionAnswers = populateSatisfactionAnswers();
			model.put(SessionAttribute.SATISFACTION_ANSWERS,
					satisfactionAnswers);
			priorityAnswers = populatePrioritizationAnswers();
			model.put(SessionAttribute.PRIORITIZATION_ANSWERS, priorityAnswers);
		}

		// this block checks if survey is still in progress
		if (!survey.getStatus().equals(SurveyStatus.IN_PROGRESS)) {
			model.put(SessionAttribute.CURRENT_SURVEY_FOR_EMPLOYEE,
					populateSurvey());
			return invalidSurvey(survey, model);
		}

		// this block sets viewName accordingly to the progress of passing
		// survey (satisfaction, prioritization and congratulation pages)
		if (satisfactionAnswers.getAnswers() == null) {
			viewName = "satisfactionAssessment";
		} else if (priorityAnswers.getAnswers() == null) {
			viewName = "prioritiesAssessment";
		} else {
			// these three calls repopulate session attributes that is session
			// loses beans
			model.put(SessionAttribute.CURRENT_SURVEY_FOR_EMPLOYEE,
					populateSurvey());
			model.put(SessionAttribute.SATISFACTION_ANSWERS,
					populateSatisfactionAnswers());
			model.put(SessionAttribute.PRIORITIZATION_ANSWERS,
					populatePrioritizationAnswers());
			return "congratulationPage";
		}

		// this block checks if user is passing survey for the first time
		if (resultService.hasResultsOnSurvey(user, survey)) {
			model.put("invalidSurveyMessage", SURVEY_IS_ALREADY_PASSED);
			return "invalidSurveyStatus";
		}

		// next code is reached when user is still in progress on passing survey
		// here we pass questions in requestScope
		List<Question> questions = questionService
				.readSurveyQuestionsById(survey.getId());
		survey.setQuestions(questions);

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Read questions from database -->" + questions);
		}

		model.put("questions", questions);
		return viewName;
	}

	/**
	 * Method to put in model text of invalid survey
	 * 
	 * @param survey
	 * 
	 * @param model
	 * 
	 * @return view name
	 * 
	 * @author Mykola Zalyayev
	 */
	private String invalidSurvey(Survey survey, ModelMap model) {
		if (survey.getStatus().equals(SurveyStatus.SCHEDULED)) {
			model.put("invalidSurveyMessage", SURVEY_SCHEDULED_ERROR_MESSAGE);
			model.put("surveyStartDate", survey.getStartDate());
		}

		if (survey.getStatus().equals(SurveyStatus.FINISHED)) {
			model.put("invalidSurveyMessage", SURVEY_FINISHED_ERROR_MESSAGE);
		}

		LOGGER.trace("Survey is scheduled, forward to error survey page");
		model.put(SessionAttribute.CURRENT_SURVEY_FOR_EMPLOYEE,
				populateSurvey());
		return "invalidSurveyStatus";
	}

	/**
	 * @author Leonid Polyakov
	 */
	@RequestMapping(value = "/{surveyLink}", method = RequestMethod.POST)
	public String saveResults(
			@PathVariable("surveyLink") String surveyLink,
			@ModelAttribute(SessionAttribute.CURRENT_SURVEY_FOR_EMPLOYEE) Survey survey,
			@ModelAttribute(SessionAttribute.USER) User user,
			@ModelAttribute(SessionAttribute.SATISFACTION_ANSWERS) AnswersBean satisfaction,
			@ModelAttribute(SessionAttribute.PRIORITIZATION_ANSWERS) AnswersBean prioritization,
			WebRequest request, ModelMap model) {
		if (survey.getId() == null) {
			LOGGER.trace("survey came null");
			throw new BadRequestException("Bad request");
		}
		// process first POST request
		if (satisfaction.getAnswers() == null) {
			satisfaction = processAnswers(request, survey, SATISFACTION_ANSWER,
					new SatisfactionAnswersValidator());
			model.put(SessionAttribute.SATISFACTION_ANSWERS, satisfaction);
			// show prioritization survey page
			return REDIRECT_TO_LINK + surveyLink;
		}
		// process second POST request
		if (prioritization.getAnswers() == null) {
			prioritization = processAnswers(request, survey, PRIORITY_ANSWER,
					new PrioritizationAnswersValidator());
			saveResults(user, survey, satisfaction, prioritization);
			model.put(SessionAttribute.PRIORITIZATION_ANSWERS, prioritization);
		}
		// show congratulation page
		return REDIRECT_TO_LINK + surveyLink;
	}

	/**
	 * @author Leonid Polyakov
	 */
	private AnswersBean processAnswers(WebRequest request, Survey survey,
			String parameterName, IAnswersValidator validator) {
		AnswersBean bean = new AnswersBean();
		Map<Question, Integer> answersMap = getAnswersMapFromRequest(request,
				survey, parameterName);
		bean.setAnswers(answersMap);

		if (!validator.validate(bean, survey)) {
			LOGGER.error("answers validation failed " + bean);
			throw new BadRequestException("Bad request");
		}
		return bean;
	}

	/**
	 * @author Leonid Polyakov
	 */
	private Map<Question, Integer> getAnswersMapFromRequest(WebRequest request,
			Survey survey, String parameterName) {
		Map<Question, Integer> answers = new HashMap<>();
		for (Question question : survey.getQuestions()) {
			String unparsedLevel = request.getParameter(parameterName
					+ question.getId());
			Integer level = parseLevelFromParameter(unparsedLevel);
			if (level != null) {
				answers.put(question, level);
			}
		}
		return answers;
	}

	/**
	 * @author Leonid Polyakov
	 */
	private Integer parseLevelFromParameter(String unparsedLevel) {
		try {
			return Integer.parseInt(unparsedLevel);
		} catch (NumberFormatException e) {
			LOGGER.debug("User sent level with wrong format: " + unparsedLevel);
			return null;
		}
	}

	/**
	 * @author Leonid Polyakov
	 */
	private void saveResults(User user, Survey survey,
			AnswersBean satisfaction, AnswersBean prioritization) {
		List<Result> results = new ArrayList<>();
		Map<Question, Integer> satisfactionMap = satisfaction.getAnswers();
		Map<Question, Integer> prioritizationMap = prioritization.getAnswers();
		for (Question question : survey.getQuestions()) {
			Result result = new Result();
			result.setUser(user);
			result.setSurvey(survey);
			result.setQuestion(question);
			result.setSatisfaction(satisfactionMap.get(question));
			result.setPriority(prioritizationMap.get(question));
			results.add(result);
		}
		resultService.saveSurveyResults(results);
	}
}