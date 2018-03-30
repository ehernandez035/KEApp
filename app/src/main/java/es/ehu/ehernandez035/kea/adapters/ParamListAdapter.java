package es.ehu.ehernandez035.kea.adapters;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.ehu.ehernandez035.kea.R;
import es.ehu.ehernandez035.kea.fragments.ParamFragment;

public class ParamListAdapter extends RecyclerView.Adapter<ParamListAdapter.ViewHolder> {
    private List<Character> alfabetoa;

    private final List<String> mValues;
    private RecyclerView rv;
    private ParamFragment paramFragment;

    public ParamListAdapter(List<String> items, RecyclerView view, ParamFragment paramFragment) {
        mValues = items;
        rv = view;
        this.paramFragment = paramFragment;
        alfabetoa = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.variable_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.varName.setText("X" + (position + 1) + ":");
        holder.varValue.setText(mValues.get(position));

        boolean error = false;
        EditText val = holder.mView.findViewById(R.id.data);
        for (char c : holder.varValue.getText().toString().toCharArray()) {
            if (!alfabetoa.contains(c)) {
                if (val.getError() == null) {
                    val.setError(holder.mView.getContext().getString(R.string.letter_not_in_alphabet));
                }
                error = true;
                break;
            }
        }
        if (!error && val.getError() != null) {
            val.setError(null);
        }


        if (holder.watcher != null) holder.varValue.removeTextChangedListener(holder.watcher);
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
                    final String newValue = holder.varValue.getText().toString();
                    if (!mValues.get(position).equals(newValue)) {
                        mValues.set(position, newValue);
                        paramFragment.onParamsChanged(mValues);
                        holder.mView.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean error = false;
                                EditText val = holder.mView.findViewById(R.id.data);
                                for (char c : newValue.toCharArray()) {
                                    if (!alfabetoa.contains(c)) {
                                        if (val.getError() == null) {
                                            val.setError(holder.mView.getContext().getString(R.string.letter_not_in_alphabet));
                                        }
                                        error = true;
                                        break;
                                    }
                                }
                                if (!error && val.getError() != null) {
                                    val.setError(null);
                                }
                            }
                        });
                    }
                }
            }
        };
        holder.watcher = newWatcher;
        holder.varValue.addTextChangedListener(newWatcher);

        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.removeButton.setOnClickListener(null);
                mValues.remove(position);
                holder.varValue.removeTextChangedListener(newWatcher);
                ParamListAdapter.this.notifyItemRemoved(position);
                ParamListAdapter.this.notifyItemRangeChanged(position, mValues.size());
            }
        });
    }

    public void onAlphabetChanged(List<Character> alfabetoa) {
        this.alfabetoa = alfabetoa;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void addNewItem() {
        mValues.add("");
        this.notifyItemInserted(mValues.size() - 1);
        rv.scrollToPosition(mValues.size() - 1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView varName;
        final TextView varValue;
        private final Button removeButton;
        TextWatcher watcher = null;

        ViewHolder(View view) {
            super(view);
            mView = view;
            varName = view.findViewById(R.id.var);
            varValue = view.findViewById(R.id.data);
            removeButton = view.findViewById(R.id.remove);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + varValue.getText() + "'";
        }
    }
}
