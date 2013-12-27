package com.epam.preprod.ma.dao.interf;

import java.util.List;

import com.epam.preprod.ma.dao.IDao;
import com.epam.preprod.ma.dao.entity.User;
import com.epam.preprod.ma.exception.DaoException;

/**
 * Extension of {@linkplain IDao} interface for {@linkplain User} entity.
 * 
 * @author Oleksandr Lagoda
 * 
 * @version 1.0
 * 
 */
public interface IUserDao extends IDao<User> {

	/**
	 * Selects a list of all current users id.
	 * 
	 * @return A list of all user IDs in the database.
	 * 
	 * @throws DaoException
	 *             if a database access error occurs.
	 * 
	 * @author Oleksandr Lagoda
	 */
	public List<User> readAllUsers() throws DaoException;

	/**
	 * Read all users subordinated by specified unit.
	 * 
	 * @param unitId
	 *            - unit id to search.
	 * 
	 * @return List of user.
	 * 
	 * @throws DaoException
	 *             - if error occurred while reading.
	 * 
	 * @author Mykola Zalyayev
	 */
	List<User> readUsersByUnit(Long unitId) throws DaoException;

	User readUserByLogin(String login) throws DaoException;

}
