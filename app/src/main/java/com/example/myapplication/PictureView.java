package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PictureView {
    private Context context;
    //private Bitmap picture;
    private MyPicture myPicture;
    private float scaleFactor;
    private ImageView imageView;
    private int color;
    private int xOffset;
    private int yOffset;

    public PictureView(Context context, final ImageView imageView, MyPicture picture) {
        this.context = context;
        this.imageView = imageView;
        this.myPicture = picture;
        Log.d("LOG_MERYRUA", "addOnLayoutChangeListener view = " + imageView.getId());
        this.imageView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Log.d("LOG_MERYRUA", "addOnLayoutChangeListener( width = " + v.getId() + " " + (right - left) + " height = " + (bottom - top) );
                DrawPicture();
            }
        });
        this.imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("LOG_MERYRUA", "imageView.getWidth() = " + imageView.getWidth() + " imageView.getHeight() = " + imageView.getHeight() );
                return onTouchEvent(event);
            }
        });
    }

    public void setCurrentColor(int color) {
        this.color = color;
    }

    public void DrawPicture(){
        Log.d("LOG_MERYRUA", "DrawPicture");

        Canvas canvas = new Canvas();

        canvas.drawARGB(255, 255, 255, 255);

        //drawMenu(canvas);
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        Point size = new Point();
        size.set(display.widthPixels, display.heightPixels);
        Log.d("LOG_MERYRUA", "DrawPicture screenWidth = " + size.x + " screenHeight = " + size.y);
        //int screenWidth = size.x;
        //int screenHeight = size.y;
        int screenWidth = imageView.getWidth();
        int screenHeight = imageView.getHeight();
        int offsetBitmap = 200;


        Log.d("LOG_MERYRUA", "imageView.getWidth() = " + imageView.getWidth() + " imageView.getHeight() = " + imageView.getHeight() );
        /*String info =
                String.format("Info: size = %s x %s, bytes = %s (%s), config = %s",
                        myPicture.getPicWidth(),
                        picture.getHeight(),
                        picture.getByteCount(),
                        picture.getRowBytes(),
                        picture.getConfig());*/
        //Log.d("LOG_MERYRUA", info);

        Matrix matrix = new Matrix();
        float px = (float)screenWidth / (float)myPicture.getPicWidth();
        float py = (float)screenHeight / (float)myPicture.getPicHeight();
        if (py < px)
            scaleFactor = py;
        else
            scaleFactor = px;

        Log.d("LOG_MERYRUA", "px = " + px + " py = " + py + " scaleFactor = " + scaleFactor);
        matrix.postScale(scaleFactor, scaleFactor);
        //matrix.postTranslate(0, 200);

        canvas.drawBitmap(myPicture.getPicture(), matrix, null);
        int newWidth = (int)(myPicture.getPicWidth() * scaleFactor);
        int newHeight = (int)(myPicture.getPicHeight() * scaleFactor);
        Log.d("LOG_MERYRUA", " newWidth = " + newWidth + " newHeight = " + newHeight);
        xOffset = (screenWidth - newWidth) / 2;
        yOffset = (screenHeight - newHeight) / 2;
        Log.d("LOG_MERYRUA", "screenWidth = " + screenWidth + " myPicture.getPicWidth() = " + myPicture.getPicWidth() + " xOffset = " + xOffset);
        Log.d("LOG_MERYRUA", "screenHeight = " + screenHeight + " newHeight = " + newHeight + " yOffset = " + yOffset);
        //Log.d("LOG_MERYRUA", "picture.getWidth() = " + picture.getWidth() + " picture.getHeight() = " + picture.getHeight());
        imageView.setImageBitmap(myPicture.getPicture());
    }

    public boolean onTouchEvent (MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            //int t_x = (int) (x * ((float) bitmap.getWidth() / (float) screenWidth));
            //int t_y = (int) (y * ((float) bitmap.getHeight() / (float) (screenHeight - offsetBitmap)));

            int t_x = (int) ((x - xOffset) * (1 / scaleFactor));
            int t_y = (int) ((y - yOffset) * (1 / scaleFactor));

            Log.d("LOG_MERYRUA", "onTouchEvent x = " + x + " y = " + y + " xOffset = " + xOffset + " yOffset = " + yOffset + " scaleFactor = " + scaleFactor + " t_x = " + t_x + " t_y = " + t_y);


            ///color saved here?
            if (((t_x >= 0) && (t_x < myPicture.getPicWidth())) && ((t_y >= 0) && (t_y < myPicture.getPicHeight()))) {
                myPicture.changeAreaColor(t_x, t_y, color);
                this.imageView.invalidate();
            }

            return true;
        }
        return false;
    }
}
