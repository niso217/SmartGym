package com.a.n.smartgym.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.a.n.smartgym.R;

import java.util.ArrayList;

import devlight.io.library.ArcProgressStackView;

import static devlight.io.library.ArcProgressStackView.Model;

/**
 * Created by GIGAMOLE on 9/21/16.
 */

public class MyDayProgressFragment extends Fragment {

    private int mCounter;
    private int mModelCount;
    private ValueAnimator valueAnimator;
    private int mCurrentIndex = -1;


    private ArcProgressStackView mArcProgressStackView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_circel_progress, null);

        mArcProgressStackView = (ArcProgressStackView) view.findViewById(R.id.apsv_presentation);
        mArcProgressStackView.setShadowColor(Color.argb(200, 0, 0, 0));
        mArcProgressStackView.setAnimationDuration(200);


        return view;
    }

    public void setUpAnimation() {
        valueAnimator = ValueAnimator.ofFloat(1.0F, 105.0F);
        valueAnimator.setDuration(500);
        valueAnimator.setStartDelay(0);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setRepeatCount(mModelCount - 1);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                animation.removeListener(this);
                animation.addListener(this);
                mCounter = 0;

//                for (final Model model : mArcProgressStackView.getModels()) model.setProgress(1);
//                mArcProgressStackView.animateProgress();
            }

            @Override
            public void onAnimationRepeat(final Animator animation) {
                mCounter++;
            }
        });

        addUpdateListener();


        mArcProgressStackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

            }
        });
    }

    private void addUpdateListener() {
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                if (mCurrentIndex > -1) {
                    mArcProgressStackView.getModels().get(mCurrentIndex)
                            .setProgress((Float) animation.getAnimatedValue());
                    mArcProgressStackView.postInvalidate();
                } else {
                    for (final Model model : mArcProgressStackView.getModels())
                        mArcProgressStackView.animateProgress();
                }

            }
        });
    }

    public void setModelCount(int size) {
        mModelCount = size;
    }

    public void setValueAnimator(float limit) {
        valueAnimator = ValueAnimator.ofFloat(1.0F, limit);
        addUpdateListener();

    }

    public float getProgress(int index) {
        return mArcProgressStackView.getModels().get(index).getProgress();
    }

    public void SetProgress(int index, long progress) {
        mArcProgressStackView.getModels().get(index).setProgress(progress);
    }

    public void AddModel(ArrayList<Model> model) {
        mArcProgressStackView.setModels(model);

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


}
