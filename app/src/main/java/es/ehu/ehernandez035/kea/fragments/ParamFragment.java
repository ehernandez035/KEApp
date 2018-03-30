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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ParamFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParamFragment extends Fragment {
    private ParamChangedListener listener;

    public ParamFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ParamFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ParamFragment newInstance(String param1, String param2) {
        ParamFragment fragment = new ParamFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
