package com.gmail.fubuky35.wothelock.reversi;

import java.util.ArrayList;

import com.gmail.fubuky35.wothelock.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

// public class GameActivity extends Activity{

public class GameStater {

	private static ReversiView mReversiView = null;
	private static View mReversiBackground = null;
	
	private static Animation mAnimWinner = null;
	private static Animation mAnimFadeOut = null;
	private static Animation mAnimGrayout = null;
	private static Animation mAnimGrayin = null;
	
	private GameStater(){}
	
	public static View createReversiView(Context context) {

		Utils.d("GameActivity.onCreate");
		
		mAnimWinner = AnimationUtils.loadAnimation(context, R.anim.winner);
		mAnimFadeOut = AnimationUtils.loadAnimation(context, R.anim.fadeout);
		mAnimGrayout = AnimationUtils.loadAnimation(context, R.anim.grayout);
		mAnimGrayin =  AnimationUtils.loadAnimation(context, R.anim.grayin);

		mReversiBackground = LayoutInflater.from(context).inflate(R.layout.reversi_background, null);
		
		//setContentView(R.layout.main);
		mReversiView = new ReversiView(context);
		ArrayList<View> arr = new ArrayList<View>();
		arr.add(mReversiView);
		
		FrameLayout frame;
		frame = (FrameLayout)mReversiBackground.findViewById(R.id.frame);
		frame.addView(mReversiView, 0);			//一番奥にReversiViewを追加。
		
		TextView txt = (TextView)mReversiBackground.findViewById(R.id.txtWinner);
		txt.bringToFront();
		
		// ロックパターン設定用のボタンは消す
		LinearLayout bttnArea = (LinearLayout) mReversiBackground.findViewById(R.id.settting_button_area);
		bttnArea.setVisibility(View.GONE);
		
		return mReversiBackground;
	}
	
//	@Override
//	protected void onPause() {
//		Utils.d("GameActivity.onPause");
//
//		Pref.setState(this.getApplicationContext(), mReversiView.getState());
//		
//		//別スレッドで思考ルーチンが動いていれば中断する。
//		mReversiView.pause();
//		
//		super.onPause();
//	}

	
	public static void startGame() {
		Utils.d("GameActivity.onResume");
		
		if( null != mReversiView ){
			mReversiView.resume(null);
		}

	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.mainmenu, menu);
//		return super.onCreateOptionsMenu(menu);
//	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()){
//		case R.id.mnuExit:
//			finish();
//			break;
//		case R.id.mnuPref:
//			openPref();
//			break;
////		case R.id.mnuStat:
////			mReversiView.showCountsToast();
////			break;
//		case R.id.mnuInit: 
//			mReversiView.init(true);
//			break;
//		default:
//			break;
//		}
//		return super.onOptionsItemSelected(item);
//	}
//
//	//設定画面を開く
//	private void openPref() {
//		Intent intent = new Intent(this, Pref.class); 
//		startActivity(intent);
//	}
//
	public static void showWinner(String msg){
		TextView txt = (TextView)mReversiBackground.findViewById(R.id.txtWinner);
		txt.setText(msg);
		if (txt.getVisibility() == View.INVISIBLE){
			txt.setVisibility(View.VISIBLE);
			txt.startAnimation(mAnimWinner);
		}
		
		View vwBack = (View)mReversiBackground.findViewById(R.id.vwBack);
		if (vwBack.getVisibility() == View.INVISIBLE){
			vwBack.startAnimation(mAnimGrayin);
			vwBack.setVisibility(View.VISIBLE);
		}
	}

	public static void hideWinner(String msg){
		TextView txt = (TextView)mReversiBackground.findViewById(R.id.txtWinner);
		if (txt.getVisibility() == View.VISIBLE){
			txt.startAnimation(mAnimFadeOut);
			txt.setVisibility(View.INVISIBLE);
		}

		View vwBack = (View)mReversiBackground.findViewById(R.id.vwBack);
		if (vwBack.getVisibility() == View.VISIBLE){
			vwBack.startAnimation(mAnimGrayout);
			vwBack.setVisibility(View.INVISIBLE);
		}
	}
}
