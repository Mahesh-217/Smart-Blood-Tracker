package com.example.mad_rv;

public class BloodBankCamp {
    private String campName;
    private String location;
    private String date;
    private String Phone;

    // Default constructor (required by Firebase)
    public BloodBankCamp() {
    }

    // Parameterized constructor
    public BloodBankCamp(String campName, String location, String date,String Phone) {
        this.campName = campName;
        this.location = location;
        this.date = date;
        this.Phone = Phone;
    }

    // Getter methods
    public String getCampName() {
        return campName;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }
    public String getPhone() {
        return Phone;
    }

    // Setter methods
    public void setCampName(String campName) {
        this.campName = campName;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public void setPhone(String Phone) {
        this.Phone = Phone;
    }
}
