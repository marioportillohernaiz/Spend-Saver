package com.example.spendsaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.example.f118326spendsaver.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LogInPage extends AppCompatActivity {

    EditText inLogIn, inPass;
    DatabaseReference rootDataRef;
    ArrayList<String> dataList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_page);

        // Sets up screen
        Button loginbtn = (Button) findViewById(R.id.loginbtn);
        Button signinbtn = (Button) findViewById(R.id.signinbtn);
        inLogIn = (EditText) findViewById(R.id.inLogIn);
        inPass = (EditText) findViewById(R.id.passtxt);

        rootDataRef = FirebaseDatabase.getInstance().getReference().child("userInfo");

        // Logs in user if the username & password are both: not empty & in the firebase database
        loginbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String un = inLogIn.getText().toString();
                String ps = inPass.getText().toString();
                final boolean[] loggin = {false};

                if (un.length() != 0 || ps.length() != 0) {

                    // Reads user from firebase
                    rootDataRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                userInfo user = dataSnapshot.getValue(userInfo.class);
                                if (user.getUsername().equals(un) && user.getPassword().equals(ps)) {
                                    loggin[0] = true;
                                }
                            }

                            if (loggin[0]) {
                                Toast.makeText(getApplicationContext(), "Loging In", Toast.LENGTH_SHORT).show();
                                savePreviousUser();
                                openHomePage();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error: wrong username or password", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Error: credentials empty", Toast.LENGTH_SHORT).show();
                }

            }
        });
        signinbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openSignInPage();
            }
        });
    }

    // Opens home page if credentials are correct
    public void openHomePage() {
        Intent intentLogIn = new Intent(this, UserGuide.class);
        startActivity(intentLogIn);
    }

    // Opens sing up page when button "Sign up" clicked
    public void openSignInPage() {
        Intent intentLogIn = new Intent(this, SignInPage.class);
        startActivity(intentLogIn);
    }

     /* If the user is able to log in, its username is stored in an internal textfile which is later
     * used along the whole up for various reasons (such as logging in or checking the data matches
     * the user
     * */
    public void savePreviousUser() {
        String usernameToSave = inLogIn.getText().toString();

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
}