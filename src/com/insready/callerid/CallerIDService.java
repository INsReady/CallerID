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
					RESTServerClient caller = new RESTServerClient(CallerIDService.this, getString(R.string.sharedpreferences_name), 
							getString(R.string.SERVER), getString(R.string.DOMAIN), Long
							.parseLong(getString(R.string.SESSION_LIFETIME)));
					String result = null;
					Boolean isflagged = null;
					String title = null;
					String addressName = null;
					JSONObject jso;
					JSONArray jsa;
					String flag = null;
					try {
						isflagged = caller.flagIsFlagged("spam", 1, 2);
						result = caller.viewsGet("callerinfo", "", incomingNumber + "/en", 0, 10);
						jsa = new JSONArray(result);
						jso = jsa.getJSONObject(0);
						title = jso.getString("node_title");
						addressName = jso.getString("location_name");
						if(isflagged){
							flag = "这个号码已经被你屏蔽";
						}else {
							flag = "这个号码没有被你屏蔽";
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Toast.makeText(CallerIDService.this,
							"来电人： " + title + "\n来电人地址: " + addressName + "\n" + flag,
							Toast.LENGTH_LONG).show();
					
					break;
				}
			}
		};
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
}
