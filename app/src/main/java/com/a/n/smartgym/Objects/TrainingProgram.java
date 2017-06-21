package com.a.n.smartgym.Objects;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nirb on 21/06/2017.
 */

public class TrainingProgram {

    private HashMap<String, Muscle> training = new HashMap<>();
    private Muscle muscle = new Muscle();

    private static final TrainingProgram ourInstance = new TrainingProgram();

    public static TrainingProgram getInstance() {
        return ourInstance;
    }

    private TrainingProgram() {
    }

    public Muscle getTraining(String day) {
        return training.get(day);
    }

    public void SetSubMuscle(ArrayList<String> submuscle) {
        muscle.setSub(submuscle);
        Add("sunday",muscle);
    }

    public void SetMainMuscle(ArrayList<String> mainmuscle) {
        muscle.setMain(mainmuscle);
        Add("sunday",muscle);
    }

    public ArrayList<String> getSubMuscles(String day) {
        if (training.get(day) == null) return new ArrayList<String>();
        return training.get(day).getSub();
    }

    public int getSubMusclesValue(String day,String val) {
        if (training.get(day) == null) return -1;
        return training.get(day).getSub().indexOf(val);
    }

    public int getMainMusclesValue(String day,String val) {
        if (training.get(day) == null) return -1;
        return training.get(day).getMain().indexOf(val);
    }

    public ArrayList<String> getMainMuscles(String day) {
        if (training.get(day) == null) return new ArrayList<String>();
        return training.get(day).getMain();
    }
//
    public String getTrainingString(String day){
        if (training.get(day)==null) return "";
        String result = "";
        ArrayList<String> sub = training.get(day).getSub();
        for (int i = 0; i < sub.size(); i++) {
            result += sub.get(i);
            if (i != sub.size() - 1)
                result += ",";
        }

        return result;
    }

    public void Add(String day, Muscle muscle) {
        training.put(day, muscle);
    }


}
