package com.example.xlsxtodatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBValvesRepository {

    private DatabaseValvesHelper dbHelper;
    MyAssetManager myAssetManager = new MyAssetManager();
    public static final String TAG = "DBValvesRepository";
    Context context;

    public DBValvesRepository(Context context) {
        this.dbHelper = DatabaseValvesHelper.getInstance(context);
        this.context=context;

    }

    // метод записи списка обьектов Valves (который хранит в себе данные из таблицы эксель) в БД
    public int listToDbTransaction(List<Valve> valves) {
        Log.e(TAG, "начал работать метод listToDbTransaction() ");
        int successCount = 0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (Valve valve : valves) {

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
                //   values.put(DatabaseValvesHelper.NAMESPACE_VIEW_OPEN, valve.name_space_view_open);
                //   values.put(DatabaseValvesHelper.DESCRIPTION_BLOCKING_OPEN, valve.description_blocking_open);
                //   values.put(DatabaseValvesHelper.NAMESPACE_VIEW_CLOSE, valve.namespace_view_close);
                //   values.put(DatabaseValvesHelper.DESCRIPTION_BLOCKING_CLOSE, valve.description_blocking_close);


                long incertResult = db.insert(DatabaseValvesHelper.TABLE, null, values);
                if (incertResult != -1) {
                    successCount++;
                    Log.e(TAG, "работает счетчик записей в БД");
                } else {

                    Log.e("listToDbTransaction", "ошибка добавления в бд" + valve.kks);
                }
            }

            db.setTransactionSuccessful();
            Log.d("DatabaseHelper", "Successfully inserted " + valves.size() + " valves");

        } catch (Exception ec) {

            Log.e("DatabaseHelper", "Error inserting valves: " + ec.getMessage());
        } finally {
            db.endTransaction();
            Log.e(TAG, "конец транзакции, закрываем подключение к БД");

            db.close();
        }
        // возвращаем количество успешных записей в БД
        return successCount;
    }


    public void imageDBwriter(List<String> listFoldersBlocksINAssets, String folderName) {
        Log.e(TAG, "начал работать метод listFoldersBlocksINAssets ");
        byte[] byteFileImageArray;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tableName = DatabaseValvesHelper.TABLE;
        String[] columns = new String[]{DatabaseValvesHelper.NAME_ENG};
        String columnNameEng = DatabaseValvesHelper.NAME_ENG;
        Cursor cursor = db.query(tableName, columns, null, null, null, null, null);
        boolean boolIsntEmpty= false;
        for (int i = 0; i <= listFoldersBlocksINAssets.size(); i++) {
            String valueImageString = listFoldersBlocksINAssets.get(i);
            List <String> listEmptyFiles = new ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) {

                ContentValues values = new ContentValues();
                int columnIndex = cursor.getColumnIndexOrThrow(columnNameEng);
                int idIndex = cursor.getColumnIndexOrThrow("_id");
                long rowId = cursor.getLong(idIndex);
                int updated;


                do {
                    // Получаем значение текущей ячейки
                    String cellValueNameEng = cursor.getString(columnIndex);
                    //Сравнение строк из БД и названия файла из папки
                    if (valueImageString.contains(cellValueNameEng)){
                        boolIsntEmpty=true;

                        switch (folderName){


                            case "OPEN_BLOCK":
                           byteFileImageArray=    myAssetManager.imageToByteArray(context,folderName,valueImageString);
                           values.put("name_space_view_open",byteFileImageArray);
                                 updated = db.update(
                                        "gate_valves",
                                        values,
                                        "_id = ?",
                                        new String[]{String.valueOf(rowId)}
                                );

                                if (updated > 0) {
                                    Log.d("DB", "Успешно обновлена колонка close");
                                } else {
                                    Log.e("DB", "Ошибка обновления");
                                }
                                break;


                            case "CLOSE_BLOCK":
                                byteFileImageArray=    myAssetManager.imageToByteArray(context,folderName,valueImageString);
                                values.put("name_space_view_close",byteFileImageArray);
                                 updated = db.update(
                                        "gate_valves",
                                        values,
                                        "_id = ?",
                                        new String[]{String.valueOf(rowId)}
                                );

                                if (updated > 0) {
                                    Log.d("DB", "Успешно обновлена колонка close");
                                } else {
                                    Log.e("DB", "Ошибка обновления");
                                }

                                break;
                            case "PERIFER_BLOCK":
                                byteFileImageArray=    myAssetManager.imageToByteArray(context,folderName,valueImageString);
                                values.put("namespace_view_perifer",byteFileImageArray);
                                updated = db.update(
                                        "gate_valves",
                                        values,
                                        "_id = ?",
                                        new String[]{String.valueOf(rowId)}
                                );

                                if (updated > 0) {
                                    Log.d("DB", "Успешно обновлена колонка close");
                                } else {
                                    Log.e("DB", "Ошибка обновления");
                                }

                                break;




                       //не забыть счетчик файлов которые не совпали

                        }






                 }

                    // Для разных типов данных:
                    // int intValue = cursor.getInt(columnIndex);
                    // long longValue = cursor.getLong(columnIndex);
                    // double doubleValue = cursor.getDouble(columnIndex);

                } while (cursor.moveToNext()); // переходим к следующей строке
            }

            // 3. Не забываем закрыть курсор
            if (cursor != null) {
                cursor.close();
            }
            db.close();
            if (!boolIsntEmpty){
              listEmptyFiles.add(valueImageString+" "+folderName+" нет совпадений");
            }{
            Log.e(TAG,"все файлы одной папки совпали");}
        }


    }
}