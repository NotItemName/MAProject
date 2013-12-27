package com.epam.preprod.ma.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.LocaleResolver;

/**
 * Filter that change locale in request according to request parameter
 * <code>locale</code>. If request doesn't contain parameter <code>locale</code>
 * locale will be resolved with Spring LocaleResolver.
 * 
 * @see LocaleResolver
 * 
 * @author Yevhen Lobazov
 */
@Component
public class LocalizationFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = Logger
			.getLogger(LocalizationFilter.class);

	@Resource(name = "locales")
	private List<Locale> supportedLocales;

	@Resource(name = "localeResolver")
	private LocaleResolver localeResolver;

	private static final String REQ_PARAM_NAME_LOCALE = "locale";

	public LocalizationFilter() {
		LOGGER.info("Localization Filter initialization");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		Locale currentLocale = null;
		String newLocale = request.getParameter(REQ_PARAM_NAME_LOCALE);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Locale from request: " + newLocale);
		}

		if (newLocale == null
				|| !supportedLocales.contains(new Locale(newLocale))) {
			newLocale = null;
		} else {
			currentLocale = StringUtils.parseLocaleString(newLocale);
		}

		if (currentLocale == null) {
			currentLocale = localeResolver.resolveLocale(request);
		}

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Current locale: " + currentLocale);
		}
		localeResolver.setLocale(request, response, currentLocale);
		request.setAttribute("supportedLocales", supportedLocales);
		RequestWrapper requsetWrapper = new RequestWrapper(request,
				currentLocale);
		filterChain.doFilter(requsetWrapper, response);
	}

	/**
	 * Replaces request locales to those that resolved by LocaleResolver
	 * 
	 * @author Yevhen Lobazov
	 * 
	 */
	private class RequestWrapper extends HttpServletRequestWrapper {

		private Locale currentLocale;

		public RequestWrapper(HttpServletRequest request, Locale currentLocale) {
			super(request);
			this.currentLocale = currentLocale;
		}

		@Override
		public Locale getLocale() {
			return currentLocale;
		}

		@Override
		public Enumeration<Locale> getLocales() {
			return new Enumeration<Locale>() {

				boolean next = true;

				@Override
				public boolean hasMoreElements() {
					return next;
				}

				@Override
				public Locale nextElement() {
					if (next == false) {
						throw new NoSuchElementException();
					}
					next = false;
					return currentLocale;
				}
			};
		}

	}

}
