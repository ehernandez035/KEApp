package es.ehu.ehernandez035.kea.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.ehu.ehernandez035.kea.R;
import es.ehu.ikasle.ehernandez035.makroprograma.Utils;

public class CalculatorFragment extends Fragment {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calculator, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CalculatorFragment.context = getActivity().getApplicationContext();
        final Spinner sarreraSpinner = getActivity().findViewById(R.id.sarrera_spinner);
        final EditText alfabetoa = getActivity().findViewById(R.id.alfabetoaET);
        final Button bihurketaButton = getActivity().findViewById(R.id.bihurketaButton);
        final Spinner irteeraSpinner = getActivity().findViewById(R.id.irteera_spinner);
        final EditText sarreraText = getActivity().findViewById(R.id.sarreraBalioakET);
        final TextView irteeraText = getActivity().findViewById(R.id.emaitzaBalioaET);

        final ArrayAdapter<SarreraMotak> sarreraAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, SarreraMotak.values());
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
                irteeraSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, irteeraMotak));


                bihurketaButton.setEnabled(true);
                irteeraSpinner.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                bihurketaButton.setEnabled(false);
                irteeraSpinner.setEnabled(false);
            }
        });


        irteeraSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, new IrteeraMotak[]{}));


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
                        for (int i = 0; i < hitzak.size(); i++) {
                            sbpila.append(hitzak.get(i));
                            if (i != hitzak.size() - 1) {
                                sbpila.append(", ");
                            }
                        }
                        sbpila.append(" ]");
                        irteeraText.setText(sbpila.toString());
                        break;
                    case Bektore:
                        StringBuilder sbbek = new StringBuilder("( ");
                        ArrayList<String> hitzakB = Utils.hitzetikBektorera(alfLista, hitzModuan);
                        for (int i = 0; i < hitzakB.size(); i++) {
                            sbbek.append(hitzakB.get(i));
                            if (i != hitzakB.size() - 1) {
                                sbbek.append(", ");
                            }
                        }
                        sbbek.append(" )");
                        irteeraText.setText(sbbek.toString());
                        break;
                    case Zenbaki:
                        BigInteger zenb = Utils.hitzakZenbakira(alfLista, hitzModuan);
                        irteeraText.setText(zenb.toString());
                        break;
                }

            }
        });


    }


}
