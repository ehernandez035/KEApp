package es.ehu.ehernandez035.kea.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.ehu.ehernandez035.kea.MakroRun;
import es.ehu.ehernandez035.kea.adapters.ErrorListAdapter;
import es.ehu.ehernandez035.kea.adapters.ParamListAdapter;
import es.ehu.ehernandez035.kea.R;
import es.ehu.ehernandez035.kea.activities.ProgActivity;
import es.ehu.ikasle.ehernandez035.makroprograma.SZA.Errorea;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ProgFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProgFragment extends Fragment {

    private TextView lineNumbers;
    private EditText programText;

    public ProgFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProgFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProgFragment newInstance(String param1, String param2) {
        ProgFragment fragment = new ProgFragment();
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
        return inflater.inflate(R.layout.fragment_prog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lineNumbers = (TextView) getActivity().findViewById(R.id.lineNumberView);
        programText = (EditText) getActivity().findViewById(R.id.programText);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            programText.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    lineNumbers.setScrollY(programText.getScrollY());
                }
            });
        }

        programText.setImeActionLabel("Custom text", KeyEvent.KEYCODE_ENTER);

        programText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER) {
                    lineNumbers.setScrollY(programText.getScrollY());
                }
                return false;
            }
        });

        programText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int lineNumber = programText.getLineCount();
                lineNumbers.setScrollY(programText.getScrollY());
                StringBuilder lineText = new StringBuilder();
                for (int z = 1; z <= lineNumber; z++) {
                    lineText.append(z).append("\n");
                }
                lineNumbers.setText(lineText.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        final Button exekuteBtn = (Button) getActivity().findViewById(R.id.exekButton);
        exekuteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText programaText = getActivity().findViewById(R.id.programText);
                final String programa = programaText.getText().toString();

                final List<Character> alfabetoa = ((ProgActivity) getActivity()).getAlfabetoa();
                final List<String> parametroak = ((ProgActivity) getActivity()).getParametroak();

                exekuteBtn.setText(R.string.gelditu_text);

                final Thread execThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final List<Errorea> erroreak = new ArrayList<>();

                        final String emaitza = MakroRun.exekutatu(programa, alfabetoa, parametroak, erroreak);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                exekuteBtn.setText(R.string.exek_title);


                                if (erroreak.isEmpty()) {
                                    TextView tvemaitza = getActivity().findViewById(R.id.emaitzaView);
                                    tvemaitza.setText(emaitza);
                                } else {
                                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                                    View mView = getActivity().getLayoutInflater().inflate(R.layout.error_alert, null);
                                    Button closeButton = (Button) mView.findViewById(R.id.closeError);
                                    RecyclerView list = mView.findViewById(R.id.error_list);

                                    list.setAdapter(new ErrorListAdapter(erroreak));
                                    list.setLayoutManager(new LinearLayoutManager(mView.getContext()));


                                    mBuilder.setView(mView);
                                    final AlertDialog dialog = mBuilder.create();
                                    dialog.show();

                                    closeButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog.cancel();
                                        }
                                    });
                                }
                            }
                        });
                    }
                }, "ExekuteThread");


                /*exekuteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        execThread.stop();
                        try {
                            execThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        exekuteBtn.setText(R.string.exek_title);
                    }
                });*/

                execThread.start();

            }
        });
    }

}
