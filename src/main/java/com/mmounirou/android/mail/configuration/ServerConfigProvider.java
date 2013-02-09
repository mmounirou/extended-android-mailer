package com.mmounirou.android.mail.configuration;

import java.util.Properties;

public interface ServerConfigProvider
{
	void updateSendMailConfig(Properties m_properties);

	void updateReceiveMailConfig(Properties m_properties);
}