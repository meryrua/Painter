package com.example.myapplication;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import android.util.Log;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ImageGridActivity extends Activity {

    String[] names = { "one", "two", "three", "four", "five", "six",
            "seven", "eight", "nine", "ten"};
    List<String> myList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_grid);

        GridView gridView;
        //ImageGridViewAdapter customGridAdapter;

        //TextView gridView;
        //ArrayList<String> gridArray = new ArrayList<String>(Arrays.asList(names));
        //List<String> imageFilesList = getImagesList();

        List<String> imageFilesList = getImages();
        myList = new ArrayList<String>();

        ImageGridViewAdapter customGridAdapter;
        gridView = (GridView) findViewById(R.id.imagegridview);
        //gridView = (TextView) findViewById(R.id.gridItemText);
        customGridAdapter = new ImageGridViewAdapter(this, R.layout.image_item_layout, imageFilesList);
        gridView.setAdapter(customGridAdapter);
    }

    private ArrayList<String> getImages() {
        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;

        //Maybe it should be grid via Cursor not the ArrayList?
        cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        ArrayList<String> res = new ArrayList<String>(cursor.getCount());
        if (cursor.moveToFirst()) {
            final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            do {
                final String data = cursor.getString(dataColumn);
                res.add(data);
                Log.d("MERYRUA_LOG", "getImages " + data);
            } while (cursor.moveToNext());
        }

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

        Log.d("MERYRUA_LOG", "ImageGridActivity cursor " + cursor.getCount() + " res " + res.size());

        cursor.close();

        return res;
    }

    private List<String> getImagesList() {
        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return false;
            }
        };
        File[] file = Environment.getExternalStorageDirectory().listFiles();

        List<String> imageFilesList = new ArrayList<String>();
        String[] file1 = new String[]{};
        boolean list = false;

        for (File f : file) {
            Log.d("MERYRUA_LOG", "f.getPath() is " + f.getPath().toString() + " " + f.getPath().endsWith("Pictures"));
            if (f.getPath().endsWith("Pictures")) {
                file1 = f.list();

                File[] fl = f.listFiles();
                list = true;
                for (String f1 : file1) {
                    Log.d("MERYRUA_LOG", "f.getPath() is " + f1);
                    imageFilesList.add(f1);
                }
                for (File f1 : fl) {
                    Log.d("MERYRUA_LOG", "f.getPath() is " + f1.getAbsolutePath());

                }
            }
        }
        if (list)
            Log.d("MERYRUA_LOG", "getImagesList() number is " + imageFilesList.size() + " file size is " + file.length + " file1 size is " + file1.length);
        else
            Log.d("MERYRUA_LOG", "getImagesList() number is " + imageFilesList.size() + " file size is " + file.length);
        return imageFilesList;
    }

    public void onImageSelect(View view) {
        if (view.getAlpha() == 1f) {
            view.setAlpha(0.2f);
            Log.d("MERYRUA_LOG", "onImageSelect select " + view.getTag());
            myList.add(view.getTag().toString());

        } else {
            view.setAlpha(1f);
            myList.remove(view.getTag().toString());
            Log.d("MERYRUA_LOG", "onImageSelect not select " + view.getTag());
        }

    }

    public void onSelectCkicked(View view) {
        Log.d("MERYRUA_LOG", " onSelectCkicked " + myList.size());
        Intent intent = new Intent();
        SaveReadPicturesList saveReadPicturesList = new SaveReadPicturesList(this);
        saveReadPicturesList.saveList(myList);

        setResult(Activity.RESULT_OK);
        super.onBackPressed();
    }
}
