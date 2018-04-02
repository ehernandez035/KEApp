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
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.ehu.ehernandez035.kea.MakroRun;
import es.ehu.ehernandez035.kea.R;
import es.ehu.ehernandez035.kea.activities.ProgActivity;
import es.ehu.ehernandez035.kea.adapters.ErrorListAdapter;
import es.ehu.ikasle.ehernandez035.makroprograma.SZA.Errorea;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ProgFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProgFragment extends Fragment implements View.OnClickListener {

    private TextView lineNumbers;
    private EditText programText;
    private HorizontalScrollView horizontalScrollView;


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

    // Gets the current position of the cursor
    public int getEditSelection() {
        return programText.getSelectionStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_prog, container, false);

        Button carButton = layout.findViewById(R.id.carButton);
        Button consButton = layout.findViewById(R.id.consButton);
        Button ifButton = layout.findViewById(R.id.ifButton);
        Button whileButton = layout.findViewById(R.id.whileButton);
        Button forButton = layout.findViewById(R.id.forButton);
        Button functionButton = layout.findViewById(R.id.functionButton);
        carButton.setOnClickListener(this);
        consButton.setOnClickListener(this);
        ifButton.setOnClickListener(this);
        whileButton.setOnClickListener(this);
        forButton.setOnClickListener(this);
        functionButton.setOnClickListener(this);

        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lineNumbers = (TextView) getActivity().findViewById(R.id.lineNumberView);
        programText = (EditText) getActivity().findViewById(R.id.programText);
        horizontalScrollView = (HorizontalScrollView) getActivity().findViewById(R.id.keyboardScrollView);
        horizontalScrollView.setVisibility(View.GONE);

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

        programText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                horizontalScrollView.setVisibility(View.VISIBLE);
            }
        });


        final Button exekuteBtn = (Button) getActivity().findViewById(R.id.exekButton);
        exekuteBtn.setOnClickListener(new View.OnClickListener()

        {
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

    @Override
    public void onClick(View v) {
        String value = "";
        int cursorOffset = 0;


        switch (v.getId()) {
            case R.id.carButton:
                value = "car_?()";
                cursorOffset = 4;
                break;
            case R.id.consButton:
                value = "cons_?()";
                cursorOffset = 5;
                break;
            case R.id.ifButton:
                value = "if  then\n\nend if;";
                cursorOffset = 3;
                break;
            case R.id.whileButton:
                value = "while  loop\n\nend loop;";
                cursorOffset = 5;
                break;
            case R.id.forButton:
                value = "for i in 1.. loop\n\nend loop;";
                cursorOffset = 12;
                break;
            case R.id.functionButton:
                value = "def  begin\n\nend def;";
                cursorOffset = 4;
                break;
            default:
                break;
        }
        int cursorPosition = programText.getSelectionStart();
        programText.getEditableText().insert(cursorPosition, value);
        programText.setSelection(cursorPosition + cursorOffset);

    }
}