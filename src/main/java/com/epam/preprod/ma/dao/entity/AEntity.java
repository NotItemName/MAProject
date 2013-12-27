package com.epam.preprod.ma.dao.entity;

/**
 * Interface for all entities which has id field.
 * 
 * @author Oleksandr Lagoda
 * 
 */
public abstract class AEntity {

	protected Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
