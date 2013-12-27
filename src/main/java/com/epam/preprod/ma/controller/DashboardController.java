package com.epam.preprod.ma.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.epam.preprod.ma.bean.ChartsData;
import com.epam.preprod.ma.bean.DashboardQuestionBean;
import com.epam.preprod.ma.constant.SessionAttribute;
import com.epam.preprod.ma.dao.entity.Dimension;
import com.epam.preprod.ma.dao.entity.Result;
import com.epam.preprod.ma.dao.entity.Survey;
import com.epam.preprod.ma.dao.entity.Unit;
import com.epam.preprod.ma.dao.entity.User;
import com.epam.preprod.ma.service.interf.IDashboardService;
import com.epam.preprod.ma.service.interf.ISurveyService;
import com.epam.preprod.ma.service.interf.IUnitService;
import com.epam.preprod.ma.service.interf.IUserService;

/**
 * 
 * 
 * @author Mykola Zalyayev, Alexandra Martyntseva
 * 
 * @version 1.0
 */
@Controller
@SessionAttributes({ SessionAttribute.USER })
public class DashboardController {

	@Autowired
	private ISurveyService surveyService;

	@Autowired
	private IUnitService unitService;

	@Autowired
	private IUserService userService;

	private static final Logger LOGGER = Logger
			.getLogger(DashboardController.class);

	@Autowired
	private IDashboardService dashboardService;

