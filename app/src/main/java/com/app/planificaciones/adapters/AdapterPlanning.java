package com.app.planificaciones.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.planificaciones.R;
import com.app.planificaciones.models.Course;
import com.app.planificaciones.models.Planification;
import com.app.planificaciones.util.ConstantApp;

import java.util.List;
import java.util.Map;

public class AdapterPlanning extends RecyclerView.Adapter<AdapterPlanning.ViewHolder> implements

        View.OnClickListener {

    private List<Planification> courses;
    private LayoutInflater inflater;
    private View.OnClickListener listener;

    private Context context;


    public AdapterPlanning(
            Context context,
            List<Planification> courses) {

        this.inflater = LayoutInflater.from(context);
        this.courses = courses;
        this.context = context;

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

            // ubicar un icono al lado del titulo

            // Obtener el icono que deseas asignar al botón

            //Drawable icon;
            if (ConstantApp.isAdmin) {
                btnReview.setText("Revisar");
                //  icon = context.getResources().getDrawable(R.drawable.baseline_rate_review_24);
            } else {
                btnReview.setText("Subir");

                List<Map<String, Object>> detailsPlanification = course.getDetailsPlanification();

                if (detailsPlanification.size() > 0) {

                    for (Map<String, Object> detail : detailsPlanification) {

                        String uidTeacher = detail.get("teacher_uid") == null ? "" : detail.get("teacher_uid").toString();

                        if (ConstantApp.teacher.getUid().equals(uidTeacher)) {

                            boolean status = detail.get("status") != null && (boolean) detail.get("status");

                            if (status) {
                                btnReview.setText("Aprobado");
                                btnReview.setEnabled(false);
                                break;
                            }
                        }


                    }


                }
                //icon = context.getResources().getDrawable(R.drawable.baseline_file_upload_24);
            }

            // Establecer el icono en el botón
            //btnReview.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);


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
