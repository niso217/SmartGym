package com.a.n.smartgym.repo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.a.n.smartgym.DatabaseManager;
import com.a.n.smartgym.model.Exercise;
import com.a.n.smartgym.model.Visits;


/**
 * Created by Tan on 1/26/2016.
 */
public class ExerciseRepo {

    private Exercise exercise;

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



    public void delete( ) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Exercise.TABLE, null,null);
        DatabaseManager.getInstance().closeDatabase();
    }








}
