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
                    }
                }
            }
            );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.text.setText("пашоль");
        Log.e(TAG,"onCREATE yes");

        openXlsxContract.launch(mimeTypeExcelStr);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri downloadsUri = DocumentsContract.buildDocumentUri(
                    "com.android.externalstorage.documents",
                    "primary:Download"
            );
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, downloadsUri);
        }
        Log.e(TAG,"ready launch result api");
        openSaveDBcontract.launch(intent);
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

    private void createFileInDirectory(Uri directoryUri) {
        String filename = "control_and_measuring_instruments.db";
            try {
                // Создаем файл в выбранной директории
        Uri fileUri = DocumentsContract.createDocument   (
                getContentResolver(),
                directoryUri,
                "application/octet-stream",
                filename
        );
        if (fileUri != null) {
            saveFileToUri(fileUri);
        } }
            catch (Exception e){
            }
    }

    private void saveFileToUri(Uri uri) {
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                File sourceFile = new File(getFilesDir(), "control_and_measuring_instruments.db");
                if (!sourceFile.exists()) {
                    Toast.makeText(this, "Исходный файл не найден", Toast.LENGTH_SHORT).show();
                    return;
                }
                FileInputStream inputStream = new FileInputStream(sourceFile);
                byte[] buffer = new byte[1024];
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
}




