package com.a.n.smartgym.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.a.n.smartgym.R;

import java.util.ArrayList;

import devlight.io.library.ArcProgressStackView;

import static devlight.io.library.ArcProgressStackView.Model;

/**
 * Created by GIGAMOLE on 9/21/16.
 */

public class ExerciseProgressFragment extends Fragment  {

    private int mCounter;
    private int mModelCount = 3;
    private ValueAnimator valueAnimator;
    private int mCurrentIndex = -1;
    private TextView tv_sets,tv_reps,tv_rest,tv_center,tv_title;
    private static final String TAG = ExerciseProgressFragment.class.getSimpleName();
    private Animation mAnimation;


    private ArcProgressStackView mArcProgressStackView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_exercise_progress, null);

        mArcProgressStackView = (ArcProgressStackView) view.findViewById(R.id.apsv_presentation);
        mArcProgressStackView.setShadowColor(Color.argb(200, 0, 0, 0));
        mArcProgressStackView.setAnimationDuration(60000);
        mArcProgressStackView.setSweepAngle(300);

        tv_sets = (TextView) view.findViewById(R.id.tv_sets);
        tv_reps = (TextView) view.findViewById(R.id.tv_reps);
        tv_rest = (TextView) view.findViewById(R.id.tv_rest);
        tv_center = (TextView) view.findViewById(R.id.tv_center);
        tv_title = (TextView) view.findViewById(R.id.tv_title);


        final String[] stringColors = getResources().getStringArray(R.array.progress);
        final String[] stringBgColors = getResources().getStringArray(R.array.bg);

        final int[] colors = new int[mModelCount];
        final int[] bgColors = new int[mModelCount];
        for (int i = 0; i < mModelCount; i++) {
            colors[i] = Color.parseColor(stringColors[i]);
            bgColors[i] = Color.parseColor(stringBgColors[i]);
        }


        final ArrayList<ArcProgressStackView.Model> models = new ArrayList<>();
        models.add(new Model("SETS", 1, bgColors[0], colors[0]));
        models.add(new Model("REPS", 1, bgColors[1], colors[1]));
        models.add(new Model("REST", 60.0F, bgColors[2], colors[2]));
        mArcProgressStackView.setModels(models);

        setUpAnimation();
        setUpTextAnimation();


        return view;
    }

    public void setCenterText(String text){
        tv_center.setText(text);
    }

    private void setUpTextAnimation(){
        mAnimation = new AlphaAnimation(0.0f, 1.0f);
        mAnimation.setDuration(200); //You can manage the blinking time with this parameter
        mAnimation.setStartOffset(20);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setRepeatCount(Animation.INFINITE);
    }

    public void setTitle(String title){
        tv_title.setText(title);
    }



    public void setUpAnimation() {
        valueAnimator = ValueAnimator.ofFloat(1.0F, 105.0F);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setRepeatCount(mModelCount - 1);

    }

    private void addAnimationListener(){
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                animation.removeListener(this);
                Log.d(TAG,"Animation Finished");

            }

        });
    }

    private void addUpdateListener() {
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                mArcProgressStackView.getModels().get(mCurrentIndex)
                        .setProgress((Float) animation.getAnimatedValue());
                        switch (mCurrentIndex)
                        {
                            case 0:
                                tv_center.setText((int)mArcProgressStackView.getModels().get(mCurrentIndex).getProgress()+"");
                                break;
                            case 1:
                                tv_center.setText((int)(mArcProgressStackView.getModels().get(mCurrentIndex).getProgress()*12/100)+"");
                                break;
                            case 2:
                                tv_center.setText((int)(mArcProgressStackView.getModels().get(mCurrentIndex).getProgress()*60/100)+"");
                                break;
                        }

                    mArcProgressStackView.postInvalidate();
                    Log.d(TAG,mArcProgressStackView.getModels().get(mCurrentIndex).getProgress()+"");
            }


        });
    }

    public void Animate(int index , float from , float to, long duration)
    {
        //valueAnimator.setDuration(duration);
        //blink(tv_rest);
        Blink(index);
        SetProgress(index,from);
        setValueAnimator(from,to,duration);
        SetCurrentIndex(index);
        startAnimation();

    }

    private void Blink(int index)
    {
        switch (index)
        {
            case 0:
                blink(tv_sets);
                break;
            case 1:
                blink(tv_reps);
                break;
            case 2:
                blink(tv_rest);
                break;
        }
    }



    public void setValueAnimator(float from, float to, long duration) {
        stopAnimation();
        valueAnimator = ValueAnimator.ofFloat(from, to);
        valueAnimator.setDuration(duration);
        addUpdateListener();
        addAnimationListener();

    }


    public float getProgress(int index) {
        return mArcProgressStackView.getModels().get(index).getProgress();
    }

    public void SetProgress(int index, float progress) {
        mArcProgressStackView.getModels().get(index).setProgress(progress);
    }





    public void SetCurrentIndex(int index) {
        valueAnimator.setRepeatCount(0);
        mCurrentIndex = index;
    }

    public void startAnimation() {
        if (valueAnimator.isRunning()) return;
        if (mArcProgressStackView.getProgressAnimator().isRunning()) return;
        valueAnimator.start();
    }

    public void stopAnimation(){
        if (valueAnimator.isRunning())
            valueAnimator.cancel();

    }

    public void stopBlinking(){
        if (mAnimation!=null)
        mAnimation.cancel();
    }

    private void blink(final TextView txt){
        stopBlinking();
        mAnimation = new AlphaAnimation(0.1f, 1.0f);
        mAnimation.setDuration(500); //You can manage the blinking time with this parameter
        mAnimation.setStartOffset(20);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setRepeatCount(Animation.INFINITE);
        txt.startAnimation(mAnimation);

    }


}
