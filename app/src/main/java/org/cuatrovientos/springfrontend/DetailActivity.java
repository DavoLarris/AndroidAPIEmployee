package org.cuatrovientos.springfrontend;

import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.cuatrovientos.springfrontend.Model.Employee;

public class DetailActivity extends AppCompatActivity {

    private Employee employee;
    private TextView txtNombre, txtTel, txtDate;
    private ProgressBar progress;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        createActionBar();

        txtNombre = (TextView)findViewById(R.id.idNombre);
        txtTel = (TextView)findViewById(R.id.idTel);
        txtDate = (TextView)findViewById(R.id.idDate);

        employee = (Employee) getIntent().getExtras().getSerializable("employee"); //getIntent().getSerializableExtra("Persona")


        if (employee != null) {
            txtNombre.setText(employee.getName());
            txtTel.setText(employee.getTelephone());
            txtDate.setText(employee.getBirthDate().toString());
        }


    }

    private void createActionBar(){
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
