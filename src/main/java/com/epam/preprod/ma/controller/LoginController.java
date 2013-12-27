package com.epam.preprod.ma.controller;

import com.epam.preprod.ma.constant.SessionAttribute;
import com.epam.preprod.ma.dao.entity.User;
import com.epam.preprod.ma.service.interf.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Stanislav_Gaponenko
 * @version 1.0
 */

@Controller
public class LoginController {

    private static final Logger LOGGER = Logger
            .getLogger(LoginController.class);
    @Autowired
    private IUserService userService;
    private String sessionHash;

    /**
     * PMC login controller method
     *
     * @param login    - Login of the user
     * @param password - password of the user
     * @param session  - HttpSession
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @author Stanislav_Gaponenko
     */

    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public String login(@RequestParam(value = "login") String login,
                        @RequestParam(value = "password") String password,
                        @RequestHeader("Referer") String referrer, HttpSession session,
                        HttpServletRequest request, HttpServletResponse response) {

        User user = userService.readUserByLogin(login);

        if (user != null) {
            session.setAttribute(SessionAttribute.USER, user);
            session.removeAttribute("loginErrorMessage");

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("user has logged in: user = " + user);
            }
        } else {
            session.setAttribute("loginErrorMessage", "jsp.logIn.error");
        }
        if (StringUtils.isBlank(referrer)) {
            referrer = "/";
        }


        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Redirect to " + referrer);
        }

        return "redirect:" + referrer;
    }

    /**
     * PMC logout controller method
     *
     * @param session - HttpSession
     * @author Stanislav_Gaponenko
     */
    @RequestMapping(value = "/logOut", method = RequestMethod.GET)
    public String logout(HttpSession session) {

        User user = (User) session.getAttribute(SessionAttribute.USER);
        if (user != null) {
            session.removeAttribute(SessionAttribute.USER);
            session.invalidate();
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("user has logged out: user = " + user);
            }
        }

        return "redirect:/";
    }
}
