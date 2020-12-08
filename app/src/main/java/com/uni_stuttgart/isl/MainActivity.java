package com.uni_stuttgart.isl;

import android.content.Intent;
import android.os.Bundle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;

import com.uni_stuttgart.isl.Interpolation.InterUApprox;
import com.uni_stuttgart.isl.Intros.Intro_App;
import com.uni_stuttgart.isl.Intros.PrefManager;
import com.uni_stuttgart.isl.Navigation.NavigationDrawerFragment;
import com.uni_stuttgart.isl.Splittingsolver.Splittingsolver;


public class MainActivity extends AppCompatActivity {

    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefManager = new PrefManager(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        freeMemory();
    }

    // Drei Punkte oben rechts!
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//
//        return true;
//
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.introduction){
            prefManager.setReopenAppIntro(false);
            Intent myIntent = new Intent(MainActivity.this, Intro_App.class);
            MainActivity.this.startActivity(myIntent);
            freeMemory();
            finish();
        }

        return true;

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (prefManager.isInterLastActivity() && !prefManager.isGoingBack()) {
            prefManager.setIsGoingBack(false);
            Intent myIntent = new Intent(MainActivity.this, InterUApprox.class);
            MainActivity.this.startActivity(myIntent);
            finish();
        }
        else if (prefManager.isSplitLastActivity() && !prefManager.isGoingBack()) {
            prefManager.setIsGoingBack(false);
            Intent myIntent = new Intent(MainActivity.this, Splittingsolver.class);
            MainActivity.this.startActivity(myIntent);
            finish();
        }
    }

    public void freeMemory(){
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    @Override
    protected void onStop() {
        super.onStop();

        prefManager.setIsGoingBack(false);
        prefManager.setIsMainLastActivity(true);
        prefManager.setIsInterLastActivity(false);
        prefManager.setIsSplitLastActivity(false);

        freeMemory();
    }
}

