package com.gmail.fubuky35.wothelock.lock;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class LockService extends Service {

	private TelephonyManager telephonyManager;
	// private boolean isVanishing = false;

	private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state, String number) {
			// 着信中と通話中はロック画面を表示しない
			if(state != TelephonyManager.CALL_STATE_IDLE && LockUtil.isShowen()){
				LockUtil.getInstance().unlock();
				// isVanishing = true;
			}
//			else if(state == TelephonyManager.CALL_STATE_IDLE && isVanishing){
//				// 待ちうけ状態に戻ったらロックをセット
//				LockUtil.getInstance().lock(getApplicationContext());
//				isVanishing = false;
//			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();

		// 通話状態リスナーの取得
		telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		if (null != telephonyManager) {
			telephonyManager.listen(mPhoneStateListener,
					PhoneStateListener.LISTEN_CALL_STATE);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("LockService.onStartCommand");
		
		String action = null;
		
		if (intent != null ) {
			action = intent.getAction();
		}
		
		
		if (action != null) {
			
			if( action.equals(Intent.ACTION_SCREEN_OFF) ||
					( action.equals(Intent.ACTION_SCREEN_ON ) && !LockUtil.isUnlock() ) ){
			
				// ロック処理呼ぶ(電話中でなければ)
				if(TelephonyManager.CALL_STATE_IDLE == telephonyManager.getCallState()){
					LockUtil.getInstance().lock(this);
				}
			
			}
			
			System.out.println("intent:"+action);
			
//			else {
//				isVanishing = true;
//			}

//			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2
//					&& Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
//				if (SaveLoadManager.getInstance(this).loadLockEnable()) {
//					// ロックが有効になっている場合、キーガードを無効化
//					LockUtil.getInstance().disableKeyguard(this);
//				}
//			}
		}

		return super.onStartCommand(intent, flags, startId);
	}

	// @Override
	// public void onStart(Intent intent, int startId) {
	// super.onStart(intent, startId);
	//
	// if ( intent != null && intent.getAction() != null ){
	//
	// // ロック処理呼ぶ
	// LockUtil.getInstance().lock(this);
	//
	// if ( Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ){
	// if ( SaveLoadManager.getInstance(this).loadLockEnable()){
	// // ロックが有効になっている場合、キーガードを無効化
	// LockUtil.getInstance().disableKeyguard(this);
	// }
	// }
	//
	// }
	//
	// }

}
