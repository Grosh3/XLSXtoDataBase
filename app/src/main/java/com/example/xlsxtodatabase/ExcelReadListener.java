package com.example.xlsxtodatabase;
import java.util.List;

public interface ExcelReadListener 
    { void onSuccess(List <Valve> valves);
    void onError(Exception e);
}
