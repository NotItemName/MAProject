package com.epam.preprod.ma.service.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epam.preprod.ma.dao.IDao;
import com.epam.preprod.ma.dao.interf.ILocaleDao;
import com.epam.preprod.ma.exception.DaoException;
import com.epam.preprod.ma.service.AbstractService;
import com.epam.preprod.ma.service.interf.ILocaleService;
import com.epam.preprod.ma.service.transaction.ITransactionOperation;

/**
 * 
 * @author Alexandra Martyntseva
 * 
 */
@Service(value = "localeService")
public class LocaleService extends AbstractService<Locale> implements
		ILocaleService {

	@Autowired
	private ILocaleDao localeDao;

	@Override
	protected IDao<Locale> getDao() {
		return localeDao;
	}

	@Override
	public List<Locale> getSupportedLocales() {
		ITransactionOperation<List<Locale>> operation = new ITransactionOperation<List<Locale>>() {

			@Override
			public List<Locale> execute() throws DaoException {
				return localeDao.getSupportedLocales();
			}
		};
		return manager.executeOperation(operation);
	}

}
