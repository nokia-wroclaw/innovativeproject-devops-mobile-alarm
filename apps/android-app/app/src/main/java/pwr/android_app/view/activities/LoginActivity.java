package pwr.android_app.view.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import pwr.android_app.R;
import pwr.android_app.dataStructures.UserData;
import pwr.android_app.network.rest.ServerApi;
import pwr.android_app.network.rest.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity
        extends AppCompatActivity
        implements LoaderCallbacks<Cursor> {

    /* ========================================== DATA ========================================== */

    // Id to identity READ_CONTACTS permission request
    private static final int REQUEST_READ_CONTACTS = 0;

    private ServerApi client =
            ServiceGenerator.createService(ServerApi.class);

    @BindView(R.id.email)
    AutoCompleteTextView mEmailView;
    @BindView(R.id.password)
    EditText mPasswordView;
    @BindView(R.id.login_progress)
    ProgressBar mProgressView;
    @BindView(R.id.login_form)
    ScrollView mLoginFormView;
    @BindView(R.id.email_sign_in_button)
    Button mEmailSignInButton;

    /* ========================================= METHODS ======================================== */

    // ----------------------------------- Activity Lifecycle ----------------------------------- //
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        populateAutoComplete();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Context context = getApplicationContext();

        // Checking cookie value in SharedPreferences
        SharedPreferences sharedPref =
                context.getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE);
        // If cookie isn't null, logging in is skipped
        if (sharedPref.getString("cookie",null) != null) {
            Intent i = new Intent(context, MainActivity.class);
            startActivity(i);
        }
    }

    // ---------------------------------------- Listeners --------------------------------------- //
    @OnEditorAction(R.id.password)
    boolean onEditorActionPassword(TextView textView, int id, KeyEvent keyEvent) {
        if (id == R.id.login || id == EditorInfo.IME_NULL) {
            attemptLogin();
            return true;
        }
        return false;
    }

    @OnClick(R.id.email_sign_in_button)
    void onClickSignInButton(Button button) {
        attemptLogin();
    }

    // -------------------------------------- Login Process ------------------------------------- //
    private void attemptLogin() {

        // Cleaning error messages
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Getting values from text views
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        // Clearing flags
        boolean cancel = false;
        View focusView = null;

        // Validating password
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.field_required_error));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.invalid_password_error));
            focusView = mPasswordView;
            cancel = true;
        }

        // Validating email
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.field_required_error));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.invalid_email_error));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // In case of any error...
            focusView.requestFocus();
        } else {
            // After successful validation...
            String token = FirebaseInstanceId.getInstance().getToken();
            login(email, password, token);
        }
    }

    private void login(String mEmail, String mPassword, String fcm_token) {

        // Show animation
        showProgress(true);
        showForm(false);

        // Fetch a user information
        Call<UserData> call = client.login(mEmail, mPassword, fcm_token);

        // Execute the call asynchronously. Get a positive or negative callback.
        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {

                showProgress(false);            // hide animation
                showForm(true);

                if (response.code() == 200) {

                    // Getting cookie from response
                    String cookie = response.headers().get("set-cookie");

                    // Converting response body into JSON
                    String userData = new Gson().toJson(response.body());

                    // Start MainActivity
                    Context context = getApplicationContext();

                    // Saving cookie and userData in shared preferences
                    SharedPreferences sharedPref =
                            context.getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("cookie", cookie);
                    editor.putString("user_data", userData);
                    editor.commit();

                    Intent i = new Intent(context, MainActivity.class);
                    startActivity(i);
                } else if (response.code() == 403) {
                    mPasswordView.setError(getString(R.string.incorrect_password_error));
                    mPasswordView.requestFocus();
                } else {
                    showToast(getResources().getString(R.string.bad_server_connection));
                }
            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {

                showProgress(false);                                                                // hide progress animation
                showForm(true);
                showToast(getResources().getString(R.string.bad_server_connection));                 // show toast message about connection failure
            }
        });
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    // --------------------------------Showing & Hiding Components ------------------------------ //
    private void showForm(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginFormView.animate().setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
    }

    private void showToast(CharSequence text) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_yellow_toast, (ViewGroup) findViewById(R.id.yellow_toast_container));

        TextView textView = (TextView) layout.findViewById(R.id.yellow_toast_text);
        textView.setText(text);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.TOP, 0, 20);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    // -------------------------------- Auto-Filling Email View --------------------------------- //
    private void populateAutoComplete() {

        if (!mayRequestContacts()) {
            return;
        }
        getLoaderManager().initLoader(0, null, this);
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private boolean mayRequestContacts() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Pobiera dane z kontaktów profilu użytkownika
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Wybiera wyłącznie adresy e-mail
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // W pierwszej kolejności proponuje ostatnio używany adres
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    /* ========================================= CLASSES ======================================== */

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /* ========================================================================================== */
}

