package com.a.n.smartgym.Helpers;

/**
 * Created by Tan on 1/26/2016.
 */

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.a.n.smartgym.SmartGymApplication;
import com.a.n.smartgym.DBModel.Muscle;
import com.a.n.smartgym.DBModel.MuscleExercise;
import com.a.n.smartgym.DBModel.Plan;
import com.a.n.smartgym.DBModel.PlanMuscle;
import com.a.n.smartgym.DBModel.Sets;
import com.a.n.smartgym.DBModel.Visits;
import com.a.n.smartgym.DBModel.Exercise;
import com.a.n.smartgym.DBModel.User;
import com.a.n.smartgym.DBRepo.ExerciseRepo;
import com.a.n.smartgym.DBRepo.MuscleExerciseRepo;
import com.a.n.smartgym.DBRepo.MuscleRepo;
import com.a.n.smartgym.DBRepo.PlanMuscleRepo;
import com.a.n.smartgym.DBRepo.PlanRepo;
import com.a.n.smartgym.DBRepo.SetsRepo;
import com.a.n.smartgym.DBRepo.VisitsRepo;
import com.a.n.smartgym.DBRepo.UserRepo;


public class DBHelper  extends SQLiteOpenHelper {
    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.
    private static final int DATABASE_VERSION =36;
    // Database Name
    private static final String DATABASE_NAME = "data.db";
    private static final String TAG = DBHelper.class.getSimpleName().toString();

    public DBHelper( ) {
        super(SmartGymApplication.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here
        db.execSQL(ExerciseRepo.createTable());
        db.execSQL(UserRepo.createTable());
        db.execSQL(VisitsRepo.createTable());
        db.execSQL(SetsRepo.createTable());
        db.execSQL(MuscleRepo.createTable());
        db.execSQL(PlanRepo.createTable());
        db.execSQL(PlanMuscleRepo.createTable());
        db.execSQL(MuscleExerciseRepo.createTable());

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, String.format("SQLiteDatabase.onUpgrade(%d -> %d)", oldVersion, newVersion));

        // Drop table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + Visits.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + User.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Exercise.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Sets.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Muscle.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Plan.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PlanMuscle.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MuscleExercise.TABLE);

        onCreate(db);
    }



}