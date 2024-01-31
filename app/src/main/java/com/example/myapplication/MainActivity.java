package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;

import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import static android.content.res.Configuration.ORIENTATION_PORTRAIT;


public class MainActivity extends AppCompatActivity {

    ConstraintLayout constraintLayout;
    private MyPicture myPicture;
    BitmapDrawable bmpDrawable;
    Canvas myCanvas;
    Context thisContext;
    private static FileLog fileLog;
    ArrayList<String> pic_list;
    PictureView pictureView;
    Button colorButtonPressed;
    int currentPicture;

    int x_size;
    int y_size;

    int[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA, Color.YELLOW};
    int defaulPictures[];
    boolean defaulListOnly = true;
    int currentColor;
    Bitmap bitmap;

    ProgressBar progressBar;

    private static final String PICTURE_ARRAY = "picture_array";
    private static final String PICTURE_WIDTH = "picture_width";
    private static final String PICTURE_HEIGHT = "picture_height";

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private static final int DIALOG_OPEN_FILE = 0;
    private static final int CHOOSE_FILE_CODE = 1;

    private class LoadPictureTask extends AsyncTask<Bitmap, Void, Void> {

        @Override
        protected void onPreExecute()
        {
            Log.d("LOG_MERYRUA", "onPreExecute progressBar = " + progressBar);
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected Void doInBackground(Bitmap... pic) {
            myPicture = new MyPicture(pic[0], fileLog);
            bitmap = null;

            return null;
        }

        @SuppressLint("WrongViewCast")
        @Override
        protected void onPostExecute(Void aVoid) {
            //loaded = true;
            Log.d("LOG_MERYRUA", "onPostExecute");
            progressBar.setVisibility(View.GONE);
            setContentView(R.layout.activity_main);

            //IT'S NEED TO CHECK!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            ImageView imageView = findViewById(R.id.pictureView);
            Button redButton = (Button) findViewById(R.id.red_color);
            redButton.setActivated(true);
            colorButtonPressed = redButton;

            Button nextPicture = (Button) findViewById(R.id.button_next);
            if (!isNextPicture()) {
                nextPicture.setBackgroundResource(R.drawable.left_smile);
                nextPicture.setEnabled(false);
            }

            Button prevPicture = (Button) findViewById(R.id.button_prev);
            if (currentPicture == 0) {
                prevPicture.setBackgroundResource(R.drawable.right_smile);
                prevPicture.setEnabled(false);
            }
            currentColor = Integer.parseInt(redButton.getTag().toString(), 16);

            //color setting

            pictureView = new PictureView(thisContext, imageView, myPicture);
            pictureView.setCurrentColor(currentColor);

        }
    }

    public void onColorPickerClick(View view) {
        Log.d("LOG_MERYRUA", "onColorPickerClick view.getTag() = " + view.getTag().toString());
        if (colorButtonPressed == null) {
            colorButtonPressed = (Button) view;
        } else {
            colorButtonPressed.setActivated(false);
            colorButtonPressed = (Button) view;
        }
        currentColor = Integer.parseInt(view.getTag().toString(), 16);
        view.setActivated(true);
        pictureView.setCurrentColor(currentColor);
    }

    public void onNextPicture(View view) {
        Log.d("LOG_MERYRUA", "onNextPicture ");
        if (isNextPicture())
            currentPicture++;
        String file_name = getPicture();
        Log.d("LOG_MERYRUA", "onNextPicture file_name = " + file_name + " currentPicture = " + currentPicture);

        loadBitmap(file_name);

        loadProgress();

    }

    public void onPrevPicture(View view) {
        Log.d("LOG_MERYRUA", "onNextPicture ");
        if (currentPicture > 0)
            currentPicture--;
        String file_name = getPicture();
        Log.d("LOG_MERYRUA", "onNextPicture file_name = " + file_name);

        loadBitmap(file_name);

        loadProgress();

    }

    public void onMakeList (View view) {
        requestRead();
    }

