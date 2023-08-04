package com.app.planificaciones;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.app.planificaciones.models.Teacher;
import com.app.planificaciones.util.ConstantApp;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.app.planificaciones.databinding.ActivityHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    // enlance de datos generado automaticamente por el view binding
    // tiene el mismo nombre que el layout xml pero en camel case y con la palabra binding al final
    private ActivityHomeBinding binding;

    private static final String COLLECTION_NAME = "teachers";

    private FirebaseUser user;

    private boolean isAdmin = false;

    private FirebaseFirestore db = null;

    private NavigationView navigationView;

    private NavController navController;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inflayar el layout xml
        binding = ActivityHomeBinding.inflate(getLayoutInflater());

        // obtener el root del layout xml
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarHome.toolbar);

        db = FirebaseFirestore.getInstance();

        context = this;
        verifySignIn();
//        binding.appBarHome.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        DrawerLayout drawer = binding.drawerLayout;

        navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_profile)
                .setOpenableLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        setValueDefault();
    }


    /**
     * Metodo para verificar si el usuario esta logueado
     */
    private void verifySignIn() {

        user = FirebaseAuth.getInstance().getCurrentUser();

        // verificar si el usuario esta logueado
        if (user != null) {

            String uidUser = user.getUid();

//            Toast.makeText(this, "email:" + uidUser, Toast.LENGTH_SHORT).show();

            db.collection(COLLECTION_NAME).document(uidUser).get().addOnSuccessListener(
                    documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Teacher teacher = documentSnapshot.toObject(Teacher.class);
                            if (teacher != null) {
                                isAdmin = teacher.getRol() != null && teacher.getRol().equals("ADMIN");

                                ConstantApp.teacher = teacher;

                                if (!ConstantApp.teacher.getStatus()) {

                                    Toast.makeText(context, "El usuario no esta activo", Toast.LENGTH_SHORT).show();
                                    FirebaseAuth.getInstance().signOut();
                                    Intent intent = new Intent(context, MainActivity.class);
                                    startActivity(intent);


                                }


                                //Toast.makeText(this, "couse:" + teacher.getCourses().size(), Toast.LENGTH_SHORT).show();

                                ConstantApp.isAdmin = isAdmin;
                                hideOrShowMenuOptions(navigationView.getMenu());
                            } else {
                                Toast.makeText(this, "No se pudo obtener el rol del usuario", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
            );
        } else {

            // si no esta logueado, redirigir al login
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();

        }
    }

    /**
     * Metodo para mostrar  u ocultar items en el navigation drawer
     *
     * @param menu
     */
    private void hideOrShowMenuOptions(Menu menu) {
        MenuItem galleryItem = menu.findItem(R.id.nav_gallery);


        if (!isAdmin) {
            galleryItem.setVisible(false);

        } else {
            galleryItem.setVisible(true);
        }
    }

    private void setValueDefault() {

        // Acceder al layout nav_header_home
        View navHeader = binding.navView.getHeaderView(0);

        // Acceder a los widgets dentro de nav_header_home
        TextView fullNameUser = navHeader.findViewById(R.id.textViewFullName);
        TextView emailUser = navHeader.findViewById(R.id.textViewEmail);

        fullNameUser.setText(user.getDisplayName());
        emailUser.setText(user.getEmail());

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.clear();

        if (user != null) {
            // inflar el menu
            getMenuInflater().inflate(R.menu.home, menu);


            MenuItem galleryItem = menu.findItem(R.id.action_new_course);

            // si no es admin, ocultar el item de nuevo curso
            if (!isAdmin) {
                galleryItem.setVisible(false);

            } else {
                galleryItem.setVisible(true);
            }
        }


        return super.onPrepareOptionsMenu(menu);

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
//        return true;
//    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Manejar el clikc del menu del toolbar, si no se valida, cuando se hace
     * click en el menu de amburgueza del dwaer tambien se dispara el evento
     *
     * @param item The menu item that was selected.
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

//        Toast.makeText(getApplicationContext(), "saldos", Toast.LENGTH_SHORT).show();

        if (item.getItemId() == R.id.action_new_course) {
            navController.navigate(R.id.nav_form_course);
            return true;

        }
        if (item.getItemId() == R.id.action_logout) {

            // cerrar sesion

            FirebaseAuth.getInstance().signOut();
            // redirigir al login
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();


//            Toast.makeText(getApplicationContext(), "LogOut", Toast.LENGTH_SHORT).show();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*

    Manejar el click del menu lateral del drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_item_1:
                // Acción cuando se selecciona el item 1 del drawer
                return true;
            case R.id.nav_item_2:
                // Acción cuando se selecciona el item 2 del drawer
                return true;
            default:
                return false;
        }
    }*/

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}