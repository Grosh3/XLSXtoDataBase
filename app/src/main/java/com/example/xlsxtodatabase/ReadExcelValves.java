package com.example.xlsxtodatabase;

import android.content.Context;
import android.net.Uri;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ReadExcelValves {


    public String column_id;
    public String name_eng;
    public String kks;
    public String name;
    public String isy;
    public String power_cabinet;
    public String full_name_of_the_position;
    public String on_place;
    public String ap_50;
    public String cda_cabinet;
    public String cda_cabinet_position;
    public String slot;
    public String name_space_view_open;
    public String description_blocking_open;
    public String namespace_view_close;
    public String description_blocking_close;
    public List<ReadExcelValves> listExcelValves = new ArrayList<>();
      DataFormatter dataFormatter =new DataFormatter();
    public void reader(Context context ,Uri uri){
        try {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
            for(int cRows=0;cRows<=525;cRows++){
                Row row= xssfSheet.getRow(cRows);
                ReadExcelValves readExcelValves = new ReadExcelValves();
                for(int i=0;i<=15;i++){
                    Cell cell = row.getCell(i);
                    String value = dataFormatter.formatCellValue(cell);
                    switch (i){
                        case 1:
                            readExcelValves.name_eng = value;
                            break;
                        case 2:
                            readExcelValves.kks = value;
                            break;
                        case 3:
                            readExcelValves.name = value;
                            break;
                        case 4:
                            readExcelValves.isy = value;
                            break;
                        case 5:
                            readExcelValves.power_cabinet = value;
                            break;
                        case 6:
                            readExcelValves.full_name_of_the_position = value;
                            break;
                        case 7:
                            readExcelValves.on_place = value;
                            break;
                        case 8:
                            readExcelValves.ap_50 = value;
                            break;
                        case 9:
                            readExcelValves.cda_cabinet = value;
                            break;
                        case 10:
                            readExcelValves.cda_cabinet_position = value;
                            break;
                        case 11:
                            readExcelValves.slot = value;
                            break;
                        case 12:
                            readExcelValves.name_space_view_open = value;
                            break;
                        case 13:
                            readExcelValves.description_blocking_open = value;
                            break;
                        case 14:
                            readExcelValves.namespace_view_close = value;
                            break;
                        case 15:
                            readExcelValves.description_blocking_close = value;
                            break;
                    }


                }

                listExcelValves.add(readExcelValves);
            }

            inputStream.close();
            xssfWorkbook.close();

        }
        catch (Exception ec){

            ec.printStackTrace();



        }





    }



 public List<ReadExcelValves> readExcelValvesSender (){
        return listExcelValves;
 }






}
