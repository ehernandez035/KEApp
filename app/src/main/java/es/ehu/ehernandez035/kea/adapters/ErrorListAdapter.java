package es.ehu.ehernandez035.kea.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import es.ehu.ehernandez035.kea.R;
import es.ehu.ikasle.ehernandez035.makroprograma.SZA.Errorea;

public class ErrorListAdapter extends RecyclerView.Adapter<ErrorListAdapter.ViewHolder> {

    private final List<Errorea> mValues;

    public ErrorListAdapter(List<Errorea> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.error_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.erroreMezua.setText(mValues.get(position).mezua);
        holder.lerroa.setText(holder.mView.getContext().getString(R.string.error_lerroa, mValues.get(position).p.hasLerro));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView lerroa;
        final EditText erroreMezua;


        ViewHolder(View view) {
            super(view);
            mView = view;
            lerroa = view.findViewById(R.id.lerroaTV);
            erroreMezua = view.findViewById(R.id.erroreMezuaTV);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + erroreMezua.getText() + "'";
        }
    }
}
