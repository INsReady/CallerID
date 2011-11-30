package com.insready.callerid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class CallerIDActivity extends Activity {
	TextView mTextOut;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Intent mServiceIntent = new Intent(this, CallerIDService.class);
		this.startService(mServiceIntent);
	}
}