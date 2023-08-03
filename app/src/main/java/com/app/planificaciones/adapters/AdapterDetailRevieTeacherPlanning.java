package com.app.planificaciones.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.planificaciones.R;
import com.app.planificaciones.models.DetailsPlanification;
import com.app.planificaciones.models.ModelItemDetail;

import java.util.List;
import java.util.Map;

public class AdapterDetailRevieTeacherPlanning extends RecyclerView.Adapter<AdapterDetailRevieTeacherPlanning.ViewHolder> implements

        View.OnClickListener {

    private List<ModelItemDetail> courses;
    private LayoutInflater inflater;
    private View.OnClickListener listener;

    public AdapterDetailRevieTeacherPlanning(
            Context context,
            List<ModelItemDetail> courses) {

        this.inflater = LayoutInflater.from(context);
        this.courses = courses;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item_details_review, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelItemDetail itemCourse = courses.get(position);

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

        private final TextView textDateUpload;
        private final TextView txtStatusDetailPlani;
        private final TextView txtObservationDetailPlani;

        //        private final Button btnReview;
        private final ImageButton btnReview;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textDateUpload = itemView.findViewById(R.id.txtDateUploadDetails);
            txtStatusDetailPlani = itemView.findViewById(R.id.txtStatusReviewDetail);
            txtObservationDetailPlani = itemView.findViewById(R.id.txtObserrvationDetails);
            btnReview = itemView.findViewById(R.id.btnDowloadReviewDetail);


            // here the listener of the button that is in the list item is configured
            btnReview.setOnClickListener(v -> {
                if (buttonClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        ModelItemDetail planification = courses.get(position);
                        buttonClickListener.onButtonClicked(planification);
                    }
                }
            });
        }

        public void setData(ModelItemDetail data) {


            txtStatusDetailPlani.setText(data.isStatus() ? "Si" : "No");
            //txtStatusDetailPlani.setTextColor(data.isStatus() ? Color.GREEN : Color.RED);
            txtObservationDetailPlani.setText(data.getObservation());
            textDateUpload.setText(data.getDateUpload());
        }


    }

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.buttonClickListener = listener;
    }

    public interface OnButtonClickListener {
        void onButtonClicked(ModelItemDetail planification);
    }

    private OnButtonClickListener buttonClickListener;


}
