package com.epam.preprod.ma.dao.entity;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * Question entity.
 * 
 * @author Mykola Zalyayev, Oleksandr Lagoda, Leonid Polyakov
 * 
 * @version 1.0
 * 
 */
public class Question {

	private Long id;

	private Dimension dimension;

	private Map<Locale, QuestionText> questionText;

	public Question() {
	}

	public Question(Dimension dimension, Map<Locale, QuestionText> questionText) {
		this.dimension = dimension;
		this.questionText = Collections.unmodifiableMap(questionText);
	}

	public Question(Long id, Dimension dimension,
			Map<Locale, QuestionText> questionText) {
		this.id = id;
		this.dimension = dimension;
		this.questionText = questionText;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Dimension getDimension() {
		return dimension;
	}

	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}

	public Map<Locale, QuestionText> getQuestionText() {
		return questionText;
	}

	public void setQuestionText(Map<Locale, QuestionText> questionText) {
		this.questionText = questionText;
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
		Question other = (Question) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Question [id=" + id + ", dimension=" + dimension
				+ ", questionText=" + questionText + "]";
	}

}
