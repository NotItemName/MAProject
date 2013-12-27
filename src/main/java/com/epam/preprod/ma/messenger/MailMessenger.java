package com.epam.preprod.ma.messenger;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.epam.preprod.ma.dao.entity.Result;
import com.epam.preprod.ma.dao.entity.Survey;
import com.epam.preprod.ma.dao.entity.User;

@Component
public class MailMessenger implements IMailMessenger {

	private static final Logger LOGGER = Logger.getLogger(MailMessenger.class);

	private static final String SURVEY_STATUS_CHANGED = "Survey status change";

	private static final String RESULT = "Result of the passed survey";

	private static final String SEND_LINK = "Link to survey";

	private static final String TEMPLATES_REMINDER = "templates/reminder.vm";

	private static final String TEMPLATES_RESULT = "templates/result.vm";

	private static final String TEMPLATES_SEND_LINK = "templates/surveyLink.vm";

	@Autowired
	protected VelocityEngine velocityEngine;

	@Autowired
	protected JavaMailSender mailSender;

	@Override
	public void sendSurveyStatusChangedMail(Survey survey, User user) {
		HashMap<String, Object> model = new HashMap<>();
		model.put("survey", survey);
		try {
			MimeMessageHelper messageHelper = getMessageHelper(
					SURVEY_STATUS_CHANGED, TEMPLATES_REMINDER, user, model);
			sendMessage(messageHelper.getMimeMessage());
		} catch (MessagingException e) {
			LOGGER.error("Error occured while mail sending", e);
		}
	}

	@Override
	public void sendSurveyResultMail(User user, List<Result> result) {
		HashMap<String, Object> model = new HashMap<>();
		model.put("results", result);
		model.put("survey", result.get(0).getSurvey());
		try {
			MimeMessageHelper messageHelper = getMessageHelper(RESULT,
					TEMPLATES_RESULT, user, model);
			sendMessage(messageHelper.getMimeMessage());
		} catch (MessagingException e) {
			LOGGER.error("Error occured while mail sending", e);
		}
	}

	@Override
	public void sendSurveyLnkToEmployee(Survey survey, List<User> users,
			String url) {
		HashMap<String, Object> model = new HashMap<>();
		model.put("survey", survey);
		model.put("url", url);
		for (User user : users) {
			try {
				MimeMessageHelper messageHelper = getMessageHelper(SEND_LINK,
						TEMPLATES_SEND_LINK, user, model);
				sendMessage(messageHelper.getMimeMessage());
			} catch (MessagingException e) {
				LOGGER.error("Error occured while mail sending", e);
			}
		}
	}

	private MimeMessageHelper getMessageHelper(String subject, String template,
			User user, HashMap<String, Object> model) throws MessagingException {
		Properties props = new Properties();
		Session instance = Session.getInstance(props);
		MimeMessage mimeMessage = new MimeMessage(instance);
		MimeMessageHelper messageHelper;
		messageHelper = new MimeMessageHelper(mimeMessage, true);
		messageHelper.setTo(user.getEmail());
		messageHelper.setSubject(subject);
		model.put("userName", user.getName());
		model.put("locale", new Locale("en"));
		String body = VelocityEngineUtils.mergeTemplateIntoString(
				velocityEngine, template, "UTF-8", model);
		messageHelper.setText(body, true);
		return messageHelper;
	}

	private void sendMessage(final MimeMessage message) {
		Thread messageThread = new Thread() {
			@Override
			public void run() {
				try {
					mailSender.send(message);
				} catch (MailException e) {
					LOGGER.error("Error occured while mail sending", e);
				}
			};
		};
		messageThread.start();
	}
}
