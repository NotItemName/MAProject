package com.epam.preprod.ma.dao.interf;

import java.util.List;

import com.epam.preprod.ma.dao.IDao;
import com.epam.preprod.ma.dao.entity.Unit;
import com.epam.preprod.ma.dao.entity.User;
import com.epam.preprod.ma.exception.DaoException;

/**
 * Extension of {@linkplain IDao} interface for {@linkplain Unit} entity.
 * 
 * @author Oleksandr Lagoda
 * 
 * @version 1.0
 * 
 */
public interface IUnitDao extends IDao<Unit> {

	/**
	 * Set new Unit data to 'units' table without rm_id and parent_unit_id
	 * column.
	 * 
	 * @param unit
	 *            - unit to insert.
	 * 
	 * @throws DaoException
	 *             - when database access error occurred.
	 */
	public void createUnitWithoutRMAndParentUnit(Unit unit) throws DaoException;

	/**
	 * Set in 'units' table rm_id and parent_unit_id for concrete unit.
	 * 
	 * @param unit
	 *            - unit to update.
	 * 
	 * @throws DaoException
	 *             - when database access error occurred.
	 */
	public void updateUnitRMAndParentUnit(Unit unit) throws DaoException;

	/**
	 * Selects a list of all current units id.
	 * 
	 * @return A list of all unit IDs in the database.
	 * 
	 * @throws DaoException
	 *             if a database access error occurs.
	 * 
	 * @author Oleksandr Lagoda
	 */
	public List<Unit> readAllUnits() throws DaoException;

	/**
	 * Read all units subordinated by specified user.
	 * 
	 * @param user
	 *            - user to search.
	 * 
	 * @return List of units.
	 * 
	 * @throws DaoException
	 *             - if error occurred while reading.
	 * 
	 * @author Mykola Zalyayev
	 */
	List<Unit> readSubordinateUnitsByUser(User user) throws DaoException;

	/**
	 * Read all units subordinated by specified unit.
	 * 
	 * @param unit
	 *            - unit to search.
	 * 
	 * @return List of units.
	 * 
	 * @throws DaoException
	 *             - if error occurred while reading.
	 * 
	 * @author Mykola Zalyayev
	 */
	List<Unit> readSubordinateUnitsByUnit(Unit unit) throws DaoException;
}
