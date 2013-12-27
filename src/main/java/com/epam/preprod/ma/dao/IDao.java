package com.epam.preprod.ma.dao;

import com.epam.preprod.ma.exception.DaoException;

/**
 * Defines CRUD methods for all DAO classes.
 * 
 * @author Oleksandr Lagoda, Leonid Polyakov
 * 
 * @version 1.0
 * 
 * @param <T>
 *            - entity with which DAO works.
 */
public interface IDao<T> {

	Long create(T entityToCreate) throws DaoException;

	T readById(Long id) throws DaoException;

	void update(T entityToUpdate) throws DaoException;

	void delete(Long id) throws DaoException;

}