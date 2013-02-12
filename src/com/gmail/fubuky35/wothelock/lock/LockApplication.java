package com.gmail.fubuky35.wothelock.lock;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class LockApplication extends Application {

	private LockReceiver mReceiver = null;
	
//	private TelephonyManager telephonyManager;
//
//	private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
//		@Override
//		public void onCallStateChanged(int state, String number) {
//			LockUtil.getInstance().setCallState(state);
//			System.out.println("state:"+state);
//		}
//	};

	@Override
	public void onCreate() {
		super.onCreate();

		// ACTION_SCREEN_OFFを受け取るためのIntentFilter
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);

		// LockReceiverでBroadcastを受け取るよう登録
		mReceiver = new LockReceiver();
		registerReceiver(mReceiver, filter);
		
//		// 通話状態リスナーの取得
//		telephonyManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
//		if(null != telephonyManager){
//			telephonyManager.listen(mPhoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);
//		}
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		// 一応解除
		unregisterReceiver(mReceiver);
	}
}
