package com.gmail.fubuky35.wothelock.reversi.lock;

import com.gmail.fubuky35.wothelock.lock.LockUtil;
import com.gmail.fubuky35.wothelock.preference.SaveLoadManager;

import android.content.Context;
import android.graphics.Point;

public class ReversiLock {
	
	public static final int LOCK_PATTERN_MAX_SIZE = 4;
	
	public static final String KEY_HEAD = "lock_pattern";
	public static final String KEY_COUNT = "_count";
	public static final String KEY_X = "_x";
	public static final String KEY_Y = "_y";
	
	private static Point[] mLockpattern = new Point[LOCK_PATTERN_MAX_SIZE];
	private static int mLockpatternLength;
	private static int mCurrentCheck;
	
	private ReversiLock(){}
	
	public static void init(Context context){
//		mLockpattern[0] = new Point(0, 0);
//		mLockpattern[1] = new Point(7, 7);
//		mLockpatternLength = 2;
		load(context);
		mCurrentCheck = 0;
	}
	
	private static void load(Context context){
		SaveLoadManager sm = SaveLoadManager.getInstance(context);

		mLockpatternLength = sm.loadLockPatternCount();
		mLockpattern = sm.loadLockPattern();
		
//		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
//		mLockpatternLength = sp.getInt(KEY_HEAD + KEY_COUNT, 0);
//		
//		for(int i = 0;i < mLockpatternLength;++i){
//			mLockpattern[i] = new Point();
//			mLockpattern[i].x = sp.getInt(KEY_HEAD + KEY_X + i, 0);
//			mLockpattern[i].y = sp.getInt(KEY_HEAD + KEY_Y + i, 0);
//		}
	}
	
	public static boolean checkLock(final Point checkPoint){
		
		Point lockPoint = mLockpattern[mCurrentCheck];
		
		if(null == lockPoint){
			LockUtil.getInstance().unlock();
			return true;
		}
		
		if(lockPoint.x == checkPoint.x && lockPoint.y == checkPoint.y){
			++mCurrentCheck;
		} else {
			mCurrentCheck = 0;
		}
		
		if(mCurrentCheck == mLockpatternLength){
			LockUtil.getInstance().unlock();
			return true;
		}
		
		return false;
		
	}
	

	
}
