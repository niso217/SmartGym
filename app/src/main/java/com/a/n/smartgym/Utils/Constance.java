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

public class Constance {


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

}
