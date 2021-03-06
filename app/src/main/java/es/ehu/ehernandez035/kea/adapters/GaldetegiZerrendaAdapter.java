package es.ehu.ehernandez035.kea.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import es.ehu.ehernandez035.kea.Quizz;
import es.ehu.ehernandez035.kea.R;
import es.ehu.ehernandez035.kea.activities.GalderaActivity;
import es.ehu.ehernandez035.kea.activities.GaldetegiZerrendaActivity;

public class GaldetegiZerrendaAdapter extends RecyclerView.Adapter<GaldetegiZerrendaAdapter.ViewHolder> {

    private List<Quizz> quizzes;
    private GaldetegiZerrendaActivity galdetegiZerrendaActivity;

    public GaldetegiZerrendaAdapter(List<Quizz> items, GaldetegiZerrendaActivity gza) {
        quizzes = items;
        galdetegiZerrendaActivity = gza;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.galdetegia_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.tittle.setText(galdetegiZerrendaActivity.getString(R.string.galdetegi_izenburua, position + 1));
        final Quizz quizz = quizzes.get(position);
        holder.description.setText(quizz.getDescription());
        if (quizz.getAmount() == 0) {
            holder.correctAnswers.setText("0 %");
        } else {
            holder.correctAnswers.setText(Integer.toString((int) (quizz.getCorrectAnswers() / (float) quizz.getAmount() * 100)) + " %");
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GAL", "Click");
                Intent intent = new Intent(galdetegiZerrendaActivity, GalderaActivity.class);
                intent.putExtra("quizzid", quizz.getQuizzid());
                galdetegiZerrendaActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    public void setData(List<Quizz> data) {
        this.quizzes = data;
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tittle;
        final TextView description;
        final TextView correctAnswers;

        ViewHolder(View view) {
            super(view);
            tittle = view.findViewById(R.id.galdetegia_tittle);
            description = view.findViewById(R.id.galdeteguia_description);
            correctAnswers = view.findViewById(R.id.galdetegia_correctAnswer);

        }
    }
}
