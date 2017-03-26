package pwr.android_app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.JsonReader;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static android.Manifest.permission.READ_CONTACTS;

// --- LOGIN SCREEN ACTIVITY --- //
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    // Id to identity READ_CONTACTS permission request
    private static final int REQUEST_READ_CONTACTS = 0;

    // Object used in login
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mEmailSignInButton;

    // === ON CREATE === //
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Przygotowanie formularza logowania
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

    }
    // === METODY ZWIĄZANE Z AUTOUZUPEŁNIANIEM ADRESU EMAIL === //
    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
        getLoaderManager().initLoader(0, null, this);
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
    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    // Proces logowania/rejestracji w zależności od sposobu wypełnienia formularza
    // Sprawdza zgodność loginu i hasła z określonym szablonem - w przypadku
    //      niezgodności wyrzuca błąd
    private void attemptLogin() {

        if (mAuthTask != null) {
            return;
        }

        // Czyszczenie poprzednich uwag/błędów
        // (komunikaty wyskakujące, po wprowadzeniu błędnego emaila lub hasła)
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Pobiera wartości z odpowiednich pól panelu logowania
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Weryfikuje zgodność hasła z szablonem, jeśli zostało ono wpisane
        if (TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Sprawdza poprawność adresu e-mail
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // W przypadku wystąpienia jakiegokolwiek błędu...
            focusView.requestFocus();
        } else {
            // Przechodzi do ekranu ładowania, a następnie przechodzi do funkcji autoryzującej użytkownika
            showProgress(true);                                 // pokazuje animację
            mAuthTask = new UserLoginTask(email, password);     // tworzy obiekt zawierający informację u uzytkowniku
            mAuthTask.execute((Void) null);                     // przejście do procedury logowania (w osobnym wątku)
        }
    }

    private boolean isEmailValid(String email) {
        // TODO: Ustalić szablon loginu
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        // TODO: Ustalić jak ma wyglądać poprawne hasło
        return password.length() > 4;
    }

    // Wyświetla ekran ładowania i ukrywa panel logowania
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
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
    @Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }
    @Override public void onLoaderReset(Loader<Cursor> cursorLoader) {}

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        // Tworzy adapter, który podpowiada AutoCompleteTextView co pokazać na liście proponowanych e-maili
        ArrayAdapter<String> adapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }
    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    // ======================= //
    // === USER LOGIN TASK === //
    // ======================= //
    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        private JsonReader responseJsonReader;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override protected Boolean doInBackground(Void... params) {
            // TODO: dokonać autoryzacji użytkownika

            HttpsURLConnection connection;
            String requestBody = null;
            try {
                // === Przygotowanie odpowiedniego JSONA === //
//                JSONObject loginObject = new JSONObject();
//                loginObject.put("email",mEmail);
//                loginObject.put("password",mPassword);
//                requestBody = loginObject.toString();

                // === Przygotowanie parametrów === //
                Uri.Builder builder = new Uri.Builder().
                        appendQueryParameter("email",mEmail).
                        appendQueryParameter("password",mPassword);
                requestBody = builder.build().getEncodedQuery();

                // === ustawianie parametrów połączenia === //
                URL url = new URL(getString(R.string.REST_API_LOGIN_URL));
                connection = (HttpsURLConnection) url.openConnection();
//                connection.setRequestProperty("Content-Type","application/json");
                connection.setRequestMethod("POST");
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(requestBody);

                wr.flush();
                wr.close();

                // === Pobranie odpowiedzi z serwera === //
                if (connection.getResponseCode() == 200) {
                    InputStream responseBody = connection.getInputStream();                         // Pobranie odpowiedzi serwera
                    InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                    responseJsonReader = new JsonReader(responseBodyReader);                        // Tutaj przechowywana jest odpowiedź serwera jako json

                    if (connection != null) {
                        connection.disconnect();
                    }
                    return true;
                }
                else {
                    responseJsonReader = null;
                    if (connection != null) {
                        connection.disconnect();
                    }
                    return false;
                }

                // === TODO Obsługa wyjątów === //
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return false;
            } catch (ProtocolException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);    // Wyłącza animację

            if (success) {          // W przypadku udanej próby zalogowania...

                // === TODO parsowanie powinno być po stronie main activity w przyszłości po ustaleniu rest api
                String xxx = null;
                String name = null;
                try {
                    responseJsonReader.beginObject();
                    while(responseJsonReader.hasNext()){
                        name = responseJsonReader.nextName();
                        if (name.equals("uid")) {
                            xxx = responseJsonReader.nextString();
                            break;
                        }
                        responseJsonReader.skipValue();
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }


                // Przejście do głównego menu aplikacji
                // Todo wyjście z aktywności logowania
                Context context = getApplicationContext();
                Intent i = new Intent(context, MainActivity.class);
                i.putExtra("server_answer_to_login_request", xxx);
                startActivity(i);
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

