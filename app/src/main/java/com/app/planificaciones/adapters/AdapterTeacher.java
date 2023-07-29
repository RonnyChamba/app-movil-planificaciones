package com.app.planificaciones.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.planificaciones.R;
import com.app.planificaciones.models.Teacher;

import java.util.List;

public class AdapterTeacher extends RecyclerView.Adapter<AdapterTeacher.ViewHolder> implements

        View.OnClickListener {

    private List<Teacher> teachers;
    private LayoutInflater inflater;
    private View.OnClickListener listener;

    public AdapterTeacher(
            Context context,
            List<Teacher> courses) {

        this.inflater = LayoutInflater.from(context);
        this.teachers = courses;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item_teacher, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Teacher itemCourse = teachers.get(position);

        holder.setData(itemCourse);
    }

    @Override
    public int getItemCount() {
        return teachers.size();
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

        private final TextView textFullName;
        private final TextView textDni;

        private final TextView textPhone;

        private final TextView textStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textFullName = itemView.findViewById(R.id.item_name_Teacher);
            textDni = itemView.findViewById(R.id.item_dni_teacher);
            textPhone = itemView.findViewById(R.id.item_phone_teacher);
            textStatus = itemView.findViewById(R.id.item_status_teacher);
        }

        public void setData(Teacher teacher) {

            textFullName.setText(teacher.getDisplayName() + " " + teacher.getLastName());
            textDni.setText(teacher.getDni());
            textPhone.setText(teacher.getPhoneNumber());
            textStatus.setText(teacher.getStatus() ? "Activo" : "Inactivo");
        }

    }

}
