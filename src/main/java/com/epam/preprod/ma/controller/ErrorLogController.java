package com.epam.preprod.ma.controller;

import com.epam.preprod.ma.bean.LogRowBean;
import com.epam.preprod.ma.service.log.LogParser;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author
 * @version 1.0
 */
@Controller
public class ErrorLogController {

    private static final Logger LOGGER = Logger
            .getLogger(ErrorLogController.class);
    private static final String REQ_PARAM_CURR_PAGE = "currentPage";
    @Value("${errorLog.fileName}")
    private String logFileName;
    @Value("${errorLog.messagesPerPage}")
    private int errorLogMessagesPerPage;

    /**
     * @return
     * @author Yevhen Lobazov
     */
    @RequestMapping(value = "/errorLog")
    public String errorLog(Model model, HttpServletRequest request) {
        String catalinaHome = System.getProperty("catalina.home");
        String logPath = catalinaHome + logFileName;

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Path to log file: " + logPath);
        }
        Integer currentPage = null;
        try {
            currentPage = Integer.parseInt(request
                    .getParameter(REQ_PARAM_CURR_PAGE));
        } catch (NumberFormatException e) {
            currentPage = 1;
        }
        if (currentPage < 1) {
            currentPage = 1;
        }
        List<LogRowBean> log = LogParser.parseLog(logPath);
        List<LogRowBean> oldLog = LogParser.parseLog(logPath + ".1");

        int currLogSize = (log == null) ? 0 : log.size();
        int oldLogSize = (oldLog == null) ? 0 : oldLog.size();
        int totalSize = currLogSize + oldLogSize;

        int totalPageAmount = totalSize / errorLogMessagesPerPage;
        if (totalSize % errorLogMessagesPerPage != 0) {
            ++totalPageAmount;
        }
        if (currentPage > totalPageAmount) {
            currentPage = totalPageAmount;
        }
        if (totalPageAmount == 0) {
            currentPage = 1;
        }
        List<LogRowBean> toReturn = LogParser.getLogMessagesForSpecifiedPage(
                log, oldLog, currentPage, errorLogMessagesPerPage);
        model.addAttribute("log", toReturn);
        model.addAttribute("totalPageAmount", totalPageAmount);
        model.addAttribute("currentPage", currentPage);
        return "errorLog";
    }
}
