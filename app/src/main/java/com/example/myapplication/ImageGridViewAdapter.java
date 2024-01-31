package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileDescriptor;
import java.util.List;

public class ImageGridViewAdapter extends ArrayAdapter<String> {

    Context mContext;
    List<String> list;
    TextView textView;
    int resId;
    private LayoutInflater layoutInflater;

    // Конструктор
    public ImageGridViewAdapter(Context context, int viewResourceId, List<String> list) {
        super(context, viewResourceId, list);
        // TODO Auto-generated constructor stub

        this.mContext = context;
        this.list = list;
        this.resId = viewResourceId;
        layoutInflater = LayoutInflater.from(context);
        //textView = (TextView) findViewById(textViewResourceId);
    }

    @Override
    public View getView(int position, View itemView, ViewGroup parent) {
        // TODO Auto-generated method stub

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inMutable = false; //to change bitmap
        opt.outHeight = 100;
        opt.outWidth = 100;
        opt.inPreferredConfig = Bitmap.Config.RGB_565; //reduce bytes of bitmap

        if (itemView == null) {
            itemView = layoutInflater.inflate(R.layout.image_item_layout, parent, false);
        }


        //TextView testText = (TextView) itemView.findViewById(R.id.elemNumber);
        //testText.setText(position);

        try {
            //!!!! IT's not URI! It's file path
            File file = new File(list.get(position));
            Log.d("MERYRUA_LOG", "ImageGridViewAdapter file name = " + list.get(position) + " position = " + position);
            //ParcelFileDescriptor parcelFileDescriptor = mContext.getContentResolver().openFileDescriptor(list.get(position), "r");
            //FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap bitmap = BitmapFactory.decodeFile(list.get(position), opt);
            //Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, new Rect(0, 0, 100, 100), opt);
            //parcelFileDescriptor.close();,
            Log.d("MERYRUA_LOG", "ImageGridViewAdapter bitmap = " + bitmap);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.gridItemImage);
            imageView.setImageBitmap(bitmap);
            imageView.setTag(list.get(position));
        } catch (Exception e) {
            Log.d("MERYRUA_LOG", "exception " + e.getMessage());
        }

        return (itemView);
    }



    // возвращает содержимое выделенного элемента списка
    public String getItem(int position) {
        return list.get(position);
    }

}
