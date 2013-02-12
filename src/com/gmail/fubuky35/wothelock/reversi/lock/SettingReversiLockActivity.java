package com.gmail.fubuky35.wothelock.reversi.lock;

import java.util.ArrayList;

import com.gmail.fubuky35.wothelock.R;
import com.gmail.fubuky35.wothelock.reversi.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class SettingReversiLockActivity extends Activity implements OnClickListener{

	SettingReversiView mReversiView = null;
	Animation mAnimWinner = null;
	Animation mAnimFadeOut = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Utils.d("GameActivity.onCreate");
		
		mAnimWinner = AnimationUtils.loadAnimation(this, R.anim.winner);
		mAnimFadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);

		setContentView(R.layout.reversi_background);
		mReversiView = new SettingReversiView(this);
		ArrayList<View> arr = new ArrayList<View>();
		arr.add(mReversiView);
		
		FrameLayout frame;
		frame = (FrameLayout)this.findViewById(R.id.frame);
		frame.addView(mReversiView, 0);			//一番奥にReversiViewを追加。
		
		View vwBack = (View)findViewById(R.id.vwBack);
		vwBack.setVisibility(View.INVISIBLE);
		
		TextView txt = (TextView)findViewById(R.id.txtWinner);
		txt.setVisibility(View.INVISIBLE);
		
		Button btn_detect = (Button) findViewById(R.id.detect_button);
		btn_detect.setOnClickListener(this);
		
		Button btn_back = (Button) findViewById(R.id.back_button);
		btn_back.setOnClickListener(this);
		
		Button btn_cancle = (Button) findViewById(R.id.cancel_button);
		btn_cancle.setOnClickListener(this);
	}
	
	@Override
	protected void onPause() {
		Utils.d("GameActivity.onPause");

		//Pref.setState(this.getApplicationContext(), mReversiView.getState());
		
		//別スレッドで思考ルーチンが動いていれば中断する。
		mReversiView.pause();
		
		super.onPause();
	}

	@Override
	protected void onResume() {
		Utils.d("GameActivity.onResume");
		
		mReversiView.resume(null);

		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.detect_button:
			mReversiView.save(this);
			finish();
			break;
		case R.id.cancel_button:
			finish();
			break;	
		case R.id.back_button:
			mReversiView.back();
			break;
		default:
			break;
		}
		
	}
}
