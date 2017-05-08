package com.a.n.smartgym.Objects;

import com.a.n.smartgym.model.Muscle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by nirb on 03/05/2017.
 */

 public class ExercisesDB {


    public List<Muscle> DB;
    public Hashtable<String,String> keys;


    private static final ExercisesDB ourInstance = new ExercisesDB();

    public static ExercisesDB getInstance() {
        return ourInstance;
    }

    private ExercisesDB() {
        DB = new ArrayList<>();
    }
}
