package com.example.xlsxtodatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
//класс для облегчения работы с DatabaseValvesHelper (который в свою очередь работает с БД)
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

     /* метод для записи двоичных данных в БД, в качкстве даоичных данных изображения фрагментов блокировок
     он получает на вход одно из трех названий папок: OPEN_BLOCK,  CLOSE_BLOCK,PERIFER_BLOCK, выясняет какую именно из папок ему передали
     и поэтому определяет в какую колонку записать данные, также он на вход получает список названий файлов в папке, чтобы по ним найти
     совпадения с колонкой 2 "name_eng" БД.
      */
    public void imageDBwriter(List<String> listFoldersBlocksINAssets, String folderName) {
        Log.e(TAG, "imageDBwriter");
        byte[] byteFileImageArray;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tableName = DatabaseValvesHelper.TABLE;
      //  String[] columns = new String[]{DatabaseValvesHelper.NAME_ENG};
        String[] columns = new String[]{
                DatabaseValvesHelper.COLUMN_ID,     // ВАЖНО: используем COLUMN_ID, а не "_id"
                DatabaseValvesHelper.NAME_ENG
        };
        String columnNameEng = DatabaseValvesHelper.NAME_ENG;
        List <String> listEmptyFiles = new ArrayList<>();
        Cursor cursor = db.query(tableName, columns, null, null, null, null, null);
        boolean found= false;
        for (int i = 0; i < listFoldersBlocksINAssets.size(); i++) {
            String valueImageString = listFoldersBlocksINAssets.get(i);
            Log.d(TAG, "распечатка значения valueImageString"+valueImageString);

            if (cursor != null && cursor.moveToFirst()) {
                //запуск перебора колонки 2 БД
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
                        Log.e(TAG,"imageDBwriter() совпадение найдено");
                        found=true;

                        switch (folderName) {


                            case "OPEN_BLOCK":
                                byteFileImageArray = myAssetManager.imageToByteArray(context, folderName, valueImageString);
                                values.put(DatabaseValvesHelper.NAMESPACE_VIEW_OPEN, byteFileImageArray);

                                break;


                            case "CLOSE_BLOCK":
                                //получаем массив бинарных данных выбранного файла название которого передали в myAssetManager
                                byteFileImageArray = myAssetManager.imageToByteArray(context, folderName, valueImageString);
                                values.put(DatabaseValvesHelper.NAMESPACE_VIEW_CLOSE, byteFileImageArray);


                                break;
                            case "PERIFER_BLOCK":
                                byteFileImageArray = myAssetManager.imageToByteArray(context, folderName, valueImageString);
                                values.put(DatabaseValvesHelper.NAMESPACE_VIEW_PERIFER, byteFileImageArray);


                                break;
                            default:
                                Log.e(TAG, "Неизвестная папка: " + folderName);
                                continue;
                        }

                        updated = db.update(
                                DatabaseValvesHelper.TABLE,
                                values,
                                DatabaseValvesHelper.COLUMN_ID + " = ?",
                                new String[]{String.valueOf(rowId)}
                        );

                        if (updated > 0) {
                            Log.d(TAG, "Успешно обновлена запись для: " + cellValueNameEng);
                        } else {
                            Log.e(TAG, "Ошибка обновления для: " + cellValueNameEng);
                        }
                    }


                       //не забыть счетчик файлов которые не совпали








                    // Для разных типов данных:
                    // int intValue = cursor.getInt(columnIndex);
                    // long longValue = cursor.getLong(columnIndex);
                    // double doubleValue = cursor.getDouble(columnIndex);
// переходим к следующей строке
                } while (cursor.moveToNext());

                cursor.close();
                cursor = null;



            }

            // 3. Не забываем закрыть курсор


            if (!found){
              listEmptyFiles.add(valueImageString+" "+folderName+" нет совпадений");
            }
            {
            Log.e(TAG,"все файлы одной папки совпали");}
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        Log.e(TAG,listEmptyFiles.toString());
    }
    }