    public void requestRead() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("LOG_MERYRUA", "request Read");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            load_picture();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                load_picture();
            } else {
                // Permission Denied
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void load_picture () {
        Log.d("LOG_MERYRUA", " load_picture function");

        Intent imageGridIntent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(imageGridIntent, CHOOSE_FILE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_FILE_CODE && resultCode == RESULT_OK) {
            Log.d("LOG_MERYRUA", "onActivityResult");

            getPictureList();
            String file_name = getPicture();
            loadBitmap(file_name);
            Log.d("LOG_MERYRUA", "onActivityResult 11");
            loadProgress();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.load_menu:
                //requestRead();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getPicture() {
        String file_name;
        if (pic_list.isEmpty())
            file_name = "";
        else {
            file_name = pic_list.get(currentPicture);
        }
        return file_name;
    }

    private boolean isNextPicture() {
        if (pic_list.isEmpty()) {
            if (defaulPictures.length == (currentPicture + 1))
                return false;
            else
                return true;
        } else {
            if (pic_list.size() == (currentPicture + 1))
                return false;
            else
                return true;
        }
    }

    private void getPictureList() {
        SaveReadPicturesList saveReadPicturesList = new SaveReadPicturesList(this);
        pic_list = new ArrayList<String>();
        if (saveReadPicturesList.file_exist()) {
            pic_list = saveReadPicturesList.readList();
            Log.d("MERYRUA_LOG", " load picture_list " + pic_list.size());
        }
        currentPicture = 0;
    }

    private void loadProgress() {
        setContentView(R.layout.progress);

        progressBar.setVisibility(View.VISIBLE);
        if (myPicture != null)
            myPicture.recyclePictures();

        LoadPictureTask lpTask = new LoadPictureTask();
        lpTask.execute(bitmap);
    }

    private void loadBitmap(String file_name) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        if (file_name.isEmpty()) {
            BitmapFactory.decodeResource(getResources(), defaulPictures[currentPicture], opt);
            if ((opt.outWidth > metrics.widthPixels) || (opt.outHeight > metrics.heightPixels)) {
                float scaleX = (float) opt.outWidth / (float) metrics.widthPixels;
                float scaleY = (float) opt.outHeight / (float) metrics.heightPixels;
                int scale = (int)((scaleX > scaleY) ? (scaleX) : (scaleY));
                Log.d ("LOG_MERYRUA", "opt.outWidth = " + opt.outWidth + " metrics.widthPixels = " + metrics.widthPixels + " opt.outHeight = " + opt.outHeight + " metrics.heightPixels = " + metrics.heightPixels);
                //x = (int) (x / scale);
                //y = (int) (y / scale);
                Log.d ("LOG_MERYRUA", "scale = " + scale);
                opt.inSampleSize = (int) scale;
            }
            opt.inJustDecodeBounds = false;
            opt.inMutable = true; //to change bitmap
            opt.inPreferredConfig = Bitmap.Config.RGB_565; //reduce bytes of bitmap
            opt.inDensity = metrics.densityDpi;

            bitmap = BitmapFactory.decodeResource(getResources(), defaulPictures[currentPicture], opt);
            Log.d("LOG_MERYRUA", " load default bitmap = " + bitmap + " bitmap.getWidth() = " + bitmap.getWidth() + " bitmap.getHeight() = " + bitmap.getHeight());
        }
        else {
            BitmapFactory.decodeFile(file_name, opt);
            if ((opt.outWidth > metrics.widthPixels) || (opt.outHeight > metrics.heightPixels)) {
                float scaleX = (float) opt.outWidth / (float) metrics.widthPixels;
                float scaleY = (float) opt.outHeight / (float) metrics.heightPixels;
                int scale = (int)((scaleX > scaleY) ? (scaleX) : (scaleY));
                Log.d ("LOG_MERYRUA", "opt.outWidth = " + opt.outWidth + " metrics.widthPixels = " + metrics.widthPixels + " y = " + opt.outHeight + " metrics.heightPixels = " + metrics.heightPixels);
                //x = (int) (x / scale);
                //y = (int) (y / scale);
                Log.d ("LOG_MERYRUA", "scale = " + scale);
                opt.inSampleSize = (int) scale;
            }
            opt.inJustDecodeBounds = false;
            opt.inMutable = true; //to change bitmap
            opt.inPreferredConfig = Bitmap.Config.RGB_565; //reduce bytes of bitmap
            bitmap = BitmapFactory.decodeFile(file_name, opt);
            Log.d("LOG_MERYRUA", " load picture from list");
        }
    }

    public void loadPainter(View view) {
        setContentView(R.layout.progress);

        currentColor = 0;

        defaulPictures = new int[] {R.drawable.ezhik, R.drawable.krosh, R.drawable.losyash_1, R.drawable.karych, R.drawable.nysha_3, R.drawable.kopatych, R.drawable.pin1, R.drawable.barash1, R.drawable.sovunya};
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        x_size = size.x;
        y_size = size.y;
        Log.d("LOG_MERYRUA", "onCreate x_size = " + x_size + " y_size = " + y_size);
        //thisContext = this;

        getPictureList();

        currentPicture = 0;
        String file_name = getPicture();
        loadBitmap(file_name);

        fileLog = new FileLog(thisContext);

        progressBar = (ProgressBar) findViewById(R.id.progressMy);

        loadProgress();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        /*ImageView imView = new ImageView(this);
        imView.setBackgroundResource(R.drawable.title);
        imView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                Log.d("LOG_MERYRUA", " loadPainter");
                loadPainter();
                return true;
            }
        });
        thisContext = this;
        imView.invalidate();*/
        setContentView(R.layout.title_layout);
        thisContext = this;

        /*setContentView(R.layout.progress);

        currentColor = 0;

        defaulPictures = new int[] {R.drawable.ezhik, R.drawable.krosh, R.drawable.losyash_1, R.drawable.nysha_3, R.drawable.karych, R.drawable.kopatych, R.drawable.pin1, R.drawable.barash1, R.drawable.sovunya};
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        x_size = size.x;
        y_size = size.y;
        Log.d("LOG_MERYRUA", "onCreate x_size = " + x_size + " y_size = " + y_size);
        thisContext = this;

        getPictureList();

        currentPicture = 0;
        String file_name = getPicture();
        loadBitmap(file_name);

        fileLog = new FileLog(thisContext);

        progressBar = (ProgressBar) findViewById(R.id.progressMy);

        loadProgress();*/
    }


    /*Seems like no effect
    @Override
    protected void onDestroy() {
        myPicture.getPicture().recycle(); //
        super.onDestroy();
    }*/
}
