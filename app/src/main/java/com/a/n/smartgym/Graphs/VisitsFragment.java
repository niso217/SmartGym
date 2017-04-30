package com.a.n.smartgym.Graphs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.a.n.smartgym.R;
import com.a.n.smartgym.repo.VisitsRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.SELECTION_MODE_RANGE;
import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.SELECTION_MODE_NONE;


/**
 * Created by nirb on 30/04/2017.
 */

public class VisitsFragment extends Fragment {

    private MaterialCalendarView calendar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.visits_fragment, container, false);

        VisitsRepo visitsRepo = new VisitsRepo();
        List<Date> dates = visitsRepo.getAllVisitsDates(FirebaseAuth.getInstance().getCurrentUser().getUid());
        calendar = (MaterialCalendarView) view.findViewById(R.id.calendar_view);

        calendar.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)

                .commit();

        calendar.setSelectionMode(SELECTION_MODE_NONE);

        for (int i = 0; i < dates.size(); i++) {
            calendar.setDateSelected(dates.get(i),true);
        }
//        calendar.setDateSelected(getHolidays("13-06-2017"), true);
//        calendar.setDateSelected(getHolidays("14-06-2017"), true);
//        calendar.setDateSelected(getHolidays("15-06-2017"), true);
//        calendar.setDateSelected(getHolidays("10-08-2017"), true);
//        calendar.setDateSelected(getHolidays("11-10-2017"), true);

        return view;
    }

//    private Calendar[] getRange(List<Date> dates) {
//        int month, year;
//        Calendar cal1 = Calendar.getInstance();
//        Calendar cal2 = Calendar.getInstance();
//
//        if (dates.size() > 0) {
//            cal1.setTime(dates.get(0));
//            month = cal1.get(Calendar.MONTH);
//            year = cal1.get(Calendar.YEAR);
//            cal1.set(year, month, 1);
//            if (dates.size() > 1) {
//                cal2.setTime(dates.get(1));
//                month = cal2.get(Calendar.MONTH);
//                year = cal2.get(Calendar.YEAR);
//                cal2.set(year, month, 1);
//            } else {
//                cal2.setTime(new Date());
//                month = cal2.get(Calendar.MONTH);
//                year = cal2.get(Calendar.YEAR);
//                cal1.set(year, month, 1);
//            }
//        } else {
//            cal1.setTime(new Date());
//            month = cal1.get(Calendar.MONTH);
//            year = cal1.get(Calendar.YEAR);
//            cal1.set(year, month, 1);
//            return new Calendar[]{cal1, cal1};
//
//        }
//
//        return new Calendar[]{cal1, cal2};
//    }


    private Date getHolidays(String dateInString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
        Date date = null;
        long startDate = 0;
        try {
            date = sdf.parse(dateInString);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
