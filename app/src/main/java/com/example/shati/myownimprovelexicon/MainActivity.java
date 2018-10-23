package com.example.shati.myownimprovelexicon;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    VocabularyFragment fragVocabulary;
    LearnFragment fragLearn;
    FrameLayout mainFrameLayout;
    FragmentTransaction fragTransaction;
    NavigationView navigationView;

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragVocabulary = new VocabularyFragment();
        fragLearn = new LearnFragment();
        mainFrameLayout = (FrameLayout) findViewById(R.id.mainFrameLayout);

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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_vocabulary) {
            if ( getSupportFragmentManager().findFragmentById(R.id.mainFrameLayout) != fragVocabulary ) {
                fragTransaction = getSupportFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.mainFrameLayout, fragVocabulary);
                fragTransaction.commit();
            }
        } else if (id == R.id.nav_learn) {
            if ( getSupportFragmentManager().findFragmentById(R.id.mainFrameLayout) != fragLearn ) {
                fragTransaction = getSupportFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.mainFrameLayout, fragLearn);
                fragTransaction.commit();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
