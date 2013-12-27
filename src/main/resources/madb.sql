SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

DROP SCHEMA IF EXISTS `madb` ;
CREATE SCHEMA IF NOT EXISTS `madb` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `madb` ;

-- -----------------------------------------------------
-- Table `madb`.`questions`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `madb`.`questions` (
  `question_id` BIGINT NOT NULL AUTO_INCREMENT ,
  `dimension` ENUM('MORALE', 'CAREER', 'PROFESSIONAL', 'COMPENSATION') NOT NULL ,
  PRIMARY KEY (`question_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `madb`.`langs`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `madb`.`langs` (
  `lang_id` INT NOT NULL AUTO_INCREMENT ,
  `locale` CHAR(8) NOT NULL ,
  PRIMARY KEY (`lang_id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------

-- Table `madb`.`users`

-- -----------------------------------------------------

DROP TABLE IF EXISTS `madb`.`users` ;
CREATE  TABLE IF NOT EXISTS `madb`.`users` (
  `user_id` BIGINT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(40) NOT NULL ,
  `role` ENUM('EMPLOYEE', 'RM', 'SUPERADMIN') NULL ,
  `email` VARCHAR(100) NULL ,
  `unit_id` BIGINT NULL ,
  `deleted` TINYINT(1) NOT NULL ,
  PRIMARY KEY (`user_id`) ,
  INDEX `fk_users_units1_idx` (`unit_id` ASC) ,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) ,
  CONSTRAINT `fk_users_units1`
    FOREIGN KEY (`unit_id` )
    REFERENCES `madb`.`units` (`unit_id` )
    ON DELETE SET NULL
    ON UPDATE SET NULL)
ENGINE = InnoDB;


-- -----------------------------------------------------

-- Table `madb`.`units`

-- -----------------------------------------------------

DROP TABLE IF EXISTS `madb`.`units` ;
CREATE  TABLE IF NOT EXISTS `madb`.`units` (
  `unit_id` BIGINT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(100) NOT NULL ,
  `parent_unit_id` BIGINT NULL DEFAULT NULL ,
  `rm_id` BIGINT NULL DEFAULT NULL ,
  `deleted` TINYINT(1) NOT NULL ,
  PRIMARY KEY (`unit_id`) ,
  INDEX `fk_units_units1_idx` (`parent_unit_id` ASC) ,
  INDEX `fk_units_users1_idx` (`rm_id` ASC) ,
  CONSTRAINT `fk_units_units1`
    FOREIGN KEY (`parent_unit_id` )
    REFERENCES `madb`.`units` (`unit_id` )
    ON DELETE SET NULL
    ON UPDATE SET NULL,
  CONSTRAINT `fk_units_users1`
    FOREIGN KEY (`rm_id` )
    REFERENCES `madb`.`users` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `madb`.`surveys`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `madb`.`surveys` (
  `survey_id` BIGINT NOT NULL AUTO_INCREMENT ,
  `start` DATE NOT NULL ,
  `end` DATE NOT NULL ,
  `link` VARCHAR(100) NOT NULL ,
  `rm_id` BIGINT NOT NULL ,
  `status` ENUM('SCHEDULED', 'IN_PROGRESS', 'FINISHED') NOT NULL ,
  PRIMARY KEY (`survey_id`) ,
  INDEX `fk_surveys_users1_idx` (`rm_id` ASC) ,
  UNIQUE INDEX `link_UNIQUE` (`link` ASC) ,
  CONSTRAINT `fk_surveys_users1`
    FOREIGN KEY (`rm_id` )
    REFERENCES `madb`.`users` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `madb`.`question_text`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `madb`.`question_text` (
  `lang_id` INT NOT NULL ,
  `question_id` BIGINT NOT NULL ,
  `name` VARCHAR(100) NOT NULL ,
  `satisfaction` VARCHAR(255) NOT NULL ,
  `priority` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`lang_id`, `question_id`) ,
  INDEX `fk_langs_has_questions_questions1_idx` (`question_id` ASC) ,
  INDEX `fk_langs_has_questions_langs1_idx` (`lang_id` ASC) ,
  CONSTRAINT `fk_langs_has_questions_langs1`
    FOREIGN KEY (`lang_id` )
    REFERENCES `madb`.`langs` (`lang_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_langs_has_questions_questions1`
    FOREIGN KEY (`question_id` )
    REFERENCES `madb`.`questions` (`question_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `madb`.`survey_text`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `madb`.`survey_text` (
  `lang_id` INT NOT NULL ,
  `survey_id` BIGINT NOT NULL ,
  `name` VARCHAR(40) NOT NULL ,
  `satisfaction` VARCHAR(255) NULL ,
  `priority` VARCHAR(255) NULL ,
  PRIMARY KEY (`lang_id`, `survey_id`) ,
  INDEX `fk_langs_has_surveys_surveys1_idx` (`survey_id` ASC) ,
  INDEX `fk_langs_has_surveys_langs1_idx` (`lang_id` ASC) ,
  CONSTRAINT `fk_langs_has_surveys_langs1`
    FOREIGN KEY (`lang_id` )
    REFERENCES `madb`.`langs` (`lang_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_langs_has_surveys_surveys1`
    FOREIGN KEY (`survey_id` )
    REFERENCES `madb`.`surveys` (`survey_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `madb`.`survey_questions`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `madb`.`survey_questions` (
  `question_id` BIGINT NOT NULL ,
  `survey_id` BIGINT NOT NULL ,
  PRIMARY KEY (`question_id`, `survey_id`) ,
  INDEX `fk_questions_has_surveys_surveys1_idx` (`survey_id` ASC) ,
  INDEX `fk_questions_has_surveys_questions1_idx` (`question_id` ASC) ,
  CONSTRAINT `fk_questions_has_surveys_questions1`
    FOREIGN KEY (`question_id` )
    REFERENCES `madb`.`questions` (`question_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_questions_has_surveys_surveys1`
    FOREIGN KEY (`survey_id` )
    REFERENCES `madb`.`surveys` (`survey_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `madb`.`results`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `madb`.`results` (
  `user_id` BIGINT NOT NULL ,
  `question_id` BIGINT NOT NULL ,
  `survey_id` BIGINT NOT NULL ,
  `satisfaction` INT NOT NULL ,
  `priority` INT NOT NULL ,
  PRIMARY KEY (`user_id`, `question_id`, `survey_id`) ,
  INDEX `fk_users_has_questions_users1_idx` (`user_id` ASC) ,
  INDEX `fk_results_survey_questions1_idx` (`question_id` ASC, `survey_id` ASC) ,
  CONSTRAINT `fk_users_has_questions_users1`
    FOREIGN KEY (`user_id` )
    REFERENCES `madb`.`users` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_results_survey_questions1`
    FOREIGN KEY (`question_id` , `survey_id` )
    REFERENCES `madb`.`survey_questions` (`question_id` , `survey_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `madb`.`current_questions`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `madb`.`current_questions` (
  `user_id` BIGINT NOT NULL ,
  `question_id` BIGINT NOT NULL ,
  PRIMARY KEY (`user_id`, `question_id`) ,
  INDEX `fk_questions_has_users_users1_idx` (`user_id` ASC) ,
  INDEX `fk_current_questions_questions1_idx` (`question_id` ASC) ,
  CONSTRAINT `fk_questions_has_users_users1`
    FOREIGN KEY (`user_id` )
    REFERENCES `madb`.`users` (`user_id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_current_questions_questions1`
    FOREIGN KEY (`question_id` )
    REFERENCES `madb`.`questions` (`question_id` )
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Placeholder table for view `madb`.`dashboard_data`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `madb`.`dashboard_data` (`user_id` INT, `survey_id` INT, `end` INT, `question_id` INT, `satisfaction` INT, `priority` INT, `dimension` INT);

-- -----------------------------------------------------
-- View `madb`.`dashboard_data`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `madb`.`dashboard_data`;
USE `madb`;
CREATE  OR REPLACE VIEW `madb`.`dashboard_data` AS 
select results.user_id, results.survey_id, surveys.end,
		results.question_id, results.satisfaction, results.priority,
		questions.dimension
from (results inner join questions
				on results.question_id = questions.question_id)
		left join surveys on results.survey_id = surveys.survey_id;

;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `madb`.`questions`
-- -----------------------------------------------------
START TRANSACTION;
USE `madb`;
INSERT INTO `madb`.`questions` (`question_id`, `dimension`) VALUES (1, 'MORALE');
INSERT INTO `madb`.`questions` (`question_id`, `dimension`) VALUES (2, 'CAREER');
INSERT INTO `madb`.`questions` (`question_id`, `dimension`) VALUES (3, 'PROFESSIONAL');
INSERT INTO `madb`.`questions` (`question_id`, `dimension`) VALUES (4, 'COMPENSATION');
INSERT INTO `madb`.`questions` (`question_id`, `dimension`) VALUES (5, 'MORALE');
INSERT INTO `madb`.`questions` (`question_id`, `dimension`) VALUES (6, 'CAREER');
INSERT INTO `madb`.`questions` (`question_id`, `dimension`) VALUES (7, 'PROFESSIONAL');
INSERT INTO `madb`.`questions` (`question_id`, `dimension`) VALUES (8, 'COMPENSATION');

COMMIT;

-- -----------------------------------------------------
-- Data for table `madb`.`langs`
-- -----------------------------------------------------
START TRANSACTION;
USE `madb`;
INSERT INTO `madb`.`langs` (`lang_id`, `locale`) VALUES (1, 'en');
INSERT INTO `madb`.`langs` (`lang_id`, `locale`) VALUES (2, 'ru');

COMMIT;

-- -----------------------------------------------------
-- Data for table `madb`.`units`
-- -----------------------------------------------------
START TRANSACTION;
USE `madb`;
INSERT INTO `madb`.`units` (`unit_id`, `name`, `parent_unit_id`, `rm_id`, `deleted`)
		VALUES (1, 'Unit name', NULL, NULL, false);

COMMIT;

-- -----------------------------------------------------
-- Data for table `madb`.`users`
-- -----------------------------------------------------
START TRANSACTION;
USE `madb`;
INSERT INTO `madb`.`users` (`user_id`, `name`, `role`, `email`, `unit_id`, `deleted`) VALUES (1, 'Oleksandr', 'RM',
                                                                                              'PSIXss@bigmir.net', 1,
                                                                                              false);
INSERT INTO `madb`.`users` (`user_id`, `name`, `role`, `email`, `unit_id`, `deleted`) VALUES (2, 'Tetiana', 'EMPLOYEE', 'PSIXss@bigmir.net', 1, false);
INSERT INTO `madb`.`users` (`user_id`, `name`, `role`, `email`, `unit_id`, `deleted`) VALUES (3, 'Glib', 'EMPLOYEE', 'PSIXss@bigmir.net', 1, false);
INSERT INTO `madb`.`users` (`user_id`, `name`, `role`, `email`, `unit_id`, `deleted`) VALUES (4, 'Leonid', 'EMPLOYEE', 'PSIXss@bigmir.net', 1, false);
INSERT INTO `madb`.`users` (`user_id`, `name`, `role`, `email`, `unit_id`, `deleted`) VALUES (5, 'Oleksii', 'SUPERADMIN', 'PSIXss@bigmir.net', 1, false);

UPDATE `madb`.`units` SET `rm_id` = 1 WHERE `unit_id` = 1;

COMMIT;

-- -----------------------------------------------------
-- Data for table `madb`.`surveys`
-- -----------------------------------------------------
START TRANSACTION;
USE `madb`;
INSERT INTO `madb`.`surveys` (`survey_id`, `start`, `end`, `link`, `rm_id`, `status`) VALUES (1, '2013-04-01', '2013-04-01', 'finished', 1, 'FINISHED');
INSERT INTO `madb`.`surveys` (`survey_id`, `start`, `end`, `link`, `rm_id`, `status`) VALUES (2, '2013-05-01', '2013-05-10', 'anotherFinished', 1, 'FINISHED');

COMMIT;

-- -----------------------------------------------------
-- Data for table `madb`.`question_text`
-- -----------------------------------------------------
START TRANSACTION;
USE `madb`;
INSERT INTO `madb`.`question_text` (`lang_id`, `question_id`, `name`, `satisfaction`, `priority`) VALUES (1, 1, 'Relationships', 'Express your satisfaction with your teammates and their behavior', 'Good relationships with teammates');
INSERT INTO `madb`.`question_text` (`lang_id`, `question_id`, `name`, `satisfaction`, `priority`) VALUES (2, 1, 'Взаимоотношения', 'Насколько вы удовлетворены взаимоотношениями с коллегами', 'Хорошие отношения в команде');
INSERT INTO `madb`.`question_text` (`lang_id`, `question_id`, `name`, `satisfaction`, `priority`) VALUES (1, 2, 'Your career', 'Express your satisfaction with your current job position', 'Job position');
INSERT INTO `madb`.`question_text` (`lang_id`, `question_id`, `name`, `satisfaction`, `priority`) VALUES (2, 2, 'Ваша карьера', 'Насколько вы довольны своей текущей должностью', 'Должность');
INSERT INTO `madb`.`question_text` (`lang_id`, `question_id`, `name`, `satisfaction`, `priority`) VALUES (1, 3, 'Interesting tasks', 'How interesting are your tasks', 'Interesting tasks');
INSERT INTO `madb`.`question_text` (`lang_id`, `question_id`, `name`, `satisfaction`, `priority`) VALUES (2, 3, 'Интересные задачи', 'Насколько интересны для вас ваши задачи', 'Интересные задачи');
INSERT INTO `madb`.`question_text` (`lang_id`, `question_id`, `name`, `satisfaction`, `priority`) VALUES (1, 4, 'Salary', 'Express your satisfaction with your salary', 'Salary');
INSERT INTO `madb`.`question_text` (`lang_id`, `question_id`, `name`, `satisfaction`, `priority`) VALUES (2, 4, 'Зарплата', 'Насколько вы довольны вашим заработком', 'Зарплата');
INSERT INTO `madb`.`question_text` (`lang_id`, `question_id`, `name`, `satisfaction`, `priority`) VALUES (1, 5, 'Question name', 'Satisfaction text', 'Priority text');
INSERT INTO `madb`.`question_text` (`lang_id`, `question_id`, `name`, `satisfaction`, `priority`) VALUES (2, 5, 'Название вопроса', 'Текст опроса удовлетворенности', 'Приоритет');
INSERT INTO `madb`.`question_text` (`lang_id`, `question_id`, `name`, `satisfaction`, `priority`) VALUES (1, 6, 'Another name', 'Another satisfaction text', 'Another priority text');
INSERT INTO `madb`.`question_text` (`lang_id`, `question_id`, `name`, `satisfaction`, `priority`) VALUES (2, 6, 'Другое название', 'Другой текст опроса удовлетворенности', 'Другой приоритет');
INSERT INTO `madb`.`question_text` (`lang_id`, `question_id`, `name`, `satisfaction`, `priority`) VALUES (1, 7, 'More questions', 'More satisfaction text', 'More priority text');
INSERT INTO `madb`.`question_text` (`lang_id`, `question_id`, `name`, `satisfaction`, `priority`) VALUES (2, 7, 'Еще вопрос', 'Еще текст опроса удовлетворенности', 'Еще приоритет');
INSERT INTO `madb`.`question_text` (`lang_id`, `question_id`, `name`, `satisfaction`, `priority`) VALUES (1, 8, 'Last question', 'Last satisfaction text', 'Last priority text');
INSERT INTO `madb`.`question_text` (`lang_id`, `question_id`, `name`, `satisfaction`, `priority`) VALUES (2, 8, 'Последний вопрос', 'Последний текст опроса удовлетворенности', 'Последний приоритет');

COMMIT;

-- -----------------------------------------------------
-- Data for table `madb`.`survey_text`
-- -----------------------------------------------------
START TRANSACTION;
USE `madb`;
INSERT INTO `madb`.`survey_text` (`lang_id`, `survey_id`, `name`, `satisfaction`, `priority`) VALUES (1, 1, 'Finished survey', 'Express your satisfaction', 'Express your priorities');
INSERT INTO `madb`.`survey_text` (`lang_id`, `survey_id`, `name`, `satisfaction`, `priority`) VALUES (2, 1, 'Оконченный опрос', 'Оцените вашу удовлетворенность', 'Расставьте ваши приоритеты');
INSERT INTO `madb`.`survey_text` (`lang_id`, `survey_id`, `name`, `satisfaction`, `priority`) VALUES (1, 2, 'Another finished survey', 'Express your satisfaction', 'Express your priorities');
INSERT INTO `madb`.`survey_text` (`lang_id`, `survey_id`, `name`, `satisfaction`, `priority`) VALUES (2, 2, 'Другой оконченный опрос', 'Оцените вашу удовлетворенность', 'Расставьте ваши приоритеты');

COMMIT;

-- -----------------------------------------------------
-- Data for table `madb`.`survey_questions`
-- -----------------------------------------------------
START TRANSACTION;
USE `madb`;
INSERT INTO `madb`.`survey_questions` (`question_id`, `survey_id`) VALUES (1, 1);
INSERT INTO `madb`.`survey_questions` (`question_id`, `survey_id`) VALUES (2, 1);
INSERT INTO `madb`.`survey_questions` (`question_id`, `survey_id`) VALUES (3, 1);
INSERT INTO `madb`.`survey_questions` (`question_id`, `survey_id`) VALUES (4, 1);
INSERT INTO `madb`.`survey_questions` (`question_id`, `survey_id`) VALUES (3, 2);
INSERT INTO `madb`.`survey_questions` (`question_id`, `survey_id`) VALUES (5, 1);
INSERT INTO `madb`.`survey_questions` (`question_id`, `survey_id`) VALUES (6, 1);
INSERT INTO `madb`.`survey_questions` (`question_id`, `survey_id`) VALUES (4, 2);
INSERT INTO `madb`.`survey_questions` (`question_id`, `survey_id`) VALUES (5, 2);
INSERT INTO `madb`.`survey_questions` (`question_id`, `survey_id`) VALUES (6, 2);
INSERT INTO `madb`.`survey_questions` (`question_id`, `survey_id`) VALUES (7, 2);
INSERT INTO `madb`.`survey_questions` (`question_id`, `survey_id`) VALUES (8, 2);

COMMIT;

-- -----------------------------------------------------
-- Data for table `madb`.`results`
-- -----------------------------------------------------
START TRANSACTION;
USE `madb`;
INSERT INTO `madb`.`results` (`user_id`, `question_id`, `survey_id`, `satisfaction`, `priority`) VALUES (2, 1, 1, 3, 5);
INSERT INTO `madb`.`results` (`user_id`, `question_id`, `survey_id`, `satisfaction`, `priority`) VALUES (2, 2, 1, 4, 1);
INSERT INTO `madb`.`results` (`user_id`, `question_id`, `survey_id`, `satisfaction`, `priority`) VALUES (2, 3, 1, 6, 0);
INSERT INTO `madb`.`results` (`user_id`, `question_id`, `survey_id`, `satisfaction`, `priority`) VALUES (2, 4, 1, 2, 5);
INSERT INTO `madb`.`results` (`user_id`, `question_id`, `survey_id`, `satisfaction`, `priority`) VALUES (2, 5, 1, 1, 2);
INSERT INTO `madb`.`results` (`user_id`, `question_id`, `survey_id`, `satisfaction`, `priority`) VALUES (2, 6, 1, 6, 3);
INSERT INTO `madb`.`results` (`user_id`, `question_id`, `survey_id`, `satisfaction`, `priority`) VALUES (3, 1, 1, 4, 3);
INSERT INTO `madb`.`results` (`user_id`, `question_id`, `survey_id`, `satisfaction`, `priority`) VALUES (3, 2, 1, 5, 0);
INSERT INTO `madb`.`results` (`user_id`, `question_id`, `survey_id`, `satisfaction`, `priority`) VALUES (3, 3, 1, 1, 1);
INSERT INTO `madb`.`results` (`user_id`, `question_id`, `survey_id`, `satisfaction`, `priority`) VALUES (3, 4, 1, 6, 2);
INSERT INTO `madb`.`results` (`user_id`, `question_id`, `survey_id`, `satisfaction`, `priority`) VALUES (3, 5, 1, 5, 4);
INSERT INTO `madb`.`results` (`user_id`, `question_id`, `survey_id`, `satisfaction`, `priority`) VALUES (3, 6, 1, 3, 1);
INSERT INTO `madb`.`results` (`user_id`, `question_id`, `survey_id`, `satisfaction`, `priority`) VALUES (3, 3, 2, 5, 1);
INSERT INTO `madb`.`results` (`user_id`, `question_id`, `survey_id`, `satisfaction`, `priority`) VALUES (3, 4, 2, 4, 3);
INSERT INTO `madb`.`results` (`user_id`, `question_id`, `survey_id`, `satisfaction`, `priority`) VALUES (3, 5, 2, 6, 4);
INSERT INTO `madb`.`results` (`user_id`, `question_id`, `survey_id`, `satisfaction`, `priority`) VALUES (3, 6, 2, 3, 2);
INSERT INTO `madb`.`results` (`user_id`, `question_id`, `survey_id`, `satisfaction`, `priority`) VALUES (3, 7, 2, 5, 5);
INSERT INTO `madb`.`results` (`user_id`, `question_id`, `survey_id`, `satisfaction`, `priority`) VALUES (3, 8, 2, 1, 1);
INSERT INTO `madb`.`results` (`user_id`, `question_id`, `survey_id`, `satisfaction`, `priority`) VALUES (4, 3, 2, 3, 2);
INSERT INTO `madb`.`results` (`user_id`, `question_id`, `survey_id`, `satisfaction`, `priority`) VALUES (4, 4, 2, 4, 2);
INSERT INTO `madb`.`results` (`user_id`, `question_id`, `survey_id`, `satisfaction`, `priority`) VALUES (4, 5, 2, 1, 3);
INSERT INTO `madb`.`results` (`user_id`, `question_id`, `survey_id`, `satisfaction`, `priority`) VALUES (4, 6, 2, 1, 5);
INSERT INTO `madb`.`results` (`user_id`, `question_id`, `survey_id`, `satisfaction`, `priority`) VALUES (4, 7, 2, 5, 1);
INSERT INTO `madb`.`results` (`user_id`, `question_id`, `survey_id`, `satisfaction`, `priority`) VALUES (4, 8, 2, 2, 3);

COMMIT;

-- -----------------------------------------------------
-- Data for table `madb`.`current_questions`
-- -----------------------------------------------------
START TRANSACTION;
USE `madb`;
INSERT INTO `madb`.`current_questions` (`user_id`, `question_id`) VALUES (1, 3);
INSERT INTO `madb`.`current_questions` (`user_id`, `question_id`) VALUES (1, 4);
INSERT INTO `madb`.`current_questions` (`user_id`, `question_id`) VALUES (1, 5);
INSERT INTO `madb`.`current_questions` (`user_id`, `question_id`) VALUES (1, 6);
INSERT INTO `madb`.`current_questions` (`user_id`, `question_id`) VALUES (1, 7);
INSERT INTO `madb`.`current_questions` (`user_id`, `question_id`) VALUES (1, 8);

COMMIT;
