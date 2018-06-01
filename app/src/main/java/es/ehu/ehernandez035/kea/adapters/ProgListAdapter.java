package es.ehu.ehernandez035.kea.adapters;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.nio.file.FileSystems;
import java.nio.file.Path;
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
                .inflate(R.layout.error_item, parent, false);
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
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView izenaTV;


        ViewHolder(View view) {
            super(view);
            mView = view;
            izenaTV = view.findViewById(R.id.progitem_TV);
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

        public LoadFileTask(String path, View view, ProgFragment fragment) {
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
}
