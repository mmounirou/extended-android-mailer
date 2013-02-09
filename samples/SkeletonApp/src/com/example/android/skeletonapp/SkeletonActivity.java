/*
 * Copyright (C) 2007 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.example.android.skeletonapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Folder;
import javax.mail.Message;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.mmounirou.android.mail.MailReceiver;
import com.mmounirou.android.mail.MailSender;
import com.mmounirou.android.mail.commons.MessageHelper;
import com.mmounirou.android.mail.commons.User;
import com.mmounirou.android.mail.configuration.ServerConfigFactory;
import com.mmounirou.android.mail.configuration.ServerConfigProvider;

/**
 * This class provides a basic demonstration of how to write an Android activity. Inside of its window, it places a
 * single view: an EditText that displays and edits some internal text.
 */
public class SkeletonActivity extends Activity
{

	private static final String PASSWORD = "XXXXXXX";
	private static final String USERNAME = "XXXXXXXXX@gmail.com";
	static final private int BACK_ID = Menu.FIRST;
	static final private int CLEAR_ID = Menu.FIRST + 1;

	private EditText mEditor;

	public SkeletonActivity()
	{
	}

	/** Called with the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Inflate our UI from its XML layout description.
		setContentView(R.layout.skeleton_activity);

		// Find the text editor view inside the layout, because we
		// want to do various programmatic things with it.
		mEditor = (EditText) findViewById(R.id.editor);

		// Hook up button presses to the appropriate event handler.
		((Button) findViewById(R.id.back)).setOnClickListener(mBackListener);
		((Button) findViewById(R.id.clear)).setOnClickListener(mClearListener);

		mEditor.setText(getText(R.string.main_label));

		// sendMail();
		receiveMail();

	}

	private void receiveMail()
	{
		String username = USERNAME;
		String password = PASSWORD;

		try
		{
			ServerConfigProvider provider = ServerConfigFactory.fromEmailAdress(username);
			MailReceiver mailReceiver = new MailReceiver(provider, new User(username, password));

			for ( Folder folders : mailReceiver.getFolders() )
			{
				try
				{
					System.out.println(folders.getFullName() + " " + folders.getMessageCount());
				}
				catch ( Exception e )
				{
					System.err.println(e.getMessage());
				}
			}

			Folder folder = mailReceiver.getFolderByName("Inbox");
			folder.open(Folder.READ_ONLY);
			for ( Message message : folder.getMessages() )
			{
				List<File> attachments = MessageHelper.getAttachments(Environment.getExternalStorageDirectory(), message);
				if (! attachments.isEmpty() )
				{
					for ( File attachment : attachments )
					{
						System.out.println(String.format("from %s subject %s attachmentname %s", message.getFrom()[0], message.getSubject(), attachment.getName()));
					}
				}
				else
				{
					System.out.println(String.format("from %s subject %s no attachement", message.getFrom()[0], message.getSubject()));
				}

			}

			mailReceiver.close();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}

	}

	private void sendMail()
	{
		String username = USERNAME;
		String password = PASSWORD;
		List<String> tos = new ArrayList<String>();
		tos.add("mmounirou@mmounirou.com");

		try
		{
			ServerConfigProvider provider = ServerConfigFactory.fromEmailAdress(username);
			MailSender expeditor = new MailSender(provider);
			List<File> attachments = new ArrayList<File>();
			File file = createFile(new File(Environment.getExternalStorageDirectory().getPath(), "mysdfile.txt"), "This is a siimple text for attachment testing");

			attachments.add(file);
			expeditor.sendMail(new User(username, password), "Test Send Mail From Android", "Mail Sended", tos, attachments);

		}
		catch ( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private File createFile(File myFile, String text) throws IOException, FileNotFoundException
	{
		myFile.createNewFile();
		FileOutputStream fOut = new FileOutputStream(myFile);
		fOut.write(text.getBytes());
		fOut.close();
		return myFile;
	}

	/**
	 * Called when the activity is about to start interacting with the user.
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
	}

	/**
	 * Called when your activity's options menu needs to be created.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);

		// We are going to create two menus. Note that we assign them
		// unique integer IDs, labels from our string resources, and
		// given them shortcuts.
		menu.add(0, BACK_ID, 0, R.string.back).setShortcut('0', 'b');
		menu.add(0, CLEAR_ID, 0, R.string.clear).setShortcut('1', 'c');

		return true;
	}

	/**
	 * Called right before your activity's option menu is displayed.
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		super.onPrepareOptionsMenu(menu);

		// Before showing the menu, we need to decide whether the clear
		// item is enabled depending on whether there is text to clear.
		menu.findItem(CLEAR_ID).setVisible(mEditor.getText().length() > 0);

		return true;
	}

	/**
	 * Called when a menu item is selected.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch ( item.getItemId() )
		{
		case BACK_ID:
			finish();
			return true;
		case CLEAR_ID:
			mEditor.setText("");
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * A call-back for when the user presses the back button.
	 */
	OnClickListener mBackListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			finish();
		}
	};

	/**
	 * A call-back for when the user presses the clear button.
	 */
	OnClickListener mClearListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			mEditor.setText("");
		}
	};
}
