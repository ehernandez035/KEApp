package es.ehu.ehernandez035.kea.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.ehu.ehernandez035.kea.Quizz;
import es.ehu.ehernandez035.kea.R;
import es.ehu.ehernandez035.kea.RequestCallback;
import es.ehu.ehernandez035.kea.ServerRequest;
import es.ehu.ehernandez035.kea.adapters.GaldetegiZerrendaAdapter;
import okhttp3.Response;

public class GaldetegiZerrendaActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView description = findViewById(R.id.galdeteguia_description);
        TextView titulua = findViewById(R.id.galdetegia_tittle);
        TextView correctAnswers = findViewById(R.id.galdetegia_correctAnswer);

        new ServerRequest(this, "http://elenah.duckdns.org/quizzes.php", new RequestCallback() {
            @Override
            public void onSuccess(Response response) throws IOException {
                String result = response.body().string();
                if (result != null) {
                    try {
                        JSONObject jObject = new JSONObject(result);
                        int status = jObject.getInt("status");
                        if (status == 0) {
                            // TODO Error
                        } else {
                            JSONArray data = jObject.getJSONArray("quizzes");

                            List<Quizz> descriptionList = new ArrayList<>();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject galdetegia = data.getJSONObject(i);
                                descriptionList.add(new Quizz(galdetegia.getInt("quizzid"),
                                        galdetegia.getString("description"),
                                        galdetegia.getInt("correctAnswers"),
                                        galdetegia.getInt("amount")));
                            }
                            Log.d("GAL", "Quiz count: " + Integer.toString(descriptionList.size()));
                            ((GaldetegiZerrendaAdapter) mRecyclerView.getAdapter()).setData(descriptionList);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).execute();

        setContentView(R.layout.activity_galdetegi_zerrenda);
        mRecyclerView = (RecyclerView) findViewById(R.id.galdetegiZerrenda_rv);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        mRecyclerView.setAdapter(new GaldetegiZerrendaAdapter(new ArrayList<Quizz>(), GaldetegiZerrendaActivity.this));

    }

}
