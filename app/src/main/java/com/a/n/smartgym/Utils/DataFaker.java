package com.a.n.smartgym.Utils;

import android.graphics.Movie;
import android.icu.util.GregorianCalendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.a.n.smartgym.model.Exercise;
import com.a.n.smartgym.model.Muscle;
import com.a.n.smartgym.model.Sets;
import com.a.n.smartgym.model.Visits;
import com.a.n.smartgym.repo.ExerciseRepo;
import com.a.n.smartgym.repo.MuscleRepo;
import com.a.n.smartgym.repo.SetsRepo;
import com.a.n.smartgym.repo.VisitsRepo;
import com.google.firebase.auth.FirebaseAuth;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Created by nirb on 13/06/2017.
 */

public class DataFaker {


    static int weightinc = 40;
    static ArrayList<String> muscle = new MuscleRepo().getAllMuscleID();
    static int counter;


    public DataFaker() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)

    public static void StartFake() {


        ArrayList<Date> date_arr = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
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


            if (counter%5==0)
            weightinc+= getRandom(0,5);


            for (int i = 0; i < 6; i++) {

                String Exerciseid = UUID.randomUUID().toString();

                String ex_name = muscle.get(i);
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
                    mCurrentSet.setWeight(weightinc);
                    mCurrentSet.setStart(System.currentTimeMillis());
                    mCurrentSet.setEnd(System.currentTimeMillis() + 1000 * 30);
                    SetsRepo setsRepo = new SetsRepo();
                    setsRepo.insert(mCurrentSet);
                }
            }
        }
    }

    public static int getRandom(int from, int to) {
        if (from < to)
            return from + new Random().nextInt(Math.abs(to - from));
        return from - new Random().nextInt(Math.abs(to - from));
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Date RandomDateOfBirth() {

        GregorianCalendar gc = new GregorianCalendar();

        int year = randBetween(2017, 2017);

        gc.set(gc.YEAR, Calendar.getInstance().get(Calendar.YEAR));

        int dayOfYear = randBetween(1, gc.getActualMaximum(gc.DAY_OF_YEAR));

        gc.set(gc.DAY_OF_YEAR, dayOfYear);

        String startDateString = gc.get(gc.YEAR) + "-" + (gc.get(gc.MONTH) + 1) + "-" + gc.get(gc.DAY_OF_MONTH);

        return java.sql.Date.valueOf(startDateString);
    }



    public static int randBetween(int start, int end) {
        return start + (int) Math.round(Math.random() * (end - start));
    }

    public static String RandomMuscle() {
        ArrayList<String> muscle = new MuscleRepo().getAllMuscleID();
        String mCurrentTagId = muscle.get(getRandom(0, muscle.size() - 1));
        MuscleRepo muscleRepo = new MuscleRepo();
        Muscle ex = muscleRepo.getExerciseByID(mCurrentTagId);
        return ex.getName();
    }

    private static void setNewExercise(String session, String scan) {

    }


}




