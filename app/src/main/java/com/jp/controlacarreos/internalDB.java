package com.jp.controlacarreos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;

public class internalDB extends SQLiteOpenHelper {

    String query;

    private static final String dbName = "negocios_g";
    private static final int dbVersion = 2;
    private static final String dbTablaName = "wapp_acarreos";
    private static final String dbTablaQuery = "CREATE TABLE wapp_Acarreos(_id INTEGER PRIMARY KEY AUTOINCREMENT,codempresa INTEGER, codsucursal TEXT, codproyecto TEXT, transOwner TEXT, transPlate TEXT, transCapacity TEXT, checkpoint TEXT)";

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

    public void agregarCheckpoint(int cEmpresa, String cSucursal, String cProyecto, String tOwner, String tPlate, String tCapacity){
        SQLiteDatabase db = getWritableDatabase();
        if (db != null){
            try {
                String currentTime = Calendar.getInstance().getTime().toString();
                query = "INSERT INTO wapp_acarreos VALUES (NULL,'"+cEmpresa+"','"+cSucursal+"','"+cProyecto+"','" + tOwner + "','" + tPlate + "','" + tCapacity + "','" + currentTime + "')";
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
