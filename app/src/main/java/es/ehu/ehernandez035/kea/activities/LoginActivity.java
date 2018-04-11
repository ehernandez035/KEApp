package es.ehu.ehernandez035.kea.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.regex.Pattern;

import es.ehu.ehernandez035.kea.R;
import es.ehu.ehernandez035.kea.SharedPrefManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText usernameET;
    private EditText passwordET;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        usernameET = (EditText) findViewById(R.id.usernameET);

        passwordET = (EditText) findViewById(R.id.passwordET);
        passwordET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.loginLayout);
        mProgressView = findViewById(R.id.login_progress);

        TextView registerLink = findViewById(R.id.registerLink);
        registerLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            CheckLoggedInTask task = new CheckLoggedInTask();
            task.execute();
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        usernameET.setError(null);
        passwordET.setError(null);

        // Store values at the time of the login attempt.
        String username = usernameET.getText().toString();
        String password = passwordET.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordET.setError(getString(R.string.error_invalid_password));
            focusView = passwordET;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            usernameET.setError(getString(R.string.error_field_required));
            focusView = usernameET;
            cancel = true;
        }
        if (!isPasswordValid(password)) {
            passwordET.setError(getString(R.string.error_invalid_password));
            focusView = passwordET;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
        }
    }


    private boolean isPasswordValid(String password) {
        String passwordRegex = "[A-Z0-9a-z._%+\\-]{6,}";
        return !(TextUtils.isEmpty(password) && Pattern.matches(passwordRegex, password));
    }


    /**
     * Shows the progress UI and hides the login form.
     */
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

    public class UserLoginTask extends AsyncTask<Void, Void, Response> {

        private final String mUsername;
        private final String mPassword;
        private final OkHttpClient client;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.followRedirects(false);
            client = builder.build();
        }


        @Override
        protected Response doInBackground(Void... params) {
            FormBody.Builder formbuilder = new FormBody.Builder();
            formbuilder.add("username", mUsername);
            formbuilder.add("password", mPassword);
            RequestBody body = formbuilder.build();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url("http://elenah.duckdns.org/login.php")
                    .post(body)
                    .build();
            try {
                okhttp3.Response response = client.newCall(request).execute();
                return response;
            } catch (Exception e) {
                Log.e("KEA", "Error logging in", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(final Response response) {
            mAuthTask = null;
            showProgress(false);
            int status = 0;
            if (response == null) {
                AlertDialog.Builder b = new AlertDialog.Builder(LoginActivity.this);
                b.setMessage(R.string.connection_error);
                b.setIcon(android.R.drawable.ic_dialog_alert);
                b.show();
                return;
            }
            try {
                status = Integer.parseInt(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            usernameET.setError(null);
            if (status == 0) {
                String cookie = response.headers().get("Set-Cookie");
                String[] parts = cookie.split(";")[0].split("=");
                Log.d("GAL", "Cookie: " + parts[1]);
                if (!parts[0].equals("PHPSESSID")) {
                    AlertDialog.Builder b = new AlertDialog.Builder(LoginActivity.this);
                    b.setMessage(R.string.connection_error);
                    b.setIcon(android.R.drawable.ic_dialog_alert);
                    b.show();
                } else {
                    SharedPrefManager.getInstance(LoginActivity.this).userLogin(mUsername, parts[1]);
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                }


            } else {
                switch (status) {
                    case 1:
                        usernameET.setError(getString(R.string.error_invalid_credentials));
                        usernameET.requestFocus();
                        break;

                    case 2:
                        AlertDialog.Builder b = new AlertDialog.Builder(LoginActivity.this);
                        b.setMessage(R.string.connection_error);
                        b.setIcon(android.R.drawable.ic_dialog_alert);
                        b.show();
                        break;

                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


    public class CheckLoggedInTask extends AsyncTask<Void, Void, String> {

        private final OkHttpClient client;

        CheckLoggedInTask() {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.followRedirects(false);
            client = builder.build();
        }


        @Override
        protected String doInBackground(Void... params) {
            String cookie = SharedPrefManager.getInstance(LoginActivity.this).getCookie();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url("http://elenah.duckdns.org/login.php")
                    .header("Cookie", "PHPSESSID=" + cookie)
                    .build();
            try {
                okhttp3.Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                Log.e("KEA", "Error logging in", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String response) {
            mAuthTask = null;
            showProgress(false);

            if ("0".equals(response)) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else if ("3".equals(response)) {
                Snackbar.make(mLoginFormView, R.string.session_expired, Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(mLoginFormView, R.string.connection_error, Snackbar.LENGTH_LONG).show();
            }
        }
    }
}

