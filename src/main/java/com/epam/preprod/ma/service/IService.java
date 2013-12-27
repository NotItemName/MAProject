package com.epam.preprod.ma.service;

import com.epam.preprod.ma.exception.ServiceException;

/**
 * Defines CRUD methods for all Service classes.
 * 
 * @author Oleksandr Lagoda, Leonid Polyakov
 * 
 * @version 1.0
 * 
 * @param <T>
 *            - entity with which Service works.
 */
public interface IService<T> {

	Long create(T newEntity) throws ServiceException;

	T readById(Long id) throws ServiceException;

	void update(T entityToUpdate) throws ServiceException;

	void delete(Long id) throws ServiceException;

}