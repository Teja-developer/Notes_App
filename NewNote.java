package com.notes.kt.kt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import yuku.ambilwarna.AmbilWarnaDialog;

import static android.widget.Toast.makeText;
import static com.notes.kt.kt.DataUtils.NEW_NOTE_REQUEST;
import static com.notes.kt.kt.DataUtils.NOTE_BODY;
import static com.notes.kt.kt.DataUtils.NOTE_COLOUR;
import static com.notes.kt.kt.DataUtils.NOTE_REQUEST_CODE;
import static com.notes.kt.kt.DataUtils.NOTE_TITLE;

public class NewNote extends AppCompatActivity {

    int mDefaultColor;
    ImageView mButton;
    EditText mbEdit;
    EditText mhEdit;
    private InputMethodManager imm;
    private AlertDialog saveDialog;
    private Bundle bundle;
    ImageView mSave;
    ImageButton voice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        mbEdit = (EditText) findViewById(R.id.bodyEdit);
        mDefaultColor = ContextCompat.getColor(NewNote.this, R.color.colorPrimary);
        mButton = (ImageView) findViewById(R.id.colorch);
        mSave = (ImageView) findViewById(R.id.save);
        mhEdit = (EditText)findViewById(R.id.heading);
        imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });


        bundle = getIntent().getExtras();
        if (bundle != null) {
            // If current note is not new -> initialize colour, font, hideBody and EditTexts
            if (bundle.getInt(NOTE_REQUEST_CODE) != NEW_NOTE_REQUEST) {
                mhEdit.setText(bundle.getString(NOTE_TITLE));
                mbEdit.setText(bundle.getString(NOTE_BODY));
            }

            // If current note is new -> request keyboard focus to note title and show keyboard
            else if (bundle.getInt(NOTE_REQUEST_CODE) == NEW_NOTE_REQUEST) {
                mhEdit.requestFocus();
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }


        }

        initDialogs(this);
    }


    public void openColorPicker()
    {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, mDefaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                mDefaultColor = color;
                mbEdit.setTextColor(mDefaultColor);
            }
        });
        colorPicker.show();
    }



    public void backs(View view)
    {
        startActivity(new Intent(this, MainScreen.class));
    }

    public void opcamera(View view)
    {
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),0);
    }

    public void opengll(View view) {  //Opens gallery
        // TODO Auto-generated method stub
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    public void save(View view)
    {
        saveChanges();
    }

    //TODO Error Dialog  for saving.....................
    protected void initDialogs(Context context)
    {
        saveDialog = new AlertDialog.Builder(context)
                .setMessage("Save Changes?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(!isEmpty(mhEdit) || !isEmpty(mbEdit))
                            saveChanges();
                        else
                            toastEditTextCannotBeEmpty();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (bundle != null && bundle.getInt(NOTE_REQUEST_CODE) ==
                                NEW_NOTE_REQUEST) {

                            Intent intent = new Intent();
                            intent.putExtra("request", "discard");

                            setResult(RESULT_CANCELED, intent);

                            imm.hideSoftInputFromWindow(mhEdit.getWindowToken(), 0);

                            dialog.dismiss();
                            finish();
                            overridePendingTransition(0, 0);
                        }
                    }
                })
                .create();

    }

    protected void saveChanges()
    {
        Intent intent =new Intent();

        intent.putExtra(NOTE_TITLE,mhEdit.getText().toString());
        intent.putExtra(NOTE_BODY,mbEdit.getText().toString());

        setResult(RESULT_OK,intent);
        imm.hideSoftInputFromInputMethod(mhEdit.getWindowToken(),0);

        finish();
        overridePendingTransition(0,0);
    }

    @Override
    public void onBackPressed() {

        if(bundle.getInt(NOTE_REQUEST_CODE)==NEW_NOTE_REQUEST)
            saveDialog.show();
        else{
            if(!isEmpty(mhEdit) || !isEmpty(mbEdit))
            {
                if(!(mhEdit.getText().toString().equals(bundle.getString(NOTE_TITLE)))||
                        !(mbEdit.getText().toString().equals(bundle.getString(NOTE_BODY))))
                {
                    saveChanges();
                }

                else {
                    imm.hideSoftInputFromWindow(mhEdit.getWindowToken(), 0);

                    finish();
                    overridePendingTransition(0, 0);
                }
            }
            else
                toastEditTextCannotBeEmpty();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!hasFocus)
            if (imm != null && mhEdit != null)
                imm.hideSoftInputFromWindow(mhEdit.getWindowToken(), 0);
    }

    protected boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    protected void toastEditTextCannotBeEmpty() {
        Toast toast = makeText(getApplicationContext(),"Title Cannot Be Empty", Toast.LENGTH_SHORT);
        toast.show();
    }
}
