package com.epam.preprod.ma.controller.validation;

import com.epam.preprod.ma.bean.AnswersBean;
import com.epam.preprod.ma.dao.entity.Survey;

public interface IAnswersValidator {

	boolean validate(AnswersBean bean, Survey survey);

}