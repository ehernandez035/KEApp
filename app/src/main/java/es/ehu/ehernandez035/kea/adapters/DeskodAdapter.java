package es.ehu.ehernandez035.kea.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigInteger;

import es.ehu.ehernandez035.kea.R;

public class DeskodAdapter extends RecyclerView.Adapter<DeskodAdapter.ViewHolder> {
    private final BigInteger[] mValues;

    public DeskodAdapter(BigInteger[] items) {
        mValues = items;
    }

    @NonNull
    @Override
    public DeskodAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.deskod_item, parent, false);
        return new DeskodAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DeskodAdapter.ViewHolder holder, final int position) {
        holder.kodHitza.setText("W" + mValues[position].toString());

    }

    @Override
    public int getItemCount() {
        return mValues.length;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView kodHitza;

        ViewHolder(View view) {
            super(view);
            mView = view;
            kodHitza = view.findViewById(R.id.deskod_itemTV);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + kodHitza.getText() + "'";
        }
    }
}
