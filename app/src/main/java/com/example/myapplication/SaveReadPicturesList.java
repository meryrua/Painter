package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SaveReadPicturesList {
    private static final String file_name = "picture_list";
    private Context context;

    public SaveReadPicturesList(Context context) {
        this.context = context;
    }

    public boolean file_exist() {
        File file = new File(context.getFilesDir(), file_name);
        return (file.exists());
    }

    private void file_delete() {
        File file = new File(context.getFilesDir(), file_name);
        file.delete();
    }

    public void saveList (List<String> list) {
        if (file_exist()) {
            file_delete();
        }
        try {
            FileOutputStream fos = context.openFileOutput(file_name, Context.MODE_PRIVATE);
            int list_size = list.size();
            StringBuffer sBuffer = new StringBuffer();
            sBuffer.append(list_size);

            sBuffer.append("\n");
            for (int i = 0; i < list_size; i++) {
                sBuffer.append(list.get(i) + "\n");
            }
            fos.write(sBuffer.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            Log.d("MERYRUA_LOG", " cannot open fie to write " + e.getMessage());
            Toast toast = new Toast(context);
            toast.setText("Cannot save picture list");
        }
    }

    public  ArrayList<String> readList() {
        ArrayList<String> picture_list = new ArrayList<String>();
        if (file_exist()) {
            try {
                FileInputStream fis = context.openFileInput(file_name);
                InputStreamReader inputStreamReader =
                        new InputStreamReader(fis);
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(inputStreamReader);

                int size = Integer.parseInt(reader.readLine());
                for (int i = 0; i < size; i++) {
                    picture_list.add(reader.readLine());
                }
                fis.close();

            } catch (Exception e) {
                Log.d("MERYRUA_LOG", " cannot open fie to read " + e.getMessage());
                Toast toast = new Toast(context);
                toast.setText("Cannot read picture list");
            }
        }
        return picture_list;
    }
}
