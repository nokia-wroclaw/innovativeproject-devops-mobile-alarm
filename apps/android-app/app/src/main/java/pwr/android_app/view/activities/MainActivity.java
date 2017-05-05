package pwr.android_app.view.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pwr.android_app.R;
import pwr.android_app.interfaces.ToastMessenger;
import pwr.android_app.dataStructures.UserData;
import pwr.android_app.network.rest.ServerApi;
import pwr.android_app.network.rest.ServiceGenerator;
import pwr.android_app.view.fragments.MainMenuFragment;
import pwr.android_app.view.fragments.MonitorFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity
        extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ToastMessenger {

    /* ========================================== DATA ========================================== */

    private int option_id;

    private ServerApi client =
            ServiceGenerator.createService(ServerApi.class);

    private SharedPreferences sharedPref;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @Nullable
    @BindView(R.id.name_surname_label)
    TextView nameSurnameLabel;
    @Nullable
    @BindView(R.id.email_label)
    TextView emailLabel;

    private ActionBar actionBar;

    // ......................................... STATIC ......................................... //
    private final static String TAG_MONITOR_FRAGMENT = "FRAGMENT_MONITOR";
    private final static String TAG_MAIN_MENU_FRAGMENT = "FRAGMENT_MAIN_MENU";

    private final static String BUNDLE_SELECTED_WINDOW = "selected_window";

    /* ========================================= METHODS ======================================== */

    // ----------------------------------- Activity Lifecycle ----------------------------------- //
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();

        sharedPref = context.getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE);

        ButterKnife.bind(this);

        if (savedInstanceState == null || !savedInstanceState.containsKey(BUNDLE_SELECTED_WINDOW)) {
            option_id = R.id.main_menu_option;
            setFragment();
        }

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_SELECTED_WINDOW, option_id);
    }

    // --------------------------------------- Components --------------------------------------- //
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        UserData userData = new Gson().fromJson(sharedPref.getString("user_data", null), UserData.class);

        setUserInfo(userData);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_about_us:
                // ToDo: page about us
                return true;

            case R.id.action_logout:
                logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        option_id = item.getItemId();

        setFragment();

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    // ---------------------------------------- Functions --------------------------------------- //
    private void setUserInfo(UserData userData) {

        ButterKnife.bind(this);
        nameSurnameLabel.setText(userData.getUserName() + " " + userData.getUserSurname());
        emailLabel.setText(userData.getUserEmail());
    }

    private void setFragment() {

        Fragment currentFragment;

        if (option_id == R.id.main_menu_option) {

            // Włączenie głównego menu po wybraniu odpowiedniej opcji z lewego panelu
            currentFragment = new MainMenuFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, currentFragment, TAG_MAIN_MENU_FRAGMENT);
            fragmentTransaction.commit();

            fab.hide();

        } else if (option_id == R.id.monitor_option) {

            // Włączenie strony testowej po wybraniu odpowiedniej opcji z lewego panelu
            currentFragment = new MonitorFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, currentFragment, TAG_MONITOR_FRAGMENT);
            fragmentTransaction.commit();

            fab.show();
            fab.bringToFront();
        }
    }

    @Override
    public void showToast(CharSequence text) {

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_yellow_toast, (ViewGroup) findViewById(R.id.yellow_toast_container));

        TextView textView = (TextView) layout.findViewById(R.id.yellow_toast_text);
        textView.setText(text);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 60);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    // ----------------------------------------- Network ---------------------------------------- //
    private void logout() {

        String cookie = sharedPref.getString("cookie", null);
        Call<Void> call = client.logout(cookie);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.code() == 200) {

                    Log.d("LOGOUT", "success");

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("cookie", null);
                    editor.commit();

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Log.d("LOGOUT", "fail, response code: " + response.code());

                    showToast("cannot logout - error in server response");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("LOGOUT", "fail");

                showToast("cannot connect with server");
            }
        });
    }

    // ---------------------------------------- Listeners --------------------------------------- //
    @OnClick(R.id.fab)
    void onFabFired() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        MonitorFragment fragment = (MonitorFragment) fragmentManager.findFragmentByTag(TAG_MONITOR_FRAGMENT);
        fragment.synchronizeServices();
    }

    /* ========================================================================================== */
}