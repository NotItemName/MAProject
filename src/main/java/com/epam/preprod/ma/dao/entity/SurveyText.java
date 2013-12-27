package com.epam.preprod.ma.dao.entity;

/**
 * 
 * Entity that contains description of {@link Survey}
 * 
 * @author Yevhen Lobazov , Oleksandr Lagoda
 * 
 * @version 1.0
 */
public class SurveyText {

	private String name;

	private String satisfaction;

	private String prioritization;

	public SurveyText() {
	}

	public SurveyText(String name, String satisfaction, String prioritization) {
		this.name = name;
		this.satisfaction = satisfaction;
		this.prioritization = prioritization;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSatisfaction() {
		return satisfaction;
	}

	public void setSatisfaction(String satisfaction) {
		this.satisfaction = satisfaction;
	}

	public String getPrioritization() {
		return prioritization;
	}

	public void setPrioritization(String prioritization) {
		this.prioritization = prioritization;
	}

	@Override
	public String toString() {
		return "SurveyText [name=" + name + ", satisfaction=" + satisfaction
				+ ", prioritization=" + prioritization + "]";
	}
}