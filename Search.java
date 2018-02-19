package com.notes.kt.kt;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.notes.kt.kt.DataUtils.NOTE_BODY;
import static com.notes.kt.kt.DataUtils.NOTE_REQUEST_CODE;
import static com.notes.kt.kt.DataUtils.NOTE_TITLE;
import static com.notes.kt.kt.MainScreen.notes;

public class Search extends AppCompatActivity{
    private ArrayList<Integer> realIndexesOfSearchResults;
    WordAdapter wordAdapter;
    private GridView listView;
    private EditText searching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_search);

        android.support.v7.widget.SearchView searchView = findViewById(R.id.simpleSearchView);
        searchView.onActionViewExpanded();

//         Code to change the icon of the search mag icon in searchview.

//        int magId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
//        ImageView magImage = (ImageView) searchView.findViewById(magId);
//        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

        if (searchView != null) {
            // If searchView not null -> set query hint and open/query/close listeners
            searchView.setQueryHint(getString(android.R.string.search_go));
            searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                    public boolean onQueryTextChange(String newText) {
                        int i;

                        newText  = newText.toLowerCase();

                        if(newText.length()>0)
                        {
                            JSONArray notesFound = new JSONArray();
                            realIndexesOfSearchResults = new ArrayList<Integer>();

                            for(i=0; i<notes.length();i++)
                            {
                                JSONObject note = null;

                                try
                                {
                                    note = notes.getJSONObject(i);
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }

                                if(note!=null)
                                {
                                    try
                                    {
                                        if(note.getString(NOTE_TITLE).toLowerCase().contains(newText)|| note.getString(NOTE_BODY).toLowerCase().contains(newText))
                                        {
                                            notesFound.put(note);
                                            realIndexesOfSearchResults.add(i);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            listView = findViewById(R.id.list_search);
                            WordAdapter search = new WordAdapter(getApplicationContext() , notesFound);
                            listView.setAdapter(search);
                        }

                        else {
                            realIndexesOfSearchResults = new ArrayList<Integer>();
                            for (i = 0; i < notes.length(); i++)
                                realIndexesOfSearchResults.add(i);

                            wordAdapter = new WordAdapter(getApplicationContext(), notes);
                            listView.setAdapter(wordAdapter);
                        }

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                try {
                                    Intent intent = new Intent(getApplicationContext(),NewNote.class);
                                    intent.putExtra(NOTE_TITLE, notes.getJSONObject(position).getString(NOTE_TITLE));
                                    intent.putExtra(NOTE_BODY, notes.getJSONObject(position).getString(NOTE_BODY));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    intent.putExtra(NOTE_REQUEST_CODE,position);
                                    startActivity(intent);
                                }
                                catch(Exception e){
                                    e.printStackTrace();
                                }

                            }
                        });

                        return false;
                    }
            });
        }
    }

    public void goback(View view)
    {
        startActivity(new Intent(this, MainScreen.class));
    }

}
