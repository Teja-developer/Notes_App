package com.notes.kt.kt;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jayanthsaikiran on 28/1/18.
 */

public class WordAdapter extends ArrayAdapter<Word>  {

    public WordAdapter(Context context, ArrayList<Word> list) {
        super(context,0,list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        Word words = getItem(position);

        TextView textView1 = listItemView.findViewById(R.id.Heading);
        textView1.setText(words.getTitle());

        TextView textView2 = listItemView.findViewById(R.id.date);
        textView2.setText(words.getDate());

        TextView textView3 = listItemView.findViewById(R.id.Time);
        textView3.setText(words.getTime());


        return listItemView;
    }
}
