package com.example.xlsxtodatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import org.apache.poi.ss.usermodel.Cell;
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

            XSSFSheet sheet = workbook.getSheetAt(0);
            for(int c=1;c<=524;c++) {
                Row row = sheet.getRow(c);



            // Пропускаем заголовок (первую строку)
                Valve valve = new Valve();
                for (int i = 1; i <= 16; i++) {
                    Cell cell = row.getCell(i);
                    if(cell.getStringCellValue()==null){
                        cell.setCellValue("");
                    }
                    String value = dataFormatter.formatCellValue(cell);
                    switch (i) {
                        case 1:
                            valve.name_eng = value;
                            break;
                        case 2:
                            valve.kks = value;
                            break;
                        case 3:
                            valve.name = value;
                            break;
                        case 4:
                            valve.isy = value;
                            break;
                        case 5:
                            valve.power_cabinet = value;
                            break;
                        case 6:
                            valve.full_name_of_the_position = value;
                            break;
                        case 7:
                            valve.on_place = value;
                            break;
                        case 8:
                            valve.ap_50 = value;

                            break;


                        case 9:
                            valve.mark = value;
                            break;
                        case 10:
                            valve.cda_cabinet = value;
                            break;
                        case 11:
                            valve.cda_cabinet_position = value;
                            break;
                        case 12:
                            valve.slot = value;
                            break;
                        case 13:
                            valve.name_space_view_open = value;
                            break;
                        case 14:
                            valve.description_blocking_open = value;
                            break;
                        case 15:
                            valve.namespace_view_close = value;
                            break;
                        case 16:
                            valve.description_blocking_close = value;
                            break;
                    }

                }
                valves.add(valve);
            }
        
        }
        catch(Exception ec){
         ec.printStackTrace(); 
        }
        return valves;
    }

    public void shutdown() {
        executor.shutdown();
    }
}