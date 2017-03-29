package com.a.n.smartgym.Quary;

import java.sql.Date;

/**
 * Created by nirb on 29/03/2017.
 */

public class DailyAvrage {

    private String fname;
    private String date;
    private double avrage;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }


    public double getAvrage() {
        return avrage;
    }

    public void setAvrage(double avrage) {
        this.avrage = avrage;
    }
}
