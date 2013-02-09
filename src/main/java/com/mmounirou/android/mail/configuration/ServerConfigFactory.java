package com.mmounirou.android.mail.configuration;

import java.util.HashMap;
import java.util.Map;

import com.mmounirou.android.mail.commons.Preconditions;
import com.mmounirou.android.mail.configuration.providers.GmailProvider;

public class ServerConfigFactory
{
	private static Map<String, ServerConfigProvider> m_providers = new HashMap<String, ServerConfigProvider>();

	static
	{
		m_providers.put("gmail.com", new GmailProvider());
	}

	public static void registerSmtpServer(String mailServer, ServerConfigProvider provider)
	{
		m_providers.put(Preconditions.checkNotBlank(mailServer), Preconditions.checkNotNull(provider));
	}

	public static ServerConfigProvider fromEmailAdress(String username)
	{
		Preconditions.checkNotBlank(username);
		String[] splittedEmail = username.split("@");
		Preconditions.checkArgument(splittedEmail.length == 2, "malformed email adress");
		return m_providers.get(splittedEmail[1]);
	}
}
