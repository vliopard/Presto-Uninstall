package com.otdshco.uninstall;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.otdshco.uninstall.R;

public class UninstallSettings extends
		PreferenceActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.uninstall_sample_preferences);
	}
}
