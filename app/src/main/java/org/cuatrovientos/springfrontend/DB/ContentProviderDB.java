package org.cuatrovientos.springfrontend.DB;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import org.cuatrovientos.springfrontend.Model.Employee;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Date;

/**
 * Created by David on 18/02/2017.
 */

public class ContentProviderDB extends ContentProvider {
    private UriMatcher uriMatcher;

    private MatrixCursor mCursor = null;
    private DbAdapter dbAdapter;

    @Override
    public boolean onCreate() {
        dbAdapter = new DbAdapter(getContext());
        dbAdapter.open();
        initUris();

        return true;
    }

    /**
     * init content provider Uris
     * we set some kind of uri patterns to route them to different queries
     */
    private void initUris() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Get all : GET
        // This will match: content://org.cuatrovientos.springfrontend.sqlcommand/api
        uriMatcher.addURI("org.cuatrovientos.springfrontend.sqlcommand", "api/", 1);

        // Get one : GET
        // This will match: content://org.cuatrovientos.springfrontend.sqlcommand/api/{id}
        uriMatcher.addURI("org.cuatrovientos.springfrontend.sqlcommand", "api/id/", 2);

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d("LARRIS:DEBUG", "ContentProvider > query \" + uri");
        switch (uriMatcher.match(uri)) {
            case 1:
                Log.d("LARRIS:DEBUG", "query to 1.");
                return dbAdapter.getAll();
            case 2:
                Log.d("LARRIS:DEBUG", "query to 2.");
                dbAdapter.getEmployee(Long.parseLong(uri.getLastPathSegment()));
            default:
                break;
        }
        return mCursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d("LARRIS:DEBUG", "ContentProvider > insert " + uri);
        Format formatter;

        Employee employee = new Employee();
        employee.setId(values.getAsInteger("id"));
        employee.setName(values.getAsString("name"));
        employee.setTelephone(values.getAsString("telephone"));
        employee.setBirthDate(values.getAsString("birthDate"));
        employee.setIdBackend(values.getAsInteger("id_backend"));

        Long id = dbAdapter.insertEmployee(employee);
        Uri resultUri = ContentUris.withAppendedId(Uri.parse("content://org.cuatrovientos.springfrontend.sqlcommand/"), id);
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d("LARRIS:DEBUG", "ContentProvider > " + uri);
        return dbAdapter.deleteEmployee(Long.parseLong(uri.getLastPathSegment()));
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d("LARRIS:DEBUG", "ContentProvider > " + uri);

        Employee employee = new Employee();
        employee.setId(values.getAsInteger("id"));
        employee.setName(values.getAsString("name"));
        employee.setTelephone(values.getAsString("telephone"));
        employee.setBirthDate(values.getAsString("birthDate"));
        employee.setIdBackend(values.getAsInteger("id_backend"));

        return dbAdapter.updateRegistry(Long.parseLong(uri.getLastPathSegment()), employee);
         //dbAdapter.setSent();
    }
}
