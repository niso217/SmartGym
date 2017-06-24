package com.a.n.smartgym.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.a.n.smartgym.DatabaseManager;
import com.a.n.smartgym.model.MuscleExercise;
import com.a.n.smartgym.model.Plan;
import com.a.n.smartgym.model.PlanMuscle;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


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

    public List<String> getSubMuscleByDay(String day) {
        List<String> exercises = new ArrayList<>();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery = " SELECT " + MuscleExercise.TABLE +"." + MuscleExercise.KEY_EXERCISE_ID
                + " FROM " + Plan.TABLE
                + " INNER JOIN " + PlanMuscle.TABLE + " ON " + Plan.TABLE + "." + Plan.KEY_PLAN_ID + "=" + PlanMuscle.TABLE + "." + PlanMuscle.KEY_PLAN_ID
                + " INNER JOIN " + MuscleExercise.TABLE + " ON " + PlanMuscle.TABLE + "." + PlanMuscle.KEY_PLAN_MUSCLE_ID + "=" + MuscleExercise.TABLE + "." + MuscleExercise.KEY_PLAN_MUSCLE_ID
                + " WHERE " + Plan.TABLE+"."+Plan.KEY_PLAN_ID + "=" + "'" + day +"'";

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                exercises.add(cursor.getString(cursor.getColumnIndex(MuscleExercise.KEY_EXERCISE_ID)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return exercises;
    }

    public boolean isSubMuscleExist(String day, String exercise, String muscle) {

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery = " SELECT " + MuscleExercise.TABLE +"." + MuscleExercise.KEY_MUSCLE_EXERCISE_ID
                + " FROM " + Plan.TABLE
                + " INNER JOIN " + PlanMuscle.TABLE + " ON " + Plan.TABLE + "." + Plan.KEY_PLAN_ID + "=" + PlanMuscle.TABLE + "." + PlanMuscle.KEY_PLAN_ID
                + " INNER JOIN " + MuscleExercise.TABLE + " ON " + PlanMuscle.TABLE + "." + PlanMuscle.KEY_PLAN_MUSCLE_ID + "=" + MuscleExercise.TABLE + "." + MuscleExercise.KEY_PLAN_MUSCLE_ID
                + " WHERE " + MuscleExercise.KEY_EXERCISE_ID + "=" + "'" + exercise +"'"
                + " AND " + Plan.TABLE+"."+Plan.KEY_PLAN_ID + "=" + "'" + day +"'";

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        boolean ans = cursor.getCount() > 0;

        if (ans)
        {
            String deleteQueryMain = " DELETE "
                    + " FROM " + MuscleExercise.TABLE
                    + " WHERE " + MuscleExercise.TABLE +"."+MuscleExercise.KEY_MUSCLE_EXERCISE_ID
                    +" IN( " + selectQuery + ")";

            Log.d(TAG, selectQuery);
            db.execSQL(deleteQueryMain);

        }
        else
        {
            MuscleExercise muscleExercise = new MuscleExercise();
            muscleExercise.setMuscleexerciseid(UUID.randomUUID().toString());
            muscleExercise.setPlanmuscleid(new PlanMuscleRepo().getPlanMuscleId(day,muscle));
            muscleExercise.setExerciseid(exercise);
            muscleExercise.setNumberofreps("10");
            muscleExercise.setNumberofsets("4");
            muscleExercise.setWeight("50");
            insert(muscleExercise);

        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return ans;
    }


    public void delete() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(MuscleExercise.TABLE, null, null);
        DatabaseManager.getInstance().closeDatabase();
    }


}
