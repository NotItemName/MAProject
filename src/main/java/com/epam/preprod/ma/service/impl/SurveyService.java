package com.epam.preprod.ma.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epam.preprod.ma.dao.IDao;
import com.epam.preprod.ma.dao.entity.Question;
import com.epam.preprod.ma.dao.entity.Survey;
import com.epam.preprod.ma.dao.entity.SurveyStatus;
import com.epam.preprod.ma.dao.entity.SurveyText;
import com.epam.preprod.ma.dao.entity.User;
import com.epam.preprod.ma.dao.interf.ILocaleDao;
import com.epam.preprod.ma.dao.interf.IQuestionDao;
import com.epam.preprod.ma.dao.interf.ISurveyDao;
import com.epam.preprod.ma.dao.interf.IUserDao;
import com.epam.preprod.ma.exception.DaoException;
import com.epam.preprod.ma.messenger.MailMessenger;
import com.epam.preprod.ma.service.AbstractService;
import com.epam.preprod.ma.service.interf.ISurveyService;
import com.epam.preprod.ma.service.transaction.ITransactionOperation;
import com.epam.preprod.ma.service.transaction.TransactionManager;

/**
 * Implementation of {@link ISurveyService}
 * 
 * @author Yevhen Lobazov
 * 
 */
@Service("surveyService")
public class SurveyService extends AbstractService<Survey> implements
		ISurveyService {

	private static final Logger LOGGER = Logger
			.getLogger(TransactionManager.class);

	@Autowired
	private IUserDao userDao;

	@Autowired
	private ISurveyDao surveyDao;

	@Autowired
	private ILocaleDao localeDao;

	@Autowired
	private IQuestionDao questionDao;

	@Autowired
	private MailMessenger mailMessenger;

	/**
	 * @author Yevhen Lobazov
	 */
	@PostConstruct
	@Override
	public void checkScheduledAndInProgressSurveys() {
		LOGGER.trace("Scheduled and In Progress surveys check started");
		ITransactionOperation<Void> operation = new ITransactionOperation<Void>() {
			@Override
			public Void execute() throws DaoException {
				List<Survey> surveys = surveyDao
						.readScheduledAndInProgressSurveys();
				Calendar currentDay = Calendar.getInstance();
				int updateCount = 0;
				for (Survey survey : surveys) {
					Calendar startDay = Calendar.getInstance();
					Calendar endDay = Calendar.getInstance();
					startDay.setTime(survey.getStartDate());
					endDay.setTime(survey.getEndDate());
					endDay.add(Calendar.DAY_OF_MONTH, 1);
					SurveyStatus status = survey.getStatus();
					SurveyStatus shouldBe = null;
					if (endDay.before(currentDay)) {
						shouldBe = SurveyStatus.FINISHED;
					}
					if (startDay.before(currentDay) && endDay.after(currentDay)) {
						shouldBe = SurveyStatus.IN_PROGRESS;
					}
					if (startDay.after(currentDay)) {
						shouldBe = SurveyStatus.SCHEDULED;
					}
					if (status != shouldBe) {
						survey.setStatus(shouldBe);
						surveyDao.update(survey);
						updateCount++;
						User user = userDao.readById(survey.getRmId());
						survey.setDescription(surveyDao.readSurveyText(survey));
						mailMessenger.sendSurveyStatusChangedMail(survey, user);
					}
				}
				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace("Survey status update count: " + updateCount);
				}
				return null;
			}
		};
		manager.executeOperation(operation);
	}

	/**
	 * @author Mykola Zalyayev, Leonid Polyakov
	 */
	@Override
	public Survey readSurveyByLink(final String link) {
		ITransactionOperation<Survey> operation = new ITransactionOperation<Survey>() {
			@Override
			public Survey execute() throws DaoException {
				Survey survey = surveyDao.readSurveyByLink(link);
				if (survey != null) {
					surveyDao.readSurveyText(survey);
				}
				return survey;
			}
		};
		return manager.executeOperation(operation);
	}

	/**
	 * @author Alexandra Martyntseva
	 */
	@Override
	public Survey readCurrentSurveyForRM(final Long userId) {
		ITransactionOperation<Survey> operation = new ITransactionOperation<Survey>() {

			@Override
			public Survey execute() throws DaoException {
				return surveyDao.readCurrentSurveyForRM(userId);
			}
		};
		return manager.executeOperation(operation);
	}

	@Override
	protected IDao<Survey> getDao() {
		return surveyDao;
	}

	/**
	 * @author Yevhen Lobazov
	 */
	@Override
	public Survey scheduleSurvey(final Date startDate, final Date endDate,
			final Map<Locale, SurveyText> description, final Long rmId,
			final List<Question> questions) {
		ITransactionOperation<Survey> operation = new ITransactionOperation<Survey>() {
			@Override
			public Survey execute() throws DaoException {
				Survey survey = null;
				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace("Read questions by RM: " + questions);
				}
				SurveyStatus status = SurveyStatus.SCHEDULED;
				String link = generateSurveyLink();
				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace("Generated survey link: " + link);
				}
				survey = new Survey(startDate, endDate, link, status, rmId,
						description, questions);
				Long surveyId = surveyDao.create(survey);
				survey.setId(surveyId);
				for (Question q : questions) {
					surveyDao.createSurveyQuestion(q.getId(), surveyId);
				}
				for (Map.Entry<Locale, SurveyText> entry : description
						.entrySet()) {
					Integer langId = localeDao.readLangIdByLocale(entry
							.getKey());
					surveyDao.createSurveyText(survey.getId(), langId,
							entry.getValue());
				}
				return survey;
			}
		};
		return manager.executeOperation(operation);
	}

	private String generateSurveyLink() {
		return Long.toHexString(UUID.randomUUID().getMostSignificantBits());
	}

	@Override
	public SurveyStatus stopSurveyById(final Long surveyId) {
		ITransactionOperation<SurveyStatus> operation = new ITransactionOperation<SurveyStatus>() {
			@Override
			public SurveyStatus execute() throws DaoException {
				Survey survey = surveyDao.readById(surveyId);
				survey.setStatus(SurveyStatus.FINISHED);
				surveyDao.update(survey);
				return SurveyStatus.FINISHED;
			}
		};
		return manager.executeOperation(operation);
	}

	@Override
	public List<Survey> readFinishedSurveysByRM(final Long userId) {
		ITransactionOperation<List<Survey>> operation = new ITransactionOperation<List<Survey>>() {
			@Override
			public List<Survey> execute() throws DaoException {
				List<Survey> surveys = surveyDao
						.readFinishedSurveysByUser(userId);
				for (Survey survey : surveys) {
					surveyDao.readSurveyText(survey);
				}
				return surveys;
			}
		};
		return manager.executeOperation(operation);
	}
}
