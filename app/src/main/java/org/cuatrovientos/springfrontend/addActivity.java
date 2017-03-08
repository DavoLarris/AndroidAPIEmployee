package org.cuatrovientos.springfrontend;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class addActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText name, telephone, date;
    private android.app.DatePickerDialog DatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private Button button;
    private String myUri = "content://org.cuatrovientos.springfrontend.sqlcommand";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        createActionBar();

        findId();
        setListeners();
    }

    private void createActionBar(){
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void findId(){
        name = (EditText) findViewById(R.id.name);
        telephone = (EditText) findViewById(R.id.telephone);
        date = (EditText) findViewById(R.id.date);
        date.setInputType(InputType.TYPE_NULL);
        name.requestFocus();
        button = (Button) findViewById(R.id.button);
    }

    private void setListeners(){
        date.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                date.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    @Override
    public void onClick(View v) {
        if(v == date) {
            DatePickerDialog.show();
        }
    }

    public void createEmployee(View view){

        if (name.getText() != null || date.getText() != null || telephone.getText() != null){
            ContentValues contentValues = new ContentValues();
            Uri uri = Uri.parse(myUri);

            Date datee = null;
            try {
                datee = dateFormatter.parse(date.getText().toString()); //Mirar esto que valor toma pa poner directamente getText sino
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String dateeee = dateFormatter.format(datee);

            contentValues.put("name", String.valueOf(name.getText()));
            contentValues.put("birthDate", dateeee);
            contentValues.put("telephone", Integer.valueOf(String.valueOf(telephone.getText())));


            contentValues.put("id_backend", 0);

            Uri resultUri = getContentResolver().insert(
                    uri,   // The content URI
                    contentValues
            );

            Snackbar.make(view, "Inserted!", Snackbar.LENGTH_LONG).show();
            finish();
        } else {
            Snackbar.make(view, "Fill fields, please", Snackbar.LENGTH_LONG).show();
        }

    }
}
