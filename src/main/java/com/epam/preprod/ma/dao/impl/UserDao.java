package com.epam.preprod.ma.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.epam.preprod.ma.dao.AbstractDao;
import com.epam.preprod.ma.dao.entity.Role;
import com.epam.preprod.ma.dao.entity.Unit;
import com.epam.preprod.ma.dao.entity.User;
import com.epam.preprod.ma.dao.interf.IUserDao;
import com.epam.preprod.ma.exception.DaoException;
import com.epam.preprod.ma.service.transaction.ThreadLocalStorage;

/**
 * Implements {@linkplain IUserDao} interface.
 * 
 * @author Oleksandr Lagoda
 * 
 * @version 1.0
 * 
 */
@Repository
public class UserDao extends AbstractDao<User> implements IUserDao {

	private static final Logger LOGGER = Logger.getLogger(UserDao.class);

	private static final String CREATE_USER = "INSERT INTO users (user_id, name, role, email, unit_id, deleted)"
			+ " VALUE (?, ?, ?, ?, ?, ?)";

	private final String UPDATE_USER = "UPDATE users SET users.name=?,users.role=?,"
			+ "users.email=?,users.unit_id=?,deleted=? WHERE users.user_id=?";

	private static final String SELECT_USERS = "SELECT user_id,name,role,email,unit_id,deleted"
			+ " FROM users";

	private final String READ_USER_BY_ID = "SELECT user_id, name, role, email, unit_id "
			+ "FROM users WHERE user_id = ? AND deleted = false";

	private final String READ_USERS_BY_UNIT = "SELECT user_id, name, role, email, unit_id "
			+ "FROM users WHERE unit_id = ? AND deleted = false";

	private final String READ_USER_BY_LOGIN = "SELECT user_id, name, role, email "
			+ "FROM users WHERE name = ? AND deleted = false";

	@Override
	public List<User> readAllUsers() throws DaoException {
		Connection conn = ThreadLocalStorage.getConnection();
		List<User> usersId = new ArrayList<>();

		try {
			PreparedStatement prStatement = conn.prepareStatement(SELECT_USERS);

			ResultSet resultSet = prStatement.executeQuery();

			Integer columnIndex = 0;

			while (resultSet.next()) {
				columnIndex = 1;
				User user = new User();
				Unit unit = new Unit();

				user.setId(resultSet.getLong(columnIndex++));
				user.setName(resultSet.getString(columnIndex++));

				String role = resultSet.getString(columnIndex++);
				if (StringUtils.isNotBlank(role)) {
					user.setRole(Role.valueOf(role));
				}

				user.setEmail(resultSet.getString(columnIndex++));

				unit.setId(resultSet.getLong(columnIndex++));
				user.setUnit(unit);

				user.setDeleted(resultSet.getBoolean(columnIndex++));

				usersId.add(user);
			}
		} catch (SQLException e) {
			String message = "Error ocured while reading database.";
			LOGGER.error(message, e);
			throw new DaoException(message);
		}

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Users id received: " + usersId);
		}

