package com.a.n.smartgym;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.a.n.smartgym.Helpers.DBHelper;
import com.a.n.smartgym.Objects.ExercisesDB;
import com.a.n.smartgym.Objects.Muscles;
import com.a.n.smartgym.Utils.Constance;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Tan on 1/26/2016.
 */
public class App extends Application {
    private static Context context;
    private static DBHelper dbHelper;
    private RequestQueue requestQueue;
    private static final String TAG =  Application.class.getSimpleName();
    private static final String ENDPOINT = "https://api.myjson.com/bins/actel";
    private Gson gson;

    @Override
    public void onCreate()
    {
        super.onCreate();
        context = this.getApplicationContext();
        dbHelper = new DBHelper();
        DatabaseManager.initializeInstance(dbHelper);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        gson = new Gson();
        fetchPosts();


    }

    private void fetchPosts() {
        StringRequest request = new StringRequest(Request.Method.GET, ENDPOINT, onPostsLoaded, onPostsError);

        requestQueue.add(request);
    }

    private final Response.Listener<String> onPostsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.i(TAG, response);

            Muscles[][] mus = gson.fromJson(response, Muscles[][].class);

            for (int i = 0; i < mus.length; i++) {
                ExercisesDB.getInstance().DB.put(Constance.MuscleType.values()[i].toString(), Arrays.asList(mus[i]));

            }
            ExercisesDB.getInstance().keys = new ArrayList<String>(ExercisesDB.getInstance().DB.keySet());
        }
    };

    private final Response.ErrorListener onPostsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG, error.toString());
        }
    };


    public static Context getContext(){
        return context;
    }

}

