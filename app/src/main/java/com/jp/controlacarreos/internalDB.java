package com.jp.controlacarreos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class internalDB extends SQLiteOpenHelper {

    private static final String dbName = "WebApps";
    private static final int dbVersion = 1;
    private static final String dbTablaName = "wapp_acarreos";
    private static final String dbTablaQuery = "CREATE TABLE wapp_Acarreos(id INTEGER PRIMARY KEY AUTOINCREMENT,codempresa TEXT, codsucursal TEXT, codproyecto TEXT, transOwner TEXT, transPlate TEXT, transCapacity TEXT, checkpoint TEXT, sync INTEGER)";

    public internalDB(Context context) {
        super(context, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(dbTablaQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + dbTablaName);
        sqLiteDatabase.execSQL(dbTablaQuery);
    }

    public void agregarCheckpoint(String cEmpresa, String cSucursal, String cProyecto, String tOwner, String tPlate, String tCapacity){
        SQLiteDatabase db = getWritableDatabase();
        if (db != null){
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String ahorita = sdf.format(new Date());
                String query = "INSERT INTO wapp_acarreos VALUES (NULL,'" + cEmpresa + "','" + cSucursal + "','" + cProyecto + "','" + tOwner + "','" + tPlate + "','" + tCapacity + "','" + ahorita + "',0)";
                Log.d("------------------Query: ", query);
                db.execSQL(query);
            }catch (Exception exception){
                exception.printStackTrace();
            }finally {
                db.close();
            }
        }
    }
}