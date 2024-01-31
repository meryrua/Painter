package com.example.myapplication;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.ParcelFileDescriptor;
import android.util.Log;

public class FileLog {
    private static final String FILE_NAME_LOG = "log_image.txt";
    private Context context;

    public FileLog(Context context) {
        this.context = context;
    }

    public void logToFile(String stringToWrite) {
        try {
            File fileDir = new File(context.getFilesDir(), FILE_NAME_LOG);
            if (!fileDir.exists()) {
                fileDir.createNewFile();
                Log.d("LOG", "create fileLog");
            } else {
                fileDir.delete();
                fileDir.createNewFile();
            }
            FileOutputStream fos = context.openFileOutput(FILE_NAME_LOG, Context.MODE_APPEND);
            fos.write(stringToWrite.getBytes());
            Log.d("LOG", "write fileLog");
            fos.close();

        } catch (IOException ex) {
            Log.d("LOG_MERYRUA", "Exception in writing to file ex = " + ex.getMessage());
        }
    }
}
