package com.a.n.smartgym.Objects;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;


@IgnoreExtraProperties
public class Dates {
    public String date;
    public List<Device> devices = new ArrayList<>();


    public Dates(String d) {
        date = d;
    }

    public void addDevice(Device device) {
        devices.add(device);
    }


}
