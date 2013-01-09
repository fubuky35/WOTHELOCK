package com.gmail.fubuky35.wothelock.mail;

import com.gmail.fubuky35.wothelock.R;
import com.gmail.fubuky35.wothelock.preference.SaveLoadManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SettingMailActivity extends Activity implements OnClickListener {

	
	private EditText mFromAccount;
	private EditText mFromPassword;
	private EditText mToAddress;
	
	private Button mBtnOk;
	private Button mBtnCancel;
	private Button mBtnTest;
	private ProgressBar mProgressBar;
	
	// private ProgressDialog waitDialog;
	private boolean isSuccess = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.mail_setting);
		
		mBtnOk = (Button) findViewById(R.id.btn_mail_ok);
		mBtnOk.setOnClickListener(this);
		
		mBtnCancel = (Button) findViewById(R.id.btn_mail_cancel);
		mBtnCancel.setOnClickListener(this);
		
		mBtnTest = (Button) findViewById(R.id.btn_mail_test);
		mBtnTest.setOnClickListener(this);
		
		mFromAccount = (EditText) findViewById(R.id.et_from_acount);
		mFromPassword = (EditText) findViewById(R.id.et_from_password);
		mToAddress = (EditText) findViewById(R.id.et_to_address);
		
		mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		load();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_mail_ok:
			save();
			finish();
			break;
		case R.id.btn_mail_cancel:
			finish();
			break;
		case R.id.btn_mail_test:
			Toast.makeText(getApplicationContext(), R.string.mail_sending, Toast.LENGTH_SHORT).show();
			disableActivity();
			
//			// プログレスダイアログの設定
//		    waitDialog = new ProgressDialog(this);
//		    // プログレスダイアログのメッセージを設定します
//		    waitDialog.setMessage(getString(R.string.mail_sending));
//		    // 円スタイル（くるくる回るタイプ）に設定します
//		    waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		    // プログレスダイアログを表示
//		    waitDialog.show();
//		    
			save();
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					isSuccess = MailSender.sendMail(getApplicationContext(),
							getString(R.string.mail_test_subject),
							getString(R.string.mail_test_text));
					Handler h = new Handler(getMainLooper());
					h.post(new Runnable() {
						@Override
						public void run() {
							// プログレスダイアログ終了
//							waitDialog.dismiss();
//							waitDialog = null;
							
							enableActivity();
							
							if (isSuccess) {
								Toast.makeText(getApplicationContext(),
										R.string.msg_mail_success,
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(getApplicationContext(),
										R.string.msg_mail_error,
										Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
			}).start();
			
			break;
		default:
			break;
		}
	}
	
	public void save() {
		SaveLoadManager sm = SaveLoadManager.getInstance(this);
		
		sm.saveFromAccount(mFromAccount.getText().toString());
		sm.saveFromAccountPassword(mFromPassword.getText().toString());
		sm.saveToAddress(mToAddress.getText().toString());
		
	}

	public void load() {
		SaveLoadManager sm = SaveLoadManager.getInstance(this);
		
		mFromAccount.setText(sm.loadFromAccount());
		mFromPassword.setText(sm.loadFromAccountPassword());
		mToAddress.setText(sm.loadToAddress());
		
	}
	
	private void  disableActivity() {
		mFromAccount.setEnabled(false);
		mFromPassword.setEnabled(false);
		mToAddress.setEnabled(false);
		
		mBtnOk.setEnabled(false);
		mBtnCancel.setEnabled(false);
		mBtnTest.setEnabled(false);
		
		mProgressBar.setVisibility(View.VISIBLE);
	}
	
	private void  enableActivity() {
		mFromAccount.setEnabled(true);
		mFromPassword.setEnabled(true);
		mToAddress.setEnabled(true);
		
		mBtnOk.setEnabled(true);
		mBtnCancel.setEnabled(true);
		mBtnTest.setEnabled(true);
		
		mProgressBar.setVisibility(View.INVISIBLE);
	}

}
