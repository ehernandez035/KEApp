package es.ehu.ehernandez035.kea.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Scanner;

import es.ehu.ehernandez035.kea.R;
import es.ehu.ehernandez035.kea.fragments.ProgFragment;

public class ProgListAdapter extends RecyclerView.Adapter<ProgListAdapter.ViewHolder> {
    private final ProgFragment fragment;
    private final List<String> paths;

    public ProgListAdapter(ProgFragment fragment, List<String> paths) {
        this.fragment = fragment;
        this.paths = paths;
    }

    @NonNull
    @Override
    public ProgListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.prog_item, parent, false);
        return new ProgListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProgListAdapter.ViewHolder holder, final int position) {
        String path = paths.get(position);
        String fileName = new File(path).getName();
        holder.izenaTV.setText(fileName);
        holder.izenaTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoadFileTask(path, holder.izenaTV, fragment).execute();
            }
        });
        holder.deleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
                builder.setTitle("Programa ezabatu")
                        .setMessage("Ziur al zaude programa ezabatu nahi duzula?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new DeleteTask(path, ProgListAdapter.this, holder.getAdapterPosition()).execute();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView izenaTV;
        final ImageView deleteIV;


        ViewHolder(View view) {
            super(view);
            mView = view;
            izenaTV = view.findViewById(R.id.progitem_TV);
            deleteIV = view.findViewById(R.id.deleteIV);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + izenaTV.getText().toString() + "'";
        }
    }

    private static class LoadFileTask extends AsyncTask<Void, Void, String> {
        private WeakReference<View> view;
        private WeakReference<ProgFragment> fragment;
        private final String path;

        LoadFileTask(String path, View view, ProgFragment fragment) {
            this.path = path;
            this.view = new WeakReference<>(view);
            this.fragment = new WeakReference<>(fragment);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                return new Scanner(new File(path)).useDelimiter("\\Z").next();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                fragment.get().setProgramText(s);
            } else {
                Snackbar.make(view.get(), R.string.error_happened, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private static class DeleteTask extends AsyncTask<Void, Void, Boolean> {

        private final String path;
        private final WeakReference<ProgListAdapter> adapter;
        private final int position;

        DeleteTask(String path, ProgListAdapter adapter, int position) {
            this.path = path;
            this.adapter = new WeakReference<>(adapter);
            this.position = position;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            File toDelete = new File(path);
            return toDelete.exists() && toDelete.delete();
        }

        @Override
        protected void onPostExecute(Boolean success) {

            ProgListAdapter adapter = this.adapter.get();
            if (adapter != null) {
                if (success) {
                    Snackbar.make(adapter.fragment.getActivity().findViewById(R.id.programText), R.string.program_deleted, Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(adapter.fragment.getActivity().findViewById(R.id.programText), R.string.program_could_not_be_deleted, Snackbar.LENGTH_LONG).show();
                }
                adapter.notifyItemRemoved(position);
            }
        }
    }
}
