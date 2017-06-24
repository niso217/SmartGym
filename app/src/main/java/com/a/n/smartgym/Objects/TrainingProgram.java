//package com.a.n.smartgym.Objects;
//
//import com.a.n.smartgym.Adapter.MuscleItem;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//
///**
// * Created by nirb on 21/06/2017.
// */
//
//public class TrainingProgram {
//
//    private HashMap<String, MusclePlan> training = new HashMap<>();
//    private String day;
//
//    public void setDay(String day) {
//        this.day = day;
//    }
//
//    public String getDay() {
//        return day;
//    }
//
//    private static final TrainingProgram ourInstance = new TrainingProgram();
//
//    public static TrainingProgram getInstance() {
//        return ourInstance;
//    }
//
//    private TrainingProgram() {
//    }
//
//    public MusclePlan getTraining() {
//        return training.get(day);
//    }
//
//    public void SetSubMuscle(ArrayList<MuscleItem> submuscle) {
//
//            MusclePlan muscle = new MusclePlan();
//           // muscle.setSub(submuscle);
//           // muscle.setMain(getMainMuscles());
//            training.put(day, muscle);
//
//    }
//
//    public void SetMainMuscle(ArrayList<MuscleItem> mainmuscle) {
//        MusclePlan muscle = new MusclePlan();
//      //  muscle.setMain(mainmuscle);
//       // muscle.setSub(getSubMuscles());
//        training.put(day, muscle);
//
//    }
//
//    public ArrayList<MuscleItem> getSubMuscles() {
//        if (training.get(day) == null)
//            return new ArrayList<MuscleItem>();
//       // else
//           // return training.get(day).getSub();
//    }
//
//    public ArrayList<String> getSubMusclesStringArray() {
//        if (training.get(day) == null)
//            return new ArrayList<String>();
//
//        else {
//            ArrayList<String> arr = new ArrayList<>();
//            ArrayList<MuscleItem> muscle = training.get(day).getSub();
//            for (int i = 0; i < muscle.size(); i++) {
//                arr.add(muscle.get(i).getTitle());
//            }
//            return arr;
//        }
//
//    }
//
//    public ArrayList<String> getMainMusclesStringArray() {
//        if (training.get(day) == null)
//            return new ArrayList<String>();
//
//        else {
//            ArrayList<String> arr = new ArrayList<>();
//            ArrayList<MuscleItem> muscle = training.get(day).getMain();
//            for (int i = 0; i < muscle.size(); i++) {
//                arr.add(muscle.get(i).getTitle());
//            }
//            return arr;
//        }
//
//    }
//
//    public boolean getSubMusclesIndex(MuscleItem val) {
//        ArrayList<String> arr = getSubMusclesStringArray();
//        if (training.get(day) == null)
//            return false;
//        else
//            return arr.contains(val.getTitle());
//    }
//
//    public int getMainMusclesIndex(MuscleItem val) {
//        if (training.get(day) == null)
//            return -1;
//        else
//            return training.get(day).getMain().indexOf(val);
//    }
//
//    public ArrayList<MuscleItem> getMainMuscles() {
//        if (training.get(day) == null)
//            return new ArrayList<MuscleItem>();
//        else
//            return training.get(day).getMain();
//    }
//
//    public String getMainMusclesString() {
//        if (training.get(day) == null) return "";
//
//        else {
//            String result = "";
//            MuscleItem[] muscles = training.get(day).getMain().toArray(new MuscleItem[training.get(day).getMain().size()]);
//            for (int i = 0; i < muscles.length; i++) {
//                result += "'" + muscles[i].getTitle() + "'";
//                if (i != muscles.length - 1)
//                    result += ",";
//            }
//
//            return result;
//        }
//
//    }
//
//    //
//    public String getTrainingString() {
//        if (training.get(day) == null)
//            return "";
//        else{
//            String result = "";
//            ArrayList<MuscleItem> sub = training.get(day).getSub();
//            for (int i = 0; i < sub.size(); i++) {
//                result += sub.get(i);
//                if (i != sub.size() - 1)
//                    result += ",";
//            }
//
//            return result;
//        }
//
//    }
//
//    public void Add(MusclePlan muscle) {
//        training.put(day, muscle);
//    }
//
//    public void addTodataBase(){
//        Iterator<Map.Entry<String, MusclePlan>> it = training.entrySet().iterator();
//        while (it.hasNext())
//        {
//            Map.Entry<String, MusclePlan> entry = it.next();
//            System.out.println("Key: " + entry.getKey());
//
//            // Each value is a List<Attribute>, so you can iterate though that as well
//            MusclePlan muscle = entry.getValue();
//
//            ArrayList<MuscleItem> main = muscle.getMain();
//            ArrayList<MuscleItem> sub = muscle.getSub();
//
//            Iterator<MuscleItem> iterator_main = main.iterator();
//            Iterator<MuscleItem> iterator_sub = sub.iterator();
//
//            while (iterator_main.hasNext()) {
//                System.out.println(iterator_main.next());
//            }
//
//            while (iterator_sub.hasNext()) {
//                System.out.println(iterator_sub.next());
//            }
//
//        }
//    }
//
//
//}
