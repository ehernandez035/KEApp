package es.ehu.ehernandez035.kea.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import es.ehu.ehernandez035.kea.R;
import es.ehu.ehernandez035.kea.adapters.KodAdapter;
import es.ehu.ikasle.ehernandez035.makroprograma.Utils;

public class KodFragment extends Fragment {
    public KodFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        final TextView runTV = getActivity().findViewById(R.id.kod_run_TV);
        runTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String kopStr = kopET.getText().toString();
                try {
                    int kop = Integer.parseInt(kopStr);
                    recyclerView.setAdapter(new KodAdapter(kop));
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }
        });

        Button bihurtuBtn = getActivity().findViewById(R.id.kod_bihurtuBtn);
        bihurtuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<BigInteger> balioak = new ArrayList<>();
                for (String s : ((KodAdapter) recyclerView.getAdapter()).getValues()) {
                    balioak.add(new BigInteger(s));
                }
                BigInteger emaitza = Utils.kod(balioak);
                TextView emaitzaTV = getActivity().findViewById(R.id.emaitzakod_TV);
                emaitzaTV.setText(emaitza.toString());
            }
        });
    }

}
