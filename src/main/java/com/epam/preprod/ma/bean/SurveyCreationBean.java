package com.epam.preprod.ma.bean;

import java.util.Locale;
import java.util.Map;

import com.epam.preprod.ma.dao.entity.SurveyText;

public class SurveyCreationBean {

	private String startDate;

	private String endDate;

	private Map<Locale, SurveyText> description;

	public SurveyCreationBean(String startDate, String endDate,
			Map<Locale, SurveyText> description) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
		this.description = description;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Map<Locale, SurveyText> getDescription() {
		return description;
	}

	public void setDescription(Map<Locale, SurveyText> description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
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
		SurveyCreationBean other = (SurveyCreationBean) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}

}
