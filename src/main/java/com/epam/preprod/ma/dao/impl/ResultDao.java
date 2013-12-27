package com.epam.preprod.ma.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import com.epam.preprod.ma.dao.AbstractDao;
import com.epam.preprod.ma.dao.entity.Result;
import com.epam.preprod.ma.dao.entity.Survey;
import com.epam.preprod.ma.dao.entity.User;
import com.epam.preprod.ma.dao.interf.IResultDao;
import com.epam.preprod.ma.exception.DaoException;
import com.epam.preprod.ma.service.transaction.ThreadLocalStorage;

/**
 * Implements {@linkplain IResultDao} interface.
 * 
 * @author Leonid Polyakov
 * 
 * @version 1.0
 * 
 */
@Repository
public class ResultDao extends AbstractDao<Result> implements IResultDao {

	private static final String INSERT_RESULT = "insert into results values(?, ?, ?, ?, ?)";

	private static final String COUNT_RESULTS = "select count(*) from results where user_id = ? and survey_id = ?";

	/**
	 * @author Leonid Polyakov
	 */
	@Override
	protected PreparedStatement prepareCreate(Connection connection,
			Result entityToCreate) throws SQLException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @author Leonid Polyakov
	 */
	@Override
	public Long create(Result result) throws DaoException {
		Connection connection = ThreadLocalStorage.getConnection();
		try {
			PreparedStatement statement = connection
					.prepareStatement(INSERT_RESULT);
			int index = 1;
			statement.setLong(index++, result.getUser().getId());
			statement.setLong(index++, result.getQuestion().getId());
			statement.setLong(index++, result.getSurvey().getId());
			statement.setInt(index++, result.getSatisfaction());
			statement.setInt(index++, result.getPriority());
			statement.executeUpdate();
			if (statement.getUpdateCount() != 1) {
				throw new DaoException("Failed to insert result: " + result);
			}
			return null;
		} catch (SQLException e) {
			throw new DaoException("SQL exception occured while inserting", e);
		}
	}

	@Override
	protected PreparedStatement prepareReadById(Connection connection, Long id)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected PreparedStatement prepareUpdate(Connection connection,
			Result entityToUpdate) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected PreparedStatement prepareDelete(Connection connection, Long id)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Result getEntityFromResultSet(ResultSet resultSet)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer countResultsOnSurvey(User user, Survey survey)
			throws DaoException {
		Connection connection = ThreadLocalStorage.getConnection();
		try {
			PreparedStatement statement = connection
					.prepareStatement(COUNT_RESULTS);
			int index = 1;
			statement.setLong(index++, user.getId());
			statement.setLong(index++, survey.getId());
			statement.executeQuery();
			ResultSet result = statement.getResultSet();
			result.next();
			return result.getInt(1);
		} catch (SQLException e) {
			throw new DaoException("SQL exception occured while counting", e);
		}
	}
}
