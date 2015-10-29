package com.example.caesar.navigation;


import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by SABA on 7/29/2015.
 */
public class DataProvider {
    public static final int TYPE_MESSAGE = 0;

    public static final int TYPE_LOG = 1;
    public static final int TYPE_ACTION = 2;
    public static final int TYPE_IMG = 3;
    private int mType;
    public boolean side;
    public boolean position;
    public String message;
    public int number;
    public Bitmap image;
    ImageView newImage;




    public DataProvider(boolean position, String message, int i,boolean side){
        super();
        this.position = position;
        this.message = message;
        this.mType = i;
        this.side = side;
    }

    public DataProvider(boolean position, Bitmap image, int i,boolean side) {
        super();
//        LinearLayout.LayoutParams imgLayout = new LinearLayout.LayoutParams(300,300);

        this.position = position;
        //       newImage.setLayoutParams(imgLayout);
        //      newImage.setImageBitmap(image);
        this.image = image;
        this.mType = i;
        this.side = side;
    }

    public DataProvider(boolean position, String message, int i){
        super();
        this.position = position;
        this.message = message;
        this.mType = i;
    }

    public int getType() {
        return mType;
    }


}
