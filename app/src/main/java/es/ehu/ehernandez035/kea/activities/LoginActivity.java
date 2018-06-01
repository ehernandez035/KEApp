package es.ehu.ehernandez035.kea.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import es.ehu.ehernandez035.kea.R;
import es.ehu.ehernandez035.kea.RequestCallback;
import es.ehu.ehernandez035.kea.ServerRequest;
import es.ehu.ehernandez035.kea.SharedPrefManager;
import okhttp3.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private ServerRequest mAuthTask = null;

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
            showProgress(true);
            new ServerRequest(this, "http://elenah.duckdns.org/login.php", new RequestCallback() {
                @Override
                public void onSuccess(Response response) throws IOException {
                    showProgress(false);
                    mAuthTask = null;
                    String result = response.body().string();

                    if ("0".equals(result)) {
                        get_user_info();
                    } else if ("3".equals(result)) {
                        Snackbar.make(mLoginFormView, R.string.session_expired, Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(mLoginFormView, R.string.connection_error, Snackbar.LENGTH_LONG).show();
                    }
                }
            }).setErrorListener(new Runnable() {
                @Override
                public void run() {
                    showProgress(false);
                    Snackbar.make(mLoginFormView, R.string.connection_error, Snackbar.LENGTH_LONG).show();
                }
            }).execute();
        }
    }

    private void get_user_info() {
        new ServerRequest(this, "http://elenah.duckdns.org/userInfo.php", new RequestCallback() {
            @Override
            public void onSuccess(Response response) throws IOException {
                showProgress(false);
                try {
                    JSONObject obj = new JSONObject(response.body().string());

                    if (obj.getInt("status") == 1) {
                        String username = obj.getString("username");
                        int level = obj.getInt("level");
                        int points = obj.getInt("points");
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("level", level);
                        intent.putExtra("points", points);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    Snackbar.make(mLoginFormView, R.string.connection_error, Snackbar.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }).execute();
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
        final String username = usernameET.getText().toString();
        String password = passwordET.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(password)) {
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


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            Map<String, String> params = new HashMap<>();
            params.put("username", username);
            params.put("password", password);

            mAuthTask = new ServerRequest(this, "http://elenah.duckdns.org/login.php", params, new RequestCallback() {
                @Override
                public void onSuccess(Response response) throws IOException {
                    mAuthTask = null;
                    showProgress(false);
                    if (response == null) {
                        AlertDialog.Builder b = new AlertDialog.Builder(LoginActivity.this);
                        b.setMessage(R.string.connection_error);
                        b.setIcon(android.R.drawable.ic_dialog_alert);
                        b.show();
                        return;
                    }
                    int status;
                    try {
                        status = Integer.parseInt(response.body().string());
                    } catch (IOException e) {
                        Snackbar.make(mLoginFormView, R.string.connection_error, Snackbar.LENGTH_LONG).show();
                        e.printStackTrace();
                        return;
                    }

                    usernameET.setError(null);
                    if (status == 0) {
                        String cookie = response.headers().get("Set-Cookie");
                        String[] parts = cookie.split(";")[0].split("=");
                        Log.d("GAL", "Cookie: " + parts[1]);
                        if (!parts[0].equals("PHPSESSID")) {
                            Snackbar.make(mLoginFormView, R.string.connection_error, Snackbar.LENGTH_LONG).show();
                        } else {
                            showProgress(true);
                            SharedPrefManager.getInstance(LoginActivity.this).userLogin(username, parts[1]);
                            get_user_info();
                        }
                    } else {
                        Log.d("GAL", Integer.toString(status));
                        switch (status) {
                            case 4:
                                usernameET.setError(getString(R.string.error_invalid_credentials));
                                usernameET.requestFocus();
                                break;
                            default:
                                Snackbar.make(mLoginFormView, R.string.connection_error, Snackbar.LENGTH_LONG).show();
                                break;
                        }
                    }
                }
            }).setErrorListener(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(mLoginFormView, R.string.connection_error, Snackbar.LENGTH_LONG).show();
                }
            }).withoutCookie();
            mAuthTask.execute();
        }
    }

    private boolean isPasswordValid(String password) {
        String passwordRegex = "[A-Z0-9a-z._%+\\-]{6,}";
        return !TextUtils.isEmpty(password) && Pattern.matches(passwordRegex, password);
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
}

