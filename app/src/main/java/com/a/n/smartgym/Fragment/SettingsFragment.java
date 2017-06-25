package com.a.n.smartgym.Fragment;

/**
 * Created by nirb on 25/05/2017.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.preference.Preference;

import com.a.n.smartgym.R;

import me.philio.preferencecompatextended.PreferenceFragmentCompat;


/**
 * shows the settings option for choosing the movie categories in ListPreference.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = SettingsFragment.class.getSimpleName();
    private Context activity;

    SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        //add xml
        addPreferencesFromResource(R.xml.preferences);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //onSharedPreferenceChanged(sharedPreferences, getString(R.string.key_mode));

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        //unregister the preferenceChange listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
//        if (preference instanceof ListPreference) {
//            ListPreference listPreference = (ListPreference) preference;
//            int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(key, ""));
//            if (prefIndex >= 0) {
//                preference.setSummary(listPreference.getEntries()[prefIndex]);
//                //post the event of movie categories changed
//
//
//            }
//        } else {
//
//        }
        //preference.setSummary(sharedPreferences.getString(key, ""));





    }

    @Override
    public void onPause() {
        super.onPause();
        //unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //unregister event bus.
    }

    public interface onSharedPreferenceChangedListener{
        public void isModeChanged(String mode);
    }
}
