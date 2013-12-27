package com.epam.preprod.ma.service.interf;

import java.util.List;
import java.util.Locale;

import com.epam.preprod.ma.service.IService;

/**
 * 
 * @author Alexandra Martyntseva
 * 
 */
public interface ILocaleService extends IService<Locale> {

	List<Locale> getSupportedLocales();

}
