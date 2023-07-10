package com.example.spendsaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.f118326spendsaver.R;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddPage extends AppCompatActivity {

    DatabaseReference databaseReference;
    itemInfo itemData;
    ArrayList<String> itemGroup = new ArrayList<>();

    Button buttonAdd, buttonSave, buttonGroceryType;
    LinearLayout container;
    EditText selectedDate;
    String loggedUser, date, newGroceryType, currency;
    int idCount = 0;
    double totalCost = 0;
    DBHandler dbHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setNav();
        readInternalStorage();
        readItemGroup();

        // Setting up the screenS
        buttonAdd = (Button)findViewById(R.id.addbtn);
        buttonSave = (Button)findViewById(R.id.savebtn);
        buttonGroceryType = (Button)findViewById(R.id.groceryTypebtn);
        container = (LinearLayout)findViewById(R.id.container);
        selectedDate = (EditText) findViewById(R.id.selectDate);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        date = dateFormat.format(calendar.getTime());
        selectedDate.setText(date);

        dbHandler = new DBHandler(AddPage.this);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        // If the date has correct format and there is at least one item on the basket then it saves the shop
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idCount <= 0) {
                    Toast.makeText(AddPage.this, "Error: Add an item", Toast.LENGTH_SHORT).show();
                } else if (!selectedDate.getText().toString().matches("\\d{2}-\\d{2}-\\d{4}")) {
                    Toast.makeText(AddPage.this, "Error: Incorrect date format", Toast.LENGTH_SHORT).show();
                } else {
                    saveShop();
                }
            }
        });

        // Creates a pop up screen where the user can add a new grocery type
        buttonGroceryType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddPage.this);
                builder.setTitle("Input New Grocery Type:");
                EditText input = new EditText(AddPage.this);
                builder.setView(input);
                // saves the grocery type
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        newGroceryType = input.getText().toString();
                        saveGroceryType();
                    }
                });
                // closes pop up screen
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        });
    }

    // Saves the new grocery type in firebase (if the text input is not empty)
    public void saveGroceryType() {
        databaseReference = FirebaseDatabase.getInstance().getReference("itemGroup");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!newGroceryType.isEmpty()) {
                    databaseReference.child(newGroceryType).setValue(loggedUser);
                    Toast.makeText(AddPage.this, "Success: Reload the page", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddPage.this, "Error: Grocery type is empty", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    /*
    * The following function retrieves all the inflated layouts from the shopping basket.
    * For each input added to the view, it retrieves its ID and then gets its value on a string.
    * If neither inputs are empty, it will then add them to the firebase database.
    * If the item exists, it will read it from firebase and update the cost and numb of the item
    * If the total cost of the shop is £100 or over, it will notify the user
    * */
    public void saveShop() {
        dbHandler.deleteTable();
        for (int i = 0; i < idCount; i=i+3) {
            // Reading values from inputted items
            String idInt1 = String.valueOf(i);
            int name = getResources().getIdentifier(idInt1, "id", getPackageName());
            Spinner nametxt = (Spinner) findViewById(name);
            String nameInput = nametxt.getSelectedItem().toString();

            String idInt2 = String.valueOf(i+1);
            int count = getResources().getIdentifier(idInt2, "id", getPackageName());
            EditText counttxt = (EditText) findViewById(count);
            String countInput = counttxt.getText().toString();

            String idInt3 = String.valueOf(i+2);
            int cost = getResources().getIdentifier(idInt3, "id", getPackageName());
            EditText costtxt = (EditText) findViewById(cost);
            String costInput = costtxt.getText().toString();

            if (!nameInput.isEmpty() && !countInput.isEmpty() && !costInput.isEmpty()) {
                totalCost += (Integer.parseInt(countInput) * Double.parseDouble(costInput));
                // Adds item to the local database
                dbHandler.addShop(loggedUser, selectedDate.getText().toString(), nameInput, Integer.parseInt(countInput), Double.parseDouble(costInput));

                databaseReference = FirebaseDatabase.getInstance().getReference("itemInfo").child(loggedUser).child(selectedDate.getText().toString());
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        itemData = new itemInfo(loggedUser, selectedDate.getText().toString(), nameInput, Integer.parseInt(countInput), (Double.parseDouble(costInput) * Integer.parseInt(countInput)));

                        // Updating item
                        int oldNumb;
                        double oldCost;
                        if (snapshot.child(nameInput).exists() && snapshot.child(nameInput+"/username").getValue(String.class).equals(loggedUser)) {

                            oldCost = snapshot.child(nameInput).child("cost").getValue(double.class);
                            oldNumb = snapshot.child(nameInput).child("number").getValue(int.class);
                            double newCost = (Double.parseDouble(costInput) * Integer.parseInt(countInput)) + oldCost;
                            int newNumb = Integer.parseInt(countInput) + oldNumb;

                            itemData = new itemInfo(loggedUser, selectedDate.getText().toString(), nameInput, newNumb, newCost);
                        }
                        databaseReference.child(itemData.getItemName()).setValue(itemData);
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(AddPage.this, "Error: Fail to access data", Toast.LENGTH_SHORT).show();
                    }
                });

                // Once the loop is on its last run, it does the following
                if ((i+3) == idCount) {
                    Toast.makeText(AddPage.this, "Success: Shop saved", Toast.LENGTH_SHORT).show();

                    // Item count is cleared & container is emptied
                    idCount = 0;
                    container.removeAllViews();
                    if (totalCost >= 100) {
                        sendNotification();
                    }
                }
            } else {
                Toast.makeText(AddPage.this, "Error: Some items may be empty", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    public void shareShop() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(AddPage.this);
//        builder.setTitle("Share shop?");
//        CheckBox shareshopbox = new CheckBox(AddPage.this);
//        shareshopbox.setText("Don't show again.");
//        shareshopbox.setPadding(50, 50, 50, 50);
//        builder.setView(shareshopbox);
//        builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                if(shareshopbox.isChecked()) {
//                    shareshop = false;
//                }
//                Intent sendIntent = new Intent();
//                sendIntent.setAction(Intent.ACTION_SEND);
//                String itemsName = "";
//                double totalCost = 0;
//                for (itemInfo item : itemArr) {
//                    itemsName += item.getItemName() + ", ";
//                    totalCost += (item.getCost() * item.getNumber());
//                }
//                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey! Today I bought: "+itemsName+" with a value of £"+totalCost);
//                sendIntent.setType("text/plain");
//                Intent shareIntent = Intent.createChooser(sendIntent, null);
//                startActivity(shareIntent);
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                if(shareshopbox.isChecked()) {
//                    shareshop = false;
//                }
//                dialogInterface.cancel();
//            }
//        });
//        builder.show();
//    }


    /*
    * The following function inflates new rows to the view.
    * In order to later on retrieve each item, cost and numb, the id of each is set by an int which
    * can later on be easily looped through. This int is global on the "Add" page
    * */
    @SuppressLint("ResourceAsColor")
    public void addItem() {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View addView = layoutInflater.inflate(R.layout.datarow, null);

        // Setting dynamic id's
        Spinner nametxt = addView.findViewById(R.id.textout);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemGroup);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nametxt.setAdapter(arrayAdapter);
        nametxt.setId(idCount);
        idCount += 1;

        EditText numbtxt = addView.findViewById(R.id.textout2);
        numbtxt.setId(idCount);
        idCount += 1;

        EditText costtxt = addView.findViewById(R.id.textout3);
        costtxt.setHint(currency);
        costtxt.setId(idCount);
        idCount += 1;

        // When clicked, it removes the inflated row and decreased the id Count
        Button buttonRemove = (Button) addView.findViewById(R.id.remove);
        buttonRemove.setBackgroundResource(R.drawable.ic_baseline_delete_24);
        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idCount -= 3;
                ((LinearLayout) addView.getParent()).removeView(addView);
            }
        });
        container.addView(addView);
    }

    // The following function reads the item types from firebase and saves it in a global arrayList
    public void readItemGroup() {
        databaseReference = FirebaseDatabase.getInstance().getReference("itemGroup");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemGroup.add("");
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

    // Sends Notifications to user
    public void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                AddPage.this, "SpenderSaver")
                .setContentTitle("SpendSaver")
                .setContentText("You've spent £100 or more today! Keep an eye on your money.")
                .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("SpenderSaver", "spendersaver", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(0, builder.build());
        }
    }

    // Sets the navigation bar
    public void setNav() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.add);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.account:
                    startActivity(new Intent(getApplicationContext(), AccountPage.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.add:
                    return true;
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), HomePage.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });
    }

    // Reads internal storage as well as Shared Preferences. All costs are stored as Pounds, but
    // are later converted.
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
    }
}