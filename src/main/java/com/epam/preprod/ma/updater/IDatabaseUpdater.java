package com.epam.preprod.ma.updater;

/**
 * Update database in real time.
 * 
 * @author Oleksandr Lagoda
 * 
 * @version 1.0
 * 
 */
public interface IDatabaseUpdater {

	/**
	 * Update database depending on the conditions.
	 * 
	 * @param filepath
	 *            - full path to source file.
	 * 
	 * @param deleteFile
	 *            - flag which tells delete file or not after updating.
	 * 
	 * @author Oleksandr Lagoda
	 * 
	 */
	public void update(String filepath, boolean deleteFile);

}