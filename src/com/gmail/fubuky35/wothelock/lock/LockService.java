package com.gmail.fubuky35.wothelock.lock;

import com.gmail.fubuky35.wothelock.preference.SaveLoadManager;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class LockService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("LockService.onStartCommand");
		
		
		if (intent != null && intent.getAction() != null) {

			// ロック処理呼ぶ
			LockUtil.getInstance().lock(this);

			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2
					&& Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
				if (SaveLoadManager.getInstance(this).loadLockEnable()) {
					// ロックが有効になっている場合、キーガードを無効化
					LockUtil.getInstance().disableKeyguard(this);
				}
			}
		}
		
		return super.onStartCommand(intent, flags, startId);
	}

	
//	@Override
//	public void onStart(Intent intent, int startId) {
//	    super.onStart(intent, startId);
//	    
//	    if ( intent != null && intent.getAction() != null ){
//	    	
//		    // ロック処理呼ぶ
//		    LockUtil.getInstance().lock(this);
//		    
//	    	if ( Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ){
//	    	    if ( SaveLoadManager.getInstance(this).loadLockEnable()){
//	    	    	// ロックが有効になっている場合、キーガードを無効化
//	    	    	LockUtil.getInstance().disableKeyguard(this);
//	    	    }
//	    	}
//	    	
//	    }
//	    
//	}
	
}
