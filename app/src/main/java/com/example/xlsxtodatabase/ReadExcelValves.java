package com.example.xlsxtodatabase;

import android.content.Context;
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

public class ReadExcelValves {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public void readValvesAsync(Context context, Uri uri, ExcelReadListener listener) {
        executor.execute(() -> {
            try {
                List<Valve> result = readFromExcel(context, uri);
                mainHandler.post(() -> listener.onSuccess(result));
            } catch (Exception e) {
                mainHandler.post(() -> listener.onError(e));
            }
        });
    }

    private List<Valve> readFromExcel(Context context, Uri uri) throws Exception {
        List<Valve> valves = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter();

        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {

            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Пропускаем заголовок (первую строку)
            if (rowIterator.hasNext()) rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Valve valve = new Valve();

                for (int i = 1; i <= 15; i++) {  // i = 1..15, как в вашем коде
                    Cell cell = row.getCell(i);
                    String value = dataFormatter.formatCellValue(cell);
                    switch (i) {
                        case 1: valve.name_eng = value; break;
                        case 2: valve.kks = value; break;
                        case 3: valve.name = value; break;
                        case 4: valve.isy = value; break;
                        case 5: valve.power_cabinet = value; break;
                        case 6: valve.full_name_of_the_position = value; break;
                        case 7: valve.on_place = value; break;
                        case 8: valve.ap_50 = value; break;
                        case 9: valve.cda_cabinet = value; break;
                        case 10: valve.cda_cabinet_position = value; break;
                        case 11: valve.slot = value; break;
                        case 12: valve.name_space_view_open = value; break;
                        case 13: valve.description_blocking_open = value; break;
                        case 14: valve.namespace_view_close = value; break;
                        case 15: valve.description_blocking_close = value; break;
                    }
                }
                valves.add(valve);
            }
        }
        return valves;
    }

    public void shutdown() {
        executor.shutdown();
    }
}