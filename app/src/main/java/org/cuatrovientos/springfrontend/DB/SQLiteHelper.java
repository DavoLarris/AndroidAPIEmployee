package org.cuatrovientos.springfrontend.DB;

/**
 * Created by David on 14/02/2017.
 * SQLiteHelper
 * In charge to initialize the DB.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class SQLiteHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "practicadb.db";
    public static final int DB_VERSION = 3;
    public static final String CREATESQLEMP = "create table employee "+
            " (_id integer primary key autoincrement, " +
            " name text not null," +
            " birthDate datetime not null," +
            " telephone integer not null default 0," +
            " id_backend integer not null);"; //This field is to know if its in the backend
    public static final String CREATESQLDEL = "create table deleted "+
            " (_id integer primary key autoincrement, " +
            " id_backend integer not null);";
    public static final String CREATESQLUPD = "create table updated "+
            " (_id integer primary key autoincrement, " +
            " name text not null," +
            " birthDate datetime not null," +
            " telephone integer not null default 0," +
            " id_backend integer not null);";

    /**
     * Constructor
     * @param context, actual activity
     */
    SQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * onCreate
     * In case DB doesn't exist.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS employee");
        db.execSQL("DROP TABLE IF EXISTS updated");
        db.execSQL("DROP TABLE IF EXISTS deleted");
        db.execSQL(CREATESQLEMP);
        db.execSQL(CREATESQLDEL);
        db.execSQL(CREATESQLUPD);
        Log.d("LARRIS:DEBUG","Databases created");
    }

    /**
     * onUpgrade
     * In case of different version, upgrade.
     *
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("LARRIS:DEBUG", "Upgrading from " + oldVersion
                + " verion to " + newVersion + ", data will be deleted");


        db.execSQL("DROP TABLE IF EXISTS employee");
        db.execSQL("DROP TABLE IF EXISTS updated");
        db.execSQL("DROP TABLE IF EXISTS deleted");

        // ... y lo volvemos a generar
        onCreate(db);
        Log.d("LARRIS:DEBUG","Database re-created");
    }

}
