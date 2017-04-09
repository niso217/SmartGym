package com.a.n.smartgym.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.a.n.smartgym.DatabaseManager;
import com.a.n.smartgym.Quary.DailyAverage;
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
                + Exercise.KEY_START + " DATETIME DEFAULT CURRENT_TIME, "
                + Exercise.KEY_END + " DATETIME DEFAULT CURRENT_TIME )";
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

    public List<DailyAverage> getDailyAverage(String user_id){
        DailyAverage dailyavrage;
        List<DailyAverage> DailyAverages = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery =  " SELECT " + User.TABLE +"." + User.KEY_FIRST_NAME
                + ", "+Visits.TABLE +"." + Visits.KEY_DATE
                + ", AVG(" + Sets.TABLE +"."+Sets.KEY_WEIGHT+") as average FROM " + User.TABLE
                + " INNER JOIN " + Visits.TABLE + " ON " + Visits.TABLE +"."+Visits.KEY_USER_ID + "=" +User.TABLE+"."+User.KEY_USER_ID
                + " INNER JOIN " + Exercise.TABLE + " ON " + Exercise.TABLE +"."+Exercise.KEY_VISIT_ID + "=" +Visits.TABLE+"."+Visits.KEY_VISIT_ID
                + " INNER JOIN " + Sets.TABLE + " ON " + Sets.TABLE +"."+Sets.KEY_EXERCISE_ID + "=" +Exercise.TABLE+"."+Exercise.KEY_EXERCISE_ID
                + " WHERE " + User.TABLE +"."+User.KEY_USER_ID + "="+ "'"+user_id
                + " GROUP BY " + Exercise.TABLE +"."+Exercise.KEY_MACHINE_NAME+" , "+Visits.TABLE +"."+Visits.KEY_DATE
                ;


        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                dailyavrage= new DailyAverage();
                dailyavrage.setMachine_name(cursor.getString(cursor.getColumnIndex(Exercise.KEY_MACHINE_NAME)));
                dailyavrage.setDate(cursor.getString(cursor.getColumnIndex(Visits.KEY_DATE)));
                dailyavrage.setFname(cursor.getString(cursor.getColumnIndex(User.KEY_FIRST_NAME)));
                dailyavrage.setAvrage(cursor.getDouble(cursor.getColumnIndex("average")));

                DailyAverages.add(dailyavrage);
            } while (cursor.moveToNext());
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return DailyAverages;

    }




    public void delete( ) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Exercise.TABLE, null,null);
        DatabaseManager.getInstance().closeDatabase();
    }








}
