package com.epam.preprod.ma.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.epam.preprod.ma.dao.AbstractDao;
import com.epam.preprod.ma.dao.entity.Dimension;
import com.epam.preprod.ma.dao.entity.Question;
import com.epam.preprod.ma.dao.entity.QuestionText;
import com.epam.preprod.ma.dao.interf.IQuestionDao;
import com.epam.preprod.ma.exception.DaoException;
import com.epam.preprod.ma.service.transaction.ThreadLocalStorage;

/**
 * Implements {@linkplain IQuestionDao} interface.
 * 
 * @author Mykola Zalyayev, Oleksandr Lagoda, Stanislav Gaponenko
 * 
 * @version 1.0
 * 
 */
@Repository
public class QuestionDao extends AbstractDao<Question> implements IQuestionDao {

	private static final Logger log = Logger.getLogger(QuestionDao.class);
	private final String SELECT_QUESTION_COUNT_FROM_SURVEY = "SELECT count(*) FROM survey_questions WHERE question_id=?;";

	private final String DELETE_QUESTION_FROM_CURRENT = "DELETE FROM current_questions "
			+ "WHERE question_id =?";

	private final String DELETE_QUESTION = "DELETE FROM questions WHERE question_id=?";

	private final String SELECT_USER_QUESTIONS = "SELECT q.question_id, q.dimension "
			+ "FROM current_questions AS cq, questions AS q "
			+ "WHERE q.question_id = cq.question_id AND cq.user_id = ?";

	private final String SELECT_SURVEY_QUESTIONS = "SELECT q.question_id, q.dimension "
			+ "FROM survey_questions AS sq, questions AS q "
			+ "WHERE q.question_id = sq.question_id AND sq.survey_id = ?";

	private final String SELECT_QUESTION_TEXT = "SELECT l.locale, qs.name, qs.satisfaction, qs.priority "
			+ "FROM question_text qs, langs l "
			+ "WHERE qs.lang_id=l.lang_id AND qs.question_id=?";

	private static final String CREATE_NEW_QUESTION = "INSERT INTO questions (question_id, dimension)"
			+ " VALUE (default, ?);";

	private static final String SET_QUESTION_TEXT = "INSERT INTO question_text (lang_id, question_id, name, satisfaction, priority)"
			+ "VALUE (?, ?, ?, ?, ?);";

	private static final String BIND_QUESTION_TO_USER = "INSERT INTO current_questions"
			+ " (user_id, question_id) VALUE (?, ?);";

	private static final String SELECT_LANG_ID_BY_LANG_NAME = "SELECT lang_id FROM langs WHERE locale=?;";

	private final String SELECT_QUESTION_COUNT_FROM_FINISHED_SURVEYS = "SELECT COUNT(*) FROM survey_questions sq "
			+ "INNER JOIN surveys s ON sq.survey_id=s.survey_id WHERE sq.question_id=? AND s.status='FINISHED'";

	private final String SELECT_QUESTION_COUNT_FROM_ACTIVE_SURVEYS = "SELECT COUNT(*) FROM survey_questions sq "
			+ "INNER JOIN surveys s ON sq.survey_id=s.survey_id WHERE sq.question_id=? AND s.status IN ('SCHEDULED','IN_PROGRESS')";

	private final String UPDATE_QUESTION = "UPDATE question_text qt SET qt.name=?,qt.satisfaction=?,qt.priority=?"
			+ " WHERE qt.question_id=? AND lang_id=(SELECT lang_id FROM langs WHERE locale=?)";

	@Override
	public List<Question> readUserQuestionsById(Long userId)
			throws DaoException {
		try {
			Connection conn = ThreadLocalStorage.getConnection();
			PreparedStatement prStatement = conn
					.prepareStatement(SELECT_USER_QUESTIONS);
			prStatement.setLong(1, userId);
			ResultSet resultSet = prStatement.executeQuery();
			return getEntitiesFromResultSet(resultSet);
		} catch (SQLException e) {
			throw new DaoException("Database access error occured", e);
		}
	}

