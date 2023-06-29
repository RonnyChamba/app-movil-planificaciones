package com.app.planificaciones.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.planificaciones.R;
import com.app.planificaciones.models.Course;
import com.app.planificaciones.models.Planification;

import java.util.List;

public class AdapterPlanning extends RecyclerView.Adapter<AdapterPlanning.ViewHolder> implements

        View.OnClickListener {

    private List<Planification> courses;
    private LayoutInflater inflater;
    private View.OnClickListener listener;

    public AdapterPlanning(
            Context context,
            List<Planification> courses) {

        this.inflater = LayoutInflater.from(context);
        this.courses = courses;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item_planning, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Planification itemCourse = courses.get(position);

        holder.setData(itemCourse);

    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public void setOnClickListener(View.OnClickListener lister
    ) {

        this.listener = lister;
    }

    @Override
    public void onClick(View v) {

        if (listener != null) {
            listener.onClick(v);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        // aqui se referencia los widgets

        private final TextView textTitle;
        private final TextView textDescription;

        private final Button btnReview;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.item_title);
            textDescription = itemView.findViewById(R.id.item_description);
            btnReview = itemView.findViewById(R.id.btnReview);


            // here the listener of the button that is in the list item is configured
            btnReview.setOnClickListener(v -> {
                if (buttonClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Planification planification = courses.get(position);
                        buttonClickListener.onButtonClicked(planification);
                    }
                }
            });

        }

        public void setData(Planification course) {

            textTitle.setText(course.getTitle());
            textDescription.setText(course.getDateCreated());
        }


    }

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.buttonClickListener = listener;
    }

    public interface OnButtonClickListener {
        void onButtonClicked(Planification planification);
    }

    private OnButtonClickListener buttonClickListener;


}
