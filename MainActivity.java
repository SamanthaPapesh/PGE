package com.example.navigationdrawer1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.slider.Slider;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Testing VERSION - Not for Submission (FARTHEST)

    //Need UUID (Of Device?) (UUID 1 of 2)
    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    //Declare Global Variables (pgeFragment.java)
        //Declare Global Variables: LED
        public static int LEDclickcount;
        public static int PILEDclickcount;
        //public TextView LEDtext  = (TextView) findViewById(R.id.textView0);
        //BluetoothConnectionService instance = new BluetoothConnectionService(this);
        //Declare Global Variables: Volume (UP & DOWN)
        public static int Volumeclickcount;
        public static TextView VOLUMEtext;
        public static int PIVOLUMEclickcount;
        //Declare Sliders (10)
        public static Slider Mod1Slider;
        public static Slider Mod2Slider;
        public static Slider Mod3Slider;
        public static Slider Mod4Slider;
        public static Slider Mod5Slider;
        public static Slider Mod6Slider;
        public static Slider Mod7Slider;
        public static Slider Mod8Slider;
        public static Slider Mod9Slider;
        public static Slider Mod10Slider;
        //Declare Global Variables: Material Sliders (10)
        public static float Mod1Value = 0;
        public static float Mod2Value = 0;
        public static float Mod3Value = 0;
        public static float Mod4Value = 0;
        public static float Mod5Value = 0;
        public static float Mod6Value = 0;
        public static float Mod7Value = 0;
        public static float Mod8Value = 0;
        public static float Mod9Value = 0;
        public static float Mod10Value = 0;
        //Declare Global Variables: Speaker Selection
        public static int[] SpeakerListclickcount = new int[10];
        public static int firstSpeakerCondition;
        public static int[] Storage = new int[10];
        public static int speakerCondition = 0;
        public static int NewSpeakerCount = 0;
        public static int[] NewSpeakersFound = new int[NewSpeakerCount];

    //Declare Drawer
    private DrawerLayout drawer;

    //Create Fragments for HIDE() and SHOW()
    Fragment fragment1 = new pgeFragment();
    Fragment fragment2 = new bluetoothFragment();
    Fragment fragment3 = new manualFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Declare & Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        //Set Toolbar into Action
        setSupportActionBar(toolbar);

        //Create Menu Drawer Items Layout
        drawer = findViewById(R.id.drawer_layout);

        //Reference to Navigation View (Select Listener)
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_closed);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Orientation
        if(savedInstanceState == null) {
            //Open this First when Activity Started
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new pgeFragment()).commit();
            //Select 1st Item/Fragment in Drawer
            navigationView.setCheckedItem(R.id.nav_pge);}
    }

    @Override
    public boolean onNavigationItemSelected(@Nullable MenuItem item) {
        switch(item.getItemId()) {
            case R.id.nav_pge:
                //FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                //if(fragment1.isAdded()) {ft.setMaxLifecycle(fragment1, Lifecycle.State.RESUMED); ft.show(fragment1);}
                //else{ft.add(R.id.fragment_container, fragment1, "pge_tag");}
                //if(fragment2.isAdded()) {ft.hide(fragment2); ft.setMaxLifecycle(fragment2, Lifecycle.State.STARTED);}
                //if(fragment3.isAdded()) {ft.hide(fragment3); ft.setMaxLifecycle(fragment3, Lifecycle.State.STARTED);}
                //ft.commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new pgeFragment()).commit();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case R.id.nav_bluetooth:
                //FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                //if(fragment2.isAdded()) {ft2.setMaxLifecycle(fragment2, Lifecycle.State.RESUMED); ft2.show(fragment2);}
                //else{ft2.add(R.id.fragment_container, fragment2, "bt_tag");}
                //if(fragment1.isAdded()) {ft2.hide(fragment1); ft2.setMaxLifecycle(fragment1, Lifecycle.State.STARTED);}
                //if(fragment3.isAdded()) {ft2.hide(fragment3); ft2.setMaxLifecycle(fragment3, Lifecycle.State.STARTED);}
                //ft2.commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new bluetoothFragment()).commit();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case R.id.nav_manual:
                //FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                //if(fragment3.isAdded()) {ft3.setMaxLifecycle(fragment3, Lifecycle.State.RESUMED); ft3.show(fragment3);}
                //else{ft3.add(R.id.fragment_container, fragment3, "manual_tag");}
                //if(fragment1.isAdded()) {ft3.hide(fragment1);}
                //if(fragment2.isAdded()) {ft3.hide(fragment2);}
                //ft3.commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new manualFragment()).commit();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
        }

        //Close Drawer
        drawer.closeDrawer(GravityCompat.START);
        return true; //trigger action after item clicked
    }

    //Override
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {drawer.closeDrawer(GravityCompat.START);}
        else {super.onBackPressed();}
    }

} //End of MainActivity Method
