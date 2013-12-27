package com.epam.preprod.ma.service.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.epam.preprod.ma.bean.LogRowBean;
import com.epam.preprod.ma.controller.ControllerUtils;

public class LogParser {

	private static final Logger LOGGER = Logger
			.getLogger(ControllerUtils.class);

	private static final String LOG_ROW_REGEX = "\\[((?:\\d{2}\\/){2}\\d{4} (?:\\d{2}:){2}\\d{2})\\] \\[(\\w+\\s*)\\] \\[([\\w\\.]+)\\] - (.+)";

	/**
	 * Parses the logs and return a list of LogRowBean that contains info about
	 * time, level, category and text of log message.
	 * 
	 * @param path
	 *            - absolute path to parsed file
	 * @return list of LogRowBean
	 * @see LogRowBean
	 * @author Yevhen Lobazov
	 */
	public static List<LogRowBean> parseLog(String path) {
		Scanner scanner;
		File file = new File(path);
		if (!file.exists()) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("File does not exist: " + path);
			}
			return null;
		}
		try {
			scanner = new Scanner(new InputStreamReader(new FileInputStream(
					file), "utf-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			LOGGER.error("Can not read the file", e);
			return null;
		}
		List<LogRowBean> allRows = new ArrayList<>();
		String line;
		Pattern pattern = Pattern.compile(LOG_ROW_REGEX);
		LogRowBean row;
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			if (StringUtils.isBlank(line)) {
				continue;
			}
			Matcher matcher = pattern.matcher(line);
			row = new LogRowBean();
			if (matcher.find()) {
				row.setTime(matcher.group(1));
				row.setLevel(matcher.group(2));
				row.setLogger(matcher.group(3));
				row.setMessage(matcher.group(4));
				allRows.add(row);
			} else {
				continue;
			}
		}
		scanner.close();
		return allRows;
	}

	/**
	 * Gets the messages for specific page from list of <code>LogRowBean</code>.
	 * 
	 * 
	 * @param log
	 *            - list of <code>LogRowBean</code> that contain rows of current
	 *            log file. Can be obtain with invoking
	 *            <code>parseLog(String path)</code> method.
	 * @param oldLog
	 *            - list of <code>LogRowBean</code> that contain rows of old log
	 *            file (that has been rolled). Can be obtain with invoking
	 *            <code>parseLog(String path)</code> method.
	 * @param currentPage
	 *            - number of current page of logs
	 * @param messagesPerPage
	 *            - number of messages per page
	 * @return list of <code>LogRowBean</code> for specified page
	 * @see LogRowBean
	 * @author Yevhen Lobazov
	 */
	public static List<LogRowBean> getLogMessagesForSpecifiedPage(
			List<LogRowBean> log, List<LogRowBean> oldLog, int currentPage,
			int messagesPerPage) {
		int currLogSize = (log == null) ? 0 : log.size();
		int oldLogSize = (oldLog == null) ? 0 : oldLog.size();
		int totalSize = currLogSize + oldLogSize;
		int startIndex = (currentPage - 1) * messagesPerPage;
		int endIndex = startIndex + messagesPerPage;
		if (endIndex > totalSize) {
			endIndex = totalSize;
		}

		if (endIndex <= currLogSize) {
			log = log.subList(currLogSize - endIndex, currLogSize - startIndex);
		}

		if (startIndex <= currLogSize && endIndex > currLogSize) {
			if (oldLogSize > 0) {
				int from = oldLogSize - endIndex + currLogSize;
				log.addAll(0, oldLog.subList(from, oldLogSize));
				log = log.subList(log.size() - endIndex, log.size()
						- startIndex);
			} else {
				log = log.subList(startIndex, currLogSize);
			}
		}

		if (oldLogSize > 0 && startIndex > currLogSize) {
			int from = oldLogSize - endIndex + currLogSize;
			int to = oldLogSize - startIndex + currLogSize;
			log = oldLog.subList(from, to);
		}
		if (log != null) {
			Collections.reverse(log);
		}
		return log;
	}
}
