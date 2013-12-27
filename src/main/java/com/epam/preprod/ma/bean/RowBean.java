package com.epam.preprod.ma.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.epam.preprod.ma.updater.IDatabaseUpdater;

/**
 * <p>
 * Represent row read from file.
 * </p>
 * <p>
 * Used with {@linkplain IDatabaseUpdater}
 * </p>
 * 
 * @author Oleksandr Lagoda
 * 
 * @version 1.0
 * 
 */
public class RowBean {

	private static final Logger LOGGER = Logger.getLogger(RowBean.class);

	private Long employeeId;

	private String employeeName;

	private Long managerId;

	private String managerName;

	private Long unitId;

	private String unitName;

	private String email;

	private Calendar date;

	public RowBean() {

	}

	public RowBean(Long employeeId, String employeeName, Long managerId,
			String managerName, Long unitId, String unitName, Calendar date) {
		this.employeeId = employeeId;
		this.employeeName = employeeName;
		this.managerId = managerId;
		this.managerName = managerName;
		this.unitId = unitId;
		this.unitName = unitName;
		this.date = date;
	}

	public RowBean(String[] rowContent) {
		if (rowContent != null) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Received values: " + Arrays.asList(rowContent));
			}

			this.employeeName = rowContent[0];
			this.employeeId = parseLong(rowContent[1]);
			this.managerId = parseLong(rowContent[2]);
			this.managerName = rowContent[3];
			this.unitId = parseLong(rowContent[4]);
			this.unitName = rowContent[5];
			this.setEmail(rowContent[6]);
			this.date = parseCalendar(rowContent[7]);
		} else {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Received values: " + rowContent);
			}
		}
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public Long getManagerId() {
		return managerId;
	}

	public void setManagerId(Long rmId) {
		this.managerId = rmId;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String rmName) {
		this.managerName = rmName;
	}

	public Long getUnitId() {
		return unitId;
	}

	public void setUnitId(Long unitId) {
		this.unitId = unitId;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result
				+ ((employeeId == null) ? 0 : employeeId.hashCode());
		result = prime * result
				+ ((managerId == null) ? 0 : managerId.hashCode());
		result = prime * result + ((unitId == null) ? 0 : unitId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RowBean other = (RowBean) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (employeeId == null) {
			if (other.employeeId != null)
				return false;
		} else if (!employeeId.equals(other.employeeId))
			return false;
		if (managerId == null) {
			if (other.managerId != null)
				return false;
		} else if (!managerId.equals(other.managerId))
			return false;
		if (unitId == null) {
			if (other.unitId != null)
				return false;
		} else if (!unitId.equals(other.unitId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RowBean [employeeId=" + employeeId + ", employeeName="
				+ employeeName + ", rmId=" + managerId + ", rmName="
				+ managerName + ", unitId=" + unitId + ", unitName=" + unitName
				+ ", date=" + date + "]";
	}

	/**
	 * Convert string value to Long format.
	 * 
	 * @param value
	 *            - string to parse.
	 * @return Converted value to Long type.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private Long parseLong(String value) {
		try {
			if (StringUtils.isNotBlank(value)) {
				return Long.parseLong(value);
			} else {
				return null;
			}

		} catch (NumberFormatException numberFormatException) {
			String message = "Wrong string to parse id parameter.";
			LOGGER.error(message);
			throw new IllegalArgumentException(message, numberFormatException);
		}
	}

	/**
	 * Convert date which has been transfered in string to {@linkplain Calendar}
	 * format.
	 * 
	 * @param date
	 *            - string to parse.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private Calendar parseCalendar(String date) {

		final String DATE_FORMAT = "dd-MMM-yy";

		final Locale DEFAULT_LOCALE = Locale.ENGLISH;

		Calendar calendar = Calendar.getInstance();

		try {
			calendar.setTime(new SimpleDateFormat(DATE_FORMAT, DEFAULT_LOCALE)
					.parse(date));
			return calendar;
		} catch (ParseException e) {
			String message = "File consist field with wrong data forrmat.";

			LOGGER.error(message);

			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Check class for null fields.
	 * 
	 * @return True if at least one field is null.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	public boolean isEmpty() {

		boolean result = true;

		result &= employeeId == null;
		result &= StringUtils.isBlank(employeeName);
		result &= managerId == null;
		result &= StringUtils.isBlank(managerName);
		result &= unitId == null;
		result &= StringUtils.isBlank(unitName);
		result &= date == null;

		return result;
	}

}
