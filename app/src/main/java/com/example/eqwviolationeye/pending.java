package com.example.eqwviolationeye;

public class pending {
    private String date;
    private String time;

    // create constructor to set the values for all the parameters of the each single view
    public pending(String timestamp) {
        this.date = timestamp.substring(0,timestamp.indexOf(","));
        this.time = timestamp.substring(timestamp.lastIndexOf(",")+1);
    }

    // getter method for returning the ID of the imageview
    public String getDate() {
        return date;
    }
    public String getTime() {
        return time;
    }


}
