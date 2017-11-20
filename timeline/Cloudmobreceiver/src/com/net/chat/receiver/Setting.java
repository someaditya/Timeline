package com.net.chat.receiver;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Setting extends PreferenceActivity {
	
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		addPreferencesFromResource(R.xml.settings);
	}

}