		return usersId;
	}

	@Override
	public List<User> readUsersByUnit(Long unitId) throws DaoException {
		Connection connection = ThreadLocalStorage.getConnection();
		try {
			PreparedStatement prepStatement = connection
					.prepareStatement(READ_USERS_BY_UNIT);
			prepStatement.setLong(1, unitId);
			ResultSet resultSet = prepStatement.executeQuery();
			return getEntitiesFromResultSet(resultSet);
		} catch (SQLException e) {
			throw new DaoException(
					"SQL exception occured while reading subordinate users", e);
		}
	}

	@Override
	protected PreparedStatement prepareCreate(Connection connection,
			User entityToCreate) throws SQLException {

		PreparedStatement preparedStatement = connection.prepareStatement(
				CREATE_USER, PreparedStatement.RETURN_GENERATED_KEYS);
		Integer parameterIndex = 1;

		preparedStatement.setLong(parameterIndex++, entityToCreate.getId());
		preparedStatement.setString(parameterIndex++, entityToCreate.getName());

		String role = getRoleName(entityToCreate.getRole());
		preparedStatement.setString(parameterIndex++, role);

		preparedStatement
				.setString(parameterIndex++, entityToCreate.getEmail());

		Long unitId = getUnitId(entityToCreate.getUnit());
		setUnitIdParameter(preparedStatement, parameterIndex++, unitId);

		preparedStatement.setBoolean(parameterIndex++,
				entityToCreate.isDeleted());

		return preparedStatement;

	}

	@Override
	protected PreparedStatement prepareUpdate(Connection connection,
			User entityToUpdate) throws SQLException {

		PreparedStatement preparedStatement = connection
				.prepareStatement(UPDATE_USER);
		Integer parameterIndex = 1;

		preparedStatement.setString(parameterIndex++, entityToUpdate.getName());

		String role = getRoleName(entityToUpdate.getRole());
		preparedStatement.setString(parameterIndex++, role);

		preparedStatement
				.setString(parameterIndex++, entityToUpdate.getEmail());

		Long unitId = getUnitId(entityToUpdate.getUnit());
		setUnitIdParameter(preparedStatement, parameterIndex++, unitId);

		preparedStatement.setBoolean(parameterIndex++,
				entityToUpdate.isDeleted());
		preparedStatement.setLong(parameterIndex++, entityToUpdate.getId());

		return preparedStatement;
	}

	@Override
	protected User getEntityFromResultSet(ResultSet resultSet)
			throws SQLException {
		User user = new User();
		user.setId(resultSet.getLong(1));
		user.setName(resultSet.getString(2));
		user.setRole(Role.valueOf(resultSet.getString(3)));
		user.setEmail(resultSet.getString(4));
		return user;
	}

	@Override
	protected PreparedStatement prepareDelete(Connection connection, Long id)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Get string representation of {@linkplain Role}.
	 * 
	 * @param role
	 *            - object from which we get the name.
	 * 
	 * @return Name of role or null if received object is null.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private String getRoleName(Role role) {
		if (role != null) {
			return role.name();
		}

		return null;
	}

	/**
	 * Get id from {@linkplain Unit}.
	 * 
	 * @param unit
	 *            - object from which get id.
	 * 
	 * @return Id of unit or null if received object is null.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private Long getUnitId(Unit unit) {
		if (unit != null) {
			return unit.getId();
		}

		return null;
	}

	/**
	 * Set parameter value or null to {@linkplain PreparedStatement}.
	 * 
	 * @param preparedStatement
	 *            - previously defined {@linkplain PreparedStatement}.
	 * @param unitId
	 *            - id to set.
	 * @param parameterIndex
	 *            - parameter index in preparedStatement. -
	 * @throws SQLException
	 *             - when database access occurred.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private void setUnitIdParameter(PreparedStatement preparedStatement,
			Integer parameterIndex, Long unitId) throws SQLException {
		if (unitId == null) {
			preparedStatement.setNull(parameterIndex, Types.BIGINT);
		} else {
			preparedStatement.setLong(parameterIndex, unitId);
		}
	}

	@Override
	protected PreparedStatement prepareReadById(Connection connection, Long id)
			throws SQLException {
		PreparedStatement preparedStatement = connection
				.prepareStatement(READ_USER_BY_ID);
		preparedStatement.setLong(1, id);
		return preparedStatement;
	}

	@Override
	public User readUserByLogin(String login) throws DaoException {

		Connection connection = ThreadLocalStorage.getConnection();
		try {
			PreparedStatement prepStatement = connection
					.prepareStatement(READ_USER_BY_LOGIN);
			prepStatement.setString(1, login);
			ResultSet resultSet = prepStatement.executeQuery();
			if (resultSet.next()) {
				return getEntityFromResultSet(resultSet);
			}
		} catch (SQLException e) {
			throw new DaoException("SQL exception occured while reading user",
					e);
		}
		return null;

	}

}
