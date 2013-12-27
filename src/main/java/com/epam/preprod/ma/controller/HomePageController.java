package com.epam.preprod.ma.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.epam.preprod.ma.constant.SessionAttribute;
import com.epam.preprod.ma.dao.entity.Role;
import com.epam.preprod.ma.dao.entity.User;

/**
 * Just shows home page on request "/"
 * 
 * @author Leonid Polyakov
 * 
 * @version 1.0
 * 
 */
@Controller
@SessionAttributes({ SessionAttribute.USER })
public class HomePageController {

	@RequestMapping(value = "/")
	public String showHomePage(@ModelAttribute(SessionAttribute.USER) User user) {
		if (user.getRole() != Role.EMPLOYEE) {
			return "redirect:dashboard";
		}
		return "home";
	}
}