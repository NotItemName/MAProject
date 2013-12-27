package com.epam.preprod.ma.dao.interf;

import java.util.Locale;

import com.epam.preprod.ma.bean.DetailedChartBean;
import com.epam.preprod.ma.bean.MotivationMapBean;
import com.epam.preprod.ma.exception.DaoException;

/**
 * 
 * @author Alexandra Martyntseva
 * 
 */
public interface IDashboardDao {

	MotivationMapBean readMotivationMapData(Long[] selectedUsers,
			Long[] selectedSurveys, Locale selectedLocale) throws DaoException;

	DetailedChartBean readDetailedChartData(Long[] selectedUsers,
			Long[] selectedSurveys, Locale selectedLocale) throws DaoException;

}
