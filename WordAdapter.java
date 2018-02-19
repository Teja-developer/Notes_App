package com.notes.kt.kt;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import static com.notes.kt.kt.DataUtils.NOTE_BODY;
import static com.notes.kt.kt.DataUtils.NOTE_COLOUR;
import static com.notes.kt.kt.DataUtils.NOTE_TITLE;

/**
 * Created by jayanthsaikiran on 28/1/18.
 */

public class WordAdapter extends BaseAdapter implements ListAdapter {

    private Context mContext;
    private JSONArray mJSONArray;

    public WordAdapter(Context context, JSONArray notes) {
        mContext = context;
        mJSONArray = notes;
    }

    @Override
    public int getCount() {
        if(mJSONArray!=null)
            return mJSONArray.length();
        return 0;
    }

    @Override
    public JSONObject getItem(int i) {
        if(mJSONArray != null)
            return mJSONArray.optJSONObject(i);
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            assert inflater != null;
            convertView = inflater.inflate(R.layout.list_item,parent,false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.Heading);
        TextView body = (TextView) convertView.findViewById(R.id.date);
        TextView time = convertView.findViewById(R.id.time);
        TextView id = convertView.findViewById(R.id.id);

        JSONObject jsonObject = getItem(position);

        if (jsonObject != null) {
            // If jsonObject not empty -> initialize variables
            String title_String = mContext.getString(R.string.note_title);
            String body_String = mContext.getString(R.string.note_body);

            try {
                title_String = jsonObject.getString(NOTE_TITLE);
                body_String = jsonObject.getString(NOTE_BODY);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            title.setText(title_String);
            body.setText(body_String);


        }

        return convertView;

    }
}
