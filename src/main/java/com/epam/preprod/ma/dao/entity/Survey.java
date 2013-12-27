package com.epam.preprod.ma.dao.entity;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Survey entity.
 * 
 * @author Yevhen Lobazov, Oleksandr Lagoda, Leonid Polyakov
 * 
 * @version 1.0
 * 
 */

public class Survey {

	private Long id;

	private Date startDate;

	private Date endDate;

	private String link;

	private SurveyStatus status;

	private Long rmId;

	private Map<Locale, SurveyText> description;

	private List<Question> questions;

	public Survey() {
	}

	public Survey(Date startDate, Date endDate, String link,
			SurveyStatus status, Long rmId,
			Map<Locale, SurveyText> description, List<Question> questions) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.link = link;
		this.status = status;
		this.rmId = rmId;
		this.description = description;
		this.questions = questions;
	}

	public Survey(Long id, Date startDate, Date endDate, String link,
			SurveyStatus status, Long rmId,
			Map<Locale, SurveyText> description, List<Question> questions) {
		this.id = id;
		this.startDate = startDate;
		this.endDate = endDate;
		this.link = link;
		this.status = status;
		this.rmId = rmId;
		this.description = description;
		this.questions = questions;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public SurveyStatus getStatus() {
		return status;
	}

	public void setStatus(SurveyStatus status) {
		this.status = status;
	}

	public Long getRmId() {
		return rmId;
	}

	public void setRmId(Long rmId) {
		this.rmId = rmId;
	}

	public Map<Locale, SurveyText> getDescription() {
		return description;
	}

	public void setDescription(Map<Locale, SurveyText> description) {
		this.description = description;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Survey other = (Survey) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Survey [id=" + id + ", startDate=" + startDate + ", endDate="
				+ endDate + ", link=" + link + ", status=" + status + ", rmId="
				+ rmId + ", description=" + description + "]";
	}

}