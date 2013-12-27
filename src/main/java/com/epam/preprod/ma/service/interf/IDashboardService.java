package com.epam.preprod.ma.service.interf;

import java.util.Locale;

import com.epam.preprod.ma.bean.ChartsData;

public interface IDashboardService {

	ChartsData readData(Long[] selectedUsers, Long[] selectedSurveys,
			Locale selectedLocale);

}
