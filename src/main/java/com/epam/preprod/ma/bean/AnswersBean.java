package com.epam.preprod.ma.bean;

import java.util.Map;

import com.epam.preprod.ma.dao.entity.Question;

/**
 * 
 * @author Leonid Polyakov
 * 
 */
public class AnswersBean {
	private Map<Question, Integer> answers;

	public void setAnswers(Map<Question, Integer> answers) {
		this.answers = answers;
	}

	public Map<Question, Integer> getAnswers() {
		return answers;
	}

	@Override
	public String toString() {
		return "AnswersBean [answers=" + answers + "]";
	}
}