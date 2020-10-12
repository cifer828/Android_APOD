package com.example.project4;

import android.graphics.Bitmap;

/******************************************************************************
 * Object of apod
 *
 * @author Qiuchen Z.
 * @date 4/2/20
 ******************************************************************************/
public class ApodObj {
    Bitmap image;
    String copyright;
    String explanation;
    String date;
    String title;

    /**
     * Constructor
     *
     * @param title image title
     * @param copyright image author
     * @param explanation image description
     * @param date date of apod
     * @param image image in bitmap
     */
    ApodObj(String title, String copyright, String explanation, String date, Bitmap image){
        this.title = title;
        this.copyright = copyright;
        this.explanation = explanation;
        this.date = date;
        this.image = image;
    }


}
