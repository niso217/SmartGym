package com.a.n.smartgym.Object;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nirb on 06/06/2017.
 */

public class LastExercise implements Parcelable {

    private int weight;
    private int count;
    private String date;
    private String name;
    private int sets;

    public LastExercise(Parcel in) {
        weight = in.readInt();
        count = in.readInt();
        date = in.readString();
        name = in.readString();
        sets = in.readInt();
    }

    public LastExercise() {
    }

    public static final Creator<LastExercise> CREATOR = new Creator<LastExercise>() {
        @Override
        public LastExercise createFromParcel(Parcel in) {
            return new LastExercise(in);
        }

        @Override
        public LastExercise[] newArray(int size) {
            return new LastExercise[size];
        }
    };

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(weight);
        dest.writeInt(count);
        dest.writeString(date);
        dest.writeString(name);
        dest.writeInt(sets);
    }
}
