package com.epam.preprod.ma.service.impl;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epam.preprod.ma.bean.ChartsData;
import com.epam.preprod.ma.bean.DetailedChartBean;
import com.epam.preprod.ma.bean.MotivationMapBean;
import com.epam.preprod.ma.dao.interf.IDashboardDao;
import com.epam.preprod.ma.exception.DaoException;
import com.epam.preprod.ma.service.interf.IDashboardService;
import com.epam.preprod.ma.service.transaction.ITransactionManager;
import com.epam.preprod.ma.service.transaction.ITransactionOperation;

/**
 * 
 * @author Alexandra Martyntseva
 * 
 */
@Service(value = "dashboardService")
public class DashboardService implements IDashboardService {

	private static final Logger log = Logger.getLogger(DashboardService.class);

	@Autowired
	private IDashboardDao dashboardDao;

	@Autowired
	private ITransactionManager manager;

	@Override
	public ChartsData readData(final Long[] selectedUsers,
			final Long[] selectedSurveys, final Locale selectedLocale) {
		ITransactionOperation<ChartsData> operation = new ITransactionOperation<ChartsData>() {

			@Override
			public ChartsData execute() throws DaoException {
				MotivationMapBean motivation = dashboardDao
						.readMotivationMapData(selectedUsers, selectedSurveys,
								selectedLocale);
				DetailedChartBean details = dashboardDao.readDetailedChartData(
						selectedUsers, selectedSurveys, selectedLocale);
				if (log.isTraceEnabled()) {
					log.trace("obtained MotivationMapBean: " + motivation);
					log.trace("obtained DetailedChartBean: " + details);
				}
				return new ChartsData(motivation, details);
			}
		};
		return manager.executeOperation(operation);
	}

}
