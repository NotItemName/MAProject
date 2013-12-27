package com.epam.preprod.ma.dao.interf;

import java.util.List;
import java.util.Locale;

import com.epam.preprod.ma.dao.IDao;
import com.epam.preprod.ma.exception.DaoException;

/**
 * 
 * @author Alexandra Martyntseva
 * 
 */
public interface ILocaleDao extends IDao<Locale> {

	List<Locale> getSupportedLocales() throws DaoException;

	/**
	 * @author Yevhen Lobazov
	 */
	Integer readLangIdByLocale(Locale locale) throws DaoException;

}
