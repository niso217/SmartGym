package com.a.n.smartgym.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.a.n.smartgym.DBRepo.PlanMuscleRepo;
import com.a.n.smartgym.R;

import static com.a.n.smartgym.Utils.Constants.PLAN_DAY_UUID;
import static com.a.n.smartgym.Utils.Constants.WIZARD_DAY_UPDATE;
import static com.a.n.smartgym.Utils.Constants.WIZARD_MAIN_UPDATE;


/**
 * Created by Ratan on 7/27/2015.
 */
public class WizardFragment extends Fragment {

    //public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 3 ;
    private Button mNextButton;
    private Button mPrevButton;
    private boolean mIsDayPicked;
    private String DayUUID;



    private final BroadcastReceiver mWizardUpdates = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch ((intent.getAction())) {
                case WIZARD_MAIN_UPDATE:
                    break;
                case WIZARD_DAY_UPDATE:
                    DayUUID = intent.getStringExtra(PLAN_DAY_UUID);
                    break;
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        getActivity().registerReceiver(mWizardUpdates, makeWizardUpdateIntentFilter());
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(mWizardUpdates);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */
            View x =  inflater.inflate(R.layout.tab_layout,null);
            //tabLayout = (TabLayout) x.findViewById(R.id.tabs);
            viewPager = (ViewPager) x.findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);

        final View touchView = x.findViewById(R.id.viewpager);

        touchView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });

        mNextButton = (Button) x.findViewById(R.id.next_button);
        mPrevButton = (Button) x.findViewById(R.id.prev_button);

        mNextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                switch (viewPager.getCurrentItem()) {
                    case 0:
                        if (DayUUID != null && !DayUUID.equals("")) {
                            viewPager.setCurrentItem(getItem(+1), true);
                        }
                        break;
                    case 1:
                        if (new PlanMuscleRepo().getMainMuscleByDay(DayUUID).size() > 0) {
                            viewPager.setCurrentItem(getItem(+1), true);
                        }
                        break;
                }
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(getItem(-1), true); //getItem(-1) for previous
            }
        });

        /**
         *Set an Apater for the View Pager
         */
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */

//        tabLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                    tabLayout.setupWithViewPager(viewPager);
//                   }
//        });

        return x;

    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position)
        {
          switch (position){
              case 0 : return new WizardDayFragment();
              case 1 : return new WizardMuscleFragment();
              case 2 : return new WizardExerciseFragment();
          }
        return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "Primary";
                case 1 :
                    return "Social";
                case 2 :
                    return "Updates";
            }
                return null;
        }
    }


    private static IntentFilter makeWizardUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WIZARD_MAIN_UPDATE);
        intentFilter.addAction(WIZARD_DAY_UPDATE);
        return intentFilter;
    }
}
