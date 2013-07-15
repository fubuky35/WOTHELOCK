package com.gmail.fubuky35.wothelock.preference;

import com.gmail.fubuky35.wothelock.R;
import com.gmail.fubuky35.wothelock.reversi.lock.ReversiLock;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Point;
import android.preference.PreferenceManager;

public class SaveLoadManager {
	
	private static SharedPreferences mSharedPreferences = null;
	
	private static SaveLoadManager mInstance = new SaveLoadManager();
	
	private static String mKeyLockEnable = null;
	private static String mKeyLockPatternCount = null;
	private static String mKeyLockPatternX = null;
	private static String mKeyLockPatternY = null;
	private static String mKeyWinLockEnable = null;
	
	private static String mKeyAlertMailEnable = null;
	private static String mKeyFromAccount = null;
	private static String mKeyFromAccountPassword = null;
	private static String mKeyToAddress = null;
	private static String mKeyMailSendTime = null;
	
	private static String mGmailTail = null;
	private static String mDefaultTimes = null;
	
	private static final String TEMP_KEY = "temp_";
	
	private SaveLoadManager(){}
	
	private static boolean init(Context context){
		
		if(null == context){
			return false;
		}
		
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		if(null ==  mKeyLockPatternCount) {
			getKyes(context);
		}
		
		return true;
	}
	
	private static void getKyes(Context context){
		mKeyLockEnable = context.getString(R.string.key_lock_enable);
		mKeyLockPatternCount = context.getString(R.string.key_lock_pattern_count);
		mKeyLockPatternX = context.getString(R.string.key_lock_pattern_x);
		mKeyLockPatternY = context.getString(R.string.key_lock_pattern_y);
		mKeyWinLockEnable  = context.getString(R.string.key_win_lock_enable);
		
		mKeyAlertMailEnable = context.getString(R.string.key_mail_enable);
		mKeyFromAccount = context.getString(R.string.key_mail_from_account);
		mKeyFromAccountPassword = context.getString(R.string.key_mail_from_password);
		mKeyToAddress = context.getString(R.string.key_mail_to);
		mKeyMailSendTime =context.getString(R.string.key_mail_send_time);
		
		mGmailTail = context.getString(R.string.gmail_tail);
		mDefaultTimes = context.getString(R.string.default_mail_time);
	}
	
	public void tmpSaveString(String key, String data){
		Editor e = mSharedPreferences.edit();

		e.putString(TEMP_KEY + key, data);

		e.commit();
	}
	
	public String tmpLoadString(String key){
		return mSharedPreferences.getString(TEMP_KEY + key, null);
	}
	
	public void tmpSaveBoolean(String key, boolean data){
		Editor e = mSharedPreferences.edit();

		e.putBoolean(TEMP_KEY + key, data);

		e.commit();
	}
	
	public boolean tmpLoadBoolean(String key){
		return mSharedPreferences.getBoolean(TEMP_KEY + key, false);
	}
	
	public boolean loadLockEnable() {
		return mSharedPreferences.getBoolean(mKeyLockEnable, false);
	}
	
	public boolean loadWinLockEnable() {
		return mSharedPreferences.getBoolean(mKeyWinLockEnable, true);
	}
	
	public boolean loadAlertMailEnable() {
		return mSharedPreferences.getBoolean(mKeyAlertMailEnable, false);
	}
	
	public static SaveLoadManager getInstance(Context context) {
		if(!init(context) || null ==  mKeyLockPatternCount){
			return null;
		}
		
		return mInstance;
	}

	
	public void saveLockPattern(int count, Point[] points) {
		
		Editor e = mSharedPreferences.edit();
		
		e.putInt(mKeyLockPatternCount, count);
		
		for(int i = 0;i < count;++i){
			e.putInt(mKeyLockPatternX + i, points[i].x);
			e.putInt(mKeyLockPatternY + i, points[i].y);
		}
		
		e.commit();
	}
	
	public int loadLockPatternCount() {
		return mSharedPreferences.getInt(mKeyLockPatternCount, 0);
	}
	
	public Point[] loadLockPattern() {
		
		Point[] points = new Point[ReversiLock.LOCK_PATTERN_MAX_SIZE];
		
		int count = mSharedPreferences.getInt(mKeyLockPatternCount, 0);
		
		for(int i = 0;i < count;++i){
			points[i] = new Point();
			points[i].x = mSharedPreferences.getInt(mKeyLockPatternX + i, 0);
			points[i].y = mSharedPreferences.getInt(mKeyLockPatternY + i, 0);
		}
		
		return points;
		
	}
	
	public void saveFromAccount(String account) {
		Editor e = mSharedPreferences.edit();
		
		e.putString(mKeyFromAccount, account);
		
		e.commit();
	}
	
	public String loadFromAccount() {
//		String account = mSharedPreferences.getString(mKeyFromAccount, null);
//		
//		if(null != account){
//			account += mGmailTail;
//		}
		
		return mSharedPreferences.getString(mKeyFromAccount, null);
	}
	
	public String loadFromAccountAddress() {
		String account = mSharedPreferences.getString(mKeyFromAccount, null);
		
		if(null != account){
			account += mGmailTail;
		}
		
		return account;
	}
	
	public void saveFromAccountPassword(String password) {
		Editor e = mSharedPreferences.edit();
		
		e.putString(mKeyFromAccountPassword, password);
		
		e.commit();
	}
	
	public String loadFromAccountPassword() {
		return mSharedPreferences.getString(mKeyFromAccountPassword, null);
	}
	
	public void saveToAddress(String address) {
		Editor e = mSharedPreferences.edit();
		
		e.putString(mKeyToAddress, address);
		
		e.commit();
	}
	
	public String loadToAddress() {
		return mSharedPreferences.getString(mKeyToAddress, null);
	}
	
	public long loadSendMailTime() {
		String index = mSharedPreferences.getString(mKeyMailSendTime, mDefaultTimes);
		
		long time = Long.parseLong(index);
		
		return time;
	}
	
	public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
		mSharedPreferences.registerOnSharedPreferenceChangeListener(listener);
	}
	
	public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
		mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
	}
	
	
}
