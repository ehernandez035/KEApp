package es.ehu.ehernandez035.kea.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.regex.Pattern;

import es.ehu.ehernandez035.kea.R;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity {


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText passwordConf;
    private TextView message;
    private ProgressBar progressBar;

    RequestQueue queue;
    private CheckBox terms;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set up the login form.
        queue = Volley.newRequestQueue(this);
        email = findViewById(R.id.emailET);
        password = findViewById(R.id.passwordET);
        passwordConf = findViewById(R.id.passwordConfET);
        username = findViewById(R.id.usernameET);
        register = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.register_progress);
        terms = findViewById(R.id.register_Terms_checkBox);
        terms.setText(Html.fromHtml(getString(R.string.registerTerminoak, "<a href='http://elenah.duckdns.org/PrivacyPolicy.html'>", "</a>")));
        terms.setClickable(true);
        terms.setMovementMethod(LinkMovementMethod.getInstance());

/*        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });*/


        register.setOnClickListener(view -> attemptLogin());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);

        }
        return super.onOptionsItemSelected(item);
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
        username.setError(null);
        email.setError(null);
        password.setError(null);
        passwordConf.setError(null);

        // Store values at the time of the login attempt.
        String usernameValue = username.getText().toString();
        String emailValue = email.getText().toString();
        String passwordValue = password.getText().toString();
        String passwordConfValue = passwordConf.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid email address.
        if (!isEmailValid(emailValue)) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        }
        if (!isPasswordValid(passwordValue)) {
            password.setError(getString(R.string.error_invalid_password));
            focusView = password;
            cancel = true;
        }


        //Check confirmation password is the same as password
        if (TextUtils.isEmpty(passwordConfValue)) {
            passwordConf.setError(getString(R.string.error_field_required));
            focusView = passwordConf;
            cancel = true;
        } else if (!passwordValue.equals(passwordConfValue)) {
            passwordConf.setError(getString(R.string.error_confirmation_password));
            focusView = passwordConf;
            cancel = true;
        }

        if (!terms.isChecked()) {
            Snackbar.make(terms, R.string.terminoakOnartu, Snackbar.LENGTH_LONG).show();
            terms.setError(getString(R.string.terminoakOnartu));
            focusView = terms;
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
            new UserLoginTask(usernameValue, emailValue, passwordValue).execute();
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        final LinearLayout loginLayout = findViewById(R.id.registerLayout);
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        loginLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        loginLayout.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBar.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    public static boolean isEmailValid(String email) {
        String emailRegex = "[A-Z0-9a-z._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,64}";
        return !(TextUtils.isEmpty(email) && Pattern.matches(emailRegex, email));
    }

    public static boolean isPasswordValid(String password) {
        String passwordRegex = "[A-Z0-9a-z._%+\\-]{6,}";
        return !(TextUtils.isEmpty(password) && Pattern.matches(passwordRegex, password));
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {

        private final String mUsername;
        private final String mEmail;
        private final String mPassword;
        private final OkHttpClient client;

        UserLoginTask(String username, String email, String password) {
            mUsername = username;
            mEmail = email;
            mPassword = password;
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.followRedirects(false);
            client = builder.build();
        }


        @Override
        protected Integer doInBackground(Void... params) {
            FormBody.Builder formbuilder = new FormBody.Builder();
            formbuilder.add("username", mUsername);
            formbuilder.add("email", mEmail);
            formbuilder.add("password", mPassword);
            RequestBody body = formbuilder.build();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url("http://elenah.duckdns.org/register.php")
                    .post(body)
                    .build();
            try {
                okhttp3.Response response = client.newCall(request).execute();
                return Integer.parseInt(response.body().string());
            } catch (Exception e) {
                Log.e("KEA", "Error registering", e);
                return -1;
            }
        }

        @Override
        protected void onPostExecute(final Integer status) {
            mAuthTask = null;
            showProgress(false);

            Log.d("KEA", status.toString());
            password.setError(null);
            email.setError(null);
            username.setError(null);
            if (status == 0) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                switch (status) {
                    case 1:
                        email.setError(getString(R.string.error_invalid_email));
                        email.requestFocus();
                        break;
                    case 2:
                        password.setError(getString(R.string.error_incorrect_password));
                        password.requestFocus();
                        break;
                    case 3:
                    case 6:
                        Snackbar.make(email, R.string.connection_error, Snackbar.LENGTH_SHORT).show();
                        break;
                    case 5:
                        email.setError(getString(R.string.error_email_taken));
                        email.requestFocus();
                        break;
                    case 4:
                        username.setError(getString(R.string.error_username_taken));
                        username.requestFocus();
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
}

