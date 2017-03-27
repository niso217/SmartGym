package com.a.n.smartgym.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.a.n.smartgym.DatabaseManager;
import com.a.n.smartgym.LogInActivity;
import com.a.n.smartgym.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Tan on 1/26/2016.
 */
public class UserRepo {

    private User user;
    private static final String TAG = UserRepo.class.getSimpleName();

    public UserRepo() {
        user = new User();
    }


    public static String createTable() {
        return "CREATE TABLE " + User.TABLE + "("
                + User.KEY_USER_ID + " TEXT PRIMARY KEY  ,"
                + User.KEY_FIRST_NAME + " TEXT, "
                + User.KEY_LAST_NAME + " TEXT )";
    }


    //return true if user exist
    public boolean isUserExist(String user_id) {

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery = " SELECT User." + User.KEY_USER_ID
                + " FROM " + User.TABLE
                + " WHERE " + User.KEY_USER_ID + "=" + "'" + user_id + "'";

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        boolean ans = cursor.getCount() > 0;
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return ans;
    }


    public void insert(User user) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(User.KEY_USER_ID, user.getId());
        values.put(User.KEY_FIRST_NAME, user.getFname());
        values.put(User.KEY_LAST_NAME, user.getLname());

        // Inserting Row
        db.insert(User.TABLE, null, values);
        DatabaseManager.getInstance().closeDatabase();
    }


    public void delete() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(User.TABLE, null, null);
        DatabaseManager.getInstance().closeDatabase();
    }


}
