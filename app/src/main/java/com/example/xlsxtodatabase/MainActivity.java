package com.example.xlsxtodatabase;

import android.os.Build;
import android.provider.DocumentsContract;
import android.util.Log;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.example.xlsxtodatabase.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final String TAG = "MyActivity////";
    Uri uriExcel;
    private static final String mimeTypeExcelStr = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    Intent intent;

// открытие эксель
    ActivityResultLauncher<String> openXlsxContract = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        if (uri != null){ uriExcel = uri;
        Log.e(TAG,"URI IS OK");}
        else {
            Log.e(TAG,"URI IS  NULL");
        }
        Log.d(TAG, "worked the resultActivityExcel");
        ReadExcelValves readexcelvalves = new ReadExcelValves();
        readexcelvalves.readValvesAsync(getApplicationContext(), uri, new ExcelReadListener() {
            @Override
            public void onSuccess(int success) {
                Log.e(TAG, "Успешно,БД готова,строк " + success);

                openSaveDBcontract.launch(intentOpenSaveDB());
            }

            @Override
            public void onError(Exception e) {
                Log.e("///readValveAssync ", e.getMessage() + "ошибка");
            }
        });
    });

    // сохранение БД в загрузках
    ActivityResultLauncher<Intent> openSaveDBcontract = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri checkFolderUri = result.getData().getData();
                    if (checkFolderUri != null) {
                        Log.e(TAG,"работает лаунчер по сохранению ДБ в загрузках");
                        takeUriPremission(checkFolderUri);
                        createFileInDirectory(checkFolderUri);

                    }
                }
            }
            );

    // метод возвращающий интент чтобы сохранить БД в загрузках
    private Intent intentOpenSaveDB(){
         intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri downloadsUri = DocumentsContract.buildDocumentUri(
                    "com.android.externalstorage.documents",
                    "primary:Download"
            );
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, downloadsUri);
        }
        Log.e(TAG,"ready launch result api");
      return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.text.setText("пашоль");
        Log.e(TAG,"onCREATE yes");
/* лаунчер который отвечал за копирование эксель в список обьектов Valves , копирование данных в БД
     копирование самой БД в доступную папку на телефоне( сейчас отключен, тк БД создана, таблица Задвижек
     в БД создана и успешно функционирует. Я его не удаляю, потому что он может еще понадобиться.
 */
        openXlsxContract.launch(mimeTypeExcelStr);

    }



    private void takeUriPremission(Uri uri){
        try{
            getContentResolver().takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION|
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createFileInDirectory(Uri treeUri) {
        String filename = "control_and_measuring_instruments.db";
            try {
                // Преобразуем tree URI в document URI для целевой папки
                Uri docUri = DocumentsContract.buildDocumentUriUsingTree(
                        treeUri,
                        DocumentsContract.getTreeDocumentId(treeUri)
                );


                // Создаем файл в выбранной директории
        Uri fileUri = DocumentsContract.createDocument   (
                getContentResolver(),
                docUri,
                "application/octet-stream",
                filename
        );
        if (fileUri != null) {
            saveFileToUri(fileUri);
            Log.e(TAG, "Файл успешно создан: " + fileUri.toString());
            Toast.makeText(this, "файл в директории создан", Toast.LENGTH_SHORT).show();
        }
        else {
            Log.e(TAG, "Не удалось создать файл");
            Toast.makeText(this, "Не удалось создать файл", Toast.LENGTH_SHORT).show();
        }

            }
            catch (Exception e){
                Log.e(TAG, "Ошибка при создании файла: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(this, "Ошибка при создании файла: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
    }

    private void saveFileToUri(Uri uri) {
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                File sourceFile = getDatabaseFile();
                if (!sourceFile.exists()) {
                    Toast.makeText(this, "Исходный файл не найден", Toast.LENGTH_SHORT).show();
                    checkAllPossibleDbLocations();
                    if (sourceFile.length() == 0) {
                        Toast.makeText(this, "Файл БД пустой! Записаны ли данные?", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Log.e(TAG, "Начинаем копирование файла размером: " + sourceFile.length() + " байт");

                    return;
                }
                FileInputStream inputStream = new FileInputStream(sourceFile);
                byte[] buffer = new byte[8192];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                inputStream.close();
                outputStream.close();
                Toast.makeText(this, "Файл сохранен в выбранную папку", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private File getDatabaseFile() {
        // Правильный способ 1: через SQLiteOpenHelper
        SQLiteDatabase db = DatabaseValvesHelper.getInstance(this).getReadableDatabase();
        String dbPath = db.getPath();
        db.close(); // Важно закрыть!

        File dbFile = new File(dbPath);
        Log.e(TAG, "Путь к БД из SQLiteOpenHelper: " + dbPath);
        Log.e(TAG, "Файл существует: " + dbFile.exists());
        Log.e(TAG, "Размер файла: " + dbFile.length() + " байт");

        return dbFile;
    }


    private void checkAllPossibleDbLocations() {
        Log.e(TAG, "=== ПРОВЕРКА ВСЕХ ВОЗМОЖНЫХ МЕСТ ===");

        // Место 1: через SQLiteOpenHelper
        SQLiteDatabase db = DatabaseValvesHelper.getInstance(this).getReadableDatabase();
        String path1 = db.getPath();
        db.close();
        File file1 = new File(path1);
        Log.e(TAG, "1. SQLiteOpenHelper путь: " + path1);
        Log.e(TAG, "   Существует: " + file1.exists());
        Log.e(TAG, "   Размер: " + file1.length());

        // Место 2: через getDatabasePath
        File file2 = getDatabasePath("control_and_measuring_instruments.db");
        Log.e(TAG, "2. getDatabasePath: " + file2.getAbsolutePath());
        Log.e(TAG, "   Существует: " + file2.exists());
        Log.e(TAG, "   Размер: " + file2.length());

        // Место 3: стандартная директория databases
        File file3 = new File(getApplicationInfo().dataDir + "/databases/control_and_measuring_instruments.db");
        Log.e(TAG, "3. /databases директория: " + file3.getAbsolutePath());
        Log.e(TAG, "   Существует: " + file3.exists());
        Log.e(TAG, "   Размер: " + file3.length());

        // Место 4: files директория (где вы искали)
        File file4 = new File(getFilesDir(), "control_and_measuring_instruments.db");
        Log.e(TAG, "4. files директория: " + file4.getAbsolutePath());
        Log.e(TAG, "   Существует: " + file4.exists());

        // Проверим содержимое databases директории
        File databasesDir = new File(getApplicationInfo().dataDir + "/databases");
        if (databasesDir.exists() && databasesDir.isDirectory()) {
            File[] files = databasesDir.listFiles();
            if (files != null && files.length > 0) {
                Log.e(TAG, "   Содержимое databases директории:");
                for (File f : files) {
                    Log.e(TAG, "     - " + f.getName() + " (" + f.length() + " байт)");
                }
            } else {
                Log.e(TAG, "   databases директория пуста");
            }
        } else {
            Log.e(TAG, "   databases директория не существует");
        }

        // Проверим, есть ли записи в таблице
        try {
            SQLiteDatabase checkDb = DatabaseValvesHelper.getInstance(this).getReadableDatabase();
            android.database.Cursor cursor = checkDb.rawQuery("SELECT COUNT(*) FROM " + DatabaseValvesHelper.TABLE, null);
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                Log.e(TAG, "Количество записей в таблице: " + count);
            }
            cursor.close();
            checkDb.close();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при проверке таблицы: " + e.getMessage());
        }

        Log.e(TAG, "=== КОНЕЦ ПРОВЕРКИ ===");
    }




}




