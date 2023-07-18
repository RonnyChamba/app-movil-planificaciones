package com.app.planificaciones.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.planificaciones.R;
import com.app.planificaciones.models.DetailsPlanification;

import java.util.List;
import java.util.Map;

public class AdapterReviewDetailPlanning extends RecyclerView.Adapter<AdapterReviewDetailPlanning.ViewHolder> implements

        View.OnClickListener {

    private List<DetailsPlanification> courses;
    private LayoutInflater inflater;
    private View.OnClickListener listener;

    public AdapterReviewDetailPlanning(
            Context context,
            List<DetailsPlanification> courses) {

        this.inflater = LayoutInflater.from(context);
        this.courses = courses;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item_details_planning, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DetailsPlanification itemCourse = courses.get(position);

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

        private final TextView textFullNameTeacher;
        private final TextView textDateUpload;
        private final TextView txtAmountReview;

//        private final Button btnReview;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textFullNameTeacher = itemView.findViewById(R.id.fullNameTeacher);
            textDateUpload = itemView.findViewById(R.id.dateUpload);
            txtAmountReview = itemView.findViewById(R.id.amountRevisitions);
//            btnReview = itemView.findViewById(R.id.btnReview);


            // here the listener of the button that is in the list item is configured
//            btnReview.setOnClickListener(v -> {
//                if (buttonClickListener != null) {
//                    int position = getAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION) {
//                        DetailsPlanification planification = courses.get(position);
//                        buttonClickListener.onButtonClicked(planification);
//                    }
//                }
//            });

        }

        public void setData(DetailsPlanification data) {

            Log.i("Teacher ", data.getTeacher() == null ? "null" : data.getTeacher().toString());

            if (data.getTeacher() != null) {
                Map<String, Object> teacher = data.getTeacher();
                String fullName = (String) teacher.get("fullName");
                textFullNameTeacher.setText(fullName);
            } else {
                textFullNameTeacher.setText("No asignado");
            }

//            if (data.getItems() != null) {
//                txtAmountReview.setText(String.valueOf(data.getItems().size()));
//            } else {
//                txtAmountReview.setText("0");
//            }

            txtAmountReview.setText(data.isStatus() ? "SI" : "NO");
            txtAmountReview.setTextColor(data.isStatus() ? Color.GREEN : Color.RED);
            textDateUpload.setText(data.getDateCreated());

//            btnReview.setText(data.isStatus() ? "Si" : "No");
//            btnReview.setBackgroundColor(data.isStatus() ? Color.GREEN : Color.RED);
        }


    }

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.buttonClickListener = listener;
    }

    public interface OnButtonClickListener {
        void onButtonClicked(DetailsPlanification planification);
    }

    private OnButtonClickListener buttonClickListener;


}
