package com.notes.kt.kt;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Handler;

import static com.notes.kt.kt.DataUtils.BACKUP_FILE_NAME;
import static com.notes.kt.kt.DataUtils.BACKUP_FOLDER_PATH;
import static com.notes.kt.kt.DataUtils.NEW_NOTE_REQUEST;
import static com.notes.kt.kt.DataUtils.NOTES_FILE_NAME;
import static com.notes.kt.kt.DataUtils.NOTE_REQUEST_CODE;
import static com.notes.kt.kt.DataUtils.isExternalStorageReadable;
import static com.notes.kt.kt.DataUtils.isExternalStorageWritable;
import static com.notes.kt.kt.DataUtils.retrieveData;

public class MainScreen extends AppCompatActivity{


    private boolean exit = false;
    private static JSONArray notes;
    private static File localPath, backupPath;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private TextView usernm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/Myriad.ttf");
        TextView textView = findViewById(R.id.app_bar_text);
        textView.setTypeface(typeface);

        navigationView = findViewById(R.id.activity_main_navigationView);
        Toolbar toolbar = findViewById(R.id.main_screen_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.nullstring);

        drawerLayout = findViewById(R.id.activity_main_drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpDrawerContent(navigationView);

        usernm = findViewById(R.id.username);

        // Initialize local file path and backup file path
        localPath = new File(getFilesDir() + "/" + NOTES_FILE_NAME);

        File backupFolder = new File(Environment.getExternalStorageDirectory() +
                BACKUP_FOLDER_PATH);

        if (isExternalStorageReadable() && isExternalStorageWritable() && !backupFolder.exists())
            backupFolder.mkdir();

        backupPath = new File(backupFolder, BACKUP_FILE_NAME);

        // Init notes array
        notes = new JSONArray();

        // Retrieve from local path
        JSONArray tempNotes = retrieveData(localPath);

        // If not null -> equal main notes to retrieved notes
        if (tempNotes != null)
            notes = tempNotes;



        //Experimenting with ArrayList
        addToList();

    }
    protected void setUpDrawerContent(NavigationView navigationView) {
        Log.v("Note", "setUpDrawer");

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.v("Note", "onNavigationItem");
                return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    private void addToList(){

        ArrayList<Word> list = new ArrayList<Word>();
        list.add(new Word("First Note","28th Jan 2k18","00:20"));
        list.add(new Word("Second Note","28th Jan 2k18","00:26"));


        //ArrayAdapter<Word> adapter = new ArrayAdapter<Word>(getApplicationContext(),R.layout.list_item,list); Default Adapter

        GridView listView = findViewById(R.id.list_view);

        //Custom Adapter
        WordAdapter wordAdapter = new WordAdapter(this,list);

        listView.setAdapter(wordAdapter);
    }

    @Override
    public void onBackPressed() {

        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }
//Todo akldsjaslkklasjdlfksaf
    public void search(View view)
    {
        startActivity(new Intent(this,Search.class));
    }

    public void addnt(View view)  //Plus button Implementation
    {
        Intent intent = new Intent(this,NewNote.class );
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(NOTE_REQUEST_CODE, NEW_NOTE_REQUEST);

        startActivityForResult(intent, NEW_NOTE_REQUEST);

    }


    public static File getBackupPath() {
        return backupPath;
    }

    // Static method to return File at localPath
    public static File getLocalPath() {
        return localPath;
    }


}
