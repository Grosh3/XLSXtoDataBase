package com.example.xlsxtodatabase;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

public interface ExcelReadListener 
    { void onSuccess(int success);
    void onError(Exception e);
}
