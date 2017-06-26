package com.a.n.smartgym.DBRepo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;

import com.a.n.smartgym.Utils.DatabaseManager;
import com.a.n.smartgym.DBModel.MuscleExercise;
import com.a.n.smartgym.DBModel.Plan;
import com.a.n.smartgym.DBModel.PlanMuscle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public Map<String,ArrayList<MuscleExercise>> getDayPlan(String day) {
        Map<String,ArrayList<MuscleExercise>> exercises = new HashMap<>();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery = " SELECT " + MuscleExercise.TABLE +"." + MuscleExercise.KEY_EXERCISE_ID
                + ", " + MuscleExercise.TABLE +"." + MuscleExercise.KEY_NUM_OF_SETS
                + ", " + MuscleExercise.TABLE +"." + MuscleExercise.KEY_NUM_OF_REPS
                + ", " + MuscleExercise.TABLE +"." + MuscleExercise.KEY_WEIGHT
                + ", " + PlanMuscle.TABLE +"." + PlanMuscle.KEY_MUSCLE_ID
                + " FROM " + Plan.TABLE
                + " INNER JOIN " + PlanMuscle.TABLE + " ON " + Plan.TABLE + "." + Plan.KEY_PLAN_ID + "=" + PlanMuscle.TABLE + "." + PlanMuscle.KEY_PLAN_ID
                + " INNER JOIN " + MuscleExercise.TABLE + " ON " + PlanMuscle.TABLE + "." + PlanMuscle.KEY_PLAN_MUSCLE_ID + "=" + MuscleExercise.TABLE + "." + MuscleExercise.KEY_PLAN_MUSCLE_ID
                + " WHERE " + Plan.TABLE+"."+Plan.KEY_DATE + "=" + "'" + day +"'";

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String key = cursor.getString(cursor.getColumnIndex(PlanMuscle.KEY_MUSCLE_ID));
                MuscleExercise muscleExercise = new MuscleExercise();
                muscleExercise.setExerciseid(cursor.getString(cursor.getColumnIndex(MuscleExercise.KEY_EXERCISE_ID)));
                muscleExercise.setNumberofsets(cursor.getString(cursor.getColumnIndex(MuscleExercise.KEY_NUM_OF_SETS)));
                muscleExercise.setNumberofreps(cursor.getString(cursor.getColumnIndex(MuscleExercise.KEY_NUM_OF_REPS)));
                muscleExercise.setWeight(cursor.getString(cursor.getColumnIndex(MuscleExercise.KEY_WEIGHT)));
                ArrayList<MuscleExercise> muscleExerciseList = exercises.get(key);
                if (muscleExerciseList!=null){
                    muscleExerciseList.add(muscleExercise);
                    exercises.put(key,muscleExerciseList);
                }
                else
                {
                    muscleExerciseList = new ArrayList<>();
                    muscleExerciseList.add(muscleExercise);
                    exercises.put(key,muscleExerciseList);
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return exercises;
    }

    public String MaptoString(Map<String, ArrayList<MuscleExercise>>hm ){
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : hm.keySet()) {
            Spannable sb = new SpannableString(key+": ");
            sb.setSpan(new BackgroundColorSpan(Color.CYAN), 0, key.length(), 0);
            stringBuilder.append(sb);
            stringBuilder.append(System.getProperty("line.separator"));
            // gets the value
            List<MuscleExercise> muscleExerciseList = hm.get(key);
            // checks for null value
            if (muscleExerciseList != null) {
                // iterates over String elements of value
                for (MuscleExercise muscleExercise : muscleExerciseList) {
                    // checks for null
                    if (muscleExercise != null) {
                        // prints whether the key is equal to the String
                        // representation of that List's element
                        stringBuilder.append(muscleExercise.getExerciseid()+": ");
                        stringBuilder.append(muscleExercise.getNumberofsets()+" X ");
                        stringBuilder.append(muscleExercise.getNumberofreps()+" X ");
                        stringBuilder.append(muscleExercise.getWeight());
                        stringBuilder.append(System.getProperty("line.separator"));


                    }
                }
            }
        }
        return stringBuilder.toString();

    }

    public boolean isSubMuscleExist(String day, String exercise) {

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

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return ans;
    }

    public void insertSelection(String day,String exercise, String muscle,String sets,String reps,String weight){

        MuscleExercise muscleExercise = new MuscleExercise();
        muscleExercise.setMuscleexerciseid(UUID.randomUUID().toString());
        muscleExercise.setPlanmuscleid(new PlanMuscleRepo().getPlanMuscleId(day,muscle));
        muscleExercise.setExerciseid(exercise);
        muscleExercise.setNumberofreps(reps);
        muscleExercise.setNumberofsets(sets);
        muscleExercise.setWeight(weight);
        insert(muscleExercise);
    }




    public void delete() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(MuscleExercise.TABLE, null, null);
        DatabaseManager.getInstance().closeDatabase();
    }


}
