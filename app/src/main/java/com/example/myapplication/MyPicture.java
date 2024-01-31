package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.File;


class MyStack {
    int stackTop;
    int stackBottom;
    int size;
    int[] pointStack;

    public MyStack(int size) {
        this.size = size;
        pointStack = new int[size];
        stackTop = 0;
        stackBottom = -1;
    }

    public void reInit() {
        stackTop = 0;
        stackBottom = -1;
    }

    public boolean isEmpty() {
        if (stackTop > stackBottom)
            return true;
        else
            return false;
    }

    public void push(int elem) {
        if (stackBottom < (size - 1)) {
            pointStack[++stackBottom] = elem;
        }
    }

    public int pop() {
        return pointStack[stackTop++];
    }
}

public class MyPicture{
    private Bitmap picture;
    private short[] pictureMap;
    private int picWidth;
    private int picHeight;
    private MyStack myStack;
    private byte areaBackground = 1;
    private int[] pictureCopy;
    private FileLog fileLog;

    public MyPicture(Bitmap bmp, FileLog fileLog) {
        picture = bmp;
        picWidth = bmp.getWidth();
        picHeight = bmp.getHeight();
        this.fileLog = fileLog;

        pictureMap = new short[picWidth * picHeight];
        myStack = new MyStack(picWidth * picHeight);
        Log.d("LOG_MERYRUA", "MyPicture:MyPicture picWidth = " + picWidth + " picHeight = " + picHeight);
        int color;

        pictureCopy = new int[picWidth * picHeight];
        bmp.getPixels(pictureCopy, 0, picWidth,0, 0, picWidth, picHeight);

        preparePicture();
        //bitmapPrint();
        Log.d("LOG_MERYRUA", "picture = " + picture);
    }

    /*protected MyPicture(Parcel in) {
        picture = in.readParcelable(Bitmap.class.getClassLoader());
        pictureMap = in.createByteArray();
        picWidth = in.readInt();
        picHeight = in.readInt();
        areaBackground = in.readByte();
        pictureCopy = in.createIntArray();
    }*/

    private void doFindArea(int i, int j, short curArea) {
        int topStack;
        int bottomStack;
        int curPoint;
        int curPoint_i, curPoint_j;

        myStack.reInit();
        //Log.d("LOG_MERYRUA", "doFindArea i = " + i + " j = " + j);

        //int pos = (i * picWidth) + j;
        myStack.push((j * picWidth) + i);
        pictureMap[i + (j * picWidth)] = curArea;

        while (!myStack.isEmpty()) {
            curPoint = myStack.pop();
            curPoint_j = curPoint / picWidth;
            curPoint_i = curPoint % picWidth;
            //Log.d("LOG_MERYRUA", "pop curPoint = " + curPoint + " curPoint_i = " + curPoint_i + " curPoint_j = " + curPoint_j);
            if ((curPoint_i - 1 >= 0) && (pictureMap[(curPoint_i - 1) + (curPoint_j * picWidth)] == 0)) {
                pictureMap[(curPoint_i - 1) + (curPoint_j * picWidth)] = curArea;
                myStack.push(((curPoint_j) * picWidth) + curPoint_i - 1);
            }
            if ((curPoint_i + 1 < picWidth) && (pictureMap[(curPoint_i + 1) + (curPoint_j * picWidth)] == 0)) {
                pictureMap[(curPoint_i + 1) + (curPoint_j * picWidth)] = curArea;
                myStack.push(((curPoint_j) * picWidth) + curPoint_i + 1);
            }
            if ((curPoint_j - 1 >= 0) && (pictureMap[curPoint_i + (curPoint_j - 1) * picWidth] == 0)) {
                pictureMap[(curPoint_i) + (curPoint_j - 1) * picWidth] = curArea;
                myStack.push(((curPoint_j - 1) * picWidth) + curPoint_i);
            }
            if ((curPoint_j + 1 < picHeight) && (pictureMap[(curPoint_i) + (curPoint_j + 1) * picWidth] == 0)) {
                pictureMap[(curPoint_i) + (curPoint_j + 1) * picWidth] = curArea;
                myStack.push(((curPoint_j + 1) * picWidth) + curPoint_i);
            }
        }



    }

