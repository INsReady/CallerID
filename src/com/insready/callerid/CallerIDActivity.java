package com.insready.callerid;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class CallerIDActivity extends Activity {
	TextView mTextOut;
	private EditText mUsername;
	private EditText mPassword;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mUsername = (EditText) findViewById(R.id.login_username);
		mPassword = (EditText) findViewById(R.id.login_password);
		Intent mServiceIntent = new Intent(this, CallerIDService.class);
		this.startService(mServiceIntent);
	}
	
	private class login extends AsyncTask<Object, String, Boolean> {
		private ProgressDialog proDialog = new ProgressDialog(CallerIDActivity.this);
			@Override
			protected Boolean doInBackground(Object... dialogs) {
				publishProgress("Signing in to callerid.insready.com ...");
				String PREFS_AUTH = getString(R.string.sharedpreferences_name);
				SharedPreferences auth = getSharedPreferences(PREFS_AUTH, 0);
				RESTServerClient callerid = new RESTServerClient(CallerIDActivity.this, getString(R.string.sharedpreferences_name), 
						getString(R.string.SERVER), getString(R.string.DOMAIN), Long
						.parseLong(getString(R.string.SESSION_LIFETIME)));
				String result = null;
				try {
					result = callerid.userLogin(mUsername.getText().toString(),
							mPassword.getText().toString());
				} catch (ServiceNotAvailableException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				return storeUserInfo(auth, result, CallerIDActivity.this, mPassword
						.getText().toString());
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				setResult(RESULT_OK);
				finish();
			} else {
				Toast.makeText(CallerIDActivity.this,
						"Wrong username or password. Please try it again.",
						Toast.LENGTH_LONG).show();
				}
				proDialog.dismiss();
		}

		@Override
		protected void onProgressUpdate(String... msg) {
			proDialog.setMessage(msg[0]);
			proDialog.show();
		}
	}
	
	public static boolean storeUserInfo(SharedPreferences auth, String result,
			Activity ctx, String password) {
		JSONObject jso;
		JSONObject ujso;
		try {
			jso = new JSONObject(result);
			ujso = jso.getJSONObject("user");
			// Save user data to storage
			SharedPreferences.Editor editor = auth.edit();
			editor.putString("sessionid", jso.getString("sessid"));
			editor.putLong("sessionid_timestamp", new Date().getTime() / 1000);
			editor.putInt("uid", ujso.getInt("uid"));
			editor.putString("name", ujso.getString("name"));
			editor.putString("mail", ujso.getString("mail"));
			editor.putString("pass", password);
			editor.commit();
			
			return true;
		} catch (JSONException e) {
			return false;
		}
	}
	
	public void onConfirmClick(View v) {
		if (mUsername.getText().length() != 0
				&& mPassword.getText().length() != 0) {
			new login().execute();
		} else
			Toast.makeText(CallerIDActivity.this,
					"Please put username and password in the login dialog.",
					Toast.LENGTH_LONG).show();
	}
}