package com.epam.preprod.ma.dao.entity;

/**
 * Represents {@linkplain User} role type.
 * 
 * @author Oleksandr Lagoda
 * 
 * @version 1.0
 * 
 */
public enum Role {

	EMPLOYEE("enum.role.employee"), RM("enum.role.rm"), SUPERADMIN(
			"enum.role.superadmin");

	private String key;

	Role(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

}