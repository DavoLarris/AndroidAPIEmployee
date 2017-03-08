package org.cuatrovientos.springfrontend.syncs;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import org.cuatrovientos.springfrontend.Interface.EmployeeManager;
import org.cuatrovientos.springfrontend.Model.Employee;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by David on 18/02/2017.
 */

public class SyncAdapter  extends AbstractThreadedSyncAdapter {
    private final AccountManager mAccountManager;
    private ContentResolver contentResolver;
    private EmployeeManager employeeManager;
    private String contentUri = "content://org.cuatrovientos.springfrontend.sqlcommand";
    private java.text.SimpleDateFormat iso8601Format = new java.text.SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        employeeManager = new EmployeeManager();
        mAccountManager = AccountManager.get(context);
        contentResolver = context.getContentResolver();
    }

    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mAccountManager = AccountManager.get(context);
        contentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d("LARRIS:DEBUG", "SyncAdapter working for: " + account.name );
        Cursor cursor = null;

        try {

            deleteOnBackend(cursor, provider);

            deleteFromDeleted(provider);

            updateOnBackend(cursor, provider);

            deleteFromUpdated(provider);

            updateFromBackend(cursor, provider);

            fromLocalToBackend(cursor, provider);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateOnBackend(Cursor cursor, ContentProviderClient provider) throws RemoteException {
        //provider.query(uri, columns, where, whereArgs, order);
        cursor = provider.query(Uri.parse(contentUri + "/updated"), null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                Employee employee = new Employee();
                employee.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                employee.setName(cursor.getString(cursor.getColumnIndex("name")));
                employee.setTelephone(cursor.getString(cursor.getColumnIndex("telephone")));
                employee.setBirthDate(cursor.getString(cursor.getColumnIndex("birthDate")));
                employee.setIdBackend(cursor.getInt(cursor.getColumnIndex("id_backend")));

                employeeManager.updateEmployee(employee, employee.getIdBackend());
                cursor.moveToNext();
            }
        }
    }

    private void deleteFromUpdated(ContentProviderClient provider) throws RemoteException {
        int rows = provider.delete(Uri.parse(contentUri + "/delete/tableupdated"), null, null);
    }

    private void deleteOnBackend(Cursor cursor, ContentProviderClient provider) throws RemoteException {
        //provider.query(uri, columns, where, whereArgs, order);
        cursor = provider.query(Uri.parse(contentUri + "/deleted"), null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                employeeManager.deleteEmployee(cursor.getInt(cursor.getColumnIndex("id_backend")));
                cursor.moveToNext();
            }
        }
    }
    private void deleteFromDeleted(ContentProviderClient provider) throws RemoteException {
        int rows = provider.delete(Uri.parse(contentUri + "/delete/tabledeleted"), null, null);
    }

    private void updateFromBackend(Cursor cursor, ContentProviderClient provider) throws RemoteException {
        int lastBackendId = 0;

        // Get the last backend_id on local
        cursor = provider.query(Uri.parse(contentUri + "/employees/lastbackend"), null, null, null, null);

        if (cursor.getCount() > 0) {
            lastBackendId = cursor.getInt(cursor.getColumnIndex("id_backend"));
            Log.d("LARRIS:DEBUG", "Last backend Id:" + lastBackendId);
        }

        // Get employees from server
        List<Employee> employees = employeeManager.getLastEmployees(lastBackendId); //Si descarga todos y no borra los que esta, hay que hacer getLastFromId(id)

        for (Employee employee : employees) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", employee.getName());
            contentValues.put("telephone", employee.getTelephone());

            String dateeee = iso8601Format.format(employee.getBirthDate());

            contentValues.put("birthDate", dateeee);
            contentValues.put("id_backend", employee.getId());

            // We finally make the request to the content provider
            Uri resultUri = provider.insert(Uri.parse(contentUri), contentValues);
            Log.d("LARRIS:DEBUG", "Inserted: " + employee.getName());
        }
    }

    private void fromLocalToBackend(Cursor cursor, ContentProviderClient provider) throws RemoteException {
        int lastLocalId = 0;
        ContentValues contentValues = null;

        // get all local record with id_backend = 0 and send them to backend
        cursor = provider.query(Uri.parse(contentUri + "/employees/lastlocal"), null, null, null, null);
        if (cursor.getCount() > 0) {
            lastLocalId = cursor.getInt(0);
            Log.d("LARRIS:DEBUG", "Last local Id: " + cursor.getString(0));

            cursor.moveToFirst();

            while (cursor.isAfterLast() == false) {
                Employee employee = new Employee();
                employee.setName(cursor.getString(cursor.getColumnIndex("name")));
                employee.setTelephone(cursor.getString(cursor.getColumnIndex("telephone")));
                employee.setBirthDate(cursor.getString(cursor.getColumnIndex("birthDate")));

                int id = employeeManager.createEmployee(employee);
                contentValues = new ContentValues();
                contentValues.put("_id", cursor.getInt(cursor.getColumnIndex("_id")));
                contentValues.put("name", employee.getName());
                contentValues.put("telephone", employee.getTelephone());

                String dateeee = iso8601Format.format(employee.getBirthDate());
                contentValues.put("birthDate", dateeee);
                contentValues.put("id_backend", id);

                cursor.moveToNext();
            }

            // To mark local records as sent.
            int total = provider.update( Uri.parse(contentUri), contentValues, null, null);
            Log.d("LARRIS:DEBUG", "Marked as sent " + total);
        }
    }
}
