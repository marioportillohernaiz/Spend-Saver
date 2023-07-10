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
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.f118326spendsaver.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class OverallData extends AppCompatActivity {
    String loggedUser, year, currency;
    double currecyChange = 1;
    BarChart barChart;
    DatabaseReference rootDataRef;
    itemInfo itemData;
    ArrayList<itemInfo> itemArr = new ArrayList<>();
    ArrayList<String> itemGroup1 = new ArrayList<>();
    ArrayList<BarEntry> barArr = new ArrayList<>();
    LinearLayout overallContainer;
    TextView displayMonth, totalCosttxt;
    CurrencyLocalService localService;
    boolean serviceOutput = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overall_data);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        // Sets up the screen
        overallContainer = (LinearLayout) findViewById(R.id.listYearContainer);

        Button backBtn = (Button) findViewById(R.id.backHomebtn);
        Button lastbtn = (Button) findViewById(R.id.lastyear);
        Button nextbtn = (Button) findViewById(R.id.nextyear);

        // Button takes user back to home screen
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentLogIn = new Intent(OverallData.this, HomePage.class);
                startActivity(intentLogIn);
            }
        });

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        year = dateFormat.format(calendar.getTime());

        displayMonth = findViewById(R.id.textView22);
        displayMonth.setText(year);

        // When holding down the button to the left of the year graph, it will rapidly decrease the years
        lastbtn.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 500);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    int newYear = Integer.parseInt(displayMonth.getText().toString()) - 1;
                    if (newYear > 1900) {
                        changeDate(newYear);
                    }
                    mHandler.postDelayed(this, 100);
                }
            };
        });

        // Decreases the year by 1
        lastbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newYear = Integer.parseInt(displayMonth.getText().toString()) - 1;
                if (newYear < 3000) {
                    changeDate(newYear);
                }
            }
        });

        // When holding down the button to the right of the year graph, it will rapidly increase the years
        nextbtn.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 500);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    int newYear = Integer.parseInt(displayMonth.getText().toString()) + 1;
                    changeDate(newYear);
                    mHandler.postDelayed(this, 100);
                }
            };
        });
        // Increases the year by 1
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newYear = Integer.parseInt(displayMonth.getText().toString()) + 1;
                changeDate(newYear);
            }
        });

        // Finishes setting up the screen
        readInternalStorage();
        readItemGroup();
        readData();
    }

    // Changes the date of the graph and thus updates it
    public void changeDate(int year) {
        displayMonth.setText(String.valueOf(year));
        itemArr.clear();
        overallContainer.removeAllViews();
        readData();
        displayDataList();
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

            displayBarChart();
            setUpBarChart();
            displayDataList();
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            serviceOutput = false;
        }
    };

    // The following function reads data from firebase and stores it in an array list
    public void readData() {
        rootDataRef = FirebaseDatabase.getInstance().getReference().child("itemInfo").child(loggedUser);
        rootDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int numb = 0;
                double cost = 0;
                String user, dateIn, name;
                for (DataSnapshot parentSnapshot : snapshot.getChildren()) { // Date Snapshot
                    for (DataSnapshot childSnapshot : parentSnapshot.getChildren()) { // Item Snapshot
                        dateIn = childSnapshot.child("date").getValue(String.class);

                        if (dateIn.contains(displayMonth.getText())) {
                            user = childSnapshot.child("username").getValue(String.class);
                            dateIn = childSnapshot.child("date").getValue(String.class);
                            name = childSnapshot.child("itemName").getValue(String.class);
                            cost = childSnapshot.child("cost").getValue(double.class);
                            numb = childSnapshot.child("number").getValue(int.class);

                            if (user.equals(loggedUser)) {
                                itemData = new itemInfo(user, dateIn, name, numb, cost);
                                itemArr.add(itemData);
                            }
                        }
                    }
                }

                displayBarChart();
                setUpBarChart();
                displayDataList();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Sets up barchart
    public void setUpBarChart() {
        BarDataSet barDataSet = new BarDataSet(barArr, "");
        BarData barData = new BarData(barDataSet);
        barChart.invalidate();
        barChart.refreshDrawableState();
        barChart.setData(barData);
        barChart.animateY(800, Easing.EaseInOutQuad);
        barChart.getAxisLeft().setDrawLabels(false);
        barChart.getAxisRight().setDrawLabels(false);
        barChart.getDescription().setText("");
        barChart.getXAxis().setLabelCount(12);

        String[] xAxisLables = new String[] {"", "Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};

        barDataSet.setColor(Color.parseColor("#DECB89"));
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(12f);

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLables));
    }

    // Displays bar chart with the values from the item array list
    public void displayBarChart() {
        barChart = findViewById(R.id.homeBarChart);
        double totalCost = 0;
        String compareDate = "";
        barArr = new ArrayList<>();

        for (int i = 1; i<13; i++) {
            totalCost = 0;
            if (i<10) {
                compareDate = "0" + String.valueOf(i);
            } else {
                compareDate = String.valueOf(i);
            }

            for (itemInfo item : itemArr) {
                if (item.getDate().charAt(3) == compareDate.charAt(0) && item.getDate().charAt(4) == compareDate.charAt(1) && item.getUsername().equals(loggedUser)) {
                    totalCost += item.getCost();
                }
            }

            barArr.add(new BarEntry((float) i, (float) (totalCost * currecyChange)));
        }
    }

    // Displays data list below the bar graph
    public void displayDataList() {
        overallContainer.removeAllViews();
        double itemSum = 0, totalSum = 0;
        totalCosttxt = findViewById(R.id.overallTotaltxt);
        DecimalFormat df = new DecimalFormat("0.00");
        for (String group: itemGroup1) {
            itemSum = 0;
            for (itemInfo item : itemArr) {
                if (group.equals(item.getItemName()) && item.getUsername().equals(loggedUser)) {
                    itemSum += item.getCost();
                }
            }

            if (itemSum != 0) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView3 = layoutInflater.inflate(R.layout.datalist, null);

                TextView nametxt = (TextView) addView3.findViewById(R.id.textin);
                nametxt.setText(group);
                TextView currencytxt = addView3.findViewById(R.id.listcurrency);
                currencytxt.setText(currency);
                TextView costtxt = (TextView) addView3.findViewById(R.id.textin2);
                costtxt.setText(df.format(itemSum * currecyChange));
                overallContainer.addView(addView3);

                totalSum += itemSum * currecyChange;
            }
        }

        totalCosttxt.setText("Total:" + currency + String.valueOf(df.format(totalSum)));
    }

    // Reads item types from firebase so that it can group data displayed by each type
    public void readItemGroup() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("itemGroup");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.getValue().toString().equals("true") || ds.getValue().toString().equals(loggedUser)) {
                        itemGroup1.add(ds.getKey());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Reads internal storage as well as Shared Preferences.s
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