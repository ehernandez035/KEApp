package es.ehu.ehernandez035.kea.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import es.ehu.ehernandez035.kea.adapters.ParamListAdapter;
import es.ehu.ehernandez035.kea.R;


public class ParamFragment extends Fragment {
    private ParamChangedListener listener;

    public ParamFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_param, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = this.getActivity().findViewById(R.id.param_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        List<String> params = new ArrayList<>();
        params.add("aab");

        final EditText alf = this.getActivity().findViewById(R.id.alfabetoa_editText);
        String[] letrak = alf.getText().toString().replaceAll(" ", "").split(",");
        List<Character> alfabetoa = new ArrayList<>();
        for (String letra : letrak) {
            if (letra.isEmpty()) continue;
            alfabetoa.add(letra.charAt(0));
        }
        final ParamListAdapter adapter = new ParamListAdapter(params, recyclerView, this);
        adapter.onAlphabetChanged(alfabetoa);
        recyclerView.setAdapter(adapter);
        alf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String[] letrak = alf.getText().toString().replaceAll(" ", "").split(",");
                List<Character> alfabetoa = new ArrayList<>();
                for (String letra : letrak) {
                    if (letra.isEmpty()) continue;
                    alfabetoa.add(letra.charAt(0));
                }
                adapter.onAlphabetChanged(alfabetoa);
                Log.d("KEA_GAL", "alfabetoaChanged param");

                if(listener!=null){
                    listener.onAlphabetChanged(alfabetoa);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        alf.setText(alf.getText());


        final FloatingActionButton addButton = this.getActivity().findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                adapter.addNewItem();

            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof ParamChangedListener){
            listener = (ParamChangedListener) context;
            Log.d("KEA_GAL", "attached");
        }
    }

    public void onParamsChanged(List<String> mValues) {
        if(listener!=null){
            listener.onParamChanged(mValues);
            Log.d("KEA_GAL", "paramChanged param");
        }
    }

    public interface ParamChangedListener{
        void onAlphabetChanged(List<Character> alfabetoa);
        void onParamChanged(List<String> parametroak);

    }
}
