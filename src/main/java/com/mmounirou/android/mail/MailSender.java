package com.mmounirou.android.mail;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.mmounirou.android.mail.commons.Preconditions;
import com.mmounirou.android.mail.commons.User;
import com.mmounirou.android.mail.configuration.ServerConfigProvider;
import com.mmounirou.android.mail.exception.MailSenderException;

public class MailSender extends javax.mail.Authenticator
{
	static
	{
		// There is something wrong with MailCap, javamail can not find a
		// handler for the multipart/mixed part, so this bit needs to be added.
		MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
		mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
		mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
		mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
		mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
		mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
		CommandMap.setDefaultCommandMap(mc);
	}

	private Properties m_properties;

	public MailSender(ServerConfigProvider provider)
	{
		Preconditions.checkNotNull(provider);

		m_properties = new Properties();
		provider.updateSendMailConfig(m_properties);
	}

	//@formatter:off
	public void sendMail(User sender,
						String subject, 
						String body, 
						List<String> tos) throws MailSenderException
	//@formatter:on
	{
		sendMail(sender, subject, body, tos, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<File>());
	}

	//@formatter:off
	public void sendMail(User sender,
						String subject, 
						String body, 
						List<String> tos, 
						List<File> attachments) throws MailSenderException
	//@formatter:on
	{
		sendMail(sender, subject, body, tos, new ArrayList<String>(), new ArrayList<String>(), attachments);

	}

	//@formatter:off
	public void sendMail(User sender,
						String subject, 
						String body, 
						List<String> tos, 
						List<String> ccs,
						List<String> bbcs, 
						List<File> attachments) throws MailSenderException
	//@formatter:on
	{

		Preconditions.checkNotNull(sender);
		Preconditions.checkNotBlank(subject);
		Preconditions.checkNotBlank(body);
		Preconditions.checkNotNull(tos);
		Preconditions.checkNotNull(ccs);
		Preconditions.checkNotNull(bbcs);
		Preconditions.checkElementIndex(0, tos.size());

		try
		{
			Session session = Session.getInstance(m_properties,sender.getAuthentificator());
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(sender.getFrom()));

			msg.setSubject(subject);
			msg.setSentDate(new Date());

			addRecipients(MimeMessage.RecipientType.TO, tos, msg);
			addRecipients(MimeMessage.RecipientType.BCC, bbcs, msg);
			addRecipients(MimeMessage.RecipientType.CC, ccs, msg);

			// setup message body
			Multipart msgContent = new MimeMultipart();

			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(body);

			msgContent.addBodyPart(messageBodyPart);
			addAttachements(msgContent, attachments);

			msg.setContent(msgContent);

			// send email
			Transport.send(msg);

		}
		catch ( AddressException e )
		{
			throw new MailSenderException(e);
		}
		catch ( MessagingException e )
		{
			throw new MailSenderException(e);
		}

	}

	private void addAttachements(Multipart msgContent, List<File> attachments) throws MessagingException
	{
		for ( File attachement : attachments )
		{
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setDataHandler(new DataHandler(new FileDataSource(attachement)));
			messageBodyPart.setFileName(attachement.getName());

			msgContent.addBodyPart(messageBodyPart);
		}
	}

	private void addRecipients(RecipientType recipientsType, List<String> recipients, MimeMessage msg) throws AddressException, MessagingException
	{
		if ( recipients.isEmpty() )
		{
			return;
		}

		InternetAddress[] addressTo = new InternetAddress[recipients.size()];

		for ( int i = 0; i < recipients.size(); i++ )
		{
			addressTo[i] = new InternetAddress(recipients.get(i));
		}
		msg.setRecipients(recipientsType, addressTo);
	}
}
