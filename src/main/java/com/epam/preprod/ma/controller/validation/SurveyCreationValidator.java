package com.epam.preprod.ma.controller.validation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.epam.preprod.ma.bean.SurveyCreationBean;
import com.epam.preprod.ma.dao.entity.Question;
import com.epam.preprod.ma.dao.entity.SurveyText;

public class SurveyCreationValidator {

	private static final String DATE_REGEX_PATTERN = "20\\d\\d-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])";

	public static final String DATE_PATTERN = "yyyy-MM-dd";

	public static final String ERROR_DATE_INTERVAL = "dateInterval";

	public static final String ERROR_START_DATE = "surveyStartDate";

	public static final String ERROR_END_DATE = "surveyEndDate";

	public static final String ERROR_SURVEY_NAME = "surveyName";

	public static final String ERROR_SURVEY_SATISFACTION = "surveySatisfactionDescription";

	public static final String ERROR_SURVEY_PRIORITIZATION = "surveyPrioritizationDescription";

	public static final String ERROR_QUESTION_LIST = "questionList";

	public static final String MSG_START_DATE_INVALID = "start_date_invalid";

	public static final String MSG_END_DATE_INVALID = "end_date_invalid";

	public static final String MSG_NAME_INVALID = "name_invalid";

	public static final String MSG_SATISFACTION_INVALID = "satisfaction_invalid";

	public static final String MSG_PRIORITIZATION_INVALID = "prioritization_invalid";

	public static final String MSG_DATE_INTERVAL_INVALID = "date_interval_invalid";

	public static final String MSG_EMPTY_QUESTION_LIST = "empty_question_list";

	public static Map<String, String> validate(SurveyCreationBean bean,
			List<Question> questions) {

		Map<String, String> errors = new HashMap<>();
		String startStr = bean.getStartDate();
		String endStr = bean.getEndDate();
		checkDescription(errors, bean.getDescription());
		checkDate(errors, startStr, endStr);
		checkQuestions(errors, questions);
		return errors;
	}

	private static void checkDescription(Map<String, String> errors,
			Map<Locale, SurveyText> description) {
		for (Map.Entry<Locale, SurveyText> entry : description.entrySet()) {
			String name = entry.getValue().getName();
			String satisfaction = entry.getValue().getSatisfaction();
			String prioritezation = entry.getValue().getPrioritization();
			if (StringUtils.isBlank(name)) {
				errors.put(ERROR_SURVEY_NAME + entry.getKey(), MSG_NAME_INVALID);
			}
			if (StringUtils.isBlank(satisfaction)) {
				errors.put(ERROR_SURVEY_SATISFACTION + entry.getKey(),
						MSG_SATISFACTION_INVALID);
			}
			if (StringUtils.isBlank(prioritezation)) {
				errors.put(ERROR_SURVEY_PRIORITIZATION + entry.getKey(),
						MSG_PRIORITIZATION_INVALID);
			}
		}
	}

	private static void checkDate(Map<String, String> errors, String startStr,
			String endStr) {
		if (StringUtils.isBlank(startStr)
				|| !startStr.matches(DATE_REGEX_PATTERN)) {
			errors.put(ERROR_START_DATE, MSG_START_DATE_INVALID);
		}
		if (StringUtils.isBlank(endStr) || !endStr.matches(DATE_REGEX_PATTERN)) {
			errors.put(ERROR_END_DATE, MSG_END_DATE_INVALID);
		}
		Date startDate = null;
		Date endDate = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
		dateFormat.setLenient(false);
		try {
			startDate = dateFormat.parse(startStr);
		} catch (ParseException e) {
			errors.put(ERROR_START_DATE, MSG_START_DATE_INVALID);
		}
		try {
			endDate = dateFormat.parse(endStr);
		} catch (ParseException e) {
			errors.put(ERROR_END_DATE, MSG_END_DATE_INVALID);
		}
		if (errors.containsKey(ERROR_START_DATE)
				|| errors.containsKey(ERROR_END_DATE)) {
			return;
		}
		checkDateInterval(errors, startDate, endDate);
	}

	private static void checkDateInterval(Map<String, String> errors,
			Date startDate, Date endDate) {
		Calendar currentDay = Calendar.getInstance();
		Calendar startDay = Calendar.getInstance();
		Calendar endDay = Calendar.getInstance();
		startDay.setTime(startDate);
		endDay.setTime(endDate);
		currentDay.add(Calendar.DAY_OF_MONTH, -1);
		if (startDay.before(currentDay)) {
			errors.put(ERROR_START_DATE, MSG_START_DATE_INVALID);
		}
		if (endDay.before(currentDay)) {
			errors.put(ERROR_END_DATE, MSG_END_DATE_INVALID);
		}
		if (endDay.before(startDay)) {
			errors.put(ERROR_DATE_INTERVAL, MSG_DATE_INTERVAL_INVALID);
		}
	}

	private static void checkQuestions(Map<String, String> errors,
			List<Question> questions) {
		if (questions == null || questions.isEmpty()) {
			errors.put(ERROR_QUESTION_LIST, MSG_EMPTY_QUESTION_LIST);
		}
	}
}
