package es.ehu.ehernandez035.kea.fragments;

import android.content.Context;
import android.content.DialogInterface;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import es.ehu.ehernandez035.kea.R;
import es.ehu.ehernandez035.kea.RunPrograma;
import es.ehu.ehernandez035.kea.activities.ProgActivity;
import es.ehu.ehernandez035.kea.adapters.ErrorListAdapter;
import es.ehu.ehernandez035.kea.adapters.ProgListAdapter;
import es.ehu.ikasle.ehernandez035.makroprograma.SZA.Errorea;


public class ProgFragment extends Fragment implements View.OnClickListener {

    private static final String DEFAULT_MACRO_PROGRAM = "def main begin\n    \nend def;";
    private static final String DEFAULT_WHILE_PROGRAM = "X0 := cons_a(X1);\n";
    private TextView lineNumbers;
    private EditText programText;
    private HorizontalScrollView horizontalScrollView;
    private Menu menu;
    private Thread execThread;
    private AlertDialog loadDialog = null;
    private boolean isMakro = true;

    public ProgFragment() {

    }

    public static ProgFragment getInstance(boolean makro) {
        ProgFragment f = new ProgFragment();
        f.setMakro(makro);
        return f;
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
                programText.setText(isMakro ? DEFAULT_MACRO_PROGRAM : DEFAULT_WHILE_PROGRAM);
                programText.setSelection(isMakro ? 19 : 18);
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
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(R.string.save_program);
        alert.setMessage(R.string.choose_program_name);
        EditText progIzenaET = new EditText(getActivity());
        alert.setView(progIzenaET);
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new SavedProgram(ProgFragment.this, progIzenaET.getText().toString(), programText.getText().toString(), false).execute();
            }
        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    private void loadProgram() {
        new GetSavedProgramList(this).execute();
    }

    private boolean geldituPrograma(MenuItem item) {
        new StopProgram(execThread, this.menu.getItem(0), programText, this).execute();
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

                String emaitza;
                if (isMakro) {
                    emaitza = RunPrograma.exekutatuMakro(programa, alfabetoa, parametroak, erroreak);
                } else {
                    emaitza = RunPrograma.exekutatuWhile(programa, alfabetoa, parametroak, erroreak);
                }


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

        programText.setText(isMakro ? DEFAULT_MACRO_PROGRAM : DEFAULT_WHILE_PROGRAM);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            programText.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    lineNumbers.setScrollY(programText.getScrollY());
                }
            });
        }

//        programText.setImeActionLabel("Custom text", KeyEvent.KEYCODE_ENTER);

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

    public void setMakro(boolean makro) {
        isMakro = makro;
    }

    static class SavedProgram extends AsyncTask<Void, Void, Boolean> {

        private final WeakReference<ProgFragment> context;
        private final String izena;
        private final String programa;
        private final boolean overwrite;

        SavedProgram(ProgFragment context, String izena, String programa, boolean overwrite) {
            this.context = new WeakReference<>(context);
            this.izena = izena;
            this.programa = programa;
            this.overwrite = overwrite;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            ProgFragment fragment = this.context.get();
            if (fragment != null) {
                Context context = fragment.getContext();
                if (context != null) {


                    File dir = context.getDir("programs", Context.MODE_PRIVATE);
                    File newProg = new File(dir, izena);
                    if (newProg.exists() && !overwrite) {
                        return false;
                    } else {
                        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newProg)))) {
                            writer.write(programa);
                            return true;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) return;
            ProgFragment fragment = this.context.get();
            if (fragment != null) {
                Context context = fragment.getContext();
                if (context != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.program_already_exists);
                    builder.setMessage(R.string.overwrite_program);
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new SavedProgram(fragment, izena, programa, true).execute();
                        }
                    }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                }
            }
        }
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
                    builder.setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    fragment.loadDialog = builder.show();
                    rv.setAdapter(new ProgListAdapter(fragment, paths));
                }
            }
        }
    }

    public void setProgramText(String program) {
        this.programText.setText(program);
        if (loadDialog != null) {
            loadDialog.dismiss();
            loadDialog = null;
        }
    }

    private class StopProgram extends AsyncTask<Void, Void, Void> {

        private final Thread toStop;
        private final WeakReference<MenuItem> item;
        private final WeakReference<View> snackbarHolder;
        private final WeakReference<ProgFragment> fragment;

        StopProgram(Thread toStop, MenuItem item, View snackbarHolder, ProgFragment fragment) {
            this.toStop = toStop;
            this.item = new WeakReference<>(item);
            this.snackbarHolder = new WeakReference<>(snackbarHolder);
            this.fragment = new WeakReference<>(fragment);
        }

        @Override
        protected void onPreExecute() {
            View view = snackbarHolder.get();
            MenuItem item = this.item.get();
            if (item != null) item.setEnabled(false);
            if (view != null) Snackbar.make(view, "Gelditzen", Snackbar.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (execThread != null) {
                execThread.interrupt();
                try {
                    execThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            View view = snackbarHolder.get();
            MenuItem item = this.item.get();
            if (item != null) {
                item.setEnabled(true);
                ProgFragment fragment = this.fragment.get();
                if (fragment != null) {
                    item.setOnMenuItemClickListener(fragment::programaExekutatu);
                    fragment.execThread = null;
                }
                menu.getItem(0).setIcon(android.R.drawable.ic_media_play);
            }
            if (view != null) Snackbar.make(view, "Gelditu da", Snackbar.LENGTH_LONG).show();

        }
    }
}