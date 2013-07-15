package com.gmail.fubuky35.wothelock.lock;

import com.gmail.fubuky35.wothelock.mail.AlertMailThread;
import com.gmail.fubuky35.wothelock.preference.SaveLoadManager;
import com.gmail.fubuky35.wothelock.reversi.GameStater;
import com.gmail.fubuky35.wothelock.reversi.lock.ReversiLock;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class LockUtil{

	private static boolean isShowen = false;
	private static boolean isUnlock = false;
	private static Thread unlocFlagTimer = null;
	private static final long UNLOCK_FLAG_INTERVAL = 1800000;
	
	// めんどいからシングルトン
	private static LockUtil instance = new LockUtil();
	
	private AlertMailThread mAlertMailThread = null;
	
	private LockUtil(){
	}
	
	public static LockUtil getInstance(){
		return instance;
	}
	
	// ロック画面用
	private View mLockView = null;
	private WindowManager mWindowManager = null;
	
	public void lock( Context _c ){
		System.out.println("LockUtil.lock");
		
		// 設定値を取得
		SaveLoadManager sm =  SaveLoadManager.getInstance(_c);
		boolean isLockEnabled = sm.loadLockEnable();
		boolean isMailEnabled = sm.loadAlertMailEnable();
		int lockCount = sm.loadLockPatternCount();
		
		if ( isLockEnabled && 0 < lockCount && !isShowen()){
			System.out.println("lock show");
			
			setShowen(true);
			
			// ロック有効の場合
			
			// ロック解除パターンのロード
			ReversiLock.init(_c);
			
			// ロック画面を作って
			mLockView = GameStater.createReversiView(_c);
			
			// android.view.WindowManager.LayoutParams
			// ロック画面表示用のパラメータ
			LayoutParams params = new LayoutParams();
			
			// 全画面
			params.width = LayoutParams.MATCH_PARENT;
			params.height = LayoutParams.MATCH_PARENT;
			
			// SystemAlert
			params.type = LayoutParams.TYPE_SYSTEM_ALERT;
			
//			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){

				// フラグセット
				params.flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | 
						WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
//			}
			
			// WindowManagerに追加
			if ( mWindowManager == null ){
				mWindowManager = (WindowManager)_c.getSystemService(Context.WINDOW_SERVICE);
			}
			mWindowManager.addView(mLockView, params);
			
			GameStater.startGame();
			
			if (isMailEnabled) {
				mAlertMailThread = new AlertMailThread(_c);
				System.out.println("AlertMailThread new");
			}
			
		}else{
			// ロックが無効
			System.out.println("lock disable "+ isShowen());
		}
	}
	
	public synchronized void unlock(){
		stoptMailThread();
		
		// WindowManagerから削除
		mWindowManager.removeView(mLockView);
		
		mLockView = null;
		
		setShowen(false);
		
		setUnlock(true);
		
		if(null == unlocFlagTimer){
			unlocFlagTimer = new Thread(){
				public void run() {
					setName("UNLOCK FLAG TIMER");
					try {
						sleep(UNLOCK_FLAG_INTERVAL);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					setUnlock(false);
					unlocFlagTimer = null;
					System.out.println("unlocFlagTimer end");
				}
			};
			
			unlocFlagTimer.start();
		}
		
	}
	
//	@Override
//	public void onClick(View v) {
//		switch ( v.getId() ){
//		case R.id.button_unlock:
//			// 解除ボタン押下時、解除処理を呼ぶ
//			unlock();
//		}
//	}

	public static boolean isShowen() {
		return isShowen;
	}

	public static void setShowen(boolean isShowen) {
		LockUtil.isShowen = isShowen;
	}
	
	public void startMailThread() {
		if(null != mAlertMailThread){
			mAlertMailThread.start();
			System.out.println("AlertMailThread start");
		}
	}
	
	public void stoptMailThread() {
		if(null != mAlertMailThread){
			mAlertMailThread.stopRun();
			mAlertMailThread = null;
			System.out.println("AlertMailThread stop");
		}
	}
	
	
	public static boolean isUnlock() {
		return isUnlock;
	}

	public static void setUnlock(boolean isUnlock) {
		LockUtil.isUnlock = isUnlock;
	}

	
//	// キーガード用
//	private KeyguardManager mKeyguard = null;
//	private KeyguardManager.KeyguardLock mLock = null;
//	
//	public void disableKeyguard( Context _c ){
//		
//		// 初期化して
//		if ( mKeyguard == null ){
//			mKeyguard = (KeyguardManager)_c.getSystemService(Context.KEYGUARD_SERVICE);
//			mLock = mKeyguard.newKeyguardLock("LockUtil");
//		}
//		
//		// キーガードを無効化
//		mLock.disableKeyguard();
//		
//	}
//	
//	public void enableKeyguard(){
//		// キーガードを有効化
//		if ( mLock != null ){
//			mLock.reenableKeyguard();
//		}else{
//			// nullの場合は無効化されて無い
//		}
//		
//	}

}
