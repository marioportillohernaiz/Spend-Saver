package com.example.spendsaver;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;


import com.example.f118326spendsaver.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

public class HomePage extends AppCompatActivity {
    String loggedUser, month, year, currency = "£";
    double currecyChange = 1;
    PieChart pieChart;
    DatabaseReference rootDataRef;
    itemInfo itemData;
    ArrayList<itemInfo> itemArr = new ArrayList<>();
    ArrayList<String> itemGroup = new ArrayList<>();
    LinearLayout homeContainer;
    TextView homeTotalCost;

    CurrencyLocalService localService;
    boolean serviceOutput = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        // Setting up the screen
        homeContainer = (LinearLayout) findViewById(R.id.listContainer);
        Button calendarBtn = (Button) findViewById(R.id.calendarbtn);
        Button dataBtn = (Button) findViewById(R.id.databtn);
        Button infobtn = (Button) findViewById(R.id.homeinfobtn);

        // Opens the calendar screen
        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), OverallData.class));
            }
        });

        // Opens the over all data screen
        dataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DataPage.class));
            }
        });

        // Opens a pop-up screen which displays information useful to the user
        infobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomePage.this);
                builder.setTitle("Information about Grocery Types");
                TextView output = new TextView(HomePage.this);
                output.setText("When using the app, you will be prompted to enter a selection of different types of groceries. " +
                        "These categories are designed to make it easier for you to find the items you're looking for and to help you navigate the app more efficiently.\n" +
                        "Some common categories you might see the app include:\n\n" +
                        "1. Meats - any sort of meats such as chicken breats or minced meat.\n" +
                        "2. Fruit & Vegtables - such as appples or cabbage.\n" +
                        "3. Other foods or Drinks - such as rice or flour\n" +
                        "4. Snacks - such as chocolate or fizzy drinks\n" +
                        "5. Clothing & Accessories - such as trousers or glasses.\n" +
                        "6. Electronics - such as phones or speakers.\n" +
                        "7. Health & Wellness - such as gym membership or skin routines\n" +
                        "8. Other - any other purchase.\n\n" +
                        "You will be able to add more grocery types in the 'Add' page.");
                output.setTextColor(Color.BLACK);
                output.setPadding(50, 50, 50, 50);
                output.setScroller(new Scroller(HomePage.this));
                output.setVerticalScrollBarEnabled(true);
                output.setMovementMethod(new ScrollingMovementMethod());
                builder.setView(output);
                builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        });

        // Finishing setting up the home screen
        setNav();
        readInternalStorage();
        readItemGroup();
        readData();
    }

    // Reads all the item types from the firebase database and stores them on a global arraylist
    public void readItemGroup() {
        DatabaseReference rootData = FirebaseDatabase.getInstance().getReference("itemGroup");
        rootData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    // Adds item type if its for everyone ("true") or if its the users item type
                    if (ds.getValue().toString().equals("true") || ds.getValue().toString().equals(loggedUser)) {
                        itemGroup.add(ds.getKey());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Reads data from the database which is then displayed on the pie chart
    public void readData() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        month = dateFormat.format(calendar.getTime());

        dateFormat = new SimpleDateFormat("yyyy");
        year = dateFormat.format(calendar.getTime());

        dateFormat = new SimpleDateFormat("MMMM");
        TextView displayMonth = (TextView) findViewById(R.id.textView12);
        displayMonth.setText("Month: " + dateFormat.format(calendar.getTime()));

        rootDataRef = FirebaseDatabase.getInstance().getReference("itemInfo"); //here.child()
        rootDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int numb = 0;
                double cost = 0;
                String user, dateIn, name;
                // Loops through each date
                for (DataSnapshot parentSnapshot : snapshot.child(loggedUser).getChildren()) {

                    // Loop through each item
                    for (DataSnapshot childSnapshot : parentSnapshot.getChildren()) { // Item Snapshot
                        dateIn = childSnapshot.child("date").getValue(String.class);

                        if (dateIn.charAt(3) == month.charAt(0) && dateIn.charAt(4) == month.charAt(1) && dateIn.contains(year)) {
                            user = childSnapshot.child("username").getValue(String.class);
                            dateIn = childSnapshot.child("date").getValue(String.class);
                            name = childSnapshot.child("itemName").getValue(String.class);
                            cost = childSnapshot.child("cost").getValue(double.class);
                            numb = childSnapshot.child("number").getValue(int.class);

                            itemData = new itemInfo(user, dateIn, name, numb, cost);
                            itemArr.add(itemData);
                        }
                    }
                }

                // Sets up the pie chart
                displayPieChart();
                displayDataList();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

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
            displayDataList();
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            serviceOutput = false;
        }
    };

    // Creates the piechart and adds values to it from the item array list
    public void displayPieChart() {
        String chartTitle = "Expenses";
        pieChart = findViewById(R.id.homePieChart);
        double itemPercentage, totalCost = 0, itemSum = 0;

        // Loop through each item in the database to get the total cost
        for (itemInfo item : itemArr) {
            totalCost += item.getCost();
        }

        // Loop through each item group in the database to get the individual values
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (String group : itemGroup) {
            itemSum = 0;
            if (itemArr.isEmpty()) {
                chartTitle = "NO DATA ADDED (add a shop on the 'Add' page to display graph)";
            } else {
                for (itemInfo item : itemArr) {
                    if (group.equals(item.getItemName()) && item.getUsername().equals(loggedUser)) {
                        itemSum += item.getCost();
                    }
                }
            }

            if (itemSum != 0) {
                itemPercentage = itemSum / totalCost;
                entries.add(new PieEntry((float) itemPercentage, group));
            }
        }

        ArrayList<Integer> colors = new ArrayList<>();
        for (int color: ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }
        for (int color: ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expenses");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();
        pieChart.animateY(800, Easing.EaseInOutQuad);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(13);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText(chartTitle);
        pieChart.setCenterTextSize(15);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
    }

    // Displays the list of items
    public void displayDataList() {
        double totalCost = 0;
        homeTotalCost = findViewById(R.id.homeTotaltxt);
        homeContainer.removeAllViews();
        DecimalFormat df = new DecimalFormat("0.00");
        double itemSum;
        // Loops through each item group and groups them together with their cost
        for (String group: itemGroup) {
            itemSum = 0;
            for (itemInfo item : itemArr) {
                if (group.equals(item.getItemName()) && item.getUsername().equals(loggedUser)) {
                    itemSum += item.getCost();
                }
            }

            // If there is a sum of the item type, it will inflate a view and display it
            if (itemSum != 0) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView2 = layoutInflater.inflate(R.layout.datalist, null);

                TextView nametxt = addView2.findViewById(R.id.textin);
                nametxt.setText(group);
                TextView currencytxt = addView2.findViewById(R.id.listcurrency);
                currencytxt.setText(currency);
                TextView costtxt = addView2.findViewById(R.id.textin2);
                costtxt.setText(df.format(itemSum * currecyChange));
                homeContainer.addView(addView2);

                totalCost += itemSum * currecyChange;
            }
        }

        homeTotalCost.setText("Total:" + currency + String.valueOf(df.format(totalCost)));
    }

    // Sets the navigation bar
    public void setNav() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.add:
                    startActivity(new Intent(getApplicationContext(), AddPage.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.home:
                    return true;
                case R.id.account:
                    startActivity(new Intent(getApplicationContext(), AccountPage.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
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