package com.notes.kt.kt;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInAct extends AppCompatActivity {

    public static final String CHAT_PREFS = "ChatPrefs";
    public static final String DISPLAY_NAME_KEY = "username";
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button reg = findViewById(R.id.register);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.pass1);
        mConfirmPasswordView = (EditText) findViewById(R.id.pass2);

        mAuth = FirebaseAuth.getInstance();
    }


    private void attemptRegistration() {

        Log.wtf("app","Reached attemptRegistration");
        // Reset errors displayed in the form.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
//        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
//            mPasswordView.setError(getString(R.string.error_invalid_password));
//            focusView = mPasswordView;
//            cancel = true;
//        }
        createFireBaseUser();
    }

    private boolean isPasswordValid(String password) {

        return true;
    }

    private boolean isEmailValid(String email) {
        boolean b =  email.contains("@") || email.contains("gmail.com") || email.contains("yahoo.com") || email.contains("outlook");
        return b;
    }

    public void register(View v) {
        Intent intent = new Intent(this, MainScreen.class);
        startActivity(intent);
    }

    private  void createFireBaseUser() {
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        Log.wtf("app","Reached createFirebaseUser() "+email+" "+password);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.wtf("app", " createUser onComplete: " + task.isSuccessful());

                if (!task.isSuccessful()) {
                    Log.wtf("app", "user creation failed");
                    showErrorDialog("Registration attempt failed");
                } else {
                    Log.wtf("app","Successful");
                    saveDisplayName();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    finish();
                    startActivity(intent);
                }

            }
        });
    }
    private void saveDisplayName(){
        String displayName = mEmailView.getText().toString();
        SharedPreferences prefs = getSharedPreferences(CHAT_PREFS,0);
        prefs.edit().putString(DISPLAY_NAME_KEY, displayName).apply();
    }


    private void showErrorDialog(String message){

        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }
}