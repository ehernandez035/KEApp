package es.ehu.ehernandez035.kea.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.ehu.ehernandez035.kea.Galdera;
import es.ehu.ehernandez035.kea.R;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;

public class GalderaActivity extends AppCompatActivity {
    private GalderaTask mAuthTask;
    private RadioGroup radioGroup;
    private ImageView hurrengoa;
    private ImageView aurrekoa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galdera);

        TextView galdera = findViewById(R.id.galderaTV);
        RadioButton op1 = findViewById(R.id.galdera_op1);
        RadioButton op2 = findViewById(R.id.galdera_op2);
        RadioButton op3 = findViewById(R.id.galdera_op3);
        RadioButton op4 = findViewById(R.id.galdera_op4);
        ImageView hurrengoa = findViewById(R.id.galdera_hurIV);
        ImageView aurrekoa = findViewById(R.id.galdera_aurreIV);

        TextView total = findViewById(R.id.galdera_enum);

        if(getIntent().getExtras().containsKey("quizzid")){
            int id = savedInstanceState.getInt("quizzid");
            mAuthTask = new GalderaTask(id);
            mAuthTask.execute((Void) null);

        }

    }

    public class GalderaTask extends AsyncTask<Void, Void, String> {

        private final int quizzid;
        private final OkHttpClient client;

        GalderaTask(int id) {
            quizzid = id;
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.followRedirects(false);
            client = builder.build();
        }


        @Override
        protected String doInBackground(Void... params) {
            FormBody.Builder formbuilder = new FormBody.Builder();
            formbuilder.add("quizzid", Integer.toString(quizzid));
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url("http://elenah.duckdns.org/question.php?quizzid=" + quizzid)
                    .build();
            try {
                okhttp3.Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                Log.e("KEA", "Error getting questions", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            if (result != null) {
                try {
                    JSONObject jObject = new JSONObject(result);
                    int status = jObject.getInt("status");
                    if (status == 0) {
                        // TODO Error
                    } else {
                        final int position = 0;
                        final JSONArray data = jObject.getJSONArray("questions");
                        final ArrayList<Galdera> galderak = new ArrayList<>();

                        TextView galdera = (TextView) findViewById(R.id.galderaTV);
                        RadioButton op1 = (RadioButton) findViewById(R.id.galdera_op1);
                        RadioButton op2 = (RadioButton) findViewById(R.id.galdera_op2);
                        RadioButton op3 = (RadioButton) findViewById(R.id.galdera_op3);
                        RadioButton op4 = (RadioButton) findViewById(R.id.galdera_op4);

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject elem = data.getJSONObject(i);
                            galdera.setText(elem.getString("question"));
                            op1.setText(elem.getString("correctAns"));
                            op2.setText(elem.getString("incorrectAns1"));
                            op3.setText(elem.getString("incorrectAns2"));
                            op4.setText(elem.getString("incorrectAns3"));
                            Galdera galderaBerria = new Galdera(galdera.getText().toString(), op1.getText().toString(),
                                    op2.getText().toString(), op3.getText().toString(), op4.getText().toString());
                            galderak.add(galderaBerria);
                        }
                        hurrengoa.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(GalderaActivity.this, GalderaActivity.class);
                                intent.putExtra("galdera", galderak);
                                intent.putExtra("position", position + 1);
                                startActivity(intent);
                            }
                        });

                        aurrekoa.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (position > 1) {
                                    Intent intent = new Intent(GalderaActivity.this, GalderaActivity.class);
                                    intent.putExtra("galdera", galderak);
                                    intent.putExtra("position", position - 1);
                                    startActivity(intent);
                                }

                            }
                        });
//                        new GalderaTask(quizzid).execute();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }

    }




}
