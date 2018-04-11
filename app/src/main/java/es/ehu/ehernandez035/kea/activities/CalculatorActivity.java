package es.ehu.ehernandez035.kea.activities;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import es.ehu.ehernandez035.kea.R;
import es.ehu.ikasle.ehernandez035.makroprograma.Utils;

public class CalculatorActivity extends AppCompatActivity {

    static Context context;

    private enum SarreraMotak {
        Hitza(R.string.hitza),
        Pila(R.string.pila),
        Bektore(R.string.bektore),
        Zenbaki(R.string.zenbaki);

        private int resourceId;

        SarreraMotak(int resourceId) {
            this.resourceId = resourceId;
        }


        @Override
        public String toString() {
            return context.getString(resourceId);
        }
    }

    private enum IrteeraMotak {
        Hitza(R.string.hitza),
        Pila(R.string.pila),
        Bektore(R.string.bektore),
        Zenbaki(R.string.zenbaki);

        private int resourceId;

        IrteeraMotak(int resourceId) {
            this.resourceId = resourceId;
        }


        @Override
        public String toString() {
            return context.getString(resourceId);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalculatorActivity.context = this.getApplicationContext();
        setContentView(R.layout.activity_calculator);

        final Spinner sarreraSpinner = findViewById(R.id.sarrera_spinner);
        final EditText alfabetoa = findViewById(R.id.alfabetoaET);
        final Button bihurketaButton = findViewById(R.id.bihurketaButton);
        final Spinner irteeraSpinner = findViewById(R.id.irteera_spinner);
        final EditText sarreraText = findViewById(R.id.sarreraBalioakET);
        final TextView irteeraText = findViewById(R.id.emaitzaBalioaET);

        final ArrayAdapter<SarreraMotak> sarreraAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SarreraMotak.values());
        sarreraAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sarreraSpinner.setAdapter(sarreraAdapter);

        sarreraSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                SarreraMotak mota = (SarreraMotak) adapterView.getItemAtPosition(pos);
                IrteeraMotak[] irteeraMotak = new IrteeraMotak[]{};
                switch (mota) {
                    case Hitza:
                        irteeraMotak = new IrteeraMotak[]{IrteeraMotak.Pila, IrteeraMotak.Bektore, IrteeraMotak.Zenbaki};
                        break;
                    case Pila:
                        irteeraMotak = new IrteeraMotak[]{IrteeraMotak.Hitza, IrteeraMotak.Bektore, IrteeraMotak.Zenbaki};
                        break;
                    case Bektore:
                        irteeraMotak = new IrteeraMotak[]{IrteeraMotak.Hitza, IrteeraMotak.Pila, IrteeraMotak.Zenbaki};
                        break;
                    case Zenbaki:
                        irteeraMotak = new IrteeraMotak[]{IrteeraMotak.Hitza, IrteeraMotak.Pila, IrteeraMotak.Bektore};
                        break;
                }
                irteeraSpinner.setAdapter(new ArrayAdapter<>(CalculatorActivity.this, android.R.layout.simple_spinner_item, irteeraMotak));


                bihurketaButton.setEnabled(true);
                irteeraSpinner.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                bihurketaButton.setEnabled(false);
                irteeraSpinner.setEnabled(false);
            }
        });


        irteeraSpinner.setAdapter(new ArrayAdapter<>(CalculatorActivity.this, android.R.layout.simple_spinner_item, new IrteeraMotak[]{}));


        bihurketaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] letrak = alfabetoa.getText().toString().replaceAll("\\s", "").split(",");
                List<Character> alfLista = new ArrayList<>();
                for (String letra : letrak) {
                    if (letra.isEmpty()) continue;
                    alfLista.add(letra.charAt(0));
                }

                SarreraMotak sarreraMota = (SarreraMotak) sarreraSpinner.getSelectedItem();
                IrteeraMotak irteeraMota = (IrteeraMotak) irteeraSpinner.getSelectedItem();

                String sarrera = sarreraText.getText().toString().replaceAll("\\s", "");
                String sarreraGarbia = sarrera.replaceAll("[,<\\]()0-9]", "");
                for (char c : sarreraGarbia.toCharArray()) {
                    if (!alfLista.contains(c)) {
                        sarreraText.setError(getString(R.string.error_out_of_alphabet));
                        Log.d("GAL2", c + "");
                        return;
                    }
                }
                sarreraText.setError(null);

                String hitzModuan = "";
                switch (sarreraMota) {
                    case Hitza:
                        hitzModuan = sarrera;
                        break;
                    case Pila:
                        hitzModuan = Utils.pilatikHitzera(alfLista, Arrays.asList(sarrera.split(",")));
                        break;
                    case Bektore:
                        hitzModuan = Utils.bektoretikHitzera(alfLista, Arrays.asList(sarrera.split(",")));
                        break;
                    case Zenbaki:
                        hitzModuan = Utils.zenbakiaHitzera(alfLista, Utils.hitzakZenbakira(alfLista, sarrera));
                        break;
                }

                switch (irteeraMota) {
                    case Hitza:
                        irteeraText.setText(hitzModuan);
                        break;
                    case Pila:
                        StringBuilder sbpila = new StringBuilder("< ");
                        ArrayList<String> hitzak = Utils.hitzetikPilera(alfLista, hitzModuan);
                        for (int i = 0; i<hitzak.size(); i++) {
                            sbpila.append(hitzak.get(i));
                            if (i != hitzak.size()-1) {
                                sbpila.append(", ");
                            }
                        }
                        sbpila.append(" ]");
                        irteeraText.setText(sbpila.toString());
                        break;
                    case Bektore:
                        StringBuilder sbbek = new StringBuilder("( ");
                        ArrayList<String> hitzakB = Utils.hitzetikBektorera(alfLista, hitzModuan);
                        for (int i = 0; i<hitzakB.size(); i++) {
                            sbbek.append(hitzakB.get(i));
                            if (i != hitzakB.size()-1) {
                                sbbek.append(", ");
                            }
                        }
                        sbbek.append(" )");
                        irteeraText.setText(sbbek.toString());
                        break;
                    case Zenbaki:
                        int zenb = Utils.hitzakZenbakira(alfLista, hitzModuan);
                        irteeraText.setText(Integer.toString(zenb));
                        break;
                }

            }
        });



    }
}
