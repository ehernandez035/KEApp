package es.ehu.ehernandez035.kea.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import es.ehu.ehernandez035.kea.R;
import es.ehu.ehernandez035.kea.adapters.KodAdapter;
import es.ehu.ikasle.ehernandez035.makroprograma.Utils;

public class KodFragment extends Fragment {
    private Menu menu;

    public KodFragment() {

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
                alert.setView(R.layout.help_kod);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kod, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final RecyclerView recyclerView = this.getActivity().findViewById(R.id.kod_valRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        final EditText kopET = this.getActivity().findViewById(R.id.kod_kopET);

        recyclerView.setAdapter(new KodAdapter(0));

        final ImageView runTV = getActivity().findViewById(R.id.kod_run_TV);
        runTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String kopStr = kopET.getText().toString();
                if(kopStr.isEmpty()){
                    kopET.setError(getString(R.string.konbertsorea_zenbakia));
                }
                try {
                    int kop = Integer.parseInt(kopStr);
                    recyclerView.setAdapter(new KodAdapter(kop));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        Button bihurtuBtn = getActivity().findViewById(R.id.kod_bihurtuBtn);
        bihurtuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<BigInteger> balioak = new ArrayList<>();
                KodAdapter adapter = (KodAdapter) recyclerView.getAdapter();
                List<String> values = adapter.getValues();
                for (int i = 0; i < values.size(); i++) {
                    String s = values.get(i);
                    if (s.isEmpty()) balioak.add(BigInteger.ZERO);
                    else {
                        try {
                            balioak.add(new BigInteger(s));
                        } catch (NumberFormatException e) {
                            adapter.setInvalid(i);
                        }
                    }
                }

                if(balioak.isEmpty()) {
                    Log.d("GAL", "Empty list");
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setMessage(R.string.esperotakoBalioa)
                        .setTitle(R.string.question_ask_result)
                .setView(input);
                BigInteger emaitza = Utils.kod(balioak);
                builder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String emaitzaErabStr = input.getText().toString();
                                if (emaitzaErabStr.isEmpty()) {
                                    dialog.dismiss();
                                    new AlertDialog.Builder(getActivity()).setTitle(R.string.okerra).setPositiveButton(android.R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss()).setMessage(R.string.emaitza_okerra).create().show();
                                    return;
                                }
                                BigInteger emaitzaErabBI = new BigInteger(emaitzaErabStr);
                                if (emaitzaErabBI.equals(emaitza)) {
                                    dialog.dismiss();
                                    new AlertDialog.Builder(getActivity()).setTitle(R.string.ondo).setPositiveButton(android.R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss()).setMessage(R.string.emaitza_zuzena).create().show();
                                } else {
                                    dialog.dismiss();
                                    new AlertDialog.Builder(getActivity()).setTitle(R.string.okerra).setPositiveButton(android.R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss()).setMessage(R.string.emaitza_okerra).create().show();
                                }
                                TextView emaitzaTV = getActivity().findViewById(R.id.emaitzakod_TV);
                                emaitzaTV.setText("W" + emaitza.toString());
                            }
                        });
                builder.create().show();
            }
        });
    }

}
