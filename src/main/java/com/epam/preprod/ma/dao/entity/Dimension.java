package com.epam.preprod.ma.dao.entity;

/**
 * Represents {@linkplain Question} dimension type.
 * 
 * @author Oleksandr Lagoda
 * 
 * @version 1.0
 * 
 */
public enum Dimension {

	MORALE("enum.dimension.morale"), CAREER("enum.dimension.career"), PROFESSIONAL(
			"enum.dimension.professional"), COMPENSATION(
			"enum.dimension.compensation");

	private String key;

	Dimension(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

}