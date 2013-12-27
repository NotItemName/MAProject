package com.epam.preprod.ma.controller.validation;

import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.epam.preprod.ma.bean.AnswersBean;
import com.epam.preprod.ma.dao.entity.Question;
import com.epam.preprod.ma.dao.entity.Survey;

public class SatisfactionAnswersValidator implements IAnswersValidator {

	private static final Logger LOGGER = Logger
			.getLogger(SatisfactionAnswersValidator.class);

	@Override
	public boolean validate(AnswersBean bean, Survey survey) {
		// checks questions
		if (survey.getQuestions().size() != bean.getAnswers().size()) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("bean has wrong number of answers: survey questions = "
						+ survey.getQuestions().size()
						+ "; beans answers = "
						+ bean.getAnswers().size());
			}
			return false;
		}
		for (Entry<Question, Integer> entry : bean.getAnswers().entrySet()) {
			if (!survey.getQuestions().contains(entry.getKey())) {
				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace("survey does not contains question from bean: questions = "
							+ entry.getKey());
				}
				return false;
			}
		}

		// checks levels
		for (Integer level : bean.getAnswers().values()) {
			if (level == null || level < 0 || level > 6) {
				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace("user sent wrong level: level = " + level);
				}
				return false;
			}
		}
		return true;
	}

}
