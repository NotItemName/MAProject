package com.epam.preprod.ma.dao.entity;

/**
 * Result (or answer) entity. Does not implement hashCode and equals since it
 * has not it's own id.
 * 
 * @author Leonid Polyakov
 * 
 * @version 1.0
 * 
 */
public class Result {
	private User user;
	private Question question;
	private Survey survey;
	private Integer satisfaction;
	private Integer priority;

	public Result() {
	}

	public Result(User user, Question question, Survey survey,
			Integer satisfaction, Integer priority) {
		this.user = user;
		this.question = question;
		this.survey = survey;
		this.satisfaction = satisfaction;
		this.priority = priority;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public Survey getSurvey() {
		return survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	public Integer getSatisfaction() {
		return satisfaction;
	}

	public void setSatisfaction(Integer satisfaction) {
		this.satisfaction = satisfaction;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	@Override
	public String toString() {
		return "Result [user=" + user + ", question=" + question + ", survey="
				+ survey + ", satisfaction=" + satisfaction + ", priority="
				+ priority + "]";
	}
}