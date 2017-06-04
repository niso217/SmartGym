package com.a.n.smartgym;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.a.n.smartgym.Helpers.CircularProgressBar;

public class Main2Activity extends ActionBarActivity {

    private CircularProgressBar c2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);





         c2 = (CircularProgressBar) findViewById(R.id.circularprogressbar2);
        c2.setTitleFontSize(100);
        c2.animateProgressTo(0, 77, new CircularProgressBar.ProgressAnimationListener() {

            @Override
            public void onAnimationStart() {


            }

            @Override
            public void onAnimationProgress(int progress) {
                c2.setTitle(progress + "%");
            }

            @Override
            public void onAnimationFinish() {
                c2.setSubTitle("done");
            }
        });

    }

}
