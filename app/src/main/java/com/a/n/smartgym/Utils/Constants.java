package com.a.n.smartgym.Utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by nirb on 03/05/2017.
 */

public class Constants {



    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    public static final int RECONNECT_ATTEMPTING = 3;

    public static final int TREND = 0;
    public static final int SUMMARY = 1;
    public static final int USAGE = 2;

    public static final int YEAR = 365;
    public static final int MONTH = 30;
    public static final int WEEK = 7;

    public final static String WIZARD_DAY_UPDATE =
            "com.a.n.smartgym.Utils.WIZARD_DAY_UPDATE";
    public final static String WIZARD_MAIN_UPDATE =
            "com.a.n.smartgym.Utils.WIZARD_MAIN_UPDATE";
    public final static String WIZARD_SUB_UPDATE =
            "com.a.n.smartgym.Utils.WIZARD_SUB_UPDATE";


    public final static String SUNDAY = "sunday";
    public final static String MONDAY = "monday";
    public final static String TUESDAY = "tuesday";
    public final static String WEDNESDAY = "wednesday";
    public final static String THURSDAY = "thursday";
    public final static String FRIDAY = "friday";
    public final static String SATURDAY = "saturday";

    public final static String PLAN_DAY_UUID = "plan_uuid";


    public enum MuscleType {
        ABS,
        ARMS,
        SHOULDERS ,
        BACK ,
        LEGS ,
        BUTTOCKS ,
        HIPS ,
        CHEST ,
        CARDIO,
        HEART
    }


    public static final Map<String, String> KEY_PROTOCOLS;

    static {
        Map<String, String> map = new HashMap<String, String>();
        map.put("ABS", "https://www.freetrainers.com/redbody/eJzbY2hgaIAPGGJhgUC8gbkpAIWrDDw=.png");
        map.put("ARMS", "https://www.freetrainers.com/redbody/eJzbY2hgaIAPGGJhgUC8gbkpAIWrDDw=.png");
        map.put("SHOULDERS", "https://www.freetrainers.com/redbody/eJzbY2hgaIAPGGJhgUC8gbkpAIWrDDw=.png");

        KEY_PROTOCOLS = Collections.unmodifiableMap(map);
    }

    public static MuscleType getRandom() {
        return MuscleType.values()[(int) (Math.random() * MuscleType.values().length)];
    }

    public final static String EMULATOR_NAME = "niso217";
    public final static String DEVICE_NAME = "GYM1";

    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    public static final String WRITE_EXTERNAL_STORAGE = "an" + "droid.permission.WRITE_EXTERNAL_STORAGE";
    public static final String ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION";
    public static final String GYM1_ADDRESS = "B8:27:EB:6B:29:BF";


}
