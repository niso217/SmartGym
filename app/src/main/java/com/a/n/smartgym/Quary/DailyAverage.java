package com.a.n.smartgym.Quary;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nirb on 29/03/2017.
 */

public class DailyAverage implements Parcelable {

    private String machine_name;
    private String date;
    private int average;
    private int count;


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public DailyAverage() {


    }

    public DailyAverage(String machine_name, int average) {
        this.machine_name = machine_name;
        this.average = average;
    }

    public String getMachine_name() {
        return machine_name;
    }

    public void setMachine_name(String machine_name) {
        this.machine_name = machine_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAverage() {
        return average;
    }

    public void setAverage(int average) {
        this.average = average;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(machine_name);
        dest.writeDouble(average);
    }

    // Creator
    public static final Parcelable.Creator
            CREATOR = new Parcelable.Creator() {
        public DailyAverage createFromParcel(Parcel in) {
            return new DailyAverage(in);
        }

        public DailyAverage[] newArray(int size) {
            return new DailyAverage[size];
        }
    };

    // "De-parcel object
    private DailyAverage(Parcel in) {
        machine_name = in.readString();
        average = in.readInt();
    }
}
