package com.insready.callerid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.insready.drupalcloud.RESTServerClient;

public class CallerIDService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		TelephonyManager tm = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		
		PhoneStateListener listener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				switch (state) {
				case TelephonyManager.CALL_STATE_RINGING:
					RESTServerClient sandbox = new RESTServerClient(
							getString(R.string.SERVER), getString(R.string.DOMAIN));
					String result = null;
					String title = null;
					String addressName = null;
					JSONObject jso;
					JSONArray jsa;
					try {
						result = sandbox.viewsGet("callerinfo", "", incomingNumber + "/en", 0, 10);
						jsa = new JSONArray(result);
						jso = jsa.getJSONObject(0);
						title = jso.getString("node_title");
						addressName = jso.getString("location_name");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Toast.makeText(CallerIDService.this,
							"来电人： " + title + "\n来电人地址: " + addressName,
							Toast.LENGTH_LONG).show();
					break;
				}
			}
		};
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
}
