package com.gmail.fubuky35.wothelock.mail;

import java.util.ArrayList;

import com.gmail.fubuky35.wothelock.R;
import com.gmail.fubuky35.wothelock.preference.SaveLoadManager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SettingMailActivity extends Activity implements OnClickListener{

	
	private EditText mFromAccount;
	private EditText mFromPassword;
	private EditText mToAddress;
	
	private Button mBtnOk;
	private Button mBtnCancel;
	private Button mBtnTest;
	private ImageButton mAddressBook;
	private Button mBtnMyAccount;
	
	private ProgressBar mProgressBar;
	
	private boolean isSuccess = false;
	
	private static final int PICK_CONTACT = 1;
	
	
	private static final String TEMP_KEY_ACCOUNT = "mail_acc";
	private static final String TEMP_KEY_PASSWORD = "mail_pass";
	private static final String TEMP_KEY_ADDRESS = "mail_add";
	private static final String TEMP_KEY_ENABLE = "mail_setting_enable";
	
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
		
		mAddressBook = (ImageButton) findViewById(R.id.btn_address_book);
		mAddressBook.setOnClickListener(this);
		
		mBtnMyAccount = (Button) findViewById(R.id.btn_my_account);
		mBtnMyAccount.setOnClickListener(this);
		
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
	protected void onPause() {
		tmpSave();
		super.onPause();
	}
	
	
	
	@Override
	protected void onDestroy() {
		clearTmp();
		super.onDestroy();
	}
	
	private DialogInterface.OnClickListener mAddresbookListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			String selectedPhoneNumber = mDialogItems[which];
			tmpLoad();
			mToAddress.setText(selectedPhoneNumber);
			tmpSave();
		}
	};
	
	private DialogInterface.OnClickListener mMyAccountListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			String selectedPhoneNumber = mDialogItems[which].split("@")[0];
			mFromAccount.setText(selectedPhoneNumber);
		}
	};

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
			
			 exchangeDispAndSave();
			
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
							load();
							exchangeDispAndSave();
							load();
							
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
		case R.id.btn_address_book:
			Intent pickIntent = new Intent(Intent.ACTION_PICK,
					ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(pickIntent, PICK_CONTACT);
			tmpSave();
			break;
		case R.id.btn_my_account:
			mDialogItems = getAccounts();
			if(null != mDialogItems && 0 != mDialogItems.length){
				if(1 < mDialogItems.length){
					showListBoxDialog("", mDialogItems, mMyAccountListener);
				} else {
					mFromAccount.setText(mDialogItems[0].split("@")[0]);
				}
			} else {
				mFromAccount.setText("");
			}
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
		
		tmpLoad();
	}
	
	private void tmpSave() {
		SaveLoadManager sm = SaveLoadManager.getInstance(this);

		sm.tmpSaveString(TEMP_KEY_ACCOUNT, mFromAccount.getText().toString());
		sm.tmpSaveString(TEMP_KEY_PASSWORD, mFromPassword.getText().toString());
		sm.tmpSaveString(TEMP_KEY_ADDRESS, mToAddress.getText().toString());

		sm.tmpSaveBoolean(TEMP_KEY_ENABLE, true);
	}

	private void tmpLoad() {
		SaveLoadManager sm = SaveLoadManager.getInstance(this);
		
		if (sm.tmpLoadBoolean(TEMP_KEY_ENABLE)) {
			mFromAccount.setText(sm.tmpLoadString(TEMP_KEY_ACCOUNT));
			mFromPassword.setText(sm.tmpLoadString(TEMP_KEY_PASSWORD));
			mToAddress.setText(sm.tmpLoadString(TEMP_KEY_ADDRESS));
			
			sm.tmpSaveBoolean(TEMP_KEY_ENABLE, false);
		}
		
	}
	
	private void clearTmp(){
		SaveLoadManager sm = SaveLoadManager.getInstance(this);
		sm.tmpSaveBoolean(TEMP_KEY_ENABLE, false);
	}
	
	private void exchangeDispAndSave(){
		String tmpAcc = mFromAccount.getText().toString();
		String tmpPass = mFromPassword.getText().toString();
		String tmpAdd = mToAddress.getText().toString();
		
		load();
		tmpSave();
		
		mFromAccount.setText(tmpAcc);
		mFromPassword.setText(tmpPass);
		mToAddress.setText(tmpAdd);
		
		save();
	}
	
	private String[] mDialogItems;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case PICK_CONTACT:
				if (resultCode == Activity.RESULT_OK) {
					ContentResolver resolver = getContentResolver();
					Uri uri = data.getData();
					
					String name = getName(uri, resolver);
					mDialogItems = getAddresses(uri, resolver);
					
					
					if(1 >= mDialogItems.length){
						tmpLoad();
						mToAddress.setText(mDialogItems[0]);
						tmpSave();
						break;
					}
					
					showListBoxDialog(name, mDialogItems, mAddresbookListener);
				}
				break;
			default:
				break;
		}
	}
	
	private void showListBoxDialog(String title, String[] items, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		
		adb.setTitle(title);
		adb.setItems(items, listener);
		
        AlertDialog alert = adb.create();
        alert.show();
	}
	
	private String[] getAddresses(Uri uri, ContentResolver resolver){
		String id = "";
		ArrayList<String> addressArry = new ArrayList<String>();
		String result[] = new String[1];
		result[0] = "";
		
		Cursor c = resolver.query(uri, null, null, null, null);
		
		// ID
		while (c.moveToNext()) {
			id = c.getString(c
					.getColumnIndex(ContactsContract.Contacts._ID));
		}
		
		c.close();
		
		c = resolver.query(
				ContactsContract.CommonDataKinds.Email.CONTENT_URI,
				null, ContactsContract.CommonDataKinds.Email.CONTACT_ID
						+ " = ?", new String[] { id }, null);
		
		while (c.moveToNext()) {
			addressArry.add(
					c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
		}
		
		c.close();
		
		if(0 < addressArry.size()){
			result = new String[addressArry.size()];
			addressArry.toArray(result);
		}
		
		return result;
		
	}
	
	private String getName(Uri uri, ContentResolver resolver){
		String name = "";
		Cursor c = resolver.query(uri, null, null, null, null);

		c.moveToFirst();

		// name
		int columnIndex = c
                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        
		name = c.getString(columnIndex);
        
        c.close();
		
		return name;
	}
	
	private String[] getAccounts() {  
	    ArrayList<String> accountsInfo = new ArrayList<String>();  
	    Account[] accounts = AccountManager.get(this).getAccounts();  
	    for (Account account : accounts) {  
	        String name = account.name;  
	        String type = account.type;  
	        
	        if(type.equals("com.google")){
	        	accountsInfo.add(name);
	        }
  
	    }
	  
	    String[] result = new String[accountsInfo.size()];  
	    accountsInfo.toArray(result);  
	    return result;  
	} 
	
	private void  disableActivity() {
		mFromAccount.setEnabled(false);
		mFromPassword.setEnabled(false);
		mToAddress.setEnabled(false);
		
		mBtnOk.setEnabled(false);
		mBtnCancel.setEnabled(false);
		mBtnTest.setEnabled(false);
		mAddressBook.setEnabled(false);
		mBtnMyAccount.setEnabled(false);
		
		mProgressBar.setVisibility(View.VISIBLE);
	}
	
	private void  enableActivity() {
		mFromAccount.setEnabled(true);
		mFromPassword.setEnabled(true);
		mToAddress.setEnabled(true);
		
		mBtnOk.setEnabled(true);
		mBtnCancel.setEnabled(true);
		mBtnTest.setEnabled(true);
		mAddressBook.setEnabled(true);
		mBtnMyAccount.setEnabled(true);
		
		mProgressBar.setVisibility(View.INVISIBLE);
	}

}
