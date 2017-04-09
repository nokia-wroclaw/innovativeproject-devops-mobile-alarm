package pwr.android_app.view.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.google.gson.Gson;

import pwr.android_app.view.fragments.MonitorFragment;
import pwr.android_app.R;
import pwr.android_app.network.rest.ServiceGenerator;
import pwr.android_app.dataStructures.UserData;
import pwr.android_app.network.rest.ApiService;
import pwr.android_app.view.fragments.MainMenuFragment;
import pwr.android_app.view.fragments.TestingFragment;
import pwr.android_app.view.fragments.WebBrowserFragment;

// --- MAIN ACTIVITY --- //
public class MainActivity
        extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /* ========================================== DATA ========================================== */
    private int option_id;

    // Data from previous activity
    private String cookie = null;
    private UserData userData = null;

    // Used in REST requests
    private ApiService client = null;

    // UI references
    private Toolbar toolbar = null;
    private ActionBar actionBar = null;
    private MainMenuFragment mainMenuFragment;
    private WebBrowserFragment webBrowserFragment;
    private TestingFragment testingFragment;
    private MonitorFragment monitorFragment;
    private FloatingActionButton fab;

    /* ========================================= METHODS ======================================== */
    private void setFragment() {
        if (option_id == R.id.main_menu_option) {

            // Włączenie głównego menu po wybraniu odpowiedniej opcji z lewego panelu
            mainMenuFragment = new MainMenuFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, mainMenuFragment);
            fragmentTransaction.commit();
            fab.show();
            actionBar.show();

        } else if (option_id == R.id.website_option) {

            // Włączenie przeglądarki po wybraniu odpowiedniej opcji z lewogo panelu
            webBrowserFragment = new WebBrowserFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, webBrowserFragment);
            fragmentTransaction.commit();
            fab.show();
            actionBar.hide();

        } else if (option_id == R.id.testing_option) {

            // Włączenie strony testowej po wybraniu odpowiedniej opcji z lewogo panelu
            testingFragment = new TestingFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, testingFragment);
            fragmentTransaction.commit();
            fab.show();
            actionBar.hide();
        } else if (option_id == R.id.website_option) {

            // Włączenie przeglądarki po wybraniu odpowiedniej opcji z lewogo panelu
            webBrowserFragment = new WebBrowserFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, webBrowserFragment);
            fragmentTransaction.commit();
            fab.show();
            actionBar.hide();
        } else if (option_id == R.id.monitor_option) {

            // Włączenie strony testowej po wybraniu odpowiedniej opcji z lewogo panelu
            monitorFragment = new MonitorFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, monitorFragment);
            fragmentTransaction.commit();
            fab.hide();
            actionBar.hide();
        }
    }
    // === ON CREATE === //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting data from previous activity
        this.cookie = getIntent().getStringExtra("cookie");
        this.userData = new Gson().fromJson(getIntent().getStringExtra("user_data"),UserData.class);

        // [Retrofit]
        client = ServiceGenerator.createService(ApiService.class);

        // Setting default fragment
        mainMenuFragment = new MainMenuFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,mainMenuFragment);
        fragmentTransaction.commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
//        actionBar.setHideOnContentScrollEnabled(true);

        // Creating floating button
        fab = (FloatingActionButton) findViewById(R.id.fab);
        //ToDo: floating button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "I will never stop watching you!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Creating drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Tworzenie górnej części panelu wysuwanego z lewej strony
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // === ON RESUME === //
    @Override
    public void onResume(){
        super.onResume();

    }

    // === ON BACK PRESSED === //
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
        }
    }

    // === ON CREATE OPTION MENU === //
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Wypełnia pasek boczny informacjami
        getMenuInflater().inflate(R.menu.main, menu);

        // Ustawia adres email zalogowanego użytkownika na panelu bocznym
        TextView nameSurnameLabel = (TextView)findViewById(R.id.name_surname_label);
        nameSurnameLabel.setText(this.userData.getUserName() + " " + this.userData.getUserSurname());

        TextView emailLabel = (TextView)findViewById(R.id.email_label);
        emailLabel.setText(this.userData.getUserEmail());

        return true;
    }

    // === ON OPTION ITEM SELECTED === //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // === ON NAVIGATION ITEM SELECTED === //
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Obsługa wybrania danej opcji z lewego wysuwanego panelu.
        option_id = item.getItemId();

        // Tworzy odpowiedni fragment w oparciu o option_id
        setFragment();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}