package com.epam.preprod.ma.bean;

/**
 * 
 * @author Alexandra Martyntseva
 * 
 */
public class DashboardQuestionBean {

	private Long id;

	private String questionName;

	private Integer avgSatisfaction;

	private Integer avgPriority;

	public DashboardQuestionBean() {
	}

	public DashboardQuestionBean(Integer questionId, String questionName,
			Integer avgSatisfaction, Integer avgPriority) {
		super();
		this.questionName = questionName;
		this.avgSatisfaction = avgSatisfaction;
		this.avgPriority = avgPriority;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getQuestionName() {
		return questionName;
	}

	public Integer getAvgSatisfaction() {
		return avgSatisfaction;
	}

	public Integer getAvgPriority() {
		return avgPriority;
	}

	public void setQuestionName(String questionName) {
		this.questionName = questionName;
	}

	public void setAvgSatisfaction(Integer avgSatisfaction) {
		this.avgSatisfaction = avgSatisfaction;
	}

	public void setAvgPriority(Integer avgPriority) {
		this.avgPriority = avgPriority;
	}

	@Override
	public String toString() {
		return "DashboardQuestionBean[ name=" + questionName + ", satisf="
				+ avgSatisfaction + ", prior=" + avgPriority + " ]";
	}

}
