package com.epam.preprod.ma.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.epam.preprod.ma.dao.AbstractDao;
import com.epam.preprod.ma.dao.entity.Survey;
import com.epam.preprod.ma.dao.entity.SurveyStatus;
import com.epam.preprod.ma.dao.entity.SurveyText;
import com.epam.preprod.ma.dao.interf.ISurveyDao;
import com.epam.preprod.ma.exception.DaoException;
import com.epam.preprod.ma.service.transaction.ThreadLocalStorage;

/**
 * Implementation of {@link ISurveyDao}
 * 
 * @author Yevhen Lobazov
 * 
 */
@Repository
public class SurveyDao extends AbstractDao<Survey> implements ISurveyDao {

	private static final Logger log = Logger.getLogger(SurveyDao.class);

	private static final String SELECT_SCHEDULED_AND_IN_PROGRESS_SURVEYS = "SELECT survey_id, start, end, link, rm_id, status "
			+ "FROM surveys WHERE status IN('SCHEDULED','IN_PROGRESS')";

	private static final String INSERT_SURVEY = "INSERT INTO `surveys` (`start`, `end`, `link`, `rm_id`, `status`) VALUES (?,?,?,?,?)";

	private static final String SELECT_SURVEY_BY_ID = "SELECT survey_id, start, end, link, rm_id, status FROM surveys WHERE survey_id = ?";

	private static final String UPDATE_SURVEY = "UPDATE `surveys` SET  `start` = ? , `end`=?, `link` = ?, `rm_id` = ?, `status`=? WHERE `survey_id` = ?";

	private static final String SELECT_SURVEY_BY_LINK = "SELECT survey_id, start, end, link, rm_id, status "
			+ "FROM surveys WHERE link = ?";

	private static final String INSERT_SURVEY_TEXT = "INSERT INTO `survey_text` (`lang_id`, `survey_id`, `name`, `satisfaction`, `priority`) VALUES (?,?,?,?,?)";

	private static final String SELECT_SURVEY_TEXT = "SELECT l.locale, st.name, st.satisfaction, st.priority "
			+ "FROM survey_text st, langs l "
			+ "WHERE st.lang_id=l.lang_id AND st.survey_id=?";

	private static final String DELETE_SURVEY = "DELETE FROM surveys WHERE survey_id=?";

	private static final String SELECT_FINISHED_SURVEY_BY_USER = "SELECT survey_id, start, end, link, rm_id, status "
			+ "FROM surveys WHERE status IN('FINISHED','IN_PROGRESS') AND rm_id=? ORDER BY `start` DESC";

	private static final String SELECT_RMS_NOT_FINISHED_SURVEY = "SELECT survey_id, start, end, link, rm_id, status "
			+ "FROM surveys WHERE rm_id=? AND status NOT IN('FINISHED')";

	private static final String INSERT_SURVEY_QUESTIONS = "INSERT INTO `survey_questions` (`question_id`, `survey_id`) VALUES (?, ?)";

	/**
	 * @author Yevhen Lobazov
	 */
	@Override
	protected PreparedStatement prepareCreate(Connection connection,
			Survey entityToCreate) throws SQLException {
		PreparedStatement prepStatement = connection.prepareStatement(
				INSERT_SURVEY, Statement.RETURN_GENERATED_KEYS);
		prepStatement.setDate(1, new Date(entityToCreate.getStartDate()
				.getTime()));
		prepStatement.setDate(2,
				new Date(entityToCreate.getEndDate().getTime()));
		prepStatement.setString(3, entityToCreate.getLink());
		prepStatement.setLong(4, entityToCreate.getRmId());
		prepStatement.setString(5, entityToCreate.getStatus().name());
		return prepStatement;
	}

	/**
	 * @author Yevhen Lobazov
	 */
	@Override
	protected PreparedStatement prepareReadById(Connection connection, Long id)
			throws SQLException {
		PreparedStatement prepStatement = connection
				.prepareStatement(SELECT_SURVEY_BY_ID);
		prepStatement.setLong(1, id);
		return prepStatement;
	}

	/**
	 * @author Yevhen Lobazov
	 */
	@Override
	protected PreparedStatement prepareUpdate(Connection connection,
			Survey entityToUpdate) throws SQLException {
		PreparedStatement prepStatement = connection
				.prepareStatement(UPDATE_SURVEY);
		prepStatement.setDate(1, new Date(entityToUpdate.getStartDate()
				.getTime()));
		prepStatement.setDate(2,
				new Date(entityToUpdate.getEndDate().getTime()));
		prepStatement.setString(3, entityToUpdate.getLink());
		prepStatement.setLong(4, entityToUpdate.getRmId());
		prepStatement.setString(5, entityToUpdate.getStatus().name());
		prepStatement.setLong(6, entityToUpdate.getId());
		return prepStatement;
	}

