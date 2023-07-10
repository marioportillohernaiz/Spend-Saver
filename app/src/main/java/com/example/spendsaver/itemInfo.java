package com.example.spendsaver;

// Java class to group item information retrieved from firebase database

public class itemInfo  {
    private String username;
    private String date;
    private String item;
    private int number;
    private double cost;

    public itemInfo(String username, String date, String item, int number, double cost) {
        this.username = username;
        this.date = date;
        this.item = item;
        this.number = number;
        this.cost = cost;
    }

    public String getUsername() {
        return username;
    }
    public String getDate() {
        return date;
    }
    public String getItemName() {
        return item;
    }
    public int getNumber() {
        return number;
    }
    public double getCost() {
        return cost;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setItemName(String item) {
        this.item = item;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }
//
//    @Override
//    public String compareTo(itemInfo item) {
//        return  item.cost - this.cost);
//    }
}
