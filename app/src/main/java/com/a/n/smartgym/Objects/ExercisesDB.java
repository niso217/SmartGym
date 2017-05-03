package com.a.n.smartgym.Objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by nirb on 03/05/2017.
 */

 public class ExercisesDB {


    public LinkedHashMap<String, List<Muscles>> DB;
    public List<String> keys;


    private static final ExercisesDB ourInstance = new ExercisesDB();

    public static ExercisesDB getInstance() {
        return ourInstance;
    }

    private ExercisesDB() {
        DB = new LinkedHashMap<String, List<Muscles>>();
    }
}
