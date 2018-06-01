package es.ehu.ehernandez035.kea.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigInteger;

import es.ehu.ehernandez035.kea.R;
import es.ehu.ehernandez035.kea.adapters.DeskodAdapter;
import es.ehu.ikasle.ehernandez035.makroprograma.Utils;

public class DeskodFragment extends Fragment{
    private Menu menu;

    public DeskodFragment() {

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
                View view = getActivity().getLayoutInflater().inflate(R.layout.help_deskod, null);
                alert.setView(view);
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
        return inflater.inflate(R.layout.fragment_deskod, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final RecyclerView recyclerView = this.getActivity().findViewById(R.id.deskod_itemRV);
        final EditText hitza = this.getActivity().findViewById(R.id.deskod_hitzaET);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        final EditText kopET = this.getActivity().findViewById(R.id.deskod_kopET);

        recyclerView.setAdapter(new DeskodAdapter(new BigInteger[0]));

        final ImageView runTV = getActivity().findViewById(R.id.deskod_run_TV);
        runTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String kopStr = kopET.getText().toString();
                String hitzaStr = hitza.getText().toString();
                try {
                    int kop = Integer.parseInt(kopStr);
                    if(kop<=0) return;
                    BigInteger hitza = new BigInteger(hitzaStr);

                    recyclerView.setAdapter(new DeskodAdapter(Utils.dekod(hitza, kop)));
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }
        });

    }

}
