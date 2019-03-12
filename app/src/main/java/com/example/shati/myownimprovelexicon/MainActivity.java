package com.example.shati.myownimprovelexicon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    VocabularyFragment fragVocabulary;
    PresettingLearnFragment fragLearn;
    FrameLayout mainFrameLayout;
    FragmentTransaction fragTransaction;
    NavigationView navigationView;
    DialogFragment dialogFilter;
    Menu optionMenu;

    SharedPreferences sharedPref;
    Locale locale;
    String lang;

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        lang = sharedPref.getString("lang", "ru");
        locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, null);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_vocabulary);

        fragVocabulary = new VocabularyFragment();
        fragLearn = new PresettingLearnFragment();
        mainFrameLayout = findViewById(R.id.mainFrameLayout);

        fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.add(R.id.mainFrameLayout, fragVocabulary);
        fragTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("logm", "activity onDestroy");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        optionMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_AddNewWord) {
            Intent intent = new Intent(this, AddNewWordActivity.class);
            startActivity(intent);
            fragVocabulary.adapterHelper.retAdapter().notifyDataSetChanged();
        } else if (id == R.id.action_Themes) {
            Intent intent = new Intent(this, ThemesActivity.class);
            startActivity(intent);
            fragVocabulary.adapterHelper.retAdapter().notifyDataSetChanged();
        } else if (id == R.id.action_Filter) {
            dialogFilter = new DialogFilterFragment();
            dialogFilter.setTargetFragment(fragVocabulary, VocabularyFragment.REQUEST_FILTER);
            dialogFilter.show(getSupportFragmentManager(), "dialogFragment");
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_vocabulary) {
            if ( getSupportFragmentManager().findFragmentById(R.id.mainFrameLayout) != fragVocabulary ) {
                fragTransaction = getSupportFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.mainFrameLayout, fragVocabulary);
                fragTransaction.commit();
                optionMenu.findItem(R.id.action_AddNewWord).setVisible(true);
                optionMenu.findItem(R.id.action_Filter).setVisible(true);
                optionMenu.findItem(R.id.action_Themes).setVisible(true);
            }
        } else if (id == R.id.nav_learn) {
            if ( getSupportFragmentManager().findFragmentById(R.id.mainFrameLayout) != fragLearn ) {
                fragTransaction = getSupportFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.mainFrameLayout, fragLearn);
                fragTransaction.commit();
                optionMenu.findItem(R.id.action_AddNewWord).setVisible(false);
                optionMenu.findItem(R.id.action_Filter).setVisible(false);
                optionMenu.findItem(R.id.action_Themes).setVisible(false);
            }
        } else if (id == R.id.nav_pref) {
            Intent intent = new Intent(this, PrefActivity.class);
            startActivity(intent);
            //fragVocabulary.adapterHelper.retAdapter().notifyDataSetChanged();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static boolean isEmptyEditText(EditText editText) {
        String str = editText.getText().toString();
        for(int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != ' ') return false;
        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public static void delSpacesEditText(EditText editText) {
        String str = editText.getText().toString();
        int start, end;
        for(start = 0; str.charAt(start)==' '; start++);
        for(end = str.length()-1; str.charAt(end)==' '; end--);

        editText.setText(str.substring(start,end+1));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, null);
    }
}
