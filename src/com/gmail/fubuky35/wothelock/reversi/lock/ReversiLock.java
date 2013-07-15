package com.gmail.fubuky35.wothelock.reversi.lock;

import com.gmail.fubuky35.wothelock.lock.LockUtil;
import com.gmail.fubuky35.wothelock.preference.SaveLoadManager;
import com.gmail.fubuky35.wothelock.reversi.model.Cell;
import com.gmail.fubuky35.wothelock.reversi.model.Cell.E_STATUS;

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
	private static boolean isWinLockEnable;
	
	private static E_STATUS MASTER_LOCK_PATTERN[] = {
		E_STATUS.Black,
		E_STATUS.White,
		E_STATUS.Black,
		E_STATUS.White,
		E_STATUS.Black,
		E_STATUS.Black,
		E_STATUS.White,
		E_STATUS.White,
		E_STATUS.White,
	};
	
	private static final int MASTER_LOCK_LEN  = MASTER_LOCK_PATTERN.length;
	private static int mMasterLockCount;
	
	private ReversiLock(){}
	
	public static void init(Context context){

		load(context);
		mCurrentCheck = 0;
		mMasterLockCount = 0;
	}
	
	private static void load(Context context){
		SaveLoadManager sm = SaveLoadManager.getInstance(context);

		mLockpatternLength = sm.loadLockPatternCount();
		mLockpattern = sm.loadLockPattern();
		isWinLockEnable = sm.loadWinLockEnable();
		
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
	
	public static boolean checMasterkLock(final Cell cell){
		
		if(cell != null && cell.getStatus() == MASTER_LOCK_PATTERN[mMasterLockCount] ){
			++mMasterLockCount;
		} else {
			mMasterLockCount = 0;
		}
		
		System.out.println("checMasterkLock:"+mMasterLockCount);
		
		if(MASTER_LOCK_LEN == mMasterLockCount){
			LockUtil.getInstance().unlock();
			return true;
		}
		
		return false;
	}

	public static void checkGameWinLock(){
		if(isWinLockEnable){
			LockUtil.getInstance().unlock();
		}
	}
}
