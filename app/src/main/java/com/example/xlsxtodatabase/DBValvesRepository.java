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
        Log.e(TAG, "imageDBwriter начал работу для папки: " + folderName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<String> listEmptyFiles = new ArrayList<>();

        // Создаем мапу для быстрого поиска: имя_файла -> имя_для_поиска
        for (String fileName : listFoldersBlocksINAssets) {
            Log.d(TAG, "Обрабатываем файл: " + fileName);
            boolean found = false;

            // Извлекаем имя для поиска из имени файла (убираем префикс и расширение)
            String searchName = extractNameFromFilename(fileName);
            Log.d(TAG, "Ищем в БД: " + searchName);

            // Ищем ТОЧНОЕ совпадение в колонке name_eng
            String[] columns = new String[]{
                    DatabaseValvesHelper.COLUMN_ID,
                    DatabaseValvesHelper.NAME_ENG
            };

            // Используем точное совпадение, а не LIKE
            String selection = DatabaseValvesHelper.NAME_ENG + " = ?";
            String[] selectionArgs = new String[]{searchName};

            Cursor cursor = null;
            try {
                cursor = db.query(DatabaseValvesHelper.TABLE, columns, selection, selectionArgs, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    // Нашли запись - обновляем только её
                    int idIndex = cursor.getColumnIndexOrThrow(DatabaseValvesHelper.COLUMN_ID);
                    long rowId = cursor.getLong(idIndex);
                    String nameEng = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseValvesHelper.NAME_ENG));

                    Log.e(TAG, "Найдена запись для: " + nameEng + " с ID: " + rowId);
                    found = true;

                    // Получаем бинарные данные изображения
                    byte[] byteFileImageArray = myAssetManager.imageToByteArray(context, folderName, fileName);

                    if (byteFileImageArray != null && byteFileImageArray.length > 0) {
                        ContentValues values = new ContentValues();

                        // Определяем колонку для обновления
                        switch (folderName) {
                            case "OPEN_BLOCK":
                                values.put(DatabaseValvesHelper.NAMESPACE_VIEW_OPEN, byteFileImageArray);
                                Log.d(TAG, "Обновляем колонку OPEN для: " + nameEng);
                                break;
                            case "CLOSE_BLOCK":
                                values.put(DatabaseValvesHelper.NAMESPACE_VIEW_CLOSE, byteFileImageArray);
                                Log.d(TAG, "Обновляем колонку CLOSE для: " + nameEng);
                                break;
                            case "PERIFER_BLOCK":
                                values.put(DatabaseValvesHelper.NAMESPACE_VIEW_PERIFER, byteFileImageArray);
                                Log.d(TAG, "Обновляем колонку PERIFER для: " + nameEng);
                                break;
                            default:
                                Log.e(TAG, "Неизвестная папка: " + folderName);
                                continue;
                        }

                        // Обновляем ТОЛЬКО одну конкретную запись
                        int updated = db.update(
                                DatabaseValvesHelper.TABLE,
                                values,
                                DatabaseValvesHelper.COLUMN_ID + " = ?",
                                new String[]{String.valueOf(rowId)}
                        );

                        if (updated > 0) {
                            Log.i(TAG, "✓ Успешно обновлена запись для: " + nameEng + " (файл: " + fileName + ")");
                        } else {
                            Log.e(TAG, "✗ Ошибка обновления для: " + nameEng);
                        }
                    } else {
                        Log.e(TAG, "Не удалось получить изображение для файла: " + fileName);
                    }
                } else {
                    Log.w(TAG, "Не найдена запись в БД для: " + searchName + " (файл: " + fileName + ")");
                }

                if (!found) {
                    listEmptyFiles.add(fileName + " в папке " + folderName + " - нет совпадения в БД");
                }

            } catch (Exception e) {
                Log.e(TAG, "Ошибка при обработке файла " + fileName + ": " + e.getMessage());
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        db.close();

        // Выводим статистику
        if (!listEmptyFiles.isEmpty()) {
            Log.e(TAG, "=== Файлы без совпадений (" + listEmptyFiles.size() + " шт.) ===");
            for (String emptyFile : listEmptyFiles) {
                Log.e(TAG, emptyFile);
            }
        } else {
            Log.i(TAG, "✓ Все файлы успешно обработаны и найдены в БД!");
        }

        Log.e(TAG, "imageDBwriter завершил работу");
    }

    /**
     * Извлекает имя для поиска из имени файла
     * Примеры:
     * "OPEN_3V4A.png" -> "3V4A"
     * "CLOSE_3V4B.png" -> "3V4B"
     * "PERIFER_A3A.png" -> "A3A"
     */
    private String extractNameFromFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }

        // Удаляем расширение .png (если есть)
        String nameWithoutExt = filename;
        if (nameWithoutExt.endsWith(".png")) {
            nameWithoutExt = nameWithoutExt.substring(0, nameWithoutExt.length() - 4);
        }

        // Удаляем префикс в зависимости от папки
        if (nameWithoutExt.startsWith("OPEN_")) {
            return nameWithoutExt.substring(5); // "OPEN_".length() = 5
        } else if (nameWithoutExt.startsWith("CLOSE_")) {
            return nameWithoutExt.substring(6); // "CLOSE_".length() = 6
        } else if (nameWithoutExt.startsWith("Perifer_")) {
            return nameWithoutExt.substring(8); // "PERIFER_".length() = 8
        }

        // Если нет известного префикса, возвращаем как есть
        return nameWithoutExt;
    }


    }
