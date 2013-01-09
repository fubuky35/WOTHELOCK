package com.gmail.fubuky35.wothelock.mail;

import com.gmail.fubuky35.wothelock.R;
import com.gmail.fubuky35.wothelock.preference.SaveLoadManager;

import android.content.Context;

public class AlertMailThread extends Thread {

	private Context parent;
	private boolean isSend = true;
	private long mInterval = 0;
	
	private String mSubject;
	private String mText;
	private String mMinite;
	private String mSecond;
	
	private static final String NAME = "AlertMailThread";
	
	public AlertMailThread(Context parent) {
		super();
		this.parent = parent;
		
		if(null != parent){
			SaveLoadManager sm = SaveLoadManager.getInstance(parent);
			mInterval = sm.loadSendMailTime();
			mMinite = parent.getString(R.string.string_minite);
			mSecond = parent.getString(R.string.string_second);
			mSubject = parent.getString(R.string.mail_subject);
			mText = parent.getString(R.string.mail_text_head);
			mText += longToStringForTime(mInterval);
			mText += parent.getString(R.string.mail_text_tail);
		} else {
			stopRun();
		}
		
		setName(NAME);
	}
	
	public String longToStringForTime(final long time){
		long k = 1000;
		long min = 60;
		
		long temp = time / k;
		
		String rtn = "";
		
		if(temp >= min){
			rtn += (temp/min) + mMinite;
		}
		
		if(0 != temp % min){
			rtn += (temp % min) + mSecond;
		}
		
		return rtn;
	}
	
	
	@Override
	public void run() {
		
		try {
			sleep(mInterval);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(isSend){
			MailSender.sendMail(parent, mSubject, mText);
		}
		
	}



	public void stopRun() {
		isSend = false;
	}
	
}
