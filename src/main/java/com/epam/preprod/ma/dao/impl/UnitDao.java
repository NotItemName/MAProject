package com.epam.preprod.ma.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.epam.preprod.ma.dao.AbstractDao;
import com.epam.preprod.ma.dao.entity.AEntity;
import com.epam.preprod.ma.dao.entity.Unit;
import com.epam.preprod.ma.dao.entity.User;
import com.epam.preprod.ma.dao.interf.IUnitDao;
import com.epam.preprod.ma.exception.DaoException;
import com.epam.preprod.ma.service.transaction.ThreadLocalStorage;

/**
 * Implements {@linkplain IUnitDao} interface.
 * 
 * @author Oleksandr Lagoda
 * 
 * @version 1.0
 * 
 */
@Repository
public class UnitDao extends AbstractDao<Unit> implements IUnitDao {

	private static final Logger LOGGER = Logger.getLogger(UnitDao.class);

	private static final String CREATE_UNIT_WITHOUT_RM_AND_PARENT_UNIT = "INSERT INTO units (unit_id, name, deleted)"
			+ " VALUE (?, ?, ?)";

	private static final String UPDATE_UNIT = "UPDATE units SET units.name=?,units.parent_unit_id=?"
			+ ",units.rm_id=?,units.deleted=? WHERE units.unit_id=?";

	private static final String UPDATE_UNIT_RM_AND_PARENT_UNIT = "UPDATE units SET units.rm_id=?,"
			+ "units.parent_unit_id=? WHERE units.unit_id=?";

	private static final String SELECT_UNITS = "SELECT unit_id,name,parent_unit_id,"
			+ "rm_id,deleted FROM units";

	private static final String SELECT_SUBORDINATE_UNIT_BY_USER = "SELECT unit_id, name, parent_unit_id, rm_id "
			+ "FROM units WHERE rm_id=? AND deleted=false";

	private static final String SELECT_SUBORDINATED_UNIT_BY_UNIT = "SELECT unit_id, name, parent_unit_id, rm_id "
			+ "FROM units WHERE parent_unit_id=? AND deleted=false";

	@Override
	public void createUnitWithoutRMAndParentUnit(Unit unit) throws DaoException {
		Connection connection = ThreadLocalStorage.getConnection();

		try {
			PreparedStatement prepStatement = connection
					.prepareStatement(CREATE_UNIT_WITHOUT_RM_AND_PARENT_UNIT);
			prepStatement.setLong(1, unit.getId());
			prepStatement.setString(2, unit.getName());
			prepStatement.setBoolean(3, unit.isDeleted());
			prepStatement.executeUpdate();

		} catch (SQLException e) {
			throw new DaoException("SQL exception occured while create unit", e);
		}
	}

