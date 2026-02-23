package com.example.xlsxtodatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

public class DBValvesRepository {

    private DatabaseValvesHelper dbHelper;
    public DBValvesRepository(Context context){
        this.dbHelper=DatabaseValvesHelper.getInstance(context);
    }

    public int  listToDbTransaction(List<Valve> valves) {
        int successCount= 0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            for(Valve valve: valves) {

                ContentValues values = new ContentValues();


                values.put(DatabaseValvesHelper.NAME_ENG, valve.name_eng);
                values.put(DatabaseValvesHelper.KKS, valve.kks);
                values.put(DatabaseValvesHelper.NAME, valve.name);
                values.put(DatabaseValvesHelper.ISY, valve.isy);
                values.put(DatabaseValvesHelper.POWER_CABINET, valve.power_cabinet);
                values.put(DatabaseValvesHelper.FULL_NAME_OF_THE_POSITION, valve.full_name_of_the_position);
                values.put(DatabaseValvesHelper.ON_PLACE, valve.on_place);
                values.put(DatabaseValvesHelper.AP_50, valve.ap_50);
                values.put(DatabaseValvesHelper.MARK, valve.mark);
                values.put(DatabaseValvesHelper.CDA_CABINET, valve.cda_cabinet);
                values.put(DatabaseValvesHelper.CDA_CABINET_POSITION, valve.cda_cabinet_position);
                values.put(DatabaseValvesHelper.SLOT, valve.slot);
                values.put(DatabaseValvesHelper.NAMESPACE_VIEW_OPEN, valve.name_space_view_open);
                values.put(DatabaseValvesHelper.DESCRIPTION_BLOCKING_OPEN, valve.description_blocking_open);
                values.put(DatabaseValvesHelper.NAMESPACE_VIEW_CLOSE, valve.namespace_view_close);
                values.put(DatabaseValvesHelper.DESCRIPTION_BLOCKING_CLOSE, valve.description_blocking_close);



                long incertResult= db.insert(DatabaseValvesHelper.TABLE, null, values);
                if(incertResult !=-1){
                    successCount++;

                }
                else {

                    Log.e("listToDbTransaction","ошибка добавления в бд"+valve.kks);
                }
            }

            db.setTransactionSuccessful();
            Log.d("DatabaseHelper", "Successfully inserted " + valves.size() + " valves");

        }
        catch (Exception ec){

            Log.e("DatabaseHelper", "Error inserting valves: " + ec.getMessage());
        } finally {
            db.endTransaction();

            db.close();
        }
        // возвращаем количество успешных записей в БД
        return successCount;
    }




}
