package com.epam.preprod.ma.updater;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Invoke <code>{@linkplain TsvUpdater#update(String, boolean)}</code> method
 * with predefined parameters.
 * </p>
 * <p>
 * <code>{@link #update()}</code> method is called by
 * <code>Quartz Scheduler</code> in specific period of time which sets in
 * settings.properties configuration file.
 * </p>
 * 
 * @author Oleksandr Lagoda
 * 
 * @version 1.0
 * 
 */
@Component("updaterInvoker")
public class UpdaterInvoker {

	private static final Logger LOGGER = Logger.getLogger(UpdaterInvoker.class);

	@Autowired
	private IDatabaseUpdater databaseUpdater;

	@Value("${updater.directorypath}")
	private String directorypath;

	@Value("${updater.filename}")
	private String shortFilename;

	/**
	 * Search for newest file in predefined directory and invoke
	 * <code>{@linkplain TsvUpdater#update(String, boolean)}</code> method to
	 * proceed file which was found.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	public void update() {

		final String filepath = getLatestFile();

		if (filepath == null) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.error("No valid file found in directory "
						+ directorypath + ".");
			}
			return;
		}

		databaseUpdater.update(getLatestFile(), false);
	}

	/**
	 * Search for last modified 'wl_export' file in specified directory.
	 * 
	 * @return full path to file or null if nothing was found.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private String getLatestFile() {

		final String fileExtension = ".tsv";

		File directory = new File(directorypath);
		File newestFile = null;

		if (!directory.exists()) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.error("Directory " + directory + " is not exist.");
			}
			return null;
		}

		for (File file : directory.listFiles()) {

			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Proceed file: " + file.getName());
			}

			if (file.isFile()) {

				String fileName = file.getName();

				if (fileName.endsWith(fileExtension)
						&& fileName.contains(shortFilename)) {

					newestFile = getNewestFile(file, newestFile);

				}
			}
		}

		if (newestFile != null) {
			return newestFile.getPath().toString();
		}

		return null;
	}

	/**
	 * Select newest file from two.
	 * 
	 * @param currentFile
	 *            - first file to compare.
	 * 
	 * @param newestFile
	 *            - second file to compare.
	 * 
	 * @return newest file.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	private File getNewestFile(File currentFile, File newestFile) {

		if (currentFile == null) {
			return newestFile;
		} else if (newestFile == null) {
			return currentFile;
		} else {
			Calendar currentFileModifyDate = GregorianCalendar.getInstance();
			Calendar newestFileModifyDate = GregorianCalendar.getInstance();

			currentFileModifyDate.setTime(new Date(currentFile.lastModified()));
			newestFileModifyDate.setTime(new Date(newestFile.lastModified()));

			if (currentFileModifyDate.after(newestFileModifyDate)) {
				return currentFile;
			} else {
				return newestFile;
			}
		}
	}

}