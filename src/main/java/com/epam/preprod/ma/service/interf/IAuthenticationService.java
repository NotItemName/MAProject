package com.epam.preprod.ma.service.interf;

public interface IAuthenticationService {
	String login(String login, String password);

	void logout(String sessionHash);
}
