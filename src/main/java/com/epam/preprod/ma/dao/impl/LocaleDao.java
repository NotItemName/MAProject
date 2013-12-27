package com.epam.preprod.ma.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.epam.preprod.ma.dao.AbstractDao;
import com.epam.preprod.ma.dao.interf.ILocaleDao;
import com.epam.preprod.ma.exception.DaoException;
import com.epam.preprod.ma.service.transaction.ThreadLocalStorage;

/**
 * 
 * @author Alexandra Martyntseva
 * 
 */
@Repository
public class LocaleDao extends AbstractDao<Locale> implements ILocaleDao {

	private static final Logger log = Logger.getLogger(LocaleDao.class);

	private static final String SELECT_ALL_LANGUAGES = "SELECT locale FROM langs";

	private static final String SELECT_LANG_ID_BY_LOCALE = "SELECT lang_id  FROM langs WHERE locale = ?";

	@Override
	public List<Locale> getSupportedLocales() throws DaoException {
		List<Locale> result = new ArrayList<>();
		Connection con = ThreadLocalStorage.getConnection();
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(SELECT_ALL_LANGUAGES);
			while (rs.next()) {
				String lang = rs.getString(1);
				result.add(new Locale(lang));
				log.debug("stored supported language - " + lang);
			}
		} catch (SQLException e) {
			throw new DaoException(e);
		}
		return result;
	}

	@Override
	public Integer readLangIdByLocale(Locale locale) throws DaoException {
		Connection connection = ThreadLocalStorage.getConnection();
		try {
			PreparedStatement prepStatement = connection
					.prepareStatement(SELECT_LANG_ID_BY_LOCALE);
			prepStatement.setString(1, locale.toString());
			prepStatement.executeQuery();
			ResultSet resultSet = prepStatement.getResultSet();
			resultSet.next();
			return resultSet.getInt(1);
		} catch (SQLException e) {
			throw new DaoException(
					"SQLException occured while reading lang id by locale");
		}
	}

	@Override
	protected PreparedStatement prepareCreate(Connection connection,
			Locale entityToCreate) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected PreparedStatement prepareReadById(Connection connection, Long id)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected PreparedStatement prepareUpdate(Connection connection,
			Locale entityToUpdate) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected PreparedStatement prepareDelete(Connection connection, Long id)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Locale getEntityFromResultSet(ResultSet resultSet)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

}
