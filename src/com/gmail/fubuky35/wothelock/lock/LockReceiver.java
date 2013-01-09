package com.gmail.fubuky35.wothelock.lock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LockReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		// サービス呼ぶだけ
		Intent service = new Intent(context, LockService.class);
		service.setAction(intent.getAction());
		context.startService(service);
			
	}
}