	/**
	 * Show dasboard page.
	 * 
	 * @param user
	 * 
	 * @return
	 * 
	 * @throws IOException
	 * 
	 * @author Mykola Zalyayev
	 */
	@RequestMapping(value = "/dashboard")
	public ModelAndView showDashboard(
			@ModelAttribute(SessionAttribute.USER) User user,
			Locale selectedLocale) {
		ModelAndView mav = new ModelAndView("dashboard");
		List<Survey> surveyList = surveyService.readFinishedSurveysByRM(user
				.getId());
		List<Survey> revertedSurveyList = new ArrayList<>(surveyList);
		Collections.reverse(revertedSurveyList);

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Read survey list from database (date desc) --> "
					+ surveyList);
			LOGGER.trace("Made survey list (date asc) --> "
					+ revertedSurveyList);
		}
		mav.addObject("surveyList", surveyList);
		mav.addObject("revertedSurveyList", revertedSurveyList);
		return mav;
	}

	/**
	 * Works with ajax. Forms data for drawing charts with survey results.
	 * Writes json into response.
	 * 
	 * @param selectedLocale
	 * @param selectedUsers
	 * @param selectedSurveys
	 * 
	 * @author Alexandra Martyntseva
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/chartData", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String chartsData(Locale selectedLocale,
			@RequestParam(value = "userId") Long[] selectedUsers,
			@RequestParam(value = "surveyId") Long[] selectedSurveys) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("obtained request param - userId: "
					+ Arrays.deepToString(selectedUsers));
			LOGGER.trace("obtained request param - surveyId: "
					+ Arrays.toString(selectedSurveys));
		}

		ChartsData chartsData = dashboardService.readData(selectedUsers,
				selectedSurveys, selectedLocale);

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("obtained data for charts: " + chartsData);
		}
		JSONObject result;
		if (selectedSurveys.length == 1) {
			result = formDataForOneSurvey(chartsData);
		} else {
			result = formDataForSeveralSurveys(chartsData);
		}
		result.put("surveysNumber", selectedSurveys.length);
		result.put("usersNumber", selectedUsers.length);
		return result.toJSONString();
	}

	/**
	 * 
	 * @param chartsData
	 * @return
	 * 
	 * @author Alexandra Martyntseva
	 */
	@SuppressWarnings("unchecked")
	private JSONObject formDataForOneSurvey(ChartsData chartsData) {
		JSONObject result = new JSONObject();
		JSONObject motivationData = new JSONObject();
		JSONArray detailsData = new JSONArray();

		JSONArray satisfArr = new JSONArray();
		JSONArray priorArr = new JSONArray();
		for (Result value : chartsData.getMotivationMap().getResults()) {
			satisfArr.add(value.getSatisfaction());
			priorArr.add(value.getPriority());
		}
		motivationData.put("satisfaction", satisfArr);
		motivationData.put("prioritization", priorArr);

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("formDataForOneSurvey() formed motivationData json: "
					+ motivationData.toJSONString());
		}

		for (DashboardQuestionBean bean : chartsData.getDetails().getResults()) {
			JSONObject piece = new JSONObject();
			piece.put("name", bean.getQuestionName());
			piece.put("satisfaction", bean.getAvgSatisfaction());
			piece.put("prioritization", bean.getAvgPriority());
			detailsData.add(piece);
		}
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("formDataForOneSurvey() formed detailsData json: "
					+ detailsData.toJSONString());
		}
		result.put("motivationData", motivationData);
		result.put("detailsData", detailsData);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("returning json: " + result.toJSONString());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private JSONObject formDataForSeveralSurveys(ChartsData chartsData) {
		JSONObject result = new JSONObject();
		JSONArray motivationData = new JSONArray();
		JSONArray detailsData = new JSONArray();
		JSONObject questions = new JSONObject();

		for (Entry<Survey, SortedMap<Dimension, Result>> entry : chartsData
				.getMotivationMap()) {
			JSONObject motivationDataPiece = new JSONObject();
			JSONArray satisfArr = new JSONArray();
			JSONArray priorArr = new JSONArray();
			for (Result value : entry.getValue().values()) {
				satisfArr.add(value.getSatisfaction());
				priorArr.add(value.getPriority());
			}
			motivationDataPiece.put("satisfaction", satisfArr);
			motivationDataPiece.put("prioritization", priorArr);
			motivationDataPiece.put("surveyId", entry.getKey().getId());
			motivationData.add(motivationDataPiece);
		}
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("formDataForSeveralSurveys() formed motivationData json: "
					+ motivationData.toJSONString());
		}

		for (Entry<Survey, List<DashboardQuestionBean>> entry : chartsData
				.getDetails()) {
			JSONArray surveyData = new JSONArray();
			for (DashboardQuestionBean question : entry.getValue()) {
				questions.put(question.getId().toString(),
						question.getQuestionName());
				JSONObject questionObj = new JSONObject();
				questionObj.put("id", question.getId());
				questionObj.put("satisfaction", question.getAvgSatisfaction());
				questionObj.put("prioritization", question.getAvgPriority());
				surveyData.add(questionObj);
			}
			detailsData.add(surveyData);
		}
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("formDataForSeveralSurveys() formed detailsData json: "
					+ detailsData.toJSONString());
		}

		result.put("motivationData", motivationData);
		result.put("detailsData", detailsData);
		result.put("questions", questions);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("returning json: " + result.toJSONString());
		}
		return result;
	}

	/**
	 * Ajax controller to get subordinate tree of user with result by surveys.
	 * 
	 * @param user
	 *            - RM to find his subordinate
	 * 
	 * @param surveysId
	 *            - surveys id's to find result
	 * 
	 * @return json with subordinate tree
	 * 
	 * @author Mykola Zalyayev
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/surveysUsers", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getUsersBySurvey(
			@ModelAttribute(SessionAttribute.USER) User user,
			@RequestParam("surveysId") Long[] surveysId) {

		List<Unit> unitList = unitService.readSubordinateUnitsByUser(user);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Read units list subbordinate by " + user + " --> "
					+ unitList);
		}

		JSONObject json = new JSONObject();
		try {
			for (Unit unit : unitList) {
				JSONObject jsonUnit = getSubordinate(unit, surveysId);
				if (!jsonUnit.isEmpty()) {
					json.put(unit.getName(), jsonUnit);
				} else {
					json.put("error", true);
				}
			}
		} catch (JSONException e) {
			LOGGER.error("Json error occurred");
		}
		return json.toString();
	}

	/**
	 * Get subordinate by unit
	 * 
	 * @param unit
	 *            - unit to find his subordinate
	 * 
	 * @param surveysId
	 *            - survey to find result
	 * 
	 * @return json with subordinate
	 * 
	 * @throws JSONException
	 * 
	 * @author Mykola Zalyayev
	 */
	@SuppressWarnings("unchecked")
	private JSONObject getSubordinate(Unit unit, Long[] surveysId)
			throws JSONException {
		JSONObject json = new JSONObject();
		List<Unit> unitList = unitService.readSubordinateUnitsByUnit(unit);
		Map<Long, String> usersMap = getUserByUnitWithResult(unit, surveysId);
		if (!usersMap.isEmpty()) {
			json.put("users", usersMap);
		}
		JSONObject jsonUnit = new JSONObject();
		for (Unit buffUnit : unitList) {
			JSONObject buff = getSubordinate(buffUnit, surveysId);
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
	 * Get subordinate users by unit with result by several surveys
	 * 
	 * @param unit
	 *            - unit to find his subordinate
	 * 
	 * @param surveysId
	 *            - surveys id's to find result
	 * 
	 * @return map of users name
	 * 
	 * @author Mykola Zalyayev
	 */
	private Map<Long, String> getUserByUnitWithResult(Unit unit,
			Long[] surveysId) {
		Map<Long, String> usersByUnit = new HashMap<>();
		List<User> usersList = userService.readUsersWithResultOnSurveysByUnit(
				unit.getId(), surveysId);
		for (User u : usersList) {
			usersByUnit.put(u.getId(), u.getName());
		}
		return usersByUnit;
	}
}
