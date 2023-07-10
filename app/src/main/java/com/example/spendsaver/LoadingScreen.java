package com.example.spendsaver;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.f118326spendsaver.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;


public class LoadingScreen extends AppCompatActivity {
    private int STORAGE_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        // If the permission hasnt been granted, it will request it, otherwise it will take the user
        // to the log in page or the home screen (if a user has previously logged in)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (checkPreviousUser() != null) {
                        Intent intentHomePage = new Intent(LoadingScreen.this, HomePage.class);
                        //intentLogIn.putExtra("Value", user);
                        startActivity(intentHomePage);
                    } else {
                        Intent intentLogIn = new Intent(LoadingScreen.this, LogInPage.class);
                        startActivity(intentLogIn);
                    }
                }
            }, 1000);
        } else {
            requestStoragePermission();
        }
    }

    // Checks the previous logged user
    private String checkPreviousUser() {
        FileInputStream fis;
        try {
            fis = openFileInput("loggedInAccount.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String user;
            user = br.readLine();
            return user;

        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Error: File Not Found", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this, "Error: IO", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return null;
    }

    // Request storage permission
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Storage Needed")
                    .setMessage("Permission for storage needed to keep using the app.")
                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(LoadingScreen.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .show();

        } else {
            ActivityCompat.requestPermissions(LoadingScreen.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Request Accepted", Toast.LENGTH_LONG).show();
                Intent intentLogIn = new Intent(LoadingScreen.this, LogInPage.class);
                startActivity(intentLogIn);
            }
        }
    }
}