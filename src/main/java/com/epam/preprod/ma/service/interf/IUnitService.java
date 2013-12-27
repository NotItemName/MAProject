package com.epam.preprod.ma.service.interf;

import java.util.List;

import com.epam.preprod.ma.dao.entity.Unit;
import com.epam.preprod.ma.dao.entity.User;
import com.epam.preprod.ma.dao.impl.UnitDao;
import com.epam.preprod.ma.service.IService;

/**
 * Extension of {@linkplain IService} interface for {@linkplain Unit} entity.
 * 
 * @author Oleksandr Lagoda
 * 
 * @version 1.0
 * 
 */
public interface IUnitService extends IService<Unit> {

	/**
	 * Selects a list of all current units id by calling special method in
	 * {@linkplain UnitDao} class.
	 * 
	 * @return A list of all unit IDs in the database.
	 * 
	 * @author Oleksandr Lagoda
	 */
	public List<Unit> readAllUnits();

	/**
	 * Find all units subordinated by specified user by calling special method
	 * in {@linkplain UnitDao} class.
	 * 
	 * @param user
	 *            - user to search.
	 * 
	 * @return List of units.
	 * 
	 * @author Mykola Zalyayev
	 */
	List<Unit> readSubordinateUnitsByUser(User user);

	/**
	 * Find all units subordinated by specified unit by calling special method
	 * in {@linkplain UnitDao} class.
	 * 
	 * @param unit
	 *            - unit to search.
	 * 
	 * @return List of units.
	 * 
	 * @author Mykola Zalyayev
	 */
	List<Unit> readSubordinateUnitsByUnit(Unit unit);

}
