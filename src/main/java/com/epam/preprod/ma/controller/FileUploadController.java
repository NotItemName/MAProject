package com.epam.preprod.ma.controller;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.epam.preprod.ma.updater.IDatabaseUpdater;

/**
 * Controller for uploading export file from web form.
 * 
 * @author Oleksandr Lagoda
 * 
 * @version 1.0
 * 
 */
@Controller
public class FileUploadController {

	private static final Logger LOGGER = Logger
			.getLogger(FileUploadController.class);

	static final String FILE_EXTENSION = ".tsv";

	static final String SYSTEM_TEMP_DIRECTORY = "java.io.tmpdir";

	static final String MODEL_ATTRIBUTE_MESSAGE = "message";

	static final String JSP_UPDATE_DATABASE = "updateDatabase";

	static final String FILE_TRANSFERING_ERROR_MESSAGE = "jsp.updateDatabase.message.error.transferingFile";

	static final String FILE_SUCCESS_UPLOADING_MESSAGE = "jsp.updateDatabase.message.transferSuccess";

	static final String FILE_EXTENSION_ERROR_MESSAGE = "jsp.updateDatabase.message.error.fileExtension";

	static final String FILE_NULL_ERROR_MESSAGE = "jsp.updateDatabase.message.error.nullFile";

	@Autowired
	private IDatabaseUpdater databaseUpdater;

	@RequestMapping(value = "/updateDatabase", method = RequestMethod.GET)
	public String showUpdateDatabasePage() {
		return "updateDatabase";
	}

	/**
	 * Load export file from web form.
	 * 
	 * @param file
	 *            - transferring file.
	 * 
	 * @param model
	 *            - container of return parameters.
	 * 
	 * @return View name.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	@RequestMapping(value = "/updateDatabase", method = RequestMethod.POST)
	public String uploadFile(@RequestParam("file") MultipartFile file,
			Model model) {

		LOGGER.info("File uploading started.");

		try {

			if (validateReceivedFile(file, model)
					|| !validateFileExtension(file, model)) {

				LOGGER.info("File uploading terminated.");
				LOGGER.trace("Redirect to " + JSP_UPDATE_DATABASE);

				return JSP_UPDATE_DATABASE;
			}

			String fileName = file.getOriginalFilename();

			LOGGER.info("Received file: " + fileName);

			String saveDirectory = System.getProperty(SYSTEM_TEMP_DIRECTORY);

			String filepath = saveDirectory + fileName;

			try {

				File destinationFile = createFile(filepath);

				LOGGER.info("Transfering file to folder: " + saveDirectory);

				file.transferTo(destinationFile);

				databaseUpdater.update(filepath, true);

			} catch (IllegalStateException | IOException e) {

				LOGGER.error(
						"Error transfering file to the given destination.", e);

				model.addAttribute(MODEL_ATTRIBUTE_MESSAGE,
						FILE_TRANSFERING_ERROR_MESSAGE);

				LOGGER.info("File uploading terminated.");
				LOGGER.trace("Redirect to " + JSP_UPDATE_DATABASE);

				return JSP_UPDATE_DATABASE;
			}

			model.addAttribute(MODEL_ATTRIBUTE_MESSAGE,
					FILE_SUCCESS_UPLOADING_MESSAGE);

			LOGGER.info("File uploading finished.");
			LOGGER.trace("Redirect to " + JSP_UPDATE_DATABASE);

		} catch (RuntimeException runtimeException) {
			LOGGER.error("Runtime error occured", runtimeException);
			LOGGER.info("File uploading terminated.");
		}

		return "redirect:" + JSP_UPDATE_DATABASE;
	}

	/**
	 * Create new file in received filepath if it is not exist.
	 * 
	 * @param filepath
	 *            - filepath to file.
	 * 
	 * @return New file.
	 * 
	 * @throws IOException
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private File createFile(String filepath) throws IOException {
		File file = new File(filepath);

		if (!file.exists()) {
			file.createNewFile();
		}

		return file;
	}

	/**
	 * Check file extension.
	 * 
	 * @param file
	 *            - file to check.
	 * 
	 * @param model
	 *            - container to store error messages.
	 * 
	 * @param MODEL_ATTRIBUTE_MESSAGE
	 *            - error message name in model.
	 * 
	 * @return true if file has '.tsv' extension.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private boolean validateFileExtension(MultipartFile file, Model model) {
		final String fileName = file.getOriginalFilename();
		boolean result = true;

		if (!fileName.endsWith(FILE_EXTENSION)) {

			model.addAttribute(MODEL_ATTRIBUTE_MESSAGE,
					FILE_EXTENSION_ERROR_MESSAGE);

			LOGGER.error("Invalid type of file.");

			result = false;
		}

		return result;
	}

	/**
	 * Check if user press 'transfer' button without selected any file.
	 * 
	 * @param file
	 *            - file to check.
	 * 
	 * @param model
	 *            - container to store error messages.
	 * 
	 * @param MODEL_ATTRIBUTE_MESSAGE
	 *            - error message name in model.
	 * 
	 * @return true if received file is null.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private boolean validateReceivedFile(MultipartFile file, Model model) {
		boolean result = false;

		if (file == null) {

			result = true;

			model.addAttribute(MODEL_ATTRIBUTE_MESSAGE, FILE_NULL_ERROR_MESSAGE);

			LOGGER.error("Null file received.");
		}

		return result;
	}
}