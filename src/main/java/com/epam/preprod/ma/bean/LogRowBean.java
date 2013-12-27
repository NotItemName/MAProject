package com.epam.preprod.ma.bean;

/**
 * Bean for log messages. Contains info about time, level, category, text of the
 * message.
 * 
 * @author Yevhen_Lobazov
 * 
 */
public class LogRowBean {

	private String time;

	private String level;

	private String logger;

	private String message;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getLogger() {
		return logger;
	}

	public void setLogger(String logger) {
		this.logger = logger;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "LogRowBean [time=" + time + ", level=" + level + ", logger="
				+ logger + ", message=" + message + "]";
	}

}
