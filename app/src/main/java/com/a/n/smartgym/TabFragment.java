package com.a.n.smartgym;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.a.n.smartgym.Objects.TrainingProgram;

/**
 * Created by Ratan on 7/27/2015.
 */
public class TabFragment extends Fragment {

    //public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 3 ;
    private Button mNextButton;
    private Button mPrevButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */
            View x =  inflater.inflate(R.layout.tab_layout,null);
            //tabLayout = (TabLayout) x.findViewById(R.id.tabs);
            viewPager = (ViewPager) x.findViewById(R.id.viewpager);

        mNextButton = (Button) x.findViewById(R.id.next_button);
        mPrevButton = (Button) x.findViewById(R.id.prev_button);

        mNextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (TrainingProgram.getInstance().getDay()==null) return;
                viewPager.setCurrentItem(getItem(+1), true); //getItem(-1) for previous
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
              case 1 : return new MuscleFragment();
              case 2 : return new SubMuscleFragment();
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

}
