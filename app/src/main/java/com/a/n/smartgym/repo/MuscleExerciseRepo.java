package com.a.n.smartgym.repo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.a.n.smartgym.DatabaseManager;
import com.a.n.smartgym.model.MuscleExercise;
import com.a.n.smartgym.model.PlanMuscle;


/**
 * Created by Tan on 1/26/2016.
 */
public class MuscleExerciseRepo {

    private MuscleExercise muscleExercise;
    private static final String TAG = MuscleExerciseRepo.class.getSimpleName();

    public MuscleExerciseRepo() {
        muscleExercise = new MuscleExercise();
    }


    public static String createTable() {
        return "CREATE TABLE " + MuscleExercise.TABLE + "("
                + MuscleExercise.KEY_MUSCLE_EXERCISE_ID + " TEXT PRIMARY KEY  ,"
                + MuscleExercise.KEY_PLAN_MUSCLE_ID + " TEXT ,"
                + MuscleExercise.KEY_EXERCISE_ID + " TEXT ,"
                + MuscleExercise.KEY_NUM_OF_SETS + " TEXT ,"
                + MuscleExercise.KEY_NUM_OF_REPS + " TEXT ,"
                + MuscleExercise.KEY_WEIGHT + " TEXT )";
    }


    public void insert(MuscleExercise muscleExercise) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(MuscleExercise.KEY_MUSCLE_EXERCISE_ID, muscleExercise.getMuscleexerciseid());
        values.put(MuscleExercise.KEY_PLAN_MUSCLE_ID, muscleExercise.getPlanmuscleid());
        values.put(MuscleExercise.KEY_EXERCISE_ID, muscleExercise.getExerciseid());
        values.put(MuscleExercise.KEY_NUM_OF_SETS, muscleExercise.getNumberofsets());
        values.put(MuscleExercise.KEY_NUM_OF_REPS, muscleExercise.getNumberofreps());
        values.put(MuscleExercise.KEY_WEIGHT, muscleExercise.getWeight());

        // Inserting Row
        db.insert(MuscleExercise.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();
    }


    public void delete() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(MuscleExercise.TABLE, null, null);
        DatabaseManager.getInstance().closeDatabase();
    }


}
