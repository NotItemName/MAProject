package com.epam.preprod.ma.controller.validation;

import java.util.Map.Entry;

import com.epam.preprod.ma.bean.AnswersBean;
import com.epam.preprod.ma.dao.entity.Question;
import com.epam.preprod.ma.dao.entity.Survey;

/**
 * @author Leonid Polyakov
 */
public class PrioritizationAnswersValidator implements IAnswersValidator {

	@Override
	public boolean validate(AnswersBean bean, Survey survey) {
		// checks questions
		if (survey.getQuestions().size() != bean.getAnswers().size()) {
			return false;
		}
		for (Entry<Question, Integer> entry : bean.getAnswers().entrySet()) {
			if (!survey.getQuestions().contains(entry.getKey())) {
				return false;
			}
		}

		// checks levels and budget
		int maxCoinsCount = bean.getAnswers().size() * 3;
		int realCoinsCount = 0;
		for (Integer level : bean.getAnswers().values()) {
			if (level == null || level < 0 || level > 6) {
				return false;
			}
			realCoinsCount += level;
		}
		if (realCoinsCount > maxCoinsCount) {
			return false;
		}
		return true;
	}

}
