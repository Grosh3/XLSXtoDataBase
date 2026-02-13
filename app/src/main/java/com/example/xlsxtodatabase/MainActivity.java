package com.example.xlsxtodatabase;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyActivity////";
    Uri uriExcel;
     private  static final   String mimeTypeExcelStr = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    SQLiteDatabase controlAndMeasuringInstruments ;
    ActivityResultLauncher<String> openXlsxContract = registerForActivityResult(new ActivityResultContracts.GetContent(),uri->{if(uri!=null)uriExcel=uri;
        Log.d(TAG, "worked the resultActivityExcel" );});

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openXlsxContract.launch(mimeTypeExcelStr);
        DatabaseValvesHelper databaseValvesHelper = new DatabaseValvesHelper(getBaseContext());
        databaseValvesHelper.getReadableDatabase();
    }
 //*   public void createBaseAndCreateTableGateValves (){
     //   controlAndMeasuringInstruments=getBaseContext().openOrCreateDatabase("control_and_measuring_instruments",MODE_PRIVATE,null);
    //    controlAndMeasuringInstruments.execSQL("CREATE TABLE IF NOT EXISTS gatevalves (name_eng TEXT,kks TEXT, name TEXT, isy TEXT, power_cabinet TEXT,full_name_of_the_position TEXT,on_place TEXT,namespace_view_open BLOB,description_blocking_open BLOB, namespace_view_close BLOB,description_blocking_close BLOB  )");
/// bpvtytybz
    }


