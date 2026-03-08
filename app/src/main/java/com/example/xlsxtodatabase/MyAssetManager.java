package com.example.xlsxtodatabase;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyAssetManager
{
    String TAG = "MyAssetManager";
private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    public List<String> listFoldersBlocksINAssets = new ArrayList<>();
    public String folderName;
    //будем вызывать этот метод из Майн и передавать в него список имен папок блокировок
    public void assetsForDB(Context context,List<String> listFoldersBlocksINAssets,DBWriteListener dbWriteListener){

            executor.execute(()->{
                try{
                    DBValvesRepository dbValvesRepository = new DBValvesRepository(context);
                    for(String folderName:listFoldersBlocksINAssets) {



                        dbValvesRepository.imageDBwriter( getFilesFromAssetsFolder(context,listFoldersBlocksINAssets,folderName ),folderName);


                    }
                }catch (Exception e){}


            });





    }




    public List<String> getFilesFromAssetsFolder(Context context,  List<String> listFoldersBlocksINAssets, String folderName) {
        AssetManager assetManager = context.getAssets();
        try {
            String[] files = assetManager.list(folderName);
            if (files != null && files.length > 0) {
                // Добавляем все найденные файлы в переданный список
                listFoldersBlocksINAssets.addAll(Arrays.asList(files));
            }
        } catch (IOException ec) {
            ec.printStackTrace();
        }
        return listFoldersBlocksINAssets;
    }


public byte[] imageToByteArray(Context context,String folderName,String fileNameDB)
{AssetManager assetManager = context.getAssets();
    try {



        String[] files = assetManager.list(folderName);
        if (files == null || files.length == 0) {
            throw new IOException("Папка OPEN_BLOCK пуста или не существует");
        }

        String targetFileName = null;
        for (String fileName : files) {
            if (fileName.contains(fileNameDB)) {
                targetFileName = fileName;
                break;
            }
        }

        if (targetFileName == null) {
            throw new IOException("Файл с подстрокой '" + fileNameDB + "' не найден в папке OPEN_BLOCK");
        }

        // Формируем полный путь к файлу
        String filePath = folderName + "/" + targetFileName;

        // Читаем файл в массив байт

       return  readAssetFileToBytes(assetManager, filePath);
    }





    catch (Exception e){
        Log.e(TAG,"метод imageToByteArray "+e)  ;
        return null;
    }



}






    private byte[] readAssetFileToBytes(AssetManager assetManager, String filePath) throws IOException {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;

        try {
            inputStream = assetManager.open(filePath);
            outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[8192]; // Буфер 8KB для чтения
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return outputStream.toByteArray();
        } finally {
            // Закрываем потоки
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }






















}
