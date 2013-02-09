package com.mmounirou.android.mail.commons;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;


public class User
{
	private final String username;
	private final String password;
	private final String from;

	public User(String username, String password)
	{
		this(username, password, username);
	}

	public User(String username, String password, String from)
	{
		this.username = Preconditions.checkNotBlank(username);
		this.password = Preconditions.checkNotNull(password);
		this.from = Preconditions.checkNotBlank(from);
	}

	public String getFrom()
	{
		return from;
	}

	public Authenticator getAuthentificator()
	{
		return new Authenticator()
		{
			@Override
			protected PasswordAuthentication getPasswordAuthentication()
			{
				// TODO Auto-generated method stub
				return new PasswordAuthentication(username, password);
			}
		};
	}

	public String getUsername()
	{
		return username;
	}

	public String getPassword()
	{
		return password;
	}
	
	

}
