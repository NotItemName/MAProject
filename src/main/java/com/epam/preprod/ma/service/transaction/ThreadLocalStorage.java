package com.epam.preprod.ma.service.transaction;

import java.sql.Connection;

/**
 * Provides thread-local connections.
 * 
 * @author Oleksandr Lagoda, Leonid Polyakov
 * 
 * @version 1.0
 * 
 * @see ThreadLocal
 * 
 */
public class ThreadLocalStorage {

	public static final ThreadLocal<Connection> localStorage = new ThreadLocal<>();

	public static void setConnection(Connection connection) {
		localStorage.set(connection);
	}

	public static Connection getConnection() {
		return localStorage.get();
	}

	public static void removeConnection() {
		localStorage.remove();
	}

}