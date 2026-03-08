package com.example.xlsxtodatabase;

public interface DBWriteListener {
    public void onDBsuccess(Boolean resultBLOB);
    public void onDBerror(Exception e);
}
