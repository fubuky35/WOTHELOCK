package com.gmail.fubuky35.wothelock.lock;

import com.gmail.fubuky35.wothelock.R;
import com.gmail.fubuky35.wothelock.preference.WothelockPreferenceActivity;
import com.gmail.fubuky35.wothelock.reversi.lock.SettingReversiLockActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class LockActivity extends Activity implements OnClickListener, OnCheckedChangeListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    System.out.println("LockActivity.onCreate");
	    
	    // レイアウトの設定
	    setContentView(R.layout.main_activity);
	    
	    // ボタンクリック時のリスナーを設定
	    findViewById(R.id.button_lock).setOnClickListener(this);
	    findViewById(R.id.button_setting).setOnClickListener(this);
	    
	    // チェックボックス
	    CheckBox check = (CheckBox)findViewById(R.id.check_isenabled);
	    // 設定値を反映
	    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
	    check.setChecked(sp.getBoolean("is_enabled", false));
	    // チェック状態変更時のリスナーを設定
	    check.setOnCheckedChangeListener(this);
	}
	
	@Override
	public void onClick(View v) {
	    switch ( v.getId() ){
	    case R.id.button_lock:
	    	// テストボタン押下時、ロック処理を呼ぶ
	    	LockUtil.getInstance().lock(getApplicationContext());
	    	break;
	    case R.id.button_setting:
	    	//Intent intent = new Intent(getApplicationContext(), SettingReversiLockActivity.class);
	    	Intent intent = new Intent(getApplicationContext(), WothelockPreferenceActivity.class);
	    	startActivity(intent);
	    	break;
	    }
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		
		// チェック状態が変更された場合、プリファレンスに保存
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		Editor e = sp.edit();
		e.putBoolean("is_enabled", isChecked);
		e.commit();
		
//		if ( isChecked ){
//			LockUtil.getInstance().disableKeyguard(this);
//		}else{
//			LockUtil.getInstance().enableKeyguard();
//		}
		
	}
	
}
