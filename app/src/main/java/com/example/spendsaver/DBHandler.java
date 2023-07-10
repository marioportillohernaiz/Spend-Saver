package com.example.spendsaver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

// Database handler used to store and read the internal database

public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "localStorageSQL.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "latestShop";
    private static final String USER = "user";
    private static final String DATE = "date";
    private static final String ITEM_NANE = "name";
    private static final String NUMB_ITEMS = "numb";
    private static final String COST_ITEM = "cost";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + USER + " TEXT,"
                + DATE + " TEXT,"
                + ITEM_NANE + " TEXT,"
                + NUMB_ITEMS + " TEXT,"
                + COST_ITEM + " TEXT )";
        db.execSQL(query);
    }

    public void addShop(String userName, String date, String itemName, int numbItems, double costItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(USER, userName);
        values.put(DATE, date);
        values.put(ITEM_NANE, itemName);
        values.put(NUMB_ITEMS, numbItems);
        values.put(COST_ITEM, costItem);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

//    public ArrayList<itemInfo> readShop() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
//        ArrayList<itemInfo> itemArr = new ArrayList<>();
//
//        if (cursorCourses.moveToFirst()) {
//            do {
//                itemArr.add(new itemInfo(cursorCourses.getString(1),
//                        cursorCourses.getString(4),
//                        cursorCourses.getString(2),
//                        cursorCourses.getString(3)));
//            } while (cursorCourses.moveToNext());
//            // moving our cursor to next.
//        }
//        // at last closing our cursor
//        // and returning our array list.
//        cursorCourses.close();
//        return courseModalArrayList;
//    }

    public void deleteTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
