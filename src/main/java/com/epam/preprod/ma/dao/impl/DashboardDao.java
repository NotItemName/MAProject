package com.epam.preprod.ma.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.epam.preprod.ma.bean.DashboardQuestionBean;
import com.epam.preprod.ma.bean.DetailedChartBean;
import com.epam.preprod.ma.bean.MotivationMapBean;
import com.epam.preprod.ma.dao.entity.Dimension;
import com.epam.preprod.ma.dao.entity.Result;
import com.epam.preprod.ma.dao.entity.Survey;
import com.epam.preprod.ma.dao.interf.IDashboardDao;
import com.epam.preprod.ma.exception.DaoException;
import com.epam.preprod.ma.service.transaction.ThreadLocalStorage;

/**
 * 
 * @author Alexandra Martyntseva
 * 
 */
@Repository
public class DashboardDao implements IDashboardDao {

	private static final Logger log = Logger.getLogger(DashboardDao.class);

	private static final String SELECT_MOTIVATION_MAP_DATA = "SELECT "
			+ "AVG(dashboard_data.satisfaction) AS satisf, "
			+ "AVG(dashboard_data.priority) AS prior, "
			+ "dimension, survey_id, end FROM dashboard_data "
			+ "WHERE user_id IN (%s) AND survey_id IN (%s) "
			+ "GROUP BY survey_id, dimension";

	private static final String SELECT_DETAILED_CHART_DATA = "SELECT "
			+ "AVG(dashboard_data.satisfaction) AS satisf, "
			+ "AVG(dashboard_data.priority) AS prior, "
			+ "name, survey_id, end, dashboard_data.question_id "
			+ "FROM dashboard_data INNER JOIN question_text "
			+ "ON dashboard_data.question_id=question_text.question_id "
			+ "WHERE user_id IN (%s) AND survey_id IN (%s) "
			+ "AND lang_id=(select lang_id from langs where locale=?) "
			+ "GROUP BY survey_id, question_id";

	@Override
	public MotivationMapBean readMotivationMapData(Long[] selectedUsers,
			Long[] selectedSurveys, Locale selectedLocale) throws DaoException {
		MotivationMapBean result = new MotivationMapBean();
		Connection con = ThreadLocalStorage.getConnection();
		try {
			PreparedStatement st = con.prepareStatement(prepareQuery(
					selectedUsers.length, selectedSurveys.length,
					SELECT_MOTIVATION_MAP_DATA));

			initializeStatement(st, selectedUsers, selectedSurveys);

			if (log.isTraceEnabled()) {
				log.trace("executing query: " + st.toString());
			}
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				Result values = new Result();
				Survey survey = new Survey();

				values.setSatisfaction(Math.round(rs.getFloat(1)));
				values.setPriority(Math.round(rs.getFloat(2)));

				Dimension dimension = Dimension.valueOf(rs.getString(3));

				survey.setId(rs.getLong(4));
				survey.setEndDate(rs.getDate(5));

				result.add(survey, dimension, values);
			}
		} catch (SQLException e) {
			throw new DaoException(e);
		}
		return result;
	}

	@Override
	public DetailedChartBean readDetailedChartData(Long[] selectedUsers,
			Long[] selectedSurveys, Locale selectedLocale) throws DaoException {
		DetailedChartBean result = new DetailedChartBean();
		Connection con = ThreadLocalStorage.getConnection();
		try {
			PreparedStatement st = con.prepareStatement(prepareQuery(
					selectedUsers.length, selectedSurveys.length,
					SELECT_DETAILED_CHART_DATA));

			int i = initializeStatement(st, selectedUsers, selectedSurveys);
			st.setString(i, selectedLocale.toString());

			if (log.isTraceEnabled()) {
				log.trace("executing query: " + st.toString());
			}
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				DashboardQuestionBean question = new DashboardQuestionBean();
				Survey survey = new Survey();

				question.setAvgSatisfaction(Math.round(rs.getFloat(1)));
				question.setAvgPriority(Math.round(rs.getFloat(2)));
				question.setQuestionName(rs.getString(3));

				survey.setId(rs.getLong(4));
				survey.setEndDate(rs.getDate(5));

				question.setId(rs.getLong(6));

				result.add(survey, question);
			}
		} catch (SQLException e) {
			throw new DaoException(e);
		}
		return result;
	}

	private String prepareQuery(int numberOfUsers, int numberOfSurveys,
			String query) {
		String result = String.format(query,
				formLineOfQuestions(numberOfUsers),
				formLineOfQuestions(numberOfSurveys));
		if (log.isTraceEnabled()) {
			log.trace("built prepared statement: " + result);
		}
		return result;
	}

	private String formLineOfQuestions(int questionsNumber) {
		StringBuilder result = new StringBuilder();
		String prefix = "";
		for (int i = 0; i < questionsNumber; i++) {
			result.append(prefix);
			prefix = ", ";
			result.append("?");
		}
		return result.toString();
	}

	private int initializeStatement(PreparedStatement st, Long[] selectedUsers,
			Long[] selectedSurveys) throws SQLException {
		int i = 1;
		for (Long userId : selectedUsers) {
			st.setLong(i++, userId);
		}
		for (Long surveyId : selectedSurveys) {
			st.setLong(i++, surveyId);
		}
		return i;
	}
}
