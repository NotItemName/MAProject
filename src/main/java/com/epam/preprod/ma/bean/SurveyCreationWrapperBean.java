package com.epam.preprod.ma.bean;

import java.util.Map;

import com.epam.preprod.ma.dao.entity.SurveyStatus;

public class SurveyCreationWrapperBean {

	private String link;

	private SurveyStatus surveyStatus;

	private Map<String, String> errors;

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Map<String, String> getErrors() {
		return errors;
	}

	public void setErrors(Map<String, String> errors) {
		this.errors = errors;
	}

	public SurveyStatus getSurveyStatus() {
		return surveyStatus;
	}

	public void setSurveyStatus(SurveyStatus surveyStatus) {
		this.surveyStatus = surveyStatus;
	}
}