	@Override
	public List<Question> readSurveyQuestionsById(Long surveyId)
			throws DaoException {
		try {
			Connection connection = ThreadLocalStorage.getConnection();
			PreparedStatement prStatement = connection
					.prepareStatement(SELECT_SURVEY_QUESTIONS);
			prStatement.setLong(1, surveyId);
			ResultSet resultSet = prStatement.executeQuery();
			return getEntitiesFromResultSet(resultSet);
		} catch (SQLException e) {
			throw new DaoException("Database access error occured", e);
		}
	}

	@Override
	public boolean isQuestionBelongToSurveys(Long questionId)
			throws DaoException {
		try {
			Connection connection = ThreadLocalStorage.getConnection();
			PreparedStatement prStatement = connection
					.prepareStatement(SELECT_QUESTION_COUNT_FROM_SURVEY);
			prStatement.setLong(1, questionId);
			ResultSet resultSet = prStatement.executeQuery();

			resultSet.next();
			return resultSet.getInt(1) > 0;

		} catch (SQLException e) {
			throw new DaoException("Database access error occured", e);
		}
	}

	@Override
	public boolean belongsToFinishedSurveys(Long questionId)
			throws DaoException {
		try {
			Connection connection = ThreadLocalStorage.getConnection();
			PreparedStatement prStatement = connection
					.prepareStatement(SELECT_QUESTION_COUNT_FROM_FINISHED_SURVEYS);
			prStatement.setLong(1, questionId);
			ResultSet resultSet = prStatement.executeQuery();

			resultSet.next();
			return resultSet.getInt(1) > 0;

		} catch (SQLException e) {
			throw new DaoException("Database access error occured", e);
		}
	}

	/**
	 * @author Alexandra Martyntseva
	 */
	@Override
	public void bindQuestionToUser(Long questionId, Long userId)
			throws DaoException {
		Connection con = ThreadLocalStorage.getConnection();
		try {
			PreparedStatement st = con.prepareStatement(BIND_QUESTION_TO_USER);
			st.setLong(1, userId);
			st.setLong(2, questionId);
			log.debug("executing query: " + st.toString());
			st.executeUpdate();
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}

	/**
	 * @author Alexandra Martyntseva
	 */
	@Override
	public void addQuestionText(Question question) throws DaoException {
		Connection con = ThreadLocalStorage.getConnection();
		try {
			PreparedStatement st = con.prepareStatement(SET_QUESTION_TEXT);
			for (Entry<Locale, QuestionText> entry : question.getQuestionText()
					.entrySet()) {
				st.setInt(1, getLangIdByName(con, entry.getKey().getLanguage()));
				st.setLong(2, question.getId());
				st.setString(3, entry.getValue().getName());
				st.setString(4, entry.getValue().getSatisfactionText());
				st.setString(5, entry.getValue().getPrioritizationText());
				log.trace("executing query: " + st.toString());
				st.executeUpdate();
			}
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}

	private Map<Locale, QuestionText> getQuestionText(Long id)
			throws SQLException {
		Connection conn = ThreadLocalStorage.getConnection();
		Map<Locale, QuestionText> questionText = new HashMap<>();
		PreparedStatement prStatement = conn
				.prepareStatement(SELECT_QUESTION_TEXT);
		prStatement.setLong(1, id);
		ResultSet rs = prStatement.executeQuery();
		while (rs.next()) {
			QuestionText qs = new QuestionText(rs.getString(2),
					rs.getString(3), rs.getString(4));
			questionText.put(new Locale(rs.getString(1)), qs);

		}
		return questionText;
	}

	/**
	 * Calling this method will store new question in common question storage.
	 * New question will not be bound to user, who created it. To bind question
	 * to user call method {@link #bindQuestionToUser(Integer, Integer)}. New
	 * question will not have localized name and text. To add text to this
	 * question call method {@link #addQuestionText(Question)}.
	 * 
	 * @author Alexandra Martyntseva
	 */
	@Override
	public void deleteCurrentQuestion(Long questionId) throws DaoException {
		Connection conn = ThreadLocalStorage.getConnection();

		PreparedStatement prStatement;
		try {
			prStatement = conn.prepareStatement(DELETE_QUESTION_FROM_CURRENT);

			prStatement.setLong(1, questionId);

			prStatement.executeUpdate();
			if (prStatement.getUpdateCount() != 1) {
				throw new DaoException(
						"Failed to delete single row. Number of affected rows: "
								+ prStatement.getUpdateCount());
			}
		} catch (SQLException e) {
			throw new DaoException("Database access error occured", e);
		}
	}

	@Override
	protected PreparedStatement prepareCreate(Connection connection,
			Question entityToCreate) throws SQLException {
		PreparedStatement result = connection.prepareStatement(
				CREATE_NEW_QUESTION, PreparedStatement.RETURN_GENERATED_KEYS);
		result.setString(1, entityToCreate.getDimension().name());
		return result;
	}

	@Override
	protected PreparedStatement prepareReadById(Connection connection, Long id)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected PreparedStatement prepareUpdate(Connection connection,
			Question entityToUpdate) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected PreparedStatement prepareDelete(Connection connection, Long id)
			throws SQLException {
		PreparedStatement prStatement = connection
				.prepareStatement(DELETE_QUESTION);
		prStatement.setLong(1, id);
		return prStatement;
	}

	@Override
	protected Question getEntityFromResultSet(ResultSet resultSet)
			throws SQLException {
		Long id = resultSet.getLong(1);
		Dimension dimension = Dimension.valueOf(resultSet.getString(2));
		return new Question(id, dimension, getQuestionText(id));
	}

	/**
	 * Returns ID of given language.
	 * 
	 * @author Alexandra Martyntseva
	 * @param con
	 *            connection to execute statement on
	 * @param lang
	 *            language to look for
	 * @return
	 * @throws DaoException
	 *             in case given language was not found or in case of DB access
	 *             error
	 */
	private Integer getLangIdByName(Connection con, String lang)
			throws DaoException {
		Integer result;
		try {
			PreparedStatement st = con
					.prepareStatement(SELECT_LANG_ID_BY_LANG_NAME);
			st.setString(1, lang);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			} else {
				throw new DaoException("could not find '" + lang
						+ "' in DB in table langs in column locale");
			}
		} catch (SQLException e) {
			throw new DaoException(e);
		}
		return result;
	}

	@Override
	public void updateQuestionText(Question question) throws DaoException {
		Connection conn = ThreadLocalStorage.getConnection();

		PreparedStatement prStatement;
		try {
			prStatement = conn.prepareStatement(UPDATE_QUESTION);

			for (Entry<Locale, QuestionText> e : question.getQuestionText()
					.entrySet()) {

				prStatement.setString(1, e.getValue().getName());
				prStatement.setString(2, e.getValue().getSatisfactionText());
				prStatement.setString(3, e.getValue().getPrioritizationText());
				prStatement.setLong(4, question.getId());
				prStatement.setString(5, e.getKey().getLanguage());

				prStatement.executeUpdate();
				if (prStatement.getUpdateCount() != 1) {
					throw new DaoException(
							"Failed to delete single row. Number of affected rows: "
									+ prStatement.getUpdateCount());
				}
			}

		} catch (SQLException e) {
			throw new DaoException("Database access error occured", e);
		}

	}

	@Override
	public boolean belongsToActiveSurveys(Long questionId) throws DaoException {
		try {
			Connection connection = ThreadLocalStorage.getConnection();
			PreparedStatement prStatement = connection
					.prepareStatement(SELECT_QUESTION_COUNT_FROM_ACTIVE_SURVEYS);
			prStatement.setLong(1, questionId);
			ResultSet resultSet = prStatement.executeQuery();

			resultSet.next();
			return resultSet.getInt(1) > 0;

		} catch (SQLException e) {
			throw new DaoException("Database access error occured", e);
		}
	}

}