	@Override
	public void updateUnitRMAndParentUnit(Unit unit) throws DaoException {
		Connection connection = ThreadLocalStorage.getConnection();

		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement(UPDATE_UNIT_RM_AND_PARENT_UNIT);
			Integer parameterIndex = 1;

			Long rmId = getId(unit.getRm());
			setIdParameter(preparedStatement, parameterIndex++, rmId);

			Long parentUnitId = getId(unit.getParent());
			setIdParameter(preparedStatement, parameterIndex++, parentUnitId);

			preparedStatement.setLong(parameterIndex++, unit.getId());
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			throw new DaoException("SQL exception occured while create unit", e);
		}
	}

	@Override
	public List<Unit> readSubordinateUnitsByUser(User user) throws DaoException {
		Connection connection = ThreadLocalStorage.getConnection();
		try {
			PreparedStatement prepStatement = connection
					.prepareStatement(SELECT_SUBORDINATE_UNIT_BY_USER);
			prepStatement.setLong(1, user.getId());
			ResultSet resultSet = prepStatement.executeQuery();
			List<Unit> unitList = new ArrayList<>();
			while (resultSet.next()) {
				Unit buff = getEntityFromResultSet(resultSet);
				buff.setRm(user);
				unitList.add(buff);
			}
			return unitList;
		} catch (SQLException e) {
			throw new DaoException(
					"SQL exception occured while reading subordinate units", e);
		}
	}

	@Override
	public List<Unit> readAllUnits() throws DaoException {
		Connection conn = ThreadLocalStorage.getConnection();
		List<Unit> unitsId = new ArrayList<>();

		try {
			PreparedStatement prStatement = conn.prepareStatement(SELECT_UNITS);

			ResultSet rs = prStatement.executeQuery();

			Integer columnIndex = 0;

			while (rs.next()) {

				columnIndex = 1;
				Unit currentUnit = new Unit();
				Unit parentUnit = new Unit();
				User user = new User();

				currentUnit.setId(rs.getLong(columnIndex++));
				currentUnit.setName(rs.getString(columnIndex++));

				parentUnit.setId(rs.getLong(columnIndex++));
				currentUnit.setParent(parentUnit);

				user.setId(rs.getLong(columnIndex++));
				currentUnit.setRm(user);

				currentUnit.setDeleted(rs.getBoolean(columnIndex++));

				unitsId.add(currentUnit);
			}
		} catch (SQLException e) {
			String message = "Error ocured while reading database.";
			LOGGER.error(message, e);
			throw new DaoException(message);
		}

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Units id received: " + unitsId);
		}

		return unitsId;
	}

	@Override
	protected PreparedStatement prepareCreate(Connection connection,
			Unit entityToCreate) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected PreparedStatement prepareReadById(Connection connection, Long id)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected PreparedStatement prepareUpdate(Connection connection,
			Unit entityToUpdate) throws SQLException {

		PreparedStatement preparedStatement = connection
				.prepareStatement(UPDATE_UNIT);
		Integer parameterIndex = 1;

		preparedStatement.setString(parameterIndex++, entityToUpdate.getName());

		Long parentUnitId = getId(entityToUpdate.getParent());
		setIdParameter(preparedStatement, parameterIndex++, parentUnitId);

		Long rmId = getId(entityToUpdate.getRm());
		setIdParameter(preparedStatement, parameterIndex++, rmId);

		preparedStatement.setBoolean(parameterIndex++,
				entityToUpdate.isDeleted());
		preparedStatement.setLong(parameterIndex++, entityToUpdate.getId());

		return preparedStatement;
	}

	@Override
	protected PreparedStatement prepareDelete(Connection connection, Long id)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Unit getEntityFromResultSet(ResultSet resultSet)
			throws SQLException {
		Unit unit = new Unit();
		unit.setId(resultSet.getLong(1));
		unit.setName(resultSet.getString(2));
		return unit;
	}

	@Override
	public List<Unit> readSubordinateUnitsByUnit(Unit unit) throws DaoException {
		Connection connection = ThreadLocalStorage.getConnection();
		try {
			PreparedStatement prepStatement = connection
					.prepareStatement(SELECT_SUBORDINATED_UNIT_BY_UNIT);
			prepStatement.setLong(1, unit.getId());
			ResultSet resultSet = prepStatement.executeQuery();
			List<Unit> unitList = new ArrayList<>();
			while (resultSet.next()) {
				Unit buff = getEntityFromResultSet(resultSet);
				buff.setParent(unit);
				unitList.add(buff);
			}
			return unitList;
		} catch (SQLException e) {
			throw new DaoException(
					"SQL exception occured while reading subordinate units", e);
		}
	}

	/**
	 * Get id from received object by calling getId() method through reflective
	 * operation.
	 * 
	 * @param entity
	 *            - object from which get id.
	 * 
	 * @return Id of object or null if received object is null.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private Long getId(AEntity entity) {
		if (entity != null) {
			return entity.getId();
		}

		return null;
	}

	/**
	 * Set parameter value or null to {@linkplain PreparedStatement}.
	 * 
	 * @param preparedStatement
	 *            - previously defined {@linkplain PreparedStatement}.
	 * @param id
	 *            - id to set.
	 * @param parameterIndex
	 *            - parameter index in preparedStatement. -
	 * @throws SQLException
	 *             - when database access occurred.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private void setIdParameter(PreparedStatement preparedStatement,
			Integer parameterIndex, Long id) throws SQLException {
		if (id == null) {
			preparedStatement.setNull(parameterIndex, Types.BIGINT);
		} else {
			preparedStatement.setLong(parameterIndex, id);
		}
	}
}
