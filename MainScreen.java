package com.notes.kt.kt;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import static com.notes.kt.kt.DataUtils.BACKUP_FILE_NAME;
import static com.notes.kt.kt.DataUtils.BACKUP_FOLDER_PATH;
import static com.notes.kt.kt.DataUtils.NEW_NOTE_REQUEST;
import static com.notes.kt.kt.DataUtils.NOTE_BODY;
import static com.notes.kt.kt.DataUtils.NOTE_REQUEST_CODE;
import static com.notes.kt.kt.DataUtils.NOTE_TITLE;
import static com.notes.kt.kt.DataUtils.deleteNotes;
import static com.notes.kt.kt.DataUtils.isExternalStorageReadable;
import static com.notes.kt.kt.DataUtils.isExternalStorageWritable;
import static com.notes.kt.kt.DataUtils.retrieveData;
import static com.notes.kt.kt.DataUtils.saveData;

public class MainScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AbsListView.MultiChoiceModeListener{
    JSONArray favNotes;
    private Toolbar toolbar;
    public ArrayList<Integer> charray = new ArrayList<Integer>();
    private boolean exit = false;
    static JSONArray notes;
    private static File localPath, backupPath;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private TextView usernm;
    private TextView email;
    private ImageView profileph;
    WordAdapter wordAdapter;
    private Toast toast;
    private DatabaseReference databaseReference;
    boolean backupSuccessful;
    boolean restoreSuccessful;
    private AlertDialog backupOKDialog,restoreFailedDialog;
    GridView listView;
    Menu mmenu;
    private boolean hideIcon= true;
    private View mView;
    private int pos;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mmenu = menu;
        getMenuInflater().inflate(R.menu.tools,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/Myriad.ttf");
        TextView textView = findViewById(R.id.app_bar_text);
        textView.setTypeface(typeface);

        navigationView = findViewById(R.id.activity_main_navigationView);

        toolbar = findViewById(R.id.main_screen_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
//        toolbar2 = findViewById(R.id.delete_toolbar);

        drawerLayout = findViewById(R.id.activity_main_drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpDrawerContent(navigationView);

        localPath = new File(getFilesDir() + "/" + "path.json");
        Log.v("app",localPath.getPath());

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
        if (tempNotes != null) {
            notes = tempNotes;
            Log.v("app","Data undi");
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Notes");
        //Experimenting with ArrayList
        addToList();
        Dialogs(this);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.settings)
                    Toast.makeText(getApplicationContext(),"Done with settings",Toast.LENGTH_SHORT).show();

                    if(item.getItemId()== R.id.logout){
                        FirebaseAuth.getInstance().signOut();
                    }


                return true;
            }
        });
    }

    protected void setUpDrawerContent(NavigationView navigationView) {
        Log.v("Note", "setUpDrawer");
        View header=navigationView.getHeaderView(0);


        //Get user details
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            Uri personPhoto = acct.getPhotoUrl();
            email =(TextView)header.findViewById(R.id.emailid);
            usernm = (TextView)header.findViewById(R.id.username);
            profileph=(ImageView)header.findViewById(R.id.dp);
            email.setText(personEmail);
            usernm.setText(personName);
            profileph.setImageURI(personPhoto);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        switch (item.getItemId()) {

            case R.id.backup:
                String id = databaseReference.push().getKey();
                databaseReference.child(id).setValue(id);
                if (notes.length() > 0)
                    backupSuccessful = saveData(backupPath, notes);
                if(backupSuccessful)
                {

                    toast = Toast.makeText(this,"Backup Successful",Toast.LENGTH_SHORT);
                    toast.show();
                    showBackupSuccessfulDialog();
                }
                else Toast.makeText(this,"BackUp dobbindi",Toast.LENGTH_SHORT).show();
                return true;


            case R.id.Restore:
                JSONArray tempNotes = retrieveData(backupPath);
                if (tempNotes != null) {
                    restoreSuccessful = saveData(localPath, tempNotes);
                    notes = tempNotes;
                    wordAdapter.notifyDataSetChanged();
                    addToList();
                    Toast.makeText(this, "Restore Successful" , Toast.LENGTH_SHORT).show();
                }
                else
                {
                    showRestoreFailedDialog();
                }
                return true;


            case R.id.floatingActionButton2:
                Intent intent = new Intent(getApplicationContext(),Search.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }


    protected void Dialogs(Context context)
    {
        // Dialog to display backup was successfully created in backupPath
        backupOKDialog = new AlertDialog.Builder(context)
                .setTitle("Backup Successful")
                .setMessage("Backup Created in" + " "
                        + backupPath.getAbsolutePath())
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

        //Dialog to display restore Unsuccessful
        restoreFailedDialog = new AlertDialog.Builder(context)
                .setTitle("Restore Failed")
                .setMessage("Restore failed")
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    // Method to dismiss backup check and show backup successful dialog
    protected void showBackupSuccessfulDialog() {
        backupOKDialog.show();
    }

    // Method to dismiss restore check and show restore failed dialog
    protected void showRestoreFailedDialog() {
        restoreFailedDialog.show();
    }


    private void addToList(){

        //String id = databaseReference.push().getKey();

        //ArrayAdapter<Word> adapter = new ArrayAdapter<Word>(getApplicationContext(),R.layout.list_item,list); Default Adapter

        wordAdapter = new WordAdapter(getApplicationContext(), notes);

        listView = findViewById(R.id.list_view);

        listView.setAdapter(wordAdapter);

        listView.setMultiChoiceModeListener(this);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        //TODO: test to CHOIICE_MODE_MULTIPLE theda

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(toast!=null)
                    toast.cancel();
                toast = Toast.makeText(getApplicationContext(),"U clicked on "+i,Toast.LENGTH_SHORT);
                toast.show();

                Intent intent = new Intent(getApplicationContext(),NewNote.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                try {
                    intent.putExtra(NOTE_TITLE, notes.getJSONObject(i).getString(NOTE_TITLE));
                    intent.putExtra(NOTE_BODY, notes.getJSONObject(i).getString(NOTE_BODY));
                }
                catch (Exception e) {

                }
                intent.putExtra(NOTE_REQUEST_CODE,i);
                startActivityForResult(intent,i);
                //view.setBackgroundColor(getResources().getColor(R.color.white));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            // Get extras
            Bundle mBundle = null;
            if (data != null)
                mBundle = data.getExtras();

            if (mBundle != null) {
                // If new note was saved
                if (requestCode == NEW_NOTE_REQUEST) {
                    JSONObject newNoteObject = null;

                    try {
                        // Add new note to array
                        newNoteObject = new JSONObject();
                        String title = mBundle.getString(NOTE_TITLE);
                        String body = mBundle.getString(NOTE_BODY);
                        newNoteObject.put(NOTE_TITLE, title);
                        newNoteObject.put(NOTE_BODY, body);
                        Log.wtf("app",mBundle.getString(NOTE_BODY));
                        notes.put(newNoteObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // If newNoteObject not null -> save notes array to local file and notify adapter
                    if (newNoteObject != null) {
                        wordAdapter.notifyDataSetChanged();

                        Boolean saveSuccessful = saveData(localPath, notes);

                        if (saveSuccessful) {
                            Log.wtf("app","Saved Successfully");
                        }
                    }
                }

                // If existing note was updated (saved)
                else {
                    JSONObject newNoteObject = null;

                    try {
                        // Update array item with new note data
                        newNoteObject = notes.getJSONObject(requestCode);
                        newNoteObject.put(NOTE_TITLE, mBundle.getString(NOTE_TITLE));
                        newNoteObject.put(NOTE_BODY, mBundle.getString(NOTE_BODY));
                        // Update note at position 'requestCode'
                        notes.put(requestCode, newNoteObject);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // If newNoteObject not null -> save notes array to local file and notify adapter
                    if (newNoteObject != null) {
                        wordAdapter.notifyDataSetChanged();

                        Boolean saveSuccessful = saveData(localPath, notes);

                        if (saveSuccessful) {
                            Log.wtf("app","Saved Successfully updation");
                        }
                    }
                }
            }
        }

        else if (resultCode == RESULT_CANCELED) {
            Bundle mBundle = null;

            // If data is not null, has "request" extra and is new note -> get extras to bundle
            if (data != null && data.hasExtra("request") && requestCode == NEW_NOTE_REQUEST) {
                mBundle = data.getExtras();

                // If new note discarded -> toast empty note discarded
                if (mBundle != null && mBundle.getString("request").equals("discard")) {
                    Log.wtf("app","We became fruit");
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.logout)
        {
            Toast.makeText(getApplicationContext(), "Worked" , Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    //TODO: geluku geluku geluku
//    protected void newNoteButtonVisibility(boolean isVisible) {
//        if (isVisible) {
//            newNote.animate().cancel();
//            newNote.animate().translationY(newNoteButtonBaseYCoordinate);
//        } else {
//            newNote.animate().cancel();
//            newNote.animate().translationY(newNoteButtonBaseYCoordinate + 500);
//        }
//    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

        if (checked) {
            charray.add(position);
        }
        else {
            int index = -1;

            for (int i = 0; i < charray.size(); i++) {
                if (position == charray.get(i)) {
                    index = i;
                    break;
                }
            }

            if (index != -1)
                charray.remove(index);
        }
        Log.wtf("app","Length: "+charray.size());

        // Set Toolbar title to 'x Selected'
        //mode.setTitle(charray.size() + " " + getString(R.string.action_delete_selected_number));
        wordAdapter.notifyDataSetChanged();

    }



    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        getMenuInflater().inflate(R.menu.delete_menu,menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.deleteicon:

                new AlertDialog.Builder(this)
                        .setTitle("Delete")
                        .setMessage("You can get this back on restore session")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                notes = deleteNotes(notes, charray);

                                wordAdapter = new WordAdapter(getApplicationContext(), notes);
                                listView.setAdapter(wordAdapter);

                                if (saveData(localPath, notes)) {
                                    Toast.makeText(getApplicationContext(), "Updated!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;


            case R.id.fav:
                favourite();
                Toast.makeText(getApplicationContext(), "Favourite"+favNotes.length(), Toast.LENGTH_SHORT).show();
                return true;
            default:
                return true;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        hideIcon = true;
        invalidateOptionsMenu();
        charray = new ArrayList<Integer>();
        wordAdapter.notifyDataSetChanged();
    }

    private void favourite() {
        favNotes = new JSONArray();

        for(int i=0;i<notes.length();i++) {
            if(charray.contains(i)) {
                try {
                    favNotes.put(notes.get(i));
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}