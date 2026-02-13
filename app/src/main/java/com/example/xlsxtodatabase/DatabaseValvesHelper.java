package com.example.xlsxtodatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseValvesHelper extends SQLiteOpenHelper {
    //названия столбцов;
    public static final String COLUMN_ID = "_id" ;
    public static final String NAME_ENG = "name_eng";
    public static final String KKS ="kks";
    public static final String NAME = "name";
    public static final String ISY = "isy";
    public static final String POWER_CABINET = "power_cabinet";
    public static final String FULL_NAME_OF_THE_POSITION = "full_name_of_the_position";
    public static final String ON_PLACE = "on_place";
    public static final String AP_50 = "ap_50";
    public static final String CDA_CABINET = "cda_cabinet";
    public static final String CDA_CABINET_POSITION = "cda_cabinet_position";
    public static final String SLOT = "slot";
    public static final String NAMESPACE_VIEW_OPEN = "name_space_view_open";
    public static final String DESCRIPTION_BLOCKING_OPEN= "description_blocking_open";
    public static final String NAMESPACE_VIEW_CLOSE = "namespace_view_close";
    public static final String DESCRIPTION_BLOCKING_CLOSE = "description_blocking_close";
    static final String TABLE = "gate_valves";
    private static final String DATABASE_NAME ="control_and_measuring_instruments.db";
    private static final int DB_VALVESVERSIONSTR =1;

    public DatabaseValvesHelper(Context context){

        super(context,DATABASE_NAME,null,DB_VALVESVERSIONSTR);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE gate_valves ("+ COLUMN_ID +"INTEGER PRIMARY KEY AUTOINCREMENT,"+ NAME_ENG + "TEXT,"+ KKS +"TEXT,"+ NAME+ "TEXT,"+ ISY+"TEXT,"+POWER_CABINET+"TEXT,"+FULL_NAME_OF_THE_POSITION+"TEXT,"+ON_PLACE+"TEXT,"+AP_50+"TEXT,"+CDA_CABINET+"TEXT,"+CDA_CABINET_POSITION+"TEXT,"+SLOT+"TEXT,"+NAMESPACE_VIEW_OPEN+"TEXT,"+DESCRIPTION_BLOCKING_OPEN+"TEXT,"+NAMESPACE_VIEW_CLOSE+"TEXT,"+DESCRIPTION_BLOCKING_CLOSE+"TEXT);");

    }


   // метод onUpgrade() констатирует изменения под измененную базу данных новой версии, он сам не
   // перезаписывает никаких данных, например если новая версия БД с дополнительным столбцом
    // то в нем необходимо прописать эти изменения. Андроид видит что версия не совпадает и
    // автоматически вызывает этот метод.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
