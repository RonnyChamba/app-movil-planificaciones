package com.app.planificaciones.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.planificaciones.R;
import com.app.planificaciones.models.Course;

import java.util.List;
import java.util.Objects;

public class AdapterCourse extends RecyclerView.Adapter<AdapterCourse.ViewHolder> implements

        View.OnClickListener {

    private List<Course> courses;
    private LayoutInflater inflater;
    private View.OnClickListener listener;

    public AdapterCourse(
            Context context,
            List<Course> courses) {

        this.inflater = LayoutInflater.from(context);
        this.courses = courses;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item_course, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course itemCourse = courses.get(position);

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


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.item_title);
            textDescription = itemView.findViewById(R.id.item_description);
        }

        public void setData(Course course) {
            textTitle.setText(String.format("%s - %s", course.getName(), course.getParallel()));

            if (course.getTutor() != null) {

                Object fullName = course.getTutor().get("fullName");
                if (fullName != null && !fullName.toString().isEmpty()) {
                    textDescription.setText(fullName.toString());
                } else {
                    textDescription.setText("No asignado");
                }
            } else {
                Log.i("TutorNoExisteParaCurso", course.getTutor() + "");
                textDescription.setText("No asignado");
            }
        }


    }

}
