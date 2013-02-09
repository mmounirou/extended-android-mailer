package com.mmounirou.android.mail;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import com.mmounirou.android.mail.commons.Preconditions;
import com.mmounirou.android.mail.commons.User;
import com.mmounirou.android.mail.configuration.ServerConfigProvider;
import com.mmounirou.android.mail.exception.MailReceiverException;
import com.mmounirou.android.mail.exception.MailSenderException;

public class MailReceiver
{
	public static final String PROTOCOL = "mail.store.protocol";
	public static final String IMAP_HOST = "mail.imap.host";

	private Properties m_properties;
	private Store store;

	public MailReceiver(ServerConfigProvider provider, User user) throws MailReceiverException, MailSenderException
	{

		Preconditions.checkNotNull(provider);

		try
		{

			m_properties = new Properties();
			provider.updateReceiveMailConfig(m_properties);

			Session session = Session.getInstance(m_properties, null);
			store = session.getStore(m_properties.getProperty(PROTOCOL));
			store.connect(m_properties.getProperty(IMAP_HOST), user.getUsername(), user.getPassword());
		}
		catch ( NoSuchProviderException e )
		{
			throw new MailSenderException(e);
		}
		catch ( MessagingException e )
		{
			throw new MailSenderException(e);

		}

	}

	public Folder getFolderByName(String strName) throws MailReceiverException
	{
		try
		{
			return store.getFolder(strName);
		}
		catch ( MessagingException e )
		{
			throw new MailReceiverException(e);
		}
	}

	public Folder[] getFolders() throws MailReceiverException
	{
		try
		{
			return store.getDefaultFolder().list();
		}
		catch ( MessagingException e )
		{
			throw new MailReceiverException(e);
		}
	}

	public void close()
	{
		// store.close();
	}

}
