package com.epam.preprod.ma.service.interf;

import java.util.List;

import com.epam.preprod.ma.dao.entity.User;
import com.epam.preprod.ma.dao.impl.ResultDao;
import com.epam.preprod.ma.dao.impl.UserDao;
import com.epam.preprod.ma.service.IService;

/**
 * Extension of {@linkplain IService} interface for {@linkplain User} entity.
 * 
 * @author Oleksandr Lagoda
 * 
 * @version 1.0
 * 
 */
public interface IUserService {

	/**
	 * Selects a list of all current users id by calling special method in
	 * {@linkplain UserDao} class.
	 * 
	 * @return A list of all user IDs in the database.
	 * 
	 * @author Oleksandr Lagoda
	 */
	public List<User> readAllUsers();

	/**
	 * Find all users by unit that have results by at least one survey by
	 * calling special method in {@linkplain UserDao} and {@linkplain ResultDao}
	 * classes.
	 * 
	 * @param unitId
	 *            - unit id to search.
	 * 
	 * @param surveysId
	 *            - list of surveys id to find result.
	 * 
	 * @return List of users.
	 * 
	 * @author Mykola Zalyayev
	 */
	List<User> readUsersWithResultOnSurveysByUnit(Long unitId, Long[] surveysId);

	User readUserByLogin(String login);

	/**
	 * Find all users by unit by calling special method in {@linkplain UserDao}
	 * and {@linkplain ResultDao}
	 * 
	 * @param unitId
	 *            - unit id to search
	 * 
	 * @return List of users
	 * 
	 * @author Mykola Zalyayev
	 */
	List<User> readUsersByUnit(Long unitId);

	/**
	 * Find list of users by their id's
	 * 
	 * @param usersId
	 *            - array of users id's
	 * 
	 * @return list of users
	 * 
	 * @author Mykola Zalyayev
	 */
	List<User> readUsersByIds(Long[] usersId);

}
