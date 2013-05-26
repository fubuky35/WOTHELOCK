package com.gmail.fubuky35.wothelock.lock;

import com.gmail.fubuky35.wothelock.preference.WothelockPreferenceActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

public class NullActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		System.out.println("NullActivity onCreate");
		
		LockUtil.getInstance().setBackgroundActivity(this);
		
		if (!LockUtil.isShowen()) {
			
			Intent i = new Intent(this, WothelockPreferenceActivity.class);
			// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
			
			// finish();
		}

		// setContentView(R.layout.null_activity);

	}

//	@Override
//	protected void onResume() {
//		super.onResume();
//		System.out.println("onResume");
//	}
//
//	
//	@Override
//	protected void onRestart() {
//		super.onRestart();
//		
//		System.out.println("onRestart");
//	}

	@Override
	protected void onDestroy() {
		System.out.println("NullActivity onDestroy");
		
		LockUtil.getInstance().setBackgroundActivity(null);
		
		super.onDestroy();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		System.out.println("NullActivity onTouchEvent");
		
		finish();
		
		return super.onTouchEvent(event);
	}
	

}
