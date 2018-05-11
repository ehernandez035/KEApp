package es.ehu.ehernandez035.kea.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import es.ehu.ehernandez035.kea.R;

public class KodAdapter extends RecyclerView.Adapter<KodAdapter.ViewHolder> {
    private final List<String> mValues;

    public KodAdapter(int kop) {
        mValues = new ArrayList<>();
        for (int i=0;i<kop;i++){
            mValues.add("");
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.kod_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.kodHitza.setText(mValues.get(position));

        if (holder.watcher != null) holder.kodHitza.removeTextChangedListener(holder.watcher);
        final TextWatcher newWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (position < mValues.size()) {
                    final String newValue = holder.kodHitza.getText().toString();
                    if (!mValues.get(position).equals(newValue)) {
                        mValues.set(position, newValue);
                    }
                }
            }
        };
        holder.watcher = newWatcher;
        holder.kodHitza.addTextChangedListener(newWatcher);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public List<String> getValues() {
        return mValues;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final EditText kodHitza;
        TextWatcher watcher = null;


        ViewHolder(View view) {
            super(view);
            mView = view;
            kodHitza = view.findViewById(R.id.kod_hitzET);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + kodHitza.getText() + "'";
        }
    }
}
