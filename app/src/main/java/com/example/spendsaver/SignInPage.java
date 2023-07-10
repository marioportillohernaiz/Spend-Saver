package com.example.spendsaver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.f118326spendsaver.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class SignInPage extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    userInfo userData;

    EditText usernametxt, passinput, repeatpassinput;
    RadioButton accType;
    RadioGroup radGroup;
    Button signupbtn, backbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_page);

        // Sets up the screen
        signupbtn = (Button) findViewById(R.id.signupbtn);
        backbtn = (Button) findViewById(R.id.backbtn);
        usernametxt = (EditText) findViewById(R.id.usernameinput);
        passinput = (EditText) findViewById(R.id.passinput);
        repeatpassinput = (EditText) findViewById(R.id.repeatpassinput);
        radGroup = findViewById(R.id.radioGroup);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("userInfo");

        // Button takes you back to the log in screen
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentLogIn = new Intent(SignInPage.this, LogInPage.class);
                startActivity(intentLogIn);
            }
        });

        // Button retrieves all the input texts and checks that the inputs are not empty and the
        // repeated passwords equal
        signupbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String user = usernametxt.getText().toString();
                String passw1 = passinput.getText().toString();
                String passw2 = repeatpassinput.getText().toString();

                int radioId = radGroup.getCheckedRadioButtonId();
                accType = findViewById(radioId);
                String type = accType.getText().toString();

                if (TextUtils.isEmpty(user) && TextUtils.isEmpty(passw1) && TextUtils.isEmpty(passw2) && TextUtils.isEmpty(type)) {
                    Toast.makeText(SignInPage.this, "Error: Empty inputs", Toast.LENGTH_LONG).show();
                } else {
                    if (passw1.equals(passw2)) {
                        signIn(user, passw1, type);
                    } else {
                        Toast.makeText(SignInPage.this, "Error: Passwords are not equal", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    // The following function firstly checks that the user exists, if it doesnt then it creats a
    // userInfo java class and adds it to the firebase
    public void signIn(String username, String password, String type) {
        userData = new userInfo();
        userData.setUsername(username);
        userData.setPassword(password);
        userData.setAccType(type);
        final boolean[] userExists = {false};

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userInfo user = dataSnapshot.getValue(userInfo.class);
                    if (user.getUsername().equals(username)) {
                        userExists[0] = true;
                    }
                }

                if (!userExists[0]) {
                    databaseReference.child(username).setValue(userData);
                    savePreviousUser();
                    openHomePage();
                    Toast.makeText(SignInPage.this, "Success: Log In", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignInPage.this, "Error: User already exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(SignInPage.this, "Error: Fail to add data " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* If the user is able to log in, its username is stored in an internal textfile which is later
     * used along the whole up for various reasons (such as logging in or checking the data matches
     * the user
     * */
    public void savePreviousUser() {
        String usernameToSave = usernametxt.getText().toString();

        try {
            File root = new File(getFilesDir(), "SpendSaver");
            if (!root.exists()){root.mkdir();} // Creates new directory

            FileOutputStream fos = openFileOutput("loggedInAccount.txt", Context.MODE_PRIVATE);
            fos.write(usernameToSave.getBytes());
            fos.close();

        } catch (IOException e) {
            Toast.makeText(this, "Error: Write to file", Toast.LENGTH_LONG).show();
        }
    }

    // Opens the home page once
    public void openHomePage() {
        Intent intentLogIn = new Intent(this, HomePage.class);
        startActivity(intentLogIn);
    }
}