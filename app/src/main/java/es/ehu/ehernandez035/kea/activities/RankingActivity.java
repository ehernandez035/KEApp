package es.ehu.ehernandez035.kea.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.ehu.ehernandez035.kea.R;
import es.ehu.ehernandez035.kea.SharedPrefManager;
import okhttp3.OkHttpClient;

public class RankingActivity extends AppCompatActivity {
    private TableLayout table;
    private ProgressBar mProgressView;
    private TextView myranking;
    private LinearLayout rankingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            Intent intent = new Intent(RankingActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        table = findViewById(R.id.ranking_table);
        mProgressView = findViewById(R.id.rankingProgress);

        RankingTask mAuthTask = new RankingTask();
        mAuthTask.execute((Void) null);

        myranking = findViewById(R.id.myrankingTV);
        rankingContainer = findViewById(R.id.rankingContainer);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        rankingContainer.setVisibility(show ? View.GONE : View.VISIBLE);
        rankingContainer.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                rankingContainer.setVisibility(show ? View.GONE : View.VISIBLE);
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
    }

    public class RankingTask extends AsyncTask<Void, Void, String> {

        private final OkHttpClient client;

        RankingTask() {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.followRedirects(false);
            client = builder.build();
        }


        @Override
        protected String doInBackground(Void... params) {
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url("http://elenah.duckdns.org/ranking.php")
                    .build();
            try {
                okhttp3.Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                Log.e("KEA", "Error in the ranking", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            showProgress(false);
            if (result != null) {
                try {
                    JSONObject jObject = new JSONObject(result);
                    int status = jObject.getInt("status");
                    if (status == 0) {

                    } else {
                        JSONArray data = jObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject elem = data.getJSONObject(i);
                            TableRow row = (TableRow) LayoutInflater.from(RankingActivity.this).inflate(R.layout.user_item, null);
                            ((TextView) row.findViewById(R.id.usernameTV)).setText(elem.getString("user"));
                            ((TextView) row.findViewById(R.id.pointTV)).setText(Integer.toString(elem.getInt("points")));
                            ((TextView) row.findViewById(R.id.rankingPosTV)).setText(Integer.toString(i + 1));
                            table.addView(row);
                        }
                        new MyRankingTask().execute();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

    public class MyRankingTask extends AsyncTask<Void, Void, String> {

        private final OkHttpClient client;

        MyRankingTask() {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.followRedirects(false);
            client = builder.build();
        }


        @Override
        protected String doInBackground(Void... params) {
            String cookie = SharedPrefManager.getInstance(RankingActivity.this).getCookie();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url("http://elenah.duckdns.org/myranking.php")
                    .header("Cookie", "PHPSESSID=" + cookie)
                    .build();
            try {
                okhttp3.Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                Log.e("KEA", "Error in the ranking", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            showProgress(false);
            if (result != null) {
                try {
                    int rank = Integer.parseInt(result);
                    if (rank <= 0) {
                        Snackbar.make(table, R.string.connection_error, Snackbar.LENGTH_SHORT).show();
                    } else {
                        String username = SharedPrefManager.getInstance(RankingActivity.this).getUsername();
                        myranking.setText(result + " : " + username);
                    }
                } catch (NumberFormatException e) {
                    Snackbar.make(table, R.string.connection_error, Snackbar.LENGTH_SHORT).show();
                }


            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

}
