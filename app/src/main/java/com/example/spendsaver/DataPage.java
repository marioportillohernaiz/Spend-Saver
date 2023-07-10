package com.example.spendsaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.f118326spendsaver.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

public class DataPage extends AppCompatActivity {
    String loggedUser, currency;
    double currecyChange = 1;
    itemInfo itemData;
    LinearLayout dataContainer;
    TextView datetxt;
    CurrencyLocalService localService;
    boolean serviceOutput = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        dataContainer = findViewById(R.id.dataContainer);

        // Back button takes you back to the Home screen
        Button backBtn = findViewById(R.id.backHomebtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentLogIn = new Intent(DataPage.this, HomePage.class);
                startActivity(intentLogIn);
            }
        });

        readInternalStorage();
        readData();
    }

    // The following functions retrieve the currency exchange from the Local Service
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, CurrencyLocalService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }
    @Override
    protected void onStop() {
        super.onStop();
        unbindService(conn);
        serviceOutput = false;
    }
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            CurrencyLocalService.LocalBinder binder = (CurrencyLocalService.LocalBinder) service;
            localService = binder.getService();
            serviceOutput = true;
            currecyChange = localService.getCurrencyChange();
            readData();
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            serviceOutput = false;
        }
    };

    // The following function reads all the shops stored for the logged in user and displays them
    // on the view
    public void readData() {
        dataContainer.removeAllViews();
        DecimalFormat df = new DecimalFormat("0.00");
        DatabaseReference rootDataRef = FirebaseDatabase.getInstance().getReference().child("itemInfo").child(loggedUser);
        rootDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int numb = 0;
                double cost = 0;
                String user, dateIn, name;

                // Loop through each different date
                for (DataSnapshot parentSnapshot : snapshot.getChildren()) {

                    // Loop through each item to check the user equals the user logged in
                    for (DataSnapshot childSnapshot : parentSnapshot.getChildren()) {
                        user = childSnapshot.child("username").getValue(String.class);
                        if (user.equals(loggedUser)) {
                            datetxt = new TextView(DataPage.this);
                            datetxt.setText(String.valueOf(parentSnapshot.getKey()));
                            datetxt.setTextColor(Color.BLACK);
                            dataContainer.addView(datetxt);
                            break;
                        }
                    }

                    // Loop through each item to output it on a view
                    for (DataSnapshot childSnapshot : parentSnapshot.getChildren()) { // Item Snapshot

                        user = childSnapshot.child("username").getValue(String.class);
                        dateIn = childSnapshot.child("date").getValue(String.class);
                        name = childSnapshot.child("itemName").getValue(String.class);
                        cost = childSnapshot.child("cost").getValue(double.class);
                        numb = childSnapshot.child("number").getValue(int.class);

                        if (user.equals(loggedUser)) {
                            itemData = new itemInfo(user, dateIn, name, numb, cost);

                            LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View addView2 = layoutInflater.inflate(R.layout.datalist, null);
                            TextView nametxt = addView2.findViewById(R.id.textin);
                            nametxt.setText(itemData.getItemName());
                            TextView currencytxt = addView2.findViewById(R.id.listcurrency);
                            currencytxt.setText(currency);
                            TextView costtxt = addView2.findViewById(R.id.textin2);
                            costtxt.setText(df.format(itemData.getCost() * currecyChange));
                            dataContainer.addView(addView2);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Reads internal storage as well as Shared Preferences.
    private void readInternalStorage() {
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

        SharedPreferences sp = getSharedPreferences("CurrencyChange", MODE_PRIVATE);
        currency = sp.getString("Currency", "");
        currency = "  " + currency;
    }
}