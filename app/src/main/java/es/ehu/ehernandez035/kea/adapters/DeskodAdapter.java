package es.ehu.ehernandez035.kea.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import es.ehu.ehernandez035.kea.R;

public class DeskodAdapter extends RecyclerView.Adapter<DeskodAdapter.ViewHolder>  {
    private final BigInteger[]  mValues;

    public DeskodAdapter(BigInteger[] items) {
        mValues = items;
    }

    @Override
    public DeskodAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.deskod_item, parent, false);
        return new DeskodAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DeskodAdapter.ViewHolder holder, final int position) {
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
