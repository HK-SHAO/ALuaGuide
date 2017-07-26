package com.sf.ALuaGuide;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.widget.Toast;


public class SettingsActivity extends MyPreferenceActivity {

    private SwitchPreference nightMode;
    private SwitchPreference heightLight;
    private EditTextPreference delay;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean("nightMode", false)) {
            setTheme(R.style.DarkAppTheme);
        }
        super.onCreate(savedInstanceState);
        context = this;
        addPreferencesFromResource(R.xml.pref_main);
        initPreference();

        delay.setEnabled(sp.getBoolean("heightLight", false));
        delay.setSummary(sp.getString("delay", "10") + " 毫秒");
    }

    private void initPreference() {
        nightMode = (SwitchPreference) findPreference("nightMode");
        nightMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                finish();
                overridePendingTransition(0, 0);
                MainActivity.context.finish();
                MainActivity.actionStart(context);
                actionStart(context);
                overridePendingTransition(0, 0);
                return true;
            }
        });
        heightLight = (SwitchPreference) findPreference("heightLight");
        heightLight.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                delay.setEnabled((Boolean) newValue);
                return true;
            }
        });
        delay = (EditTextPreference) findPreference("delay");
        delay.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((newValue + "").length() > 10) {
                    Toast.makeText(delay.getContext(), "你输入的值太大", Toast.LENGTH_SHORT).show();
                    return false;
                }
                delay.setSummary(newValue + " 毫秒");
                return true;
            }
        });
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }
}
