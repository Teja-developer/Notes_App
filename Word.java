package com.notes.kt.kt;

import android.widget.TextView;

/**
 * Created by jayanthsaikiran on 28/1/18.
 */

public class Word {

    public String title;
    public String time;
    public String date;

    public Word(String title, String time, String date) {
        this.title = title;
        this.time = time;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }
}
