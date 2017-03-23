package com.a.n.smartgym.Objects;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;


@IgnoreExtraProperties
public class Device {

    public List<Sessions> sessions = new ArrayList<>();
    public String DeviceId;

    public Device(String deviceid) {
        DeviceId =  deviceid;
    }

    public void addSession(Sessions session) {
        sessions.add(session);
    }


}
