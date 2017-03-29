package com.a.n.smartgym;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.a.n.smartgym.Objects.Dates;
import com.a.n.smartgym.Quary.DailyAvrage;
import com.a.n.smartgym.repo.ExerciseRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Ratan on 7/29/2015.
 */
public class PrimaryFragment extends Fragment {
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth mAuth;
    private static final String TAG = PrimaryFragment.class.getSimpleName();
    private List<DailyAvrage> mDailyAvrage;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        //mFirebaseDatabase = mFirebaseInstance.getReference("users");


        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootFragment = inflater.inflate(R.layout.primary_layout, null);

        GraphView graph = (GraphView) rootFragment.findViewById(R.id.graph);
        //addSessionChangeListener();
        initGraph(graph);
        return rootFragment;
    }

    public void initGraph(GraphView graph) {

        mDailyAvrage = new ExerciseRepo().getDailyAvrage(mAuth.getCurrentUser().getUid());

        DataPoint[] dataPoint = new DataPoint[mDailyAvrage.size()];


        for (int i = 0; i < dataPoint.length; i++) {
            dataPoint[i] = new DataPoint(StringtoDate(mDailyAvrage.get(i).getDate()),Math.round(mDailyAvrage.get(i).getAvrage()));
        }

        // you can directly pass Date objects to DataPoint-Constructor
        // this will convert the Date to double via Date#getTime()
        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(dataPoint);
        series.setSpacing(10);
        series.setAnimated(true);
        series.setColor(Color.RED);
        graph.addSeries(series);

        // set date label formatter
       graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(graph.getContext()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(mDailyAvrage.size());
        graph.getGridLabelRenderer().setNumVerticalLabels(mDailyAvrage.size()+10);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("date");
        graph.getGridLabelRenderer().setVerticalAxisTitle("weight");



        // set manual x bounds to have nice steps
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(150);
        graph.getViewport().setYAxisBoundsManual(true);

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not nessecary
        graph.getGridLabelRenderer().setHumanRounding(false);

    }

    private Date StringtoDate(String date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date theDate = null;
        try {
            theDate = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return theDate;
    }



    private void addSessionChangeListener() {
        // User data change listener
        mFirebaseDatabase
                .child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            // TODO: handle the post
                            //Dates dates = postSnapshot.getValue(Dates.class);
                            Log.d(TAG,postSnapshot.toString());
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.e(TAG, "Failed to read user", error.toException());
                    }
                });
    }
}
