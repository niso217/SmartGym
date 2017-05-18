package com.a.n.smartgym;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.a.n.smartgym.Helpers.DBHelper;
import com.a.n.smartgym.Objects.ExercisesDB;
import com.a.n.smartgym.model.Muscle;
import com.a.n.smartgym.repo.MuscleRepo;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by Tan on 1/26/2016.
 */
public class App extends Application {
    private static Context context;
    private static DBHelper dbHelper;
    private RequestQueue requestQueue;
    private static final String TAG = Application.class.getSimpleName();

    private static final String ENDPOINT = "https://api.myjson.com/bins/gmvr5";


    private Gson gson;

    @Override
    public void onCreate() {
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
            Map<String, Map<String, Map<String, Muscle>>> categoryMap = gson.fromJson(response, new TypeToken<Map<String, Map<String, Map<String, Muscle>>>>() {
            }.getType());
            List<Muscle> list = new ArrayList<>();

            Iterator<Map.Entry<String, Map<String, Map<String, Muscle>>>> parent = categoryMap.entrySet().iterator();
            while (parent.hasNext()) {
                Map.Entry<String, Map<String, Map<String, Muscle>>> parentPair = parent.next();

                Iterator<Map.Entry<String, Map<String, Muscle>>> child = (parentPair.getValue()).entrySet().iterator();
                while (child.hasNext()) {

                    Map.Entry<String, Map<String, Muscle>> ChildcarePair = child.next();

                    Iterator<Map.Entry<String, Muscle>> childOfChild = (ChildcarePair.getValue()).entrySet().iterator();

                    while (childOfChild.hasNext()) {
                        Map.Entry c = childOfChild.next();
                        Muscle muscle = (Muscle) c.getValue();
                        list.add(muscle);
                        Log.d(TAG, c.getValue() + "");
                        childOfChild.remove();
                    }
                }

            }

            MuscleRepo muscleRepo = new MuscleRepo();
            muscleRepo.BulkMuscle(list);
            ExercisesDB.getInstance().keys = muscleRepo.getMainMuscle();


            Log.d(TAG, response.toString());

        }
    };


    private final Response.ErrorListener onPostsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG, error.toString());
        }
    };


    public static Context getContext() {
        return context;
    }

}

