package com.a.n.smartgym.Utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

    public static MuscleType getRandom() {
        return MuscleType.values()[(int) (Math.random() * MuscleType.values().length)];
    }

}
