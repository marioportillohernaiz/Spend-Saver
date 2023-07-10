package com.example.spendsaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.f118326spendsaver.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class AccountPage extends AppCompatActivity {

    String loggedUser;
    DatabaseReference rootDataRef;
    TextView usertxtview, acctxtview;
    ShareActionProvider shareAP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        setNav();
        loadAccountDetails();

        // Gets the Shared Preferences for the currency change
        SharedPreferences sp = getSharedPreferences("CurrencyChange", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sp.edit();

        // Setting up the screen
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account");
        Button logoutbtn = findViewById(R.id.logoutbtn);
        Button openExternal = findViewById(R.id.openExternalPage);
        Button openUserGuide = findViewById(R.id.userguidebtn);
        usertxtview = findViewById(R.id.usernametxtview);
        acctxtview = findViewById(R.id.acctypetxtview);
        Spinner currencySpinner = findViewById(R.id.currencySpinner);

        // When a currency is selected on the spinner, it will update the Shared Preferences over the whole app
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String currencySelected = currencySpinner.getSelectedItem().toString();
                if (currencySelected.equals("$") || currencySelected.equals("£") || currencySelected.equals("€")) {
                    myEdit.putString("Currency", currencySelected);
                    myEdit.commit();
                    Toast.makeText(AccountPage.this, "Success: Currency saved", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        // Opens the user guide
        openUserGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentLogIn = new Intent(AccountPage.this, UserGuide.class);
                startActivity(intentLogIn);
            }
        });

        // Opens external app, Google, with a guide on how to save money
        openExternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri openUrl = Uri.parse("https://www.hsbc.co.uk/savings/everyday-spending-hacks/");
                Intent intent = new Intent(Intent.ACTION_VIEW, openUrl);
                startActivity(intent);
            }
        });

        // When clicked, the internal text file will be emptied so that it doesn't automatically log in
        // The log in page will be opened after that
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String usernameToSave = "";
                try {
                    File root = new File(getFilesDir(), "SpendSaver");
                    if (!root.exists()){root.mkdir();} // Creates new directory

                    FileOutputStream fos = openFileOutput("loggedInAccount.txt", Context.MODE_PRIVATE);
                    fos.write(usernameToSave.getBytes());
                    fos.close();

                    Intent intentLogIn = new Intent(AccountPage.this, LogInPage.class);
                    startActivity(intentLogIn);

                } catch (IOException e) {
                    Toast.makeText(AccountPage.this, "Error: Couldn't Log out, try again", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Creates Shared Preferences in order to share a simple text, shown below.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_action_provider, menu);
        MenuItem item = menu.findItem(R.id.share_action);
        shareAP = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "Hey! Want to use an amazing money management app? Jump on the PlayStore and search 'SpendSaver'");
        intent.setType("text/plain");

        Intent chooser = Intent.createChooser(intent, "Share via:");
        shareAP.setShareIntent(chooser);
        return true;
    }

    // Sets the navigation bar. Either clicking the home or add page, will open each screen respectively
    public void setNav() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.account);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), HomePage.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.account:
                    return true;
                case R.id.add:
                    startActivity(new Intent(getApplicationContext(), AddPage.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });
    }

    public void loadAccountDetails() {
        // Loading internal text file to read the username of the user logged in
        FileInputStream fis;
        try {
            fis = openFileInput("loggedInAccount.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String user;
            user = br.readLine();
            loggedUser = user;

        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Error: File Not Found", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this, "Error: IO", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        // Retrieving account data from the user logged in and displaying it on the account screen
        rootDataRef = FirebaseDatabase.getInstance().getReference().child("userInfo");
        rootDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userInfo user = dataSnapshot.getValue(userInfo.class);
                    if (user.getUsername().equals(loggedUser)) {
                        usertxtview.setText(user.getUsername());
                        acctxtview.setText(user.getAccType());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}