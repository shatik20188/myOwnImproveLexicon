package com.example.shati.myownimprovelexicon;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class PrefActivity  extends PreferenceActivity {

    CheckBoxPreference cbIsActive;
    NumberPickerDialogPreference npdpMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);

        cbIsActive = (CheckBoxPreference) findPreference("isActiveMaxInSucc");
        npdpMax = (NumberPickerDialogPreference) findPreference("maxAmountInSucc");
        npdpMax.setEnabled(cbIsActive.isChecked());

        cbIsActive.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                npdpMax.setEnabled(cbIsActive.isChecked());
                return false;
            }
        });
    }



}
