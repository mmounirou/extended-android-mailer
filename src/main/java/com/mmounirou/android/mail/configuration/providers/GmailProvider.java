package com.mmounirou.android.mail.configuration.providers;

import java.util.Properties;

import com.mmounirou.android.mail.MailReceiver;
import com.mmounirou.android.mail.commons.Preconditions;
import com.mmounirou.android.mail.configuration.ServerConfigFactory;
import com.mmounirou.android.mail.configuration.ServerConfigProvider;

public class GmailProvider implements ServerConfigProvider
{

	static
	{
		ServerConfigFactory.registerSmtpServer("gmail.com", new GmailProvider());
	}
	
	public void updateSendMailConfig(Properties props)
	{
		Preconditions.checkNotNull(props);

		props.put("mail.smtp.host", "smtp.gmail.com");

		props.put("mail.debug", "true");

		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");

	}

	public void updateReceiveMailConfig(Properties props)
	{
		props.setProperty(MailReceiver.PROTOCOL, "imaps");
		props.setProperty(MailReceiver.IMAP_HOST, "imap.gmail.com");
	}

}
