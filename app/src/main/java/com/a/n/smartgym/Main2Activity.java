package com.a.n.smartgym;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;

import com.a.n.smartgym.Helpers.CircularProgressBar;
import com.a.n.smartgym.Helpers.LayoutTouchListener;

public class Main2Activity extends AppCompatActivity {

    private CircularProgressBar c2;
    private LinearLayout linearLayout;
    private TabHost host;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        linearLayout = (LinearLayout) findViewById(R.id.ll);
        linearLayout.setOnTouchListener(new LayoutTouchListener(this));
        host = (TabHost) findViewById(R.id.tab_host);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Tab One");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Tab One");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Tab Two");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Tab Three");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Tab Three");
        host.addTab(spec);


        c2 = (CircularProgressBar) findViewById(R.id.pb_repetition);
        c2.setTitleFontSize(100);
        c2.animateProgressTo(0, 77, new CircularProgressBar.ProgressAnimationListener() {
            @Override
            public void onAnimationStart(View view) {
            Log.d("dsf",view.getId()+"");
            }

            @Override
            public void onAnimationFinish(View view) {
                Log.d("dsf",view.getId()+"");

            }

            @Override
            public void onAnimationProgress(int progress, View view) {
                Log.d("dsf",view.getId()+"");


            }
        });
    }

    public void onLeftToRightSwipe() {
        int current = host.getCurrentTab()+1;
        host.setCurrentTab(current% 3);
        Log.d("============= ",current+" onRightToLeftSwipe");


    }

    public void onRightToLeftSwipe() {
        int current = host.getCurrentTab()-1;
        if (current<0) current += 3;
        host.setCurrentTab(current%3);
        Log.d("============= ",current+" onLeftToRightSwipe");


    }

}
