package com.insready.callerid;

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
					Toast.makeText(CallerIDService.this,
							"我要阻挡这个号码，呜哈: " + incomingNumber,
							Toast.LENGTH_LONG).show();
					String result = null;
					try {
						result = sandbox.viewsGet("callerinfo", "", "15058643709", 0, 10);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
		};
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}
}
