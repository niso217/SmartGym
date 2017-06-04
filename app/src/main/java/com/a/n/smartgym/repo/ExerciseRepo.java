package com.a.n.smartgym.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.a.n.smartgym.DatabaseManager;
import com.a.n.smartgym.Quary.DailyAverage;
import com.a.n.smartgym.Quary.MachineUsage;
import com.a.n.smartgym.model.Exercise;
import com.a.n.smartgym.model.Sets;
import com.a.n.smartgym.model.User;
import com.a.n.smartgym.model.Visits;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Tan on 1/26/2016.
 */
public class ExerciseRepo {

    private Exercise exercise;
    public static final String TAG = ExerciseRepo.class.getSimpleName();


    public ExerciseRepo(){

        exercise= new Exercise();

    }



    public static String createTable(){
        return "CREATE TABLE " + Exercise.TABLE  + "("
                + Exercise.KEY_EXERCISE_ID + " TEXT  PRIMARY KEY,"
                + Exercise.KEY_VISIT_ID + " TEXT, "
                + Exercise.KEY_MACHINE_NAME + " TEXT, "
                + Exercise.KEY_START + " INTEGER, "
                + Exercise.KEY_END + " INTEGER )";
    }



    public void insert(Exercise exercise) {

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Exercise.KEY_EXERCISE_ID, exercise.getexerciseid());
        values.put(Exercise.KEY_VISIT_ID, exercise.getVisitid());
        values.put(Exercise.KEY_MACHINE_NAME, exercise.getMachinename());
        values.put(Exercise.KEY_START, String.valueOf(exercise.getStart()));
        values.put(Exercise.KEY_END, String.valueOf(exercise.getEnd()));

        // Inserting Row
        db.insert(Exercise.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();

    }

    public void update(String id, long time) {

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Exercise.KEY_END, time);
        // update Row
        db.update(Exercise.TABLE, values,Exercise.TABLE +"."+Exercise.KEY_EXERCISE_ID +"="+ "'"+id+"'",null);
        DatabaseManager.getInstance().closeDatabase();

    }

    public ArrayList<DailyAverage> getAllDaysAverages(String user_id){
        DailyAverage dailyAverage;
        ArrayList<DailyAverage> DailyAverages = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + User.TABLE +"." + User.KEY_FIRST_NAME
                + ", "+Visits.TABLE +"." + Visits.KEY_DATE
                + ", "+Exercise.TABLE +"." + Exercise.KEY_MACHINE_NAME
                + ", AVG(" + Sets.TABLE +"."+Sets.KEY_WEIGHT+") as average FROM " + User.TABLE
                + " INNER JOIN " + Visits.TABLE + " ON " + Visits.TABLE +"."+Visits.KEY_USER_ID + "=" +User.TABLE+"."+User.KEY_USER_ID
                + " INNER JOIN " + Exercise.TABLE + " ON " + Exercise.TABLE +"."+Exercise.KEY_VISIT_ID + "=" +Visits.TABLE+"."+Visits.KEY_VISIT_ID
                + " INNER JOIN " + Sets.TABLE + " ON " + Sets.TABLE +"."+Sets.KEY_EXERCISE_ID + "=" +Exercise.TABLE+"."+Exercise.KEY_EXERCISE_ID
                + " WHERE " + User.TABLE +"."+User.KEY_USER_ID + "="+ "'"+user_id+"'"
                + " GROUP BY " + Exercise.TABLE +"."+Exercise.KEY_MACHINE_NAME+" , "+Visits.TABLE +"."+Visits.KEY_DATE
                + " ORDER BY date(" +Visits.TABLE +"."+Visits.KEY_DATE+") ASC"

                ;


        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                dailyAverage= new DailyAverage();
                dailyAverage.setMachine_name(cursor.getString(cursor.getColumnIndex(Exercise.KEY_MACHINE_NAME)));
                dailyAverage.setDate(cursor.getString(cursor.getColumnIndex(Visits.KEY_DATE)));
                dailyAverage.setAverage(cursor.getDouble(cursor.getColumnIndex("average")));

                DailyAverages.add(dailyAverage);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return DailyAverages;

    }

    public ArrayList<MachineUsage> getUsage(String user_id){
        MachineUsage machineUsage;
        ArrayList<MachineUsage> machineUsages = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT "
                + Exercise.TABLE +"." + Exercise.KEY_MACHINE_NAME
                + ", COUNT(" + Sets.TABLE +"."+Sets.KEY_SET_ID+") as counter FROM " + User.TABLE
                + " INNER JOIN " + Visits.TABLE + " ON " + Visits.TABLE +"."+Visits.KEY_USER_ID + "=" +User.TABLE+"."+User.KEY_USER_ID
                + " INNER JOIN " + Exercise.TABLE + " ON " + Exercise.TABLE +"."+Exercise.KEY_VISIT_ID + "=" +Visits.TABLE+"."+Visits.KEY_VISIT_ID
                + " INNER JOIN " + Sets.TABLE + " ON " + Sets.TABLE +"."+Sets.KEY_EXERCISE_ID + "=" +Exercise.TABLE+"."+Exercise.KEY_EXERCISE_ID
                + " WHERE " + User.TABLE +"."+User.KEY_USER_ID + "="+ "'"+user_id+"'"
                + " GROUP BY " + Exercise.TABLE +"."+Exercise.KEY_MACHINE_NAME
                + " ORDER BY counter DESC "
//                + " LIMIT 5"


                ;


        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                machineUsage= new MachineUsage();
                machineUsage.setMachine_name(cursor.getString(cursor.getColumnIndex(Exercise.KEY_MACHINE_NAME)));
                machineUsage.setCounter(cursor.getInt(cursor.getColumnIndex("counter")));

                machineUsages.add(machineUsage);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        int sum = CalcSum(machineUsages);

        for (int i = 0; i < machineUsages.size(); i++) {
            machineUsages.get(i).setPresent(100L * machineUsages.get(i).getCounter() / sum);

        }

        return machineUsages;

    }
    private int CalcSum(List<MachineUsage> list) {
        int sum = 0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i).getCounter();
        }
        return sum;
    }





    public void delete( ) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Exercise.TABLE, null,null);
        DatabaseManager.getInstance().closeDatabase();
    }








}
