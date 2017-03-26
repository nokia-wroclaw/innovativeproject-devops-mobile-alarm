package pwr.android_app;

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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // --- DATA --- //
    private int option_id = 1;

    private Toolbar toolbar = null;             // Toolbar na górze ekranu.
    private ActionBar actionBar = null;

    private MainMenuFragment mainMenuFragment;
    private WebBrowserFragment webBrowserFragment;
    private TestingFragment testingFragment;

    // --- MY METHODS --- //
    // Podejmuję konkretną akcję w zależności od wyboru opcji na lewym wysuwanym panelu.
    private void chooseOption() {
        if (option_id == R.id.main_menu_option) {

            // Włączenie głównego menu po wybraniu odpowiedniej opcji z lewego panelu
            mainMenuFragment = new MainMenuFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,mainMenuFragment);
//            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            actionBar.show();

        } else if (option_id == R.id.website_option) {

            // Włączenie przeglądarki po wybraniu odpowiedniej opcji z lewogo panelu
            webBrowserFragment = new WebBrowserFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,webBrowserFragment);
//            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            actionBar.hide();

        } else if (option_id == R.id.testing_option) {

            // Włączenie strony testowej po wybraniu odpowiedniej opcji z lewogo panelu
            testingFragment = new TestingFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,testingFragment);
//            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            actionBar.hide();
        }
    }

    // --- METHODS --- //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tworzy odpowiedni fragment w oparciu o option_id
        chooseOption();

        // Tworzenie toolbara na górze
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Pobranie uchwytu do actionBar (pasek na górze głownej aktywności), aby móc nim manipulować.
        actionBar = getSupportActionBar();
//        actionBar.setHideOnContentScrollEnabled(true);


        // Tworzenie przycisku na dole po prawej
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Tworzenie wysuwanego panelu po lewej stronie
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Tworzenie górnej części panelu wysuwanego z lewej strony
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override public void onResume(){
        super.onResume();

    }


    // Obsługa wciśnięcia klawisza wstecz
    @Override public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {

        // Wypełnia pasek boczny informacjami
        getMenuInflater().inflate(R.menu.main, menu);

        // Ustawia adres email zalogowanego użytkownika na panelu bocznym
        TextView userEmailView = (TextView)findViewById(R.id.userDataTextView);
        userEmailView.setText(getIntent().getStringExtra("server_answer_to_login_request"));

        return true;
    }

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Obsługa wybrania danej opcji z lewego wysuwanego panelu.
        option_id = item.getItemId();

        // Tworzy odpowiedni fragment w oparciu o option_id
        chooseOption();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
