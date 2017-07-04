package com.a.n.smartgym.Utils;

import android.icu.util.GregorianCalendar;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.a.n.smartgym.DBModel.Exercise;
import com.a.n.smartgym.DBModel.Muscle;
import com.a.n.smartgym.DBModel.Sets;
import com.a.n.smartgym.DBModel.Visits;
import com.a.n.smartgym.DBRepo.ExerciseRepo;
import com.a.n.smartgym.DBRepo.MuscleRepo;
import com.a.n.smartgym.DBRepo.SetsRepo;
import com.a.n.smartgym.DBRepo.VisitsRepo;
import com.google.firebase.auth.FirebaseAuth;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Created by nirb on 13/06/2017.
 */

public class DataFaker {


    static int weightinc = 20;
    static int current_weight;
    static ArrayList<String> muscle = new MuscleRepo().getAllMuscleID();
    static int counter;
    static int max=6,min=3;
    static int increase =1;



    public DataFaker() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)

    public static void StartFake() {


        ArrayList<Date> date_arr = new ArrayList<>();
        for (int i = 0; i < Calendar.getInstance().get(Calendar.DAY_OF_YEAR)/3  ; i++) {
            date_arr.add(RandomDateOfBirth());
        }
        Collections.sort(date_arr);

        Set<Date> dateset = new LinkedHashSet<Date>();

        for (int i = 0; i < date_arr.size(); i++) {
            dateset.add(date_arr.get(i));
        }

        Iterator it = dateset.iterator();
        while (it.hasNext()) {
            counter++;

            Date date = null;
            date = (Date) it.next();
            VisitsRepo visitsRepo = new VisitsRepo();
            //String uuid = visitsRepo.getRandomDateUUID(FirebaseAuth.getInstance().getCurrentUser().getUid(), date);
            //if (uuid.isEmpty()) {
            Visits visits = new Visits();
            String uuid = UUID.randomUUID().toString();
            visits.setVisitid(uuid);
            visits.setUserid(FirebaseAuth.getInstance().getCurrentUser().getUid());
            visits.setDate(date);
            visitsRepo.insert(visits);


            if (counter%14==0)
            weightinc+= getRandom(min++,max++);

            increase=0;

            if (counter % 2 == 0) {
                for (int i = 0; i < 6; i++) {
                    addsets(uuid,date,i);
                }

            }
            else{
                for (int i = 6; i < 12; i++) {
                    addsets(uuid,date,i);
                }
            }


        }
    }


    public static int getRandom(int from, int to) {
        if (from < to)
            return from + new Random().nextInt(Math.abs(to - from));
        return from - new Random().nextInt(Math.abs(to - from));
    }

    public static void addsets(String uuid, Date date, int index){
        String Exerciseid = UUID.randomUUID().toString();
        increase+=2;
        current_weight =weightinc +increase;
        String ex_name = muscle.get(index);
        MuscleRepo muscleRepo = new MuscleRepo();
        Muscle ex = muscleRepo.getExerciseByID(ex_name);

        ExerciseRepo exerciseRepo = new ExerciseRepo();
        Exercise exercise = new Exercise();
        exercise.setexerciseid(Exerciseid);
        exercise.setVisitid(uuid);
        exercise.setMachinename(ex.getName());
        exercise.setStart(date.getTime());
        exerciseRepo.insert(exercise);

        for (int j = 0; j < 4; j++) {
            Sets mCurrentSet = new Sets();
            mCurrentSet.setSetid(UUID.randomUUID().toString());
            mCurrentSet.setexerciseid(Exerciseid);
            mCurrentSet.setCount(10);
            mCurrentSet.setWeight(current_weight);
            mCurrentSet.setStart(System.currentTimeMillis());
            mCurrentSet.setEnd(System.currentTimeMillis() + 1000 * 30);
            SetsRepo setsRepo = new SetsRepo();
            setsRepo.insert(mCurrentSet);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Date RandomDateOfBirth() {

        SimpleDateFormat dfDateTime = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        GregorianCalendar gc = new GregorianCalendar();

        gc.set(gc.YEAR, Calendar.getInstance().get(Calendar.YEAR));

        int dayOfYear = randBetween(1, Calendar.getInstance().get(Calendar.DAY_OF_YEAR));

        gc.set(gc.DAY_OF_YEAR, dayOfYear);

        int hour = randBetween(9, 22); //Hours will be displayed in between 9 to 22
        int min = randBetween(0, 59);
        int sec = randBetween(0, 59);

        gc.set(gc.HOUR_OF_DAY, hour);

        gc.set(gc.MINUTE, min);

        gc.set(gc.SECOND, sec);

        String startDateString = dfDateTime.format(gc.getTime());

        //String startDateString = gc.get(gc.YEAR) + "-" + (gc.get(gc.MONTH) + 1) + "-" + gc.get(gc.DAY_OF_MONTH);

        return java.sql.Date.valueOf(startDateString);
    }



    public static int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }



//    public static String RandomMuscle() {
//        ArrayList<String> muscle = new MuscleRepo().getAllMuscleID();
//        String mCurrentTagId = muscle.get(getRandom(0, muscle.size() - 1));
//        MuscleRepo muscleRepo = new MuscleRepo();
//        Muscle ex = muscleRepo.getExerciseByID(mCurrentTagId);
//        return ex.getName();
//    }

    private static void setNewExercise(String session, String scan) {

    }


}




