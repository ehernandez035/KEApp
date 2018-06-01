package es.ehu.ehernandez035.kea.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import es.ehu.ehernandez035.kea.MakroRun;
import es.ehu.ehernandez035.kea.R;
import es.ehu.ehernandez035.kea.activities.ProgActivity;
import es.ehu.ehernandez035.kea.adapters.ErrorListAdapter;
import es.ehu.ehernandez035.kea.adapters.ProgListAdapter;
import es.ehu.ikasle.ehernandez035.makroprograma.SZA.Errorea;


public class ProgFragment extends Fragment implements View.OnClickListener {

    private TextView lineNumbers;
    private EditText programText;
    private HorizontalScrollView horizontalScrollView;
    private Menu menu;
    private Thread execThread;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_prog, menu);
        this.menu = menu;
        this.menu.getItem(0).setOnMenuItemClickListener(this::programaExekutatu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.prog_execute_clear:
                programText.setText("def main begin\n    \nend def;");
                programText.setSelection(19);
                return true;
            case R.id.prog_execute_load:
                loadProgram();
                return true;
            case R.id.prog_execute_save:
                saveProgram();
                return true;

        }
        return false;
    }

    private void saveProgram() {

    }

    private void loadProgram() {
        new GetSavedProgramList(this).execute();
    }

    private boolean geldituPrograma(MenuItem item) {
        if (execThread != null) {
//            execThread.stop();
            Snackbar.make(programText, "Oraingoz ezin da programa gelditu, itxi aplikazioa gelditzeko.", Snackbar.LENGTH_LONG).show();
//            try {
//                execThread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            execThread = null;
        }

        this.menu.getItem(0).setOnMenuItemClickListener(this::programaExekutatu);
        return true;
    }

    private boolean programaExekutatu(MenuItem item) {
        EditText programaText = getActivity().findViewById(R.id.programText);
        final String programa = programaText.getText().toString();

        final List<Character> alfabetoa = ((ProgActivity) getActivity()).getAlfabetoa();
        final List<String> parametroak = ((ProgActivity) getActivity()).getParametroak();

        menu.getItem(0).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        this.menu.getItem(0).setOnMenuItemClickListener(this::geldituPrograma);

        execThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Errorea> erroreak = new ArrayList<>();

                final String emaitza = MakroRun.exekutatu(programa, alfabetoa, parametroak, erroreak);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        menu.getItem(0).setIcon(android.R.drawable.ic_media_play);
                        ProgFragment.this.menu.getItem(0).setOnMenuItemClickListener(ProgFragment.this::programaExekutatu);
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
        execThread.start();
        return true;
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
                value = "cons_()";
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

    static class GetSavedProgramList extends AsyncTask<Void, Void, List<String>> {

        private final WeakReference<ProgFragment> context;

        GetSavedProgramList(ProgFragment context) {
            this.context = new WeakReference<>(context);
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            ProgFragment fragment = this.context.get();
            if (fragment != null) {
                Context context = fragment.getContext();
                if (context != null) {
                    List<String> paths = new ArrayList<>();

                    File dir = context.getDir("programs", Context.MODE_PRIVATE);
                    for (File f : dir.listFiles()) {
                        paths.add(f.getAbsolutePath());
                    }
                    return paths;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<String> paths) {
            ProgFragment fragment = this.context.get();
            if (fragment != null) {
                Context context = fragment.getContext();
                if (context != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.prog_load_dialog_title);
                    RecyclerView rv = new RecyclerView(context);

                    rv.setLayoutManager(new LinearLayoutManager(context));
                    builder.setView(rv);
                    builder.show();
                    rv.setAdapter(new ProgListAdapter(fragment, paths));
                }
            }
        }
    }

    public void setProgramText(String program) {
        this.programText.setText(program);
    }
}