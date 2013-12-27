package com.epam.preprod.ma.service.interf;

import java.util.Map;

import com.epam.preprod.ma.constant.EMarker;
import com.epam.preprod.ma.dao.entity.Unit;
import com.epam.preprod.ma.dao.entity.User;
import com.epam.preprod.ma.dao.interf.IUnitDao;
import com.epam.preprod.ma.dao.interf.IUserDao;

/**
 * Interface to update database. Use {@linkplain IUserDao} and
 * {@linkplain IUnitDao}.
 * 
 * @author Oleksandr Lagoda
 * 
 * @version 1.0
 * 
 */
public interface IUpdaterService {

	/**
	 * Set new data to database.
	 * 
	 * @param units
	 *            - data to fill 'units' table.
	 * @param users
	 *            - data to fill 'users' table.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	public void update(Map<User, EMarker> users, Map<Unit, EMarker> units);

}