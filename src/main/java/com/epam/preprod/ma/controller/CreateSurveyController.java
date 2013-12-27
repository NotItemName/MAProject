package com.epam.preprod.ma.controller;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.epam.preprod.ma.bean.SurveyCreationBean;
import com.epam.preprod.ma.bean.SurveyCreationWrapperBean;
import com.epam.preprod.ma.constant.SessionAttribute;
import com.epam.preprod.ma.controller.validation.SurveyCreationValidator;
import com.epam.preprod.ma.dao.entity.Dimension;
import com.epam.preprod.ma.dao.entity.Question;
import com.epam.preprod.ma.dao.entity.QuestionText;
import com.epam.preprod.ma.dao.entity.Survey;
import com.epam.preprod.ma.dao.entity.SurveyStatus;
import com.epam.preprod.ma.dao.entity.SurveyText;
import com.epam.preprod.ma.dao.entity.Unit;
import com.epam.preprod.ma.dao.entity.User;
import com.epam.preprod.ma.exception.BadRequestException;
import com.epam.preprod.ma.messenger.IMailMessenger;
import com.epam.preprod.ma.service.interf.IQuestionService;
import com.epam.preprod.ma.service.interf.ISurveyService;
import com.epam.preprod.ma.service.interf.IUnitService;
import com.epam.preprod.ma.service.interf.IUserService;

/**
 * Processes survey page for RM user.
 * 
 * @author Oleksandr Lagoda
 * 
 * @version 1.0
 */
@Controller
@SessionAttributes(SessionAttribute.USER)
public class CreateSurveyController {

	private static final Logger LOGGER = Logger
			.getLogger(CreateSurveyController.class);

	public static final String REQ_PARAM_QUESTION_TYPE = "type";

	public static final String REQ_PARAM_QUESTION_NAME = "name";

	public static final String REQ_PARAM_QUESTION_SATISF_DESCR = "satisfactionText";

	public static final String REQ_PARAM_QUESTION_PRIOR_DESCR = "prioritizationText";

	private static final String REQ_PARAM_START_DATE = "startDate";

	private static final String REQ_PARAM_END_DATE = "endDate";

	private static final String REQ_PARAM_SURVEY_NAME = "name";

	private static final String REQ_PARAM_SURVEY_SATISFACTION = "satisfactionDescription";

	private static final String REQ_PARAM_SURVEY_PRIORITIZATION = "prioritizationDescription";

	public static final String RES_PARAM_SURVEY_STATUS = "surveyStatus";

	@Autowired
	private IQuestionService questionService;

	@Autowired
	private ISurveyService surveyService;

	@Autowired
	private IUnitService unitService;

	@Autowired
	private IUserService userService;

	@Resource(name = "locales")
	private List<Locale> locales;

	@Autowired
	private IMailMessenger mailMessenger;

	@RequestMapping(value = "/createSurvey", method = RequestMethod.GET)
	public String showCreateSurveyPage(HttpServletRequest request,
			@ModelAttribute(SessionAttribute.USER) User user, Model model)
			throws IOException {

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("User received. --> " + user);
		}

