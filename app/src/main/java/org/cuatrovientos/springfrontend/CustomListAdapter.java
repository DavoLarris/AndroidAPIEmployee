package org.cuatrovientos.springfrontend;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by David on 25/02/2017.
 */

public class CustomListAdapter extends CursorAdapter{
    private Activity activity;

    public CustomListAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        View v = view;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_row, null);
        }

        //TextView textViewId = (TextView) v.findViewById(R.id.idId);
        //textViewId.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("_id"))));

        TextView textViewName = (TextView) v.findViewById(R.id.name);
        textViewName.setText(cursor.getString(cursor.getColumnIndex("name")));

        //TextView textViewBD = (TextView) v.findViewById(R.id.birthDate);
        //textViewBD.setText(cursor.getString(cursor.getColumnIndex("birthDate")));

        TextView textViewTel = (TextView) v.findViewById(R.id.telephone);
        textViewTel.setText(cursor.getString(cursor.getColumnIndex("telephone")));
    }
}
