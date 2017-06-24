package com.a.n.smartgym.repo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.a.n.smartgym.DatabaseManager;
import com.a.n.smartgym.model.Plan;
import com.a.n.smartgym.model.PlanMuscle;


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


    public void insert(PlanMuscle planMuscle) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(PlanMuscle.KEY_PLAN_MUSCLE_ID, planMuscle.getMuscleid());
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
