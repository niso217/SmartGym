package com.a.n.smartgym.Helpers;

/**
 * Created by Tan on 1/26/2016.
 */

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.a.n.smartgym.App;
import com.a.n.smartgym.model.Muscle;
import com.a.n.smartgym.model.Sets;
import com.a.n.smartgym.model.Visits;
import com.a.n.smartgym.model.Exercise;
import com.a.n.smartgym.model.User;
import com.a.n.smartgym.repo.ExerciseRepo;
import com.a.n.smartgym.repo.MuscleRepo;
import com.a.n.smartgym.repo.SetsRepo;
import com.a.n.smartgym.repo.VisitsRepo;
import com.a.n.smartgym.repo.UserRepo;


public class DBHelper  extends SQLiteOpenHelper {
    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.
    private static final int DATABASE_VERSION =9;
    // Database Name
    private static final String DATABASE_NAME = "data.db";
    private static final String TAG = DBHelper.class.getSimpleName().toString();

    public DBHelper( ) {
        super(App.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here
        db.execSQL(ExerciseRepo.createTable());
        db.execSQL(UserRepo.createTable());
        db.execSQL(VisitsRepo.createTable());
        db.execSQL(SetsRepo.createTable());
        db.execSQL(MuscleRepo.createTable());

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

        onCreate(db);
    }



}