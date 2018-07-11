package es.ehu.ehernandez035.kea.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private Menu menu;

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_kod_dekod, menu);
        this.menu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setView(R.layout.help_konbertsorea);
                alert.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alert.show();
                return true;
        }
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calculator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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
                ArrayAdapter<IrteeraMotak> irteeraAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, irteeraMotak);
                irteeraAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                irteeraSpinner.setAdapter(irteeraAdapter);


                bihurketaButton.setEnabled(true);
                irteeraSpinner.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                bihurketaButton.setEnabled(false);
                irteeraSpinner.setEnabled(false);
            }
        });

        ArrayAdapter<IrteeraMotak> irteeraAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, new IrteeraMotak[]{});
        irteeraAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        irteeraSpinner.setAdapter(irteeraAdapter);


        bihurketaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] letrak = alfabetoa.getText().toString().replaceAll("\\s", "").split(",");
                List<Character> alfLista = new ArrayList<>();
                for (String letra : letrak) {
                    if (letra.isEmpty()) continue;
                    alfLista.add(letra.charAt(0));
                }
                String sarrera = sarreraText.getText().toString().replaceAll("\\s", "");
                SarreraMotak sarreraMota = (SarreraMotak) sarreraSpinner.getSelectedItem();
                IrteeraMotak irteeraMota = (IrteeraMotak) irteeraSpinner.getSelectedItem();
                if (sarreraMota != SarreraMotak.Pila && sarreraMota != SarreraMotak.Bektore && sarrera.contains(",")) {
                    sarreraText.setError(getString(R.string.malformed_input));
                    return;
                }
                if (sarreraMota == SarreraMotak.Zenbaki && !isNumeric(sarrera)) {
                    sarreraText.setError(getString(R.string.konbertsorea_zenbaki_errorea));
                    return;
                }
                if (sarreraMota != SarreraMotak.Zenbaki && isNumeric(sarrera)) {
                    sarreraText.setError(getString(R.string.malformed_input));
                    return;
                }


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
                        hitzModuan = Utils.pilatikHitzera(alfLista, Arrays.asList(sarrera.replaceAll("[<\\]()]", "").split(",", -1)));
                        break;
                    case Bektore:
                        hitzModuan = Utils.bektoretikHitzera(alfLista, Arrays.asList(sarrera.replaceAll("[<\\]()]", "").split(",", -1)));
                        break;
                    case Zenbaki:
                        hitzModuan = Utils.zenbakiaHitzera(alfLista, new BigInteger(sarrera));
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
                            sbpila.append(hitzak.get(i).isEmpty() ? "ε" : hitzak.get(i));
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
                            sbbek.append(hitzakB.get(i).isEmpty() ? "ε" : hitzakB.get(i));
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

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

}

