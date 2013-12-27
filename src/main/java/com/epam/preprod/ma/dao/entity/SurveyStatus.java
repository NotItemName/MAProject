package com.epam.preprod.ma.dao.entity;

/**
 * Enum that represents status of the {@link Survey}
 * 
 * @author Yevhen Lobazov
 * 
 * @version 1.0
 * 
 */
public enum SurveyStatus {

	SCHEDULED("enum.surveyStatus.scheduled"), IN_PROGRESS(
			"enum.surveyStatus.inProgress"), FINISHED(
			"enum.surveyStatus.finished");

	private String key;

	private SurveyStatus(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

}
