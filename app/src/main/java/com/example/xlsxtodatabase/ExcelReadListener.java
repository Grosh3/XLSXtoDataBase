package com.example.xlsxtodatabase;
import java.util.List;

public interface ExcelReadListener 
    { void onSuccess(List <ReadExcelValves> valves);
    void onError(Exception e);
}
