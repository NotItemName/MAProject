package com.epam.preprod.ma.dao.entity;

/**
 * Question text entity.
 * 
 * @author Mykola Zalyayev, Leonid Polyakov
 * 
 * @version 1.0
 * 
 */
public class QuestionText {

	private String name;

	private String satisfactionText;

	private String prioritizationText;

	public QuestionText() {
		super();
	}

	public QuestionText(String name, String satisfactionText,
			String priorityText) {
		super();
		this.name = name;
		this.satisfactionText = satisfactionText;
		this.prioritizationText = priorityText;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSatisfactionText() {
		return satisfactionText;
	}

	public void setSatisfactionText(String satisfactionText) {
		this.satisfactionText = satisfactionText;
	}

	public String getPrioritizationText() {
		return prioritizationText;
	}

	public void setPrioritizationText(String prioritizationText) {
		this.prioritizationText = prioritizationText;
	}

	@Override
	public String toString() {
		return "QuestionText [name=" + name + ", satisfactionText="
				+ satisfactionText + ", priorityText=" + prioritizationText
				+ "]";
	}

}