package org.cuatrovientos.springfrontend.DB;

import android.content.ContentProvider;
import android.content.ContentResolver;
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
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Locale;

/**
 * Created by David on 18/02/2017.
 */

public class ContentProviderDB extends ContentProvider {
    private UriMatcher uriMatcher;
    private DbAdapter dbAdapter;
    private MatrixCursor mCursor;
    private java.text.SimpleDateFormat iso8601Format = new java.text.SimpleDateFormat("yyyy-MM-dd");

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
        // This will match: content://org.cuatrovientos.springfrontend.sqlcommand/employees
        uriMatcher.addURI("org.cuatrovientos.springfrontend.sqlcommand", "employees", 1);

        // Get one : GET
        // This will match: content://org.cuatrovientos.springfrontend.sqlcommand/employees/{id}
        uriMatcher.addURI("org.cuatrovientos.springfrontend.sqlcommand", "employees/id/", 2);

        // the last one from the backend
        // This will match: content://org.cuatrovientos.springfrontend.sqlcommand/employees/last/backend
        uriMatcher.addURI("org.cuatrovientos.springfrontend.sqlcommand", "employees/lastbackend", 3);

        // This will match: content://org.cuatrovientos.springfrontend.sqlcommand/employees/last/local
        uriMatcher.addURI("org.cuatrovientos.springfrontend.sqlcommand", "employees/lastlocal", 4);

        // This will match: content://org.cuatrovientos.springfrontend.sqlcommand/deleted
        uriMatcher.addURI("org.cuatrovientos.springfrontend.sqlcommand", "deleted", 5);

        // This will match: content://org.cuatrovientos.springfrontend.sqlcommand/updated
        uriMatcher.addURI("org.cuatrovientos.springfrontend.sqlcommand", "updated", 6);

        // This will match: content://org.cuatrovientos.springfrontend.sqlcommand/delete/tabledeleted
        uriMatcher.addURI("org.cuatrovientos.springfrontend.sqlcommand", "delete/tabledeleted", 7);

        // This will match: content://org.cuatrovientos.springfrontend.sqlcommand/delete/employees
        uriMatcher.addURI("org.cuatrovientos.springfrontend.sqlcommand", "delete/employees", 8);

        // This will match: content://org.cuatrovientos.springfrontend.sqlcommand/delete/tableupdated
        uriMatcher.addURI("org.cuatrovientos.springfrontend.sqlcommand", "delete/tableupdated", 9);


    }

    @Override           //uri           columns             where            paramsToWhere          order
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d("LARRIS:DEBUG", "ContentProvider > query " + uri);
        ContentResolver cr = getContext().getContentResolver();
        Cursor myCursor;

        switch (uriMatcher.match(uri)) {
            case 1:
                Log.d("LARRIS:DEBUG", "query to 1.");
                myCursor = dbAdapter.getAll();
                myCursor.setNotificationUri(cr, uri);
                return myCursor;
            case 2:
                Log.d("LARRIS:DEBUG", "query to 2.");
                return dbAdapter.getEmployee(Long.parseLong(uri.getLastPathSegment()));
            case 3:
                Log.d("LARRIS:DEBUG", "query to 3. " + uri.getLastPathSegment());
                myCursor = dbAdapter.getLastBackend();
                myCursor.setNotificationUri(cr, uri);
                return myCursor;
            case 4:
                Log.d("LARRIS:DEBUG", "query to 4. " + uri.getLastPathSegment());
                myCursor = dbAdapter.getLastLocal();
                myCursor.setNotificationUri(cr, uri);
                return myCursor;
            case 5:
                Log.d("LARRIS:DEBUG", "query to 5. " + uri.getLastPathSegment());
                myCursor = dbAdapter.getDeleted();
                myCursor.setNotificationUri(cr, uri);
                return myCursor;
            case 6:
                Log.d("LARRIS:DEBUG", "query to 6. " + uri.getLastPathSegment());
                myCursor = dbAdapter.getUpdated();
                myCursor.setNotificationUri(cr, uri);
                return myCursor;
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
        Date date = null;

        Employee employee = new Employee();
        employee.setId(values.getAsInteger("id"));
        employee.setName(values.getAsString("name"));
        employee.setTelephone(values.getAsString("telephone"));
        try {
            date = iso8601Format.parse(values.getAsString("birthDate"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        employee.setBirthDate(date);
        employee.setIdBackend(values.getAsInteger("id_backend"));

        Long id = dbAdapter.insertEmployee(employee);
        Uri resultUri = Uri.parse("content://org.cuatrovientos.springfrontend.sqlcommand/2");
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d("LARRIS:DEBUG", "ContentProvider > " + uri);
        switch (uriMatcher.match(uri)) {
            case 7:
                Log.d("LARRIS:DEBUG", "query to 7.");
                return dbAdapter.deleteDeleted();
            case 8:
                Log.d("LARRIS:DEBUG", "query to 8.");
                return dbAdapter.deleteEmployee(Long.parseLong(selectionArgs[0]));
            case 9:
                Log.d("LARRIS:DEBUG", "query to 9. " + uri.getLastPathSegment());
                return dbAdapter.deleteUpdated();
            default:
                break;
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d("LARRIS:DEBUG", "ContentProvider > " + uri);
        Date date = null;

        Employee employee = new Employee();
        employee.setId(values.getAsInteger("id"));
        employee.setName(values.getAsString("name"));
        employee.setTelephone(values.getAsString("telephone"));

        try {
            date = iso8601Format.parse(values.getAsString("birthDate"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        employee.setBirthDate(date);
        employee.setIdBackend(values.getAsInteger("id_backend"));

        return dbAdapter.updateRegistry(Long.parseLong(uri.getLastPathSegment()), employee);
    }
}
