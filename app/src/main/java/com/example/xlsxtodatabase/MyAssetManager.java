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
    /*этот метод получает три названия папок и перебором каждой по найденным совпадениям в БД их содержимого, записывает содержащиеся в них изображения
    в качестве бинарных данных, каждый в соотв кололнку БД
     */

    public void assetsForDB(Context context,List<String> listFoldersBlocksINAssets,DBWriteListener dbWriteListener){

            executor.execute(()-> {
                try {
                    DBValvesRepository dbValvesRepository = new DBValvesRepository(context);
                    for (String folderName : listFoldersBlocksINAssets) {


                        dbValvesRepository.imageDBwriter(getImageFilesFromAssetsFolder(context,  folderName), folderName);


                    }
                    Boolean resultBLOB = true;
                    mainHandler.post(() -> dbWriteListener.onDBsuccess(resultBLOB));
                } catch (Exception e) {
                    mainHandler.post(() -> dbWriteListener.onDBerror(e));

                }

            });
    }

// метод для извлечения списка файлов из папки в assets
    public List<String> getImageFilesFromAssetsFolder(Context context, String folderName) {
        Log.d(TAG, "Сработал метод getImageFilesFromAssetsFolder()" );
        AssetManager assetManager = context.getAssets();
        List<String> imageFiles = new ArrayList<>();

        // Расширения изображений
        List<String> imageExtensions = Arrays.asList(".png", ".jpg", ".jpeg", ".bmp", ".gif", ".webp");

        try {
            String[] items = assetManager.list(folderName);
            if (items != null && items.length > 0) {
                for (String item : items) {
                    // Проверяем по расширению
                    String lowerItem = item.toLowerCase();
                    boolean hasImageExtension = false;

                    for (String ext : imageExtensions) {
                        if (lowerItem.endsWith(ext)) {
                            hasImageExtension = true;
                            break;
                        }
                    }

                    if (hasImageExtension) {
                        // Дополнительно проверяем, что это действительно файл (не папка)
                        try {
                            assetManager.open(folderName + "/" + item).close();
                            imageFiles.add(item);
                            Log.d(TAG, "✅ Изображение: " + item);
                        } catch (IOException e) {
                            Log.d(TAG, "⏭️ Папка с именем как файл: " + item);
                        }
                    } else {
                        Log.d(TAG, "⏭️ Не изображение: " + item);
                    }
                }
            }
        } catch (IOException ec) {
            Log.e(TAG, "Ошибка при чтении папки " + folderName, ec);
        }

        return imageFiles;
    }





//получение бинарных данных для дальнейшей записи, этот метод сейчас использует класс DBValvesRepository
public byte[] imageToByteArray(Context context,String folderName,String fileNameDB)
{  Log.e(TAG,"сработал метод конвертор изображений в бинарный код -> imageToByteArray");

    AssetManager assetManager = context.getAssets();
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
        Log.e(TAG,"ссылка на файл изображений "+filePath)  ;
       return  readAssetFileToBytes(assetManager, filePath);
    }





    catch (Exception e){
        Log.e(TAG,"метод imageToByteArray "+e)  ;
        return null;
    }



}




//метод для передачи изображений блокировок в массив байт

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
            Log.e(TAG,"данные загружены в поток байт")  ;
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
