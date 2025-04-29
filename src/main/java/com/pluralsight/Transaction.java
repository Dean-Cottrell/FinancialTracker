package com.pluralsight;

import java.time.LocalDate;
import java.time.LocalTime;

public class Transaction {
    protected final LocalDate date;
    protected final LocalTime time;
    protected final String description;
    protected final String vendor;
    protected final double amount;


    public Transaction(LocalDate date, LocalTime time, String description, String vendor, double amount) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }


    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
    public String getDescription() { return description; }
    public String getVendor() { return vendor; }
    public double getAmount() { return amount; }

    public boolean date() {
        return false;
    }

    public int amount() {
        return 0;
    }

    public Object time() {
        return null;
    }

    public Object description() {
        return null;
    }

    public Object vendor() {
        return null;
    }
}