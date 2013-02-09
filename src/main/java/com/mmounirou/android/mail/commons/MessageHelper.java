package com.mmounirou.android.mail.commons;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

public class MessageHelper
{

	public static List<File> getAttachments(File destFolders, Message message) throws IOException, MessagingException
	{

		List<File> attachments = new ArrayList<File>();

		if ( message.getContent() instanceof Multipart )
		{
			Multipart multipart = (Multipart) message.getContent();

			for ( int i = 0; i < multipart.getCount(); i++ )
			{
				BodyPart bodyPart = multipart.getBodyPart(i);
				if ( Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) )
				{
					InputStream is = bodyPart.getInputStream();
					File f = new File(destFolders, bodyPart.getFileName());
					f.createNewFile();

					FileOutputStream fos = new FileOutputStream(f);
					byte[] buf = new byte[4096];
					int bytesRead;
					while ( (bytesRead = is.read(buf)) != -1 )
					{
						fos.write(buf, 0, bytesRead);
					}
					fos.close();
					attachments.add(f);
				}
			}

		}

		return attachments;

	}

}
