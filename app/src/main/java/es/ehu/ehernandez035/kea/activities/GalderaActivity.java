package es.ehu.ehernandez035.kea.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import es.ehu.ehernandez035.kea.Galdera;
import es.ehu.ehernandez035.kea.R;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class GalderaActivity extends AppCompatActivity {
    private GalderaTask mAuthTask;
    private RadioGroup radioGroup;
    private ImageView hurrengoa;
    private ImageView aurrekoa;
    private TextView galdera;
    private RadioGroup radiogroup;
    private RadioButton op1;
    private RadioButton op2;
    private RadioButton op3;
    private RadioButton op4;
    private TextView total;
    private Button send;
    private ArrayList<Galdera> galderak;
    private ArrayList<String> erantzunak;
    private int[] aukeratutakoPosizioak;

    private static final int OP1_ID = 1000;//first radio button id
    private static final int OP2_ID = 1001;//second radio button id
    private static final int OP3_ID = 1002;//third radio button id
    private static final int OP4_ID = 1003;//third radio button id

    private int position = 0;
    private RadioButton zuzena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galdera);

        galdera = findViewById(R.id.galderaTV);
        op1 = findViewById(R.id.galdera_op1);
        op2 = findViewById(R.id.galdera_op2);
        op3 = findViewById(R.id.galdera_op3);
        op4 = findViewById(R.id.galdera_op4);
        hurrengoa = findViewById(R.id.galdera_hurIV);
        aurrekoa = findViewById(R.id.galdera_aurreIV);

        total = findViewById(R.id.galdera_enum);
        send = findViewById(R.id.sendGalderaButton);

        radioGroup = findViewById(R.id.galderaRadioGroup);

        if (getIntent().hasExtra("quizzid")) {
            final int quizzid = getIntent().getExtras().getInt("quizzid");
            mAuthTask = new GalderaTask(quizzid);
            mAuthTask.execute((Void) null);

            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BidaliGalderaTask mAuthTask = new BidaliGalderaTask(quizzid, erantzunak);
                    mAuthTask.execute((Void) null);
                }
            });
        } else {
            throw new IllegalStateException("Quizz id not given");
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
                        final JSONArray data = jObject.getJSONArray("questions");
                        galderak = new ArrayList<>(data.length());
                        erantzunak = new ArrayList<>(data.length());
                        aukeratutakoPosizioak = new int[data.length()];
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject elem = data.getJSONObject(i);

                            Galdera galderaBerria = new Galdera(
                                    elem.getString("question"),
                                    elem.getString("correctAns"),
                                    elem.getString("incorrectAns1"),
                                    elem.getString("incorrectAns2"),
                                    elem.getString("incorrectAns3"));

                            galderak.add(galderaBerria);
                        }


                        erakutsiGaldera(position);

                        send.setVisibility(galderak.size() == 1 ? View.VISIBLE : View.INVISIBLE);
                        hurrengoa.setVisibility(galderak.size() == 1 ? View.INVISIBLE : View.VISIBLE);

                        for (int i = 0; i<galderak.size(); i++) {
                            erantzunak.add("");
                        }


                        hurrengoa.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                position++;
                                if (position == galderak.size() - 1) {
                                    hurrengoa.setVisibility(View.INVISIBLE);
                                    send.setVisibility(View.VISIBLE);
                                }
                                aurrekoa.setVisibility(View.VISIBLE);
                                erakutsiGaldera(position);
                            }
                        });

                        aurrekoa.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                position--;
                                if (position == 0) {
                                    aurrekoa.setVisibility(View.INVISIBLE);
                                }

                                send.setVisibility(View.INVISIBLE);

                                hurrengoa.setVisibility(View.VISIBLE);
                                erakutsiGaldera(position);
                            }
                        });
                        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                            @Override
                            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                int selectedId = radioGroup.getCheckedRadioButtonId();
                                String aukeratutakoBalioa = "";
                                int aukeratutakoPosizioa = 0;
                                switch (selectedId) {
                                    case R.id.galdera_op1:
                                        aukeratutakoPosizioa = 0;
                                        aukeratutakoBalioa = op1.getText().toString();
                                        break;
                                    case R.id.galdera_op2:
                                        aukeratutakoPosizioa = 1;
                                        aukeratutakoBalioa = op2.getText().toString();
                                        break;
                                    case R.id.galdera_op3:
                                        aukeratutakoPosizioa = 2;
                                        aukeratutakoBalioa = op3.getText().toString();
                                        break;
                                    case R.id.galdera_op4:
                                        aukeratutakoPosizioa = 3;
                                        aukeratutakoBalioa = op4.getText().toString();
                                        break;

                                }
                                erantzunak.set(position, aukeratutakoBalioa);
                                aukeratutakoPosizioak[position] = aukeratutakoPosizioa;
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    private void erakutsiGaldera(int position) {
        Galdera g = galderak.get(position);
        galdera.setText(g.getGaldera());

        op1.setText(g.getAuk0());
        op2.setText(g.getAuk1());
        op3.setText(g.getAuk2());
        op4.setText(g.getAuk3());

        total.setText((position + 1) + "/" + galderak.size());


        RadioButton toSelect = (RadioButton) radioGroup.getChildAt(aukeratutakoPosizioak[position]);
        radioGroup.check(toSelect.getId());
        switch (aukeratutakoPosizioak[position]) {
            case 0:
                op1.setChecked(true);
                op2.setChecked(false);
                op3.setChecked(false);
                op4.setChecked(false);
                op1.setSelected(true);
                op2.setSelected(false);
                op3.setSelected(false);
                op4.setSelected(false);
                break;
            case 1:
                op1.setChecked(false);
                op2.setChecked(true);
                op4.setChecked(false);
                op3.setChecked(false);
                op1.setSelected(false);
                op2.setSelected(true);
                op4.setSelected(false);
                op3.setSelected(false);
                break;
            case 2:
                op1.setChecked(false);
                op2.setChecked(false);
                op3.setChecked(true);
                op4.setChecked(false);
                op1.setSelected(false);
                op2.setSelected(false);
                op3.setSelected(true);
                op4.setSelected(false);
                break;
            case 3:
                op1.setChecked(false);
                op2.setChecked(false);
                op3.setChecked(false);
                op4.setChecked(true);
                op1.setSelected(false);
                op2.setSelected(false);
                op3.setSelected(false);
                op4.setSelected(true);
                break;
        }

    }

    public class BidaliGalderaTask extends AsyncTask<Void, Void, Integer> {

        private final int quizzid;
        private final ArrayList<String> erantzunak;
        private final OkHttpClient client;

        BidaliGalderaTask(int quizzid, ArrayList<String> erantzunak) {
            this.quizzid = quizzid;
            this.erantzunak = erantzunak;
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.followRedirects(false);
            client = builder.build();
        }


        @Override
        protected Integer doInBackground(Void... params) {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsArray = new JSONArray(erantzunak);
            try {
                jsonObject.put("answers", jsArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String message = jsonObject.toString();
            Log.d("GAL", message);
            // Luego ya como mandar el usuario/contraseña por POST:
            RequestBody formBody = new FormBody.Builder()
                    .add("answers", message)
                    .add("quizzid", Integer.toString(quizzid))
                    .build();
            Request request = new Request.Builder()
                    .url("http://elenah.duckdns.org/check_answers.php")
                    .post(formBody)
                    .build();

            try {
                okhttp3.Response response = client.newCall(request).execute();
                return Integer.parseInt(response.body().string());
            } catch (Exception e) {
                Log.e("KEA", "Error sending answers", e);
                return -1;
            }
        }

        @Override
        protected void onPostExecute(final Integer result) {

            if (result != null) {
                try {
                    if (result < 0) {
                        Log.d("GAL", Integer.toString(result));
                        Snackbar.make(findViewById(R.id.galderaLayout), R.string.connection_error, Snackbar.LENGTH_SHORT).show();
                    } else {
                        galdera.setText(Integer.toString(result));
                    }
                } catch (NumberFormatException e) {
                    Snackbar.make(findViewById(R.id.galderaLayout), R.string.connection_error, Snackbar.LENGTH_SHORT).show();
                }
            }

        }

    }


}
