package com.epam.preprod.ma.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.epam.preprod.ma.constant.SessionAttribute;
import com.epam.preprod.ma.dao.entity.Role;
import com.epam.preprod.ma.dao.entity.User;

@Component
public class SecurityFilter extends OncePerRequestFilter {
	private static final Logger LOGGER = Logger.getLogger(SecurityFilter.class);
	private static final int ACCESS_DENIED = 0;
	private static final int ACCESS_GRANTED = 1;
	private static final int AUTHENTICATION_REQUIRED = 2;

	private static final String AUTHENTICATION_COMMAND = "/auth";

	private static final String PATH_TO_LOGIN_PAGE = "/WEB-INF/views/login.jsp";

	private static final String PATH_TO_ACCESS_DENIED_PAGE = "/WEB-INF/views/accessDenied.jsp";

	private Map<String, List<Role>> constraints;

	public SecurityFilter() {
		// constraints are hardcoded now as approved by Maxim Labazov
		constraints = new HashMap<String, List<Role>>();

		List<Role> dashboardAccess = new ArrayList<>();
		dashboardAccess.add(Role.RM);
		dashboardAccess.add(Role.SUPERADMIN);
		constraints.put("/dashboard", dashboardAccess);

		List<Role> surveyAccess = new ArrayList<>();
		surveyAccess.add(Role.RM);
		surveyAccess.add(Role.SUPERADMIN);
		constraints.put("/createSurvey", surveyAccess);

		List<Role> errorLogAccess = new ArrayList<>();
		errorLogAccess.add(Role.SUPERADMIN);
		constraints.put("/errorLog", errorLogAccess);
		LOGGER.trace("initialized");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(SessionAttribute.USER);
		String url = request.getServletPath();
		switch (getAccess(url, user)) {
		case ACCESS_DENIED:
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("denied access: user = " + user + "; url = " + url);
			}
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			request.getRequestDispatcher(PATH_TO_ACCESS_DENIED_PAGE).forward(
					request, response);
			break;
		case ACCESS_GRANTED:
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("granted access: user = " + user + "; url = "
						+ url);
			}
			filterChain.doFilter(request, response);
			break;
		case AUTHENTICATION_REQUIRED:
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("authentication required: user = " + user
						+ "; url = " + url);
			}
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			request.getRequestDispatcher(PATH_TO_LOGIN_PAGE).forward(request,
					response);
			break;
		}
	}

	private int getAccess(String url, User user) {
		if (user == null || user.getId() == null) {
			if (url.equals(AUTHENTICATION_COMMAND)) {
				return ACCESS_GRANTED;
			} else {
				return AUTHENTICATION_REQUIRED;
			}
		}
		if (!constraints.containsKey(url)) {
			return ACCESS_GRANTED;
		}
		if (!constraints.get(url).contains(user.getRole())) {
			return ACCESS_DENIED;
		}
		return ACCESS_GRANTED;
	}
}