		List<Question> questions = questionService.readUserQuestionsById(user
				.getId());

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("List of questions received. --> " + questions);
		}

		Survey survey = surveyService.readCurrentSurveyForRM(user.getId());
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("current user survey recieved: " + survey);
		}

		model.addAttribute("survey", survey);
		model.addAttribute("questionList", questions);
		model.addAttribute("locales", locales);
		model.addAttribute("url", ControllerUtils.getApplicationURL(request));
		model.addAttribute("subordinateTree", getSubordinateTree(user));

		return "createSurvey";
	}

	/**
	 * 
	 * Invoked with ajax to schedule the survey
	 * 
	 * @param response
	 *            <code>HttpServletResponse</code>
	 * @param request
	 *            <code>WebRequest</code>
	 * @return <code>ModelAndView</code>
	 * @throws IOException
	 * @author Yevhen Lobazov
	 */
	@RequestMapping(value = "/scheduleSurvey", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public @ResponseBody
	SurveyCreationWrapperBean scheduleSurvey(HttpServletRequest request,
			@ModelAttribute(SessionAttribute.USER) User user,
			@RequestParam(REQ_PARAM_START_DATE) String startDate,
			@RequestParam(REQ_PARAM_END_DATE) String endDate) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Entered survey start date: " + startDate);
			LOGGER.trace("Entered survey end date: " + endDate);
		}
		Map<Locale, SurveyText> description = new HashMap<>();
		for (Locale locale : locales) {
			String name = request.getParameter(REQ_PARAM_SURVEY_NAME + locale);
			String satisfaction = request
					.getParameter(REQ_PARAM_SURVEY_SATISFACTION + locale);
			String prioritization = request
					.getParameter(REQ_PARAM_SURVEY_PRIORITIZATION + locale);
			description.put(locale, new SurveyText(name, satisfaction,
					prioritization));
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Inputed survey description "
						+ locale.toString().toUpperCase() + ": name: " + name
						+ ", satisfaction: " + satisfaction
						+ ", prioritization: " + prioritization);
			}
		}
		SurveyCreationWrapperBean wrapper = new SurveyCreationWrapperBean();
		SurveyCreationBean bean = new SurveyCreationBean(startDate, endDate,
				description);
		List<Question> questions = questionService.readUserQuestionsById(user
				.getId());
		Map<String, String> errors = SurveyCreationValidator.validate(bean,
				questions);
		if (errors.isEmpty()) {
			Date from = new Date();
			Date to = new Date();
			fillDate(from, to, startDate, endDate, errors);
			if (!errors.isEmpty()) {
				return errorResult(wrapper, errors);
			}
			Survey survey = surveyService.scheduleSurvey(from, to, description,
					user.getId(), questions);
			surveyService.checkScheduledAndInProgressSurveys();
			survey = surveyService.readById(survey.getId());
			wrapper.setLink(ControllerUtils.getApplicationURL(request)
					+ survey.getLink());
			wrapper.setSurveyStatus(survey.getStatus());
			LOGGER.info("Survey was scheduled");
		} else {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Invalid input for survey scheduling. Errors: "
						+ errors);
			}
			LOGGER.info("Invalid input for survey scheduling");
			wrapper.setErrors(errors);
		}
		return wrapper;
	}

	private void fillDate(Date from, Date to, String startDate, String endDate,
			Map<String, String> errors) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				SurveyCreationValidator.DATE_PATTERN);
		dateFormat.setLenient(false);
		try {
			from.setTime(dateFormat.parse(startDate).getTime());
		} catch (ParseException e) {
			errors.put(SurveyCreationValidator.ERROR_START_DATE,
					SurveyCreationValidator.MSG_START_DATE_INVALID);
		}
		try {
			to.setTime(dateFormat.parse(endDate).getTime());
		} catch (ParseException e) {
			errors.put(SurveyCreationValidator.ERROR_END_DATE,
					SurveyCreationValidator.MSG_END_DATE_INVALID);
		}
	}

	private SurveyCreationWrapperBean errorResult(
			SurveyCreationWrapperBean wrapper, Map<String, String> errors) {
		wrapper.setErrors(errors);
		return wrapper;
	}

	/**
	 * Ajax method that deletes a quesition of a survey
	 * 
	 * @author Stanislav Gaponenko
	 * @param questionId
	 *            - Id of question to delete
	 * 
	 * @return Id of question to delete
	 * 
	 */
	@RequestMapping(value = "/deleteQuestion", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Long deleteQuestion(
			@RequestParam(value = "questionId") Long questionId) {

		if (questionService.deleteQuestionFromSurvey(questionId)) {
			return questionId;
		} else {
			return (long) -1;
		}

	}

	/**
	 * Ajax method that edits a quesition of a survey
	 * 
	 * @author Stanislav Gaponenko
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/editQuestion", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public boolean editQuestion(HttpServletRequest req) {
		Long questionId = null;
		if (req.getParameter("questionId") != null) {
			try {
				questionId = Long.valueOf(req.getParameter("questionId"));
			} catch (NumberFormatException e) {
				LOGGER.error("QuestionId parameter is invalid", e);
				return false;
			}
		}

		Map<Locale, QuestionText> questionText = new HashMap<>();

		for (Locale loc : locales) {

			String name = req.getParameter(REQ_PARAM_QUESTION_NAME
					+ loc.getLanguage());

			String satisfactionText = req
					.getParameter(REQ_PARAM_QUESTION_SATISF_DESCR
							+ loc.getLanguage());
			String priorityText = req
					.getParameter(REQ_PARAM_QUESTION_PRIOR_DESCR
							+ loc.getLanguage());

			if (name != null && !name.isEmpty() && satisfactionText != null
					&& !satisfactionText.isEmpty() && satisfactionText != null
					&& !satisfactionText.isEmpty()) {

				questionText.put(loc, new QuestionText(name, satisfactionText,
						priorityText));
			} else {
				LOGGER.error("Invalid parameters");
				return false;
			}
		}
		Question question = new Question(questionId, null, questionText);

		if (questionService.belongsToFinishedSurveys(question)) {
			return false;
		} else {
			questionService.update(question);
			return true;
		}

	}

	/**
	 * Inserts new question into storage if request parameters are not empty. Is
	 * used with ajax requests. Returns json string. Json contains full info
	 * about added question. If some data is wrong and question can't be added
	 * json contains only { error : true }.
	 * 
	 * @param req
	 * @return
	 * @author Alexandra Martyntseva
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/addQuestion", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String addQuestion(@ModelAttribute(SessionAttribute.USER) User user,
			@RequestParam(REQ_PARAM_QUESTION_TYPE) String dimensionParam,
			HttpServletRequest req, Locale locale) {
		JSONObject result = new JSONObject();

		Dimension dimension;
		try {
			dimension = Dimension.valueOf(dimensionParam);
		} catch (IllegalArgumentException e) {
			LOGGER.info("got " + dimensionParam + " instead of Dimension", e);
			throw new BadRequestException(e);
		}

		Map<Locale, QuestionText> questionText = new HashMap<>();

		JSONArray nameArr = new JSONArray();
		JSONArray satisfArr = new JSONArray();
		JSONArray priorArr = new JSONArray();

		for (Locale loc : locales) {
			String name = req.getParameter(REQ_PARAM_QUESTION_NAME
					+ loc.getLanguage());
			String satisfactionText = req
					.getParameter(REQ_PARAM_QUESTION_SATISF_DESCR
							+ loc.getLanguage());
			String priorityText = req
					.getParameter(REQ_PARAM_QUESTION_PRIOR_DESCR
							+ loc.getLanguage());
			if (isBlank(name) || isBlank(satisfactionText)
					|| isBlank(priorityText)) {
				// this indicates that adding question failed and user
				// should get a message
				result.put("error", true);
				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace("returning json: " + result.toJSONString());
				}
				return result.toJSONString();
			}

			nameArr.add(name);
			satisfArr.add(satisfactionText);
			priorArr.add(priorityText);

			questionText.put(loc, new QuestionText(name, satisfactionText,
					priorityText));
		}
		Question question = new Question(dimension, questionText);
		long addedQuestionId = questionService.createNewQuestion(question,
				user.getId());
		result.put("id", addedQuestionId);
		result.put("type", dimension.name());
		result.put("name", nameArr);
		result.put("satisfactionText", satisfArr);
		result.put("prioritizationText", priorArr);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("returning json: " + result.toJSONString());
		}
		return result.toJSONString();
	}

	/**
	 * Cancels survey with given id. Deletes all its data.
	 * 
	 * @param surveyId
	 * @return
	 * @author Alexandra Martyntseva
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/cancelSurvey", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public @ResponseBody
	JSONObject cancelSurvey(@ModelAttribute(SessionAttribute.USER) User user) {
		Survey survey = surveyService.readCurrentSurveyForRM(user.getId());
		if (survey != null) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Delete survey with id : " + survey.getId());
			}
			surveyService.delete(survey.getId());
		}
		JSONObject json = new JSONObject();
		json.put("deleted", "true");
		return json;
	}

	/**
	 * Ajax method that change survey status to FINISHED
	 * 
	 * @param surveyId
	 *            - Id of survey to search
	 * 
	 * @return New status of the survey.
	 * 
	 */
	@RequestMapping(value = "/stopSurvey", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public @ResponseBody
	Map<String, SurveyStatus> stopSurvey(
			@ModelAttribute(SessionAttribute.USER) User user) {

		Map<String, SurveyStatus> responseParam = new HashMap<>();
		Survey survey = surveyService.readCurrentSurveyForRM(user.getId());

		responseParam.put(RES_PARAM_SURVEY_STATUS,
				surveyService.stopSurveyById(survey.getId()));

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Survey status set as "
					+ responseParam.get(RES_PARAM_SURVEY_STATUS));
		}

		return responseParam;
	}

	/**
	 * Ajax method that send survey link to users mail.
	 * 
	 * @param request
	 *            - HttpServletRequest used in
	 *            {@linkplain ControllerUtils#getApplicationURL}
	 * 
	 * @param user
	 *            - RM that sending mail
	 * 
	 * @param usersId
	 *            - users id to send mail
	 * 
	 * @author Mykola Zalyayev
	 */
	@RequestMapping(value = "/sendLink", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public void sendLinkToMail(HttpServletRequest request,
			@ModelAttribute(SessionAttribute.USER) User user,
			@RequestParam(value = "usersId") Long[] usersId) {
		Survey survey = surveyService.readCurrentSurveyForRM(user.getId());
		List<User> users = userService.readUsersByIds(usersId);
		mailMessenger.sendSurveyLnkToEmployee(survey, users,
				ControllerUtils.getApplicationURL(request));

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Link sended to " + users);
		}
	}

	/**
	 * Method to create subordinate tree.
	 * 
	 * @param user
	 *            - user to find his subordinate
	 * 
	 * @return created tree
	 * 
	 * @author Mykola Zalyayev
	 */
	@SuppressWarnings("unchecked")
	private JSONObject getSubordinateTree(User user) {
		List<Unit> unitList = unitService.readSubordinateUnitsByUser(user);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Read units list subbordinate by " + user + " --> "
					+ unitList);
		}

		JSONObject json = new JSONObject();
		try {
			for (Unit unit : unitList) {
				JSONObject jsonUnit = getSubordinate(unit);
				if (!jsonUnit.isEmpty()) {
					json.put(unit.getName(), jsonUnit);
				} else {
					json.put("error", true);
				}
			}
		} catch (JSONException e) {
			LOGGER.error("Json error occurred");
		}
		return json;
	}

	/**
	 * Get subordinate by unit
	 * 
	 * @param unit
	 *            - unit to find his subordinate
	 * 
	 * @return json with subordinate
	 * 
	 * @throws JSONException
	 * 
	 * @author Mykola Zalyayev
	 */
	@SuppressWarnings("unchecked")
	private JSONObject getSubordinate(Unit unit) throws JSONException {
		JSONObject json = new JSONObject();
		List<Unit> unitList = unitService.readSubordinateUnitsByUnit(unit);
		Map<Long, String> usersMap = getUserByUnit(unit);
		if (!usersMap.isEmpty()) {
			json.put("users", usersMap);
		}
		JSONObject jsonUnit = new JSONObject();
		for (Unit buffUnit : unitList) {
			JSONObject buff = getSubordinate(buffUnit);
			if (!buff.isEmpty()) {
				jsonUnit.put(buffUnit.getName(), buff);
			}
		}
		if (!jsonUnit.isEmpty()) {
			json.put("units", jsonUnit);
		}
		return json;
	}

	/**
	 * Get subordinate users by unit
	 * 
	 * @param unit
	 *            - unit to find his subordinate
	 * 
	 * @return map of users name
	 * 
	 * @author Mykola Zalyayev
	 */
	private Map<Long, String> getUserByUnit(Unit unit) {
		Map<Long, String> usersByUnit = new HashMap<>();
		List<User> usersList = userService.readUsersByUnit(unit.getId());
		for (User u : usersList) {
			usersByUnit.put(u.getId(), u.getName());
		}
		return usersByUnit;
	}
}
