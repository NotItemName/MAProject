package com.epam.preprod.ma.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.epam.preprod.ma.exception.DaoException;
import com.epam.preprod.ma.service.transaction.ThreadLocalStorage;

/**
 * Simplifies the use of {@linkplain IDao} interface.
 * 
 * @author Leonid Polyakov, Yevhen Lobazov, Oleksandr Lagoda
 * 
 * @version 1.0
 * 
 * @param <T>
 *            - entity with which DAO works.
 */
public abstract class AbstractDao<T> implements IDao<T> {

	protected abstract PreparedStatement prepareCreate(Connection connection,
			T entityToCreate) throws SQLException;

	protected abstract PreparedStatement prepareReadById(Connection connection,
			Long id) throws SQLException;

	protected abstract PreparedStatement prepareUpdate(Connection connection,
			T entityToUpdate) throws SQLException;

	protected abstract PreparedStatement prepareDelete(Connection connection,
			Long id) throws SQLException;

	/**
	 * WARN: Do not invoke method next() on ResultSet!
	 * 
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	protected abstract T getEntityFromResultSet(ResultSet resultSet)
			throws SQLException;

	@Override
	public Long create(T entityToCreate) throws DaoException {
		Connection connection = ThreadLocalStorage.getConnection();
		try {
			PreparedStatement prepStatement = prepareCreate(connection,
					entityToCreate);
			prepStatement.executeUpdate();
			ResultSet generatedKey = prepStatement.getGeneratedKeys();
			if (generatedKey.next()) {
				return generatedKey.getLong(1);
			} else {
				return null;
			}
		} catch (SQLException e) {
			throw new DaoException("SQL exception occured while inserting", e);
		}
	}

	@Override
	public T readById(Long id) throws DaoException {
		Connection connection = ThreadLocalStorage.getConnection();
		try {
			PreparedStatement prepStatement = prepareReadById(connection, id);
			prepStatement.executeQuery();
			ResultSet resultSet = prepStatement.getResultSet();
			resultSet.next();
			return getEntityFromResultSet(resultSet);
		} catch (SQLException e) {
			throw new DaoException("SQL exception occured while reading", e);
		}
	}

	@Override
	public void update(T entityToUpdate) throws DaoException {
		Connection connection = ThreadLocalStorage.getConnection();
		try {
			PreparedStatement prepStatement = prepareUpdate(connection,
					entityToUpdate);
			prepStatement.executeUpdate();
			if (prepStatement.getUpdateCount() != 1) {
				throw new DaoException(
						"Failed to update single row. Number of affected rows: "
								+ prepStatement.getUpdateCount());
			}
		} catch (SQLException e) {
			throw new DaoException("SQL exception occured while updating", e);
		}
	}

	@Override
	public void delete(Long id) throws DaoException {
		Connection connection = ThreadLocalStorage.getConnection();
		try {
			PreparedStatement prepStatement = prepareDelete(connection, id);
			prepStatement.executeUpdate();
			if (prepStatement.getUpdateCount() != 1) {
				throw new DaoException(
						"Failed to delete single row. Number of affected rows: "
								+ prepStatement.getUpdateCount());
			}

		} catch (SQLException e) {
			throw new DaoException("SQL exception occured while deleting", e);
		}
	}

	protected List<T> getEntitiesFromResultSet(ResultSet resultSet)
			throws DaoException {
		try {
			List<T> list = new ArrayList<>();
			while (resultSet.next()) {
				list.add(getEntityFromResultSet(resultSet));
			}
			return list;
		} catch (SQLException e) {
			throw new DaoException("Can't navigate on result set", e);
		}
	}

}
