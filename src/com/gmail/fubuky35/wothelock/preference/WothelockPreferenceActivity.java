package com.gmail.fubuky35.wothelock.preference;

import java.util.List;

import com.gmail.fubuky35.wothelock.R;
import com.gmail.fubuky35.wothelock.lock.LockUtil;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WothelockPreferenceActivity extends PreferenceActivity {

	
	private PrefMailFragment mMailPreference = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
			addPreferencesFromResource(R.xml.preference_category_lock);
			addPreferencesFromResource(R.xml.preference_lock);
			addPreferencesFromResource(R.xml.preference_category_mail);
			addPreferencesFromResource(R.xml.preference_mail);
			
			Preference p = findPreference(getString(R.string.key_lock_test));
			p.setOnPreferenceClickListener(new OnPreferenceClickListener(){

				@Override
				public boolean onPreferenceClick(Preference preference) {
					SaveLoadManager sm = SaveLoadManager.getInstance(getApplicationContext());
					
					if (0 == sm.loadLockPatternCount()) {
						Toast.makeText(getApplicationContext(),
								R.string.msg_lock_pattern_nothing,
								Toast.LENGTH_LONG).show();
					} else if (!sm.loadLockEnable()) {
						Toast.makeText(getApplicationContext(),
								R.string.msg_lock_disanable, Toast.LENGTH_LONG)
								.show();
					} else {
						LockUtil.getInstance().lock(getApplicationContext());
					}
					
					return true;
				}});
			
			dispSummary();
		
			System.out.println("this is under HONEYCOMB");
		}
		
	}
	

    @Override  
    protected void onResume() {  
        super.onResume();
        SaveLoadManager slm = SaveLoadManager.getInstance(getApplicationContext());
        
        slm.registerOnSharedPreferenceChangeListener(listener);
        
    }  
       
    @Override  
    protected void onPause() {  
        super.onPause();  
        SaveLoadManager slm = SaveLoadManager.getInstance(getApplicationContext());
        
        slm.unregisterOnSharedPreferenceChangeListener(listener);
    }  
    
    private SharedPreferences.OnSharedPreferenceChangeListener listener =   
        new SharedPreferences.OnSharedPreferenceChangeListener() {  
           
		public void onSharedPreferenceChanged(SharedPreferences sp, String key) { 
			if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
				dispSummary();
			} else if(null != mMailPreference) {
				mMailPreference.dispSummary();
			}
		}
    };

	@Override
	public void onBuildHeaders(List<Header> target) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			super.onBuildHeaders(target);
			loadHeadersFromResource(R.xml.preference_header, target);
		}
	}
	
	private void dispSummary() {
		SaveLoadManager slm = SaveLoadManager
				.getInstance(getApplicationContext());
		
		ListPreference list = (ListPreference) findPreference(getString(R.string.key_mail_send_time));
		
		String msg = getString(R.string.pref_mail_time_hint_head)
				+ list.getEntry()
				+ getString(R.string.pref_mail_time_hint_tail);

		
		list.setSummary(msg);
	}
	
    /**
     * This fragment contains a second-level set of preference that you
     * can get to by tapping an item in the first preferences fragment.
     */
    public static class PrefLockFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

         // Make sure default values are applied.  In a real app, you would
            // want this in a shared function that is used to retrieve the
            // SharedPreferences wherever they are needed.
            PreferenceManager.setDefaultValues(getActivity(),
                    R.xml.preference_lock, false);
            
            // Can retrieve arguments from preference XML.
            Log.i("args", "Arguments: " + getArguments());

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preference_lock);
            
            Preference p = findPreference(getString(R.string.key_lock_test));
			p.setOnPreferenceClickListener(new OnPreferenceClickListener(){

				@Override
				public boolean onPreferenceClick(Preference preference) {
					SaveLoadManager sm = SaveLoadManager
							.getInstance(getActivity());

					if (0 == sm.loadLockPatternCount()) {
						Toast.makeText(getActivity(),
								R.string.msg_lock_pattern_nothing,
								Toast.LENGTH_LONG).show();
					} else if (!sm.loadLockEnable()) {
						Toast.makeText(getActivity(),
								R.string.msg_lock_disanable, Toast.LENGTH_LONG).show();
					} else {
						LockUtil.getInstance().lock(getActivity());
					}
					
					return true;
				}});
        }
    }
    
    public static class PrefMailFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

			// Can retrieve arguments from preference XML.
			Log.i("args", "Arguments: " + getArguments());

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preference_mail);

			dispSummary();
			
			((WothelockPreferenceActivity)getActivity()).mMailPreference = this;
			
        }
        
        public void dispSummary(){
        	
        	// なぜかメール送信テスト後にロックの有効/無効をいじるとnullでくる　謎
        	if(null == getActivity()){
        		return;
        	}
        	     
        	
        	ListPreference list = (ListPreference) findPreference(getString(R.string.key_mail_send_time));
        	
        	String msg = getString(R.string.pref_mail_time_hint_head)
    				+ list.getEntry()
    				+ getString(R.string.pref_mail_time_hint_tail);
        	
			list.setSummary(msg);
		
        }
    }
    

}
