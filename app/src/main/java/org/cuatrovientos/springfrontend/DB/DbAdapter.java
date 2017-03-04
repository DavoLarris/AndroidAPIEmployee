package org.cuatrovientos.springfrontend.DB;

/**
 * Created by David on 14/02/2017.
 * DBAdapter
 * Intermediary class between class and BD. Here we have the CRUD operations
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.cuatrovientos.springfrontend.Model.Employee;

import java.text.SimpleDateFormat;

public class DbAdapter {

    private SQLiteDatabase db;
    private SQLiteHelper dbHelper;
    private final Context context;

    /**
     * DbAdapter
     * @param context, actual activity
     */
    public DbAdapter(Context context) {
        this.context = context;
    }


    /**
     * open
     * Using SQLiteHelper to create de DB in case it doesn't exist
     * @return SQLiteDatabase object to manage the BD
     * @throws SQLException
     */
    public SQLiteDatabase open() throws SQLException {
        dbHelper = new SQLiteHelper(context);

        // Opens db in write mode (read also permitted).
        db = dbHelper.getWritableDatabase();

        Log.d("LARRIS:DEBUG","DB obtained: " + db.toString());

        return db;
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * insertEmployee
     *
     * @param employee
     * @return number of registry inserted 0, -1 means error
     */
    public long insertEmployee(Employee employee) {
        ContentValues registro = new ContentValues();
        Log.d("LARRIS:DEBUG","DB insert: " + employee.getName());

        // Agrega los datos.
        registro.put("name", employee.getName());
        registro.put("telephone", employee.getTelephone());
        registro.put("birthDate", employee.getBirthDate());
        registro.put("id_backend", employee.getIdBackend());

        // Inserta el registro y devuelve el resultado.
        return db.insert("employee", null, registro);
    }

    /**
     * deleteEmployee and add to the deleted employees, to send to the server
     *
     * @param idEmp
     * @return number of affected rows
     */
    public int deleteEmployee(long idEmp) {
        newDeleted(idEmp);
        return db.delete("employee",  "_id = "
                + idEmp, null);
    }

    /**
     * Delete all the deleted ids
     * @return
     */
    public int deleteDeleted() {
        return db.delete("deleted", null, null);
    }

    /**
     * insert new row in deleted to know which one was deleted
     * @param id
     */
    private void newDeleted(long id) {
        Cursor cursor = db.query(true, "employee", null, "_id=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        int id_backend = cursor.getInt(cursor.getColumnIndex("id_backend"));

        ContentValues row = new ContentValues();
        row.put("id_backend", id_backend);

        db.insert("deleted", null, row);
    }

    /**
     * @return all id_backend from deleted
     */
    public Cursor getDeleted() {
        return db.query("deleted", new String[]{"id_backend"}, null, null, null, null, null);
    }

    /**
     * @return all the updated rows inserted in updated
     */
    public Cursor getUpdated() {
        return db.query("updated", new String[]{"_id", "name", "birthDate", "telephone", "id_backend"}, null, null, null, null, null);
    }

    /**
     * getAll
     * @return Cursor with rows
     */
    public Cursor getAll() {
        Log.d("LARRIS:DEBUG", "I'm in getAll()");
        return db.query("employee", new String[] {"_id","name","birthDate","telephone", "id_backend"}, null, null, null, null, null);
    }

    /**
     * getEmployee
     *
     * @param idEmployee
     * @return Cursor with the row
     * @throws SQLException
     */
    public Cursor getEmployee(long idEmployee) throws SQLException {
        Cursor registry = db.query(true, "employee", new String[] { "_id","name","birthDate","telephone", "id_backend"},
                "_id =" + idEmployee, null, null, null, null, null);

        // If found, points to the first
        if (registry != null) {
            registry.moveToFirst();
        }
        return registry;
    }

    /**
     * getLastLocal
     *
     * @return last id received by server
     * @throws SQLException
     */
    public Cursor getLastLocal() throws SQLException {
        Cursor registry = db.query(true, "employee", new String[] { "_id","name","birthDate","telephone", "id_backend"},
                "id_backend = 0"  , null, null, null, null, null);

        // If found, points to the first
        if (registry != null) {
            registry.moveToFirst();
        }
        return registry;
    }

    /**
     * @return the last id_backend
     * @throws SQLException
     */
    public Cursor getLastBackend() throws SQLException {
        Cursor row = db.query(true, "employee", new String[]{"_id","name","birthDate","telephone", "id_backend"},
                null, null, null, null, "id_backend DESC", " 1"); // "id_backend DESC" => orderBy   " 1" => maxRows

        if (row != null) {
            row.moveToFirst();
        }

        return row;
    }

    /**
     * updateRegistry
     *
     * @param  idRegistry
     * @param  employee
     * @return int amount affected
     */
    public int updateRegistry(long idRegistry, Employee employee) {
        newUpdated(idRegistry, employee);
        ContentValues registry = new ContentValues();

        // Agrega los datos.
        registry.put("name", employee.getName());
        registry.put("telephone", employee.getTelephone());
        registry.put("birthDate", employee.getBirthDate());
        registry.put("id_backend", employee.getIdBackend());

        return db.update("employee", registry,
                "_id=" + idRegistry, null);
    }

    private long newUpdated(long id, Employee employee) {
        ContentValues row = new ContentValues();
        row.put("_id", id);
        row.put("name", employee.getName());
        row.put("telephone", employee.getTelephone());
        row.put("birthDate", employee.getBirthDate());
        row.put("id_backend", employee.getIdBackend());


        return db.insert("updated", null, row);
    }

    /**
     * Delete the updated table
     * @return
     */
    public int deleteUpdated() {
        return db.delete("updated", null, null);
    }
}