	/**
	 * @author Alexandra Martyntseva
	 */
	@Override
	protected PreparedStatement prepareDelete(Connection connection, Long id)
			throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(DELETE_SURVEY);
		stmt.setLong(1, id);
		return stmt;
	}

	/**
	 * @author Leonid Polyakov
	 */
	@Override
	protected Survey getEntityFromResultSet(ResultSet resultSet)
			throws SQLException {
		int index = 1;
		Survey survey = new Survey();
		survey.setId(resultSet.getLong(index++));
		survey.setStartDate(resultSet.getDate(index++));
		survey.setEndDate(resultSet.getDate(index++));
		survey.setLink(resultSet.getString(index++));
		survey.setRmId(resultSet.getLong(index++));
		String status = resultSet.getString(index++);
		survey.setStatus(SurveyStatus.valueOf(status));
		return survey;
	}

	/**
	 * @author Yevhen Lobazov, Leonid Polyakov
	 */
	@Override
	public List<Survey> readScheduledAndInProgressSurveys() throws DaoException {
		Connection connection = ThreadLocalStorage.getConnection();
		try {
			PreparedStatement prepStatement = connection
					.prepareStatement(SELECT_SCHEDULED_AND_IN_PROGRESS_SURVEYS);
			prepStatement.executeQuery();
			ResultSet resultSet = prepStatement.getResultSet();
			return getEntitiesFromResultSet(resultSet);
		} catch (SQLException e) {
			throw new DaoException(
					"SQL exception occured while reading all surveys", e);
		}
	}

	/**
	 * @author Mykola Zalyayev
	 */
	@Override
	public Survey readSurveyByLink(String link) throws DaoException {
		Connection connection = ThreadLocalStorage.getConnection();
		PreparedStatement prepStatement;
		try {
			prepStatement = connection.prepareStatement(SELECT_SURVEY_BY_LINK);
			prepStatement.setString(1, link);
			ResultSet resultSet = prepStatement.executeQuery();
			Survey survey = null;
			if (resultSet.next()) {
				survey = getEntityFromResultSet(resultSet);
				return survey;
			}
		} catch (SQLException e) {
			throw new DaoException(
					"SQL exception occured while reading survey", e);
		}
		return null;
	}

	@Override
	public Map<Locale, SurveyText> readSurveyText(Survey survey)
			throws DaoException {
		Connection connection = ThreadLocalStorage.getConnection();
		Map<Locale, SurveyText> surveyDescription = new HashMap<>();
		PreparedStatement prStatement;
		try {
			prStatement = connection.prepareStatement(SELECT_SURVEY_TEXT);
			prStatement.setLong(1, survey.getId());
			ResultSet result = prStatement.executeQuery();
			while (result.next()) {
				int index = 1;
				Locale locale = new Locale(result.getString(index++));
				String name = result.getString(index++);
				String satisfactionDescription = result.getString(index++);
				String prioritizationDescription = result.getString(index++);
				SurveyText surveyText = new SurveyText(name,
						satisfactionDescription, prioritizationDescription);
				surveyDescription.put(locale, surveyText);
			}
			survey.setDescription(surveyDescription);
		} catch (SQLException e) {
			throw new DaoException(
					"SQLException occured while reading survey text", e);
		}
		return surveyDescription;
	}

	@Override
	public List<Survey> readFinishedSurveysByUser(Long userId)
			throws DaoException {
		Connection conn = ThreadLocalStorage.getConnection();
		List<Survey> surveyList = null;
		try {
			PreparedStatement prStatement = conn
					.prepareStatement(SELECT_FINISHED_SURVEY_BY_USER);
			prStatement.setLong(1, userId);
			ResultSet resultSet = prStatement.executeQuery();
			surveyList = new ArrayList<>();
			while (resultSet.next()) {
				Survey survey = getEntityFromResultSet(resultSet);
				surveyList.add(survey);
			}
		} catch (SQLException e) {
			throw new DaoException(
					"SQL exception occured while reading survey", e);
		}
		return surveyList;
	}

	/**
	 * @author Alexandra Martyntseva
	 */
	@Override
	public Survey readCurrentSurveyForRM(Long userId) throws DaoException {
		Connection con = ThreadLocalStorage.getConnection();
		Survey result = null;
		try {
			PreparedStatement st = con
					.prepareStatement(SELECT_RMS_NOT_FINISHED_SURVEY);
			st.setLong(1, userId);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				result = getEntityFromResultSet(rs);
				result.setDescription(readSurveyText(result));
			}
		} catch (SQLException e) {
			throw new DaoException(
					"SQL exception occured while reading rm's survey", e);
		}
		if (log.isTraceEnabled()) {
			log.trace("returning current rms survey: " + result);
		}
		return result;
	}

	/**
	 * @author Yevhen Lobazov
	 */
	@Override
	public void createSurveyText(Long surveyId, Integer langId,
			SurveyText surveyText) throws DaoException {
		Connection connection = ThreadLocalStorage.getConnection();
		try {
			PreparedStatement prepStatement = connection
					.prepareStatement(INSERT_SURVEY_TEXT);
			prepStatement.setInt(1, langId);
			prepStatement.setLong(2, surveyId);
			prepStatement.setString(3, surveyText.getName());
			prepStatement.setString(4, surveyText.getSatisfaction());
			prepStatement.setString(5, surveyText.getPrioritization());
			prepStatement.executeUpdate();
		} catch (SQLException e) {
			throw new DaoException(
					"SQL exception occured while creating survey text", e);
		}
	}

	/**
	 * @author Yevhen Lobazov
	 * @param questionId
	 * @param surveyId
	 * @throws DaoException
	 */
	@Override
	public void createSurveyQuestion(Long questionId, Long surveyId)
			throws DaoException {
		Connection connection = ThreadLocalStorage.getConnection();
		try {
			PreparedStatement prepStatement = connection
					.prepareStatement(INSERT_SURVEY_QUESTIONS);
			prepStatement.setLong(1, questionId);
			prepStatement.setLong(2, surveyId);
			prepStatement.executeUpdate();
		} catch (SQLException e) {
			throw new DaoException(
					"SQL exception occured while creating survey questions", e);
		}
	}
}