    private void logPicture() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < picWidth; i++) {
            for (int j = 0; j < picHeight; j++) {
                str.append(pictureMap[i + j * picWidth]);
                str.append(" ");

            }
            str.append("\n");
        }
        fileLog.logToFile(str.toString());
    }


    private boolean checkIfIspixelAlone(int i, int j, short color) {
        boolean alone = false;
        //Log.d("LOG_MERYRUA", "checkIfIsBlackAlone i = " + i + " j = " + j);
        if ((i - 1 >= 0) && (pictureMap[(i - 1) + (j * picWidth)] != color)) {
            if ((i + 1 < picWidth) && (pictureMap[(i + 1) + (j * picWidth)] != color)) {
                if ((j - 1 >= 0) && (pictureMap[i + (j - 1) * picWidth] != color)) {
                    if ((j + 1 < picHeight) && (pictureMap[(i) + (j + 1) * picWidth] != color)) {
                        //Log.d("LOG_MERYRUA", "alone color = " + color);
                        alone = true;
                    }
                }
            }

        }
        return alone;
    }

    //remove single black points
    private void cleanBorderOfPicture() {
        short whiteColor = 0;
        short blackColor = -1;
        for (int i = 0; i < picWidth; i++) {
            for (int j = 0; j < picHeight; j++) {
                if (pictureMap[i + j * picWidth] == blackColor) {
                    if (checkIfIspixelAlone(i, j, blackColor)) {
                        pictureMap[i + j * picWidth] = whiteColor;
                        pictureCopy[(j * picWidth) + i] = Color.WHITE;
                    }
                }
            }
        }
        for (int i = 0; i < picWidth; i++) {
            for (int j = 0; j < picHeight; j++) {
                if (pictureMap[i + j * picWidth] == whiteColor) {
                    if (checkIfIspixelAlone(i, j, whiteColor)) {
                        pictureMap[i + j * picWidth] = blackColor;
                        pictureCopy[(j * picWidth) + i] = Color.BLACK;
                    }
                }
            }
        }
    }


    private void preparePicture() {
        for (int i = 0; i < picWidth; i++) {
            for (int j = 0; j < picHeight; j++) {
                if (pictureCopy[(j * picWidth) + i] == Color.BLACK) {
                    pictureMap[i + j * picWidth] = -1;
                    //Log.d("LOG_MERYRUA", "set byte to 1");
                } else
                    if (pictureCopy[(j * picWidth) + i] != Color.WHITE) {
                    Integer red = Integer.parseInt("1111100000000000", 2);
                    Integer green = Integer.parseInt("0000011111100000", 2);
                    Integer blue = Integer.parseInt("0000000000011111", 2);
                    Integer point_color_red = (pictureCopy[(j * picWidth) + i] & red) >> 11;
                    Integer point_color_green = (pictureCopy[(j * picWidth) + i] & green) >> 5;
                    Integer point_color_blue = (pictureCopy[(j * picWidth) + i] & blue);
                        //Log.d("LOG_MERYRUA", "not black and not white " + pictureCopy[(j * picWidth) + i] + " " + point_color_red + " " + point_color_green + " " + point_color_blue);
                    if ((point_color_red >= 28) && (point_color_green >= 53) && (point_color_blue >= 28)) {
                        pictureCopy[(j * picWidth) + i] = Color.BLACK;
                        pictureMap[i + j * picWidth] = -1;
                    } else {
                        pictureCopy[(j * picWidth) + i] = Color.WHITE;
                        pictureMap[i + j * picWidth] = 0;
                    }
                }
                /*if (pictureCopy[(j * picWidth) + i] != Color.WHITE) {
                    pictureMap[i + j * picWidth] = -1;
                    pictureCopy[(j * picWidth) + i] = Color.BLACK;
                    //Log.d("LOG_MERYRUA", "set byte to 1");
                } else
                    {
                      pictureCopy[(j * picWidth) + i] = Color.WHITE;
                    }*/
            }
        }
        //cleanBorderOfPicture();
        logPicture();


        short curArea = areaBackground;

        for (int i = 0; i < picWidth; i++) {
            for (int j = 0; j < picHeight; j++) {
                if (pictureMap[i + j * picWidth] == 0) {
                    doFindArea(i, j, curArea);
                    //Log.d("LOG_AREA", "curArea = " + curArea);
                    curArea++;
                }
            }
        }

        logPicture();

        picture.setPixels(pictureCopy, 0, picWidth, 0, 0, picWidth, picHeight);
        Log.d("LOG_MERYRUA", "preparePicture end curArea = " + curArea);
    }

    private void changeColorToPixel(int x, int y, int color) {
        picture.setPixel(x, y, color);
    }

    public void changeAreaColor(int x, int y, int color) {
        //y = y - 200;
        //bitmapPrint();
        Log.d("LOG_MERYRUA", "MyPicture:changeAreaColor x = " + x + " y = " + y + " color = " + Integer.toHexString(color));
        Log.d("LOG_AREA", "x = " + x + " y = " + y + " color == BLACK = " + pictureMap[x + y * picWidth]);
        if (pictureMap[x + y * picWidth] != -1) {
            short area = pictureMap[x + y * picWidth];
            Log.d("AREA", "area = " + area + " x = " + x + " y = " + y);
            if (area != areaBackground) { //do not paint background
                Log.d ("LOG_MERYRUA", "area number is " + area);
                for (int i = 0; i < picWidth; i++) {
                    for (int j = 0; j < picHeight; j++) {
                        if (pictureMap[i + j * picWidth] == area) {
                            //picture.setPixel(i, j, color);
                            pictureCopy[(j * picWidth) + i] = color;
                        }
                    }
                }
            }
        }
        picture.setPixels(pictureCopy, 0, picWidth, 0, 0, picWidth, picHeight);
        Log.d("LOG_MERYRUA", "load pictureCopy");
        //bitmapPrint();
    }

    public void bitmapPrint() {
        for (int i = 0; i < picWidth; i++) {
            for (int j = 0; j < picHeight; j++) {
                Log.d("LOG_MERYRUA", "pixel = " + Integer.toHexString(picture.getPixel(i, j)));
            }
        }
    }

    public void recyclePictures() {
        picture.recycle();
        picture = null;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public int getPicWidth() { return picWidth;}

    public int getPicHeight() { return picHeight;}
}
