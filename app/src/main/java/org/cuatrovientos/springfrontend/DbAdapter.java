package org.cuatrovientos.springfrontend;

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

import java.text.SimpleDateFormat;
import java.util.Date;

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
     * @param name
     * @return number of registry inserted 0, -1 means error
     */
    public long insertEmployee(String name, Integer phone, Date birthDate, int idBackend) {
        ContentValues registro = new ContentValues();
        Log.d("LARRIS:DEBUG","DB insert: " + name);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Agrega los datos.
        registro.put("name", name);
        registro.put("telephone", phone);
        registro.put("birthDate", dateFormat.format(birthDate));
        registro.put("id_backend", idBackend);

        // Inserta el registro y devuelve el resultado.
        return db.insert("employee", null, registro);
    }

    /**
     * deleteEmployee
     *
     * @param idEmp
     * @return number of affected rows
     */
    public int deleteEmployee(long idEmp) {
        return db.delete("employee",  "id = "
                + idEmp, null);
    }

    /**
     * getAll
     *
     * @return Cursor with rows
     */
    public Cursor getAll() {
        return db.query("employee", new String[] {"id","name","birthDate","telephone", "id_backend"}, null, null, null, null, null);
    }

    /**
     * getEmployee
     *
     * @param idEmployee
     * @return Cursor with the row
     * @throws SQLException
     */
    public Cursor getEmployee(long idEmployee) throws SQLException {
        Cursor registry = db.query(true, "employee", new String[] { "id","name","birthDate","telephone", "id_backend"},
                "id =" + idEmployee, null, null, null, null, null);

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
        Cursor registry = db.query(true, "employee", new String[] { "id","name","birthDate","telephone", "id_backend"},
                "id_backend = 0"  , null, null, null, null, null);

        // If found, points to the first
        if (registry != null) {
            registry.moveToFirst();
        }
        return registry;
    }

    public int setSent() throws SQLException {
        ContentValues registry = new ContentValues();

        registry.put("id_backend", -1);

        return db.update("employee", registry, "id_backend=0", null);
    }
    /**
     * getLastBackend
     *
     * @return last id in server
     * @throws SQLException
     */
    public Cursor getLastBackend() throws SQLException {
        Cursor registry = db.query(true, "employee", new String[] { "id","name","birthDate","telephone", "id_backend"},
                null, null, null, null, "id_backend DESC", " 1");

        // If found, points to the first
        if (registry != null) {
            System.out.println("Register exists!");
            registry.moveToFirst();
        }
        return registry;
    }
    /**
     * updateRegistry
     * Hace un UPDATE de los valores del registro cuyo id es idRegistro.
     *
     * @param  idRegistry id del registro que se quiere modificar.
     * @param  name
     * @return int amount affected
     */
    public int updateRegistry(long idRegistry, String name) {
        ContentValues registry = new ContentValues();

        registry.put("name", name);

        return db.update("employee", registry,
                "id=" + idRegistry, null);
    }

}
