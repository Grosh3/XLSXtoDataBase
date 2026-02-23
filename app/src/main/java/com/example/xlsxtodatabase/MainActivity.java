package com.example.xlsxtodatabase;

import android.util.Log;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import com.example.xlsxtodatabase.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {
 
     private ActivityMainBinding binding;
	
 
    private static final String TAG = "MyActivity////";
    Uri uriExcel;
     private  static final   String mimeTypeExcelStr = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    SQLiteDatabase controlAndMeasuringInstruments ;
    ActivityResultLauncher<String> openXlsxContract = registerForActivityResult(new ActivityResultContracts.GetContent(),uri->{if(uri!=null)uriExcel=uri;
        Log.d(TAG, "worked the resultActivityExcel" );
    ReadExcelValves readexcelvalves = new ReadExcelValves();
    readexcelvalves.readValvesAsync(getApplicationContext(), uri, new ExcelReadListener() {
        @Override
        public void onSuccess(int success) {

        }

        @Override
        public void onError(Exception e) {

        }
    });



    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   binding =ActivityMainBinding.inflate(getLayoutInflater()); 
     setContentView(binding.getRoot())   ;
        binding.text.setText("пашоль");
        
        openXlsxContract.launch(mimeTypeExcelStr);

    }

    }


