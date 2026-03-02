package com.example.xlsxtodatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.example.xlsxtodatabase.Valve;

public class ReadExcelValves {
public String TAG= "ReadExcelValves Message" ;
    List<Valve> valves;
    Valve val2 = new Valve();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
     //Читаем Эксель таблицу, сохраняем в List, затем передаем в DatabaseValvesHelper, копируем в
    // бд SQLite, возвращаем количество записанных строк в калбек listener.onSucsess в MainActivity
    public void readValvesAsync(Context context, Uri uri, ExcelReadListener listener) {
        executor.execute(() -> {
            try {
                List<Valve> result = readFromExcel(context, uri);
                DBValvesRepository repository = new DBValvesRepository(context);
                mainHandler.post(() -> listener.onSuccess(repository.listToDbTransaction(result)));

            } catch (Exception e) {
                mainHandler.post(() -> listener.onError(e));
            }
        });
    }

    private List<Valve> readFromExcel(Context context, Uri uri)  {
        List<Valve> valves = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter();

        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            Log.e(TAG,"успешно подключена таблица эксель");
            XSSFSheet sheet = workbook.getSheetAt(0);
            for(int c=1;c<=524;c++) {
                Row row = sheet.getRow(c);

                if (row == null) {
                    Log.w(TAG, "Строка " + (c + 1) + " пустая, пропускаем");
                    continue;
                }


            // Пропускаем заголовок (первую строку)
                Valve valve = new Valve();
                for (int i = 0; i <= 15; i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String value = "";
                    if(cell==null){
                        Log.e(TAG,"cell is null");
                        try{
                            value= dataFormatter.formatCellValue(cell);
                            Log.e(TAG," форматтер отработал успешно");

                        }
                        catch (Exception ec){
                            Log.e(TAG,ec.getMessage());
                        }
                    }

                   CellType cellType = cell.getCellType();
                   try{
                       switch (cellType){
                           case STRING:
                               value = cell.getStringCellValue();
                               break;
                           case NUMERIC:
                               value = String.valueOf(cell.getNumericCellValue());
                               break;
                           case BOOLEAN:
                               value = String.valueOf(cell.getBooleanCellValue());
                               break;

                       }

                    }
                   catch (Exception ec){
                       Log.e(TAG,ec.getMessage());
                   }
                    switch (i) {
                        case 0: // Первая колонка (индекс 0)
                            valve.name_eng = value; // или другая логика для первой колонки
                            break;
                        case 1:
                            valve.kks = value;
                            break;
                        case 2:
                            valve.name = value;
                            break;
                        case 3:
                            valve.isy = value;
                            break;
                        case 4:
                            valve.power_cabinet = value;
                            break;
                        case 5:
                            valve.full_name_of_the_position = value;
                            break;
                        case 6:
                            valve.on_place = value;
                            break;
                        case 7:
                            valve.ap_50 = value;
                            break;
                        case 8:
                            valve.mark = value;
                            break;
                        case 9:
                            valve.cda_cabinet = value;
                            break;
                        case 10:
                            valve.cda_cabinet_position = value;
                            break;
                        case 11:
                            valve.slot = value;
                            break;
                        case 12:
                            valve.name_space_view_open = value;
                            break;
                        case 13:
                            valve.description_blocking_open = value;
                            break;
                        case 14:
                            valve.namespace_view_close = value;
                            break;
                        case 15:
                            valve.description_blocking_close = value;
                            break;

                    }

                }
                valves.add(valve);
                Log.e(TAG,"успешно добавлены значения ячеек в список valves");
            }
        
        }
        catch(Exception ec){
         ec.printStackTrace();
         Log.e(TAG,"проблема при создании и прочтении эксель");
        }
        return valves;
    }

    public void shutdown() {
        executor.shutdown();
    }
}