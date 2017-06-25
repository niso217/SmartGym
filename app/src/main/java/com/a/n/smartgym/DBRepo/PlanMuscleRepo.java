package com.a.n.smartgym.DBRepo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.a.n.smartgym.Utils.DatabaseManager;
import com.a.n.smartgym.DBModel.MuscleExercise;
import com.a.n.smartgym.DBModel.Plan;
import com.a.n.smartgym.DBModel.PlanMuscle;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Created by Tan on 1/26/2016.
 */
public class PlanMuscleRepo {

    private PlanMuscle planMuscle;
    private static final String TAG = PlanMuscleRepo.class.getSimpleName();

    public PlanMuscleRepo() {
        planMuscle = new PlanMuscle();
    }


    public static String createTable() {
        return "CREATE TABLE " + PlanMuscle.TABLE + "("
                + PlanMuscle.KEY_PLAN_MUSCLE_ID + " TEXT PRIMARY KEY  ,"
                + PlanMuscle.KEY_PLAN_ID + " TEXT ,"
                + PlanMuscle.KEY_MUSCLE_ID + " TEXT )";
    }


    public List<String> getMainMuscleByDay(String day) {
        List<String> main_muscles = new ArrayList<>();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery = " SELECT " + PlanMuscle.TABLE +"." + PlanMuscle.KEY_MUSCLE_ID
                + " FROM " + Plan.TABLE
                + " INNER JOIN " + PlanMuscle.TABLE + " ON " + Plan.TABLE + "." + Plan.KEY_PLAN_ID + "=" + PlanMuscle.TABLE + "." + PlanMuscle.KEY_PLAN_ID
                + " WHERE " + Plan.TABLE +"."+Plan.KEY_PLAN_ID+"='"+day+"'";

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                main_muscles.add(cursor.getString(cursor.getColumnIndex(PlanMuscle.KEY_MUSCLE_ID)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return main_muscles;
    }

    public String getPlanMuscleId(String day, String muscle) {
        String PlanMuscleId = "";
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery = " SELECT " + PlanMuscle.TABLE +"." + PlanMuscle.KEY_PLAN_MUSCLE_ID
                + " FROM " + Plan.TABLE
                + " INNER JOIN " + PlanMuscle.TABLE + " ON " + Plan.TABLE + "." + Plan.KEY_PLAN_ID + "=" + PlanMuscle.TABLE + "." + PlanMuscle.KEY_PLAN_ID
                + " WHERE " + PlanMuscle.TABLE +"."+PlanMuscle.KEY_PLAN_ID+"='"+day+"' AND " + PlanMuscle.TABLE +"."+PlanMuscle.KEY_MUSCLE_ID+"='"+muscle+"'";

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PlanMuscleId = cursor.getString(cursor.getColumnIndex(PlanMuscle.KEY_PLAN_MUSCLE_ID));
            } while (cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return PlanMuscleId;
    }

    public String getMainMuscleByDayAsString(String day) {
        List<String> main_muscles = new ArrayList<>();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery = " SELECT " + PlanMuscle.TABLE +"." + PlanMuscle.KEY_MUSCLE_ID
                + " FROM " + Plan.TABLE
                + " INNER JOIN " + PlanMuscle.TABLE + " ON " + Plan.TABLE + "." + Plan.KEY_PLAN_ID + "=" + PlanMuscle.TABLE + "." + PlanMuscle.KEY_PLAN_ID
                + " WHERE " + Plan.TABLE +"."+Plan.KEY_PLAN_ID+"='"+day+"'";

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                main_muscles.add(cursor.getString(cursor.getColumnIndex(PlanMuscle.KEY_MUSCLE_ID)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return getMainMusclesString(main_muscles);
    }

        public String getMainMusclesString(List<String> main) {
            String result = "";
            String[] muscles = main.toArray(new String[main.size()]);
            for (int i = 0; i < muscles.length; i++) {
                result += "'" + muscles[i] + "'";
                if (i != muscles.length - 1)
                    result += ",";
            }

            return result;
    }

    public boolean isMainMuscleExist(String day, String muscle) {

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String planmuscleid ="";
        String selectQuery = " SELECT " + PlanMuscle.TABLE +"." + PlanMuscle.KEY_PLAN_MUSCLE_ID
                + " FROM " + Plan.TABLE
                + " INNER JOIN " + PlanMuscle.TABLE + " ON " + Plan.TABLE + "." + Plan.KEY_PLAN_ID + "=" + PlanMuscle.TABLE + "." + PlanMuscle.KEY_PLAN_ID
                + " WHERE " + PlanMuscle.KEY_MUSCLE_ID + "=" + "'" + muscle +"'"
                + " AND " + Plan.TABLE+"."+Plan.KEY_PLAN_ID + "=" + "'" + day +"'";

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        boolean ans = cursor.getCount() > 0;

        if (ans)
        {
            if (cursor.moveToFirst()) {
                do {
                    planmuscleid = cursor.getString(cursor.getColumnIndex(PlanMuscle.KEY_PLAN_MUSCLE_ID));
                } while (cursor.moveToNext());
            }
            cursor.close();


            String selectMuscleExerciseQuery = " SELECT " + MuscleExercise.TABLE +"." + MuscleExercise.KEY_PLAN_MUSCLE_ID
                    + " FROM " + Plan.TABLE
                    + " INNER JOIN " + PlanMuscle.TABLE + " ON " + Plan.TABLE + "." + Plan.KEY_PLAN_ID + "=" + PlanMuscle.TABLE + "." + PlanMuscle.KEY_PLAN_ID
                    + " INNER JOIN " + MuscleExercise.TABLE + " ON " + PlanMuscle.TABLE + "." + PlanMuscle.KEY_PLAN_MUSCLE_ID + "=" + MuscleExercise.TABLE + "." + MuscleExercise.KEY_PLAN_MUSCLE_ID
                    + " WHERE " + MuscleExercise.TABLE+"."+MuscleExercise.KEY_PLAN_MUSCLE_ID + "=" + "'" + planmuscleid +"'"
                    + " AND " + Plan.TABLE+"."+Plan.KEY_PLAN_ID + "=" + "'" + day +"'";

            String deleteMuscleExerciseQuery = " DELETE "
                    + " FROM " + MuscleExercise.TABLE
                    + " WHERE " + MuscleExercise.TABLE +"."+MuscleExercise.KEY_PLAN_MUSCLE_ID
                    +" IN( " + selectMuscleExerciseQuery + ")";

            Log.d(TAG, deleteMuscleExerciseQuery);
            db.execSQL(deleteMuscleExerciseQuery);

            String deleteQueryMain = " DELETE "
                    + " FROM " + PlanMuscle.TABLE
                    + " WHERE " + PlanMuscle.KEY_MUSCLE_ID + "=" + "'" + muscle +"'"
                    + " AND " + PlanMuscle.KEY_PLAN_ID + "=" + "'" + day +"'";

            Log.d(TAG, selectQuery);
            db.execSQL(deleteQueryMain);
        }
        else
        {
            PlanMuscle planMuscle = new PlanMuscle();
            planMuscle.setPlanmuscleid(UUID.randomUUID().toString());
            planMuscle.setPlanid(day);
            planMuscle.setMuscleid(muscle);
            insert(planMuscle);

        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return ans;
    }

//    public List<MuscleItem> getMainMuscleByDay(String day) {
//        List<MuscleItem> main_muscles = new ArrayList<>();
//        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
//        String selectQuery = " SELECT " + PlanMuscle.TABLE +"." + PlanMuscle.KEY_MUSCLE_ID
//                + " FROM " + Plan.TABLE
//                + " INNER JOIN " + PlanMuscle.TABLE + " ON " + Plan.TABLE + "." + Plan.KEY_PLAN_ID + "=" + PlanMuscle.TABLE + "." + PlanMuscle.KEY_PLAN_ID
//                + " INNER JOIN " + Muscle.TABLE + " ON " + Muscle.TABLE + "." + Muscle.KEY_MUSCLE + "=" + PlanMuscle.TABLE + "." + PlanMuscle.KEY_MUSCLE_ID
//                + " WHERE " + Plan.KEY_PLAN_ID+"='"+day+"'"
//                + " GROUP BY " + PlanMuscle.TABLE+ "." + PlanMuscle.KEY_MUSCLE_ID;
//
//
//        Log.d(TAG, selectQuery);
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        // looping through all rows and adding to list
//        if (cursor.moveToFirst()) {
//            do {
//                String image = cursor.getString(cursor.getColumnIndex(Muscle.KEY_IMAGE));
//                String name = cursor.getString(cursor.getColumnIndex(PlanMuscle.KEY_MUSCLE_ID));
//                main_muscles.add(new MuscleItem(image,name));
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        DatabaseManager.getInstance().closeDatabase();
//        return main_muscles;
//    }


    public void insert(PlanMuscle planMuscle) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(PlanMuscle.KEY_PLAN_MUSCLE_ID, planMuscle.getPlanmuscleid());
        values.put(PlanMuscle.KEY_PLAN_ID, planMuscle.getPlanid());
        values.put(PlanMuscle.KEY_MUSCLE_ID, planMuscle.getMuscleid());

        // Inserting Row
        db.insert(PlanMuscle.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();
    }


    public void delete() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(PlanMuscle.TABLE, null, null);
        DatabaseManager.getInstance().closeDatabase();
    }


}
