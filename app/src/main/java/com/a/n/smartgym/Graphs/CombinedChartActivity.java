package com.a.n.smartgym.Graphs;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.a.n.smartgym.Objects.LastExercise;
import com.a.n.smartgym.Quary.DailyAverage;
import com.a.n.smartgym.R;
import com.a.n.smartgym.Utils.MyMarkerView;
import com.a.n.smartgym.repo.ExerciseRepo;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import static com.a.n.smartgym.Utils.Constants.MONTH;
import static com.a.n.smartgym.Utils.Constants.WEEK;
import static com.a.n.smartgym.Utils.Constants.YEAR;


/**
 * Created by nirb on 15/06/2017.
 */

public class CombinedChartActivity extends Fragment implements View.OnClickListener {

    private CombinedChart mChart;
    private final int itemcount = 20;

    protected String[] mMonths = new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    protected Typeface mTfLight;
    private Spinner spCharts;
    private List<Integer> mCount;
    private List<Integer> mWeight;
    private List<String> mLabels;
    private String mTitle;
    private CombinedData mCombinedData;
    private Button mWeek,mMonth,mYear;
    private XAxis mXAxis;
    private MarkerView mMarkerView;
    private LinearLayout mUpperMenu;
    private int mCurrentRange = YEAR;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_combined, container, false);

        mTfLight = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");
        spCharts = (Spinner) view.findViewById(R.id.spinner2);
        mUpperMenu = (LinearLayout) view.findViewById(R.id.upper);

        mMarkerView= new MyMarkerView(getContext(), R.layout.custom_marker_view);


        mWeek = (Button) view.findViewById(R.id.week);
        mMonth = (Button) view.findViewById(R.id.month);
        mYear = (Button) view.findViewById(R.id.year);

        mWeek.setOnClickListener(this);
        mMonth.setOnClickListener(this);
        mYear.setOnClickListener(this);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mChart = (CombinedChart) view.findViewById(R.id.chart1);
        mChart.getDescription().setEnabled(false);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setHighlightFullBarEnabled(false);
        mMarkerView.setChartView(mChart); // For bounds control
        mChart.setMarker(mMarkerView);

        // draw bars behind lines
//        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
//                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
//        });

        Legend l = mChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        mXAxis = mChart.getXAxis();
        mXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        mXAxis.setAxisMinimum(0f);
        mXAxis.setGranularity(1f);
        mXAxis.setAxisMinimum(0f);
        mXAxis.setLabelRotationAngle(60);
        mXAxis.setAvoidFirstLastClipping(true);
        mCombinedData = new CombinedData();
        mCombinedData.setValueTypeface(mTfLight);


        ArrayList<String> exercisesList = new ExerciseRepo().getAllExercises("");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, exercisesList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCharts.setAdapter(dataAdapter);
        spCharts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                mTitle = parent.getSelectedItem().toString();
                setUpRange(mCurrentRange);
                //setUpSummary();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });



        return view;
    }


    private void setUpSummary(){
        mUpperMenu.setVisibility(View.GONE);
        ArrayList<LastExercise> summary = new ExerciseRepo().getLastSummary("", "");
        if (summary.size()==0) return;
        mTitle = summary.get(0).getDate();

        mLabels = new ArrayList<>();
        mWeight = new ArrayList<>();
        mCount = new ArrayList<>();

        Iterator<LastExercise> summary_iterator = summary.iterator();
        while (summary_iterator.hasNext()) {
            LastExercise current = summary_iterator.next();

            mLabels.add(shortcut(current.getName()));
            mWeight.add(current.getWeight());
            mCount.add(current.getCount());

        }

        mMonths = toArray(mLabels);

        mCombinedData.setData(generateBarData());

        mXAxis.setAxisMaximum(mCombinedData.getXMax() + 0.25f);
        mXAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mMonths[(int) value % mMonths.length];
            }
        });

        mChart.setData(mCombinedData);
        mChart.invalidate();
    }

    private void setUpRange(int range) {

        ArrayList<DailyAverage> avg = null;
        mLabels = new ArrayList<>();
        mWeight = new ArrayList<>();
        mCount = new ArrayList<>();
        //mChart.clear();
        switch (range)
        {
            case YEAR:
                avg = new ExerciseRepo().getAllMonthAverages("", mTitle,"-"+range+" days");

                Iterator<DailyAverage> iterator = avg.iterator();
                while (iterator.hasNext()) {
                    DailyAverage current = iterator.next();

                    mLabels.add(current.getYr_mon());
                    mWeight.add(current.getAverage());
                    mCount.add(current.getCount());

                }
                mMonths = toArray(mLabels,new SimpleDateFormat("yyyy-MM"),new SimpleDateFormat("MM/yy", Locale.ENGLISH));

                break;
            default:
                avg = new ExerciseRepo().getAllDaysAverages2("", mTitle,"-"+range+" days");
                Iterator<DailyAverage> iterator2 = avg.iterator();
                while (iterator2.hasNext()) {
                    DailyAverage current = iterator2.next();

                    mLabels.add(current.getDate());
                    mWeight.add(current.getAverage());
                    mCount.add(current.getCount());

                }
                mMonths = toArray(mLabels,new SimpleDateFormat("yyyy-MM-dd"),new SimpleDateFormat("MM/dd", Locale.ENGLISH));

                break;

        }

        //ArrayList<DailyAverage> avg = new ExerciseRepo().getAllDaysAverages2("", mTitle,"-"+range+" days");

        if (avg.size()<3) {
            mChart.clear();
            return;
        }





        mCombinedData.setData(generateLineData());
        // mCombinedData.setData(generateSetsCandleData());
        //mCombinedData.setData(generateBarData());

        mXAxis.setAxisMaximum(mCombinedData.getXMax() + 0.25f);
        mXAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mMonths[(int) value % mMonths.length];
            }
        });

        mChart.setData(mCombinedData);
        mChart.invalidate();
    }

    private String [] toArray(List<String> arrayList,SimpleDateFormat from, SimpleDateFormat to){
        String[] stockArr = new String[arrayList.size()];
        arrayList.toArray(stockArr);
        for (int i = 0; i < stockArr.length; i++) {
            Date date = null;
            try {
                 date = from.parse(stockArr[i]);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            stockArr[i] = to.format(date);
        }
        return stockArr;
    }


    private String shortcut(String word) {
        String ans = "";
        String[] arr = word.split("(?<=[\\S])[\\S]*\\s*");
        for (String a : arr) {
            ans += a.toUpperCase();
        }
        return ans;
    }


    private String [] toArray(List<String> arrayList){
        String[] stockArr = new String[arrayList.size()];
        return arrayList.toArray(stockArr);
    }

    private LineData generateLineData() {
        int alpha = (int)(0.5 * 255.0f);


        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < mWeight.size(); index++)
            entries.add(new Entry(index, mWeight.get(index)));

        LineDataSet set = new LineDataSet(entries, "Weight");
        set.setColor(Color.argb(alpha,151,187,205));
        set.setDrawFilled(true);
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleRadius(2f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(false);
        //set.setValueTextSize(10f);
        //set.setValueTextColor(Color.rgb(240, 238, 70));
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            //Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_red);
            //set.setFillDrawable(drawable);
            set.setFillColor(Color.argb(alpha,151,187,205));
        }
        else {
            set.setFillColor(Color.BLACK);
        }

        ArrayList<Entry> entries2 = new ArrayList<Entry>();

        for (int index = 0; index < mCount.size(); index++)
            entries2.add(new Entry(index, mCount.get(index)));

        LineDataSet set2 = new LineDataSet(entries2, "Sets");
        set2.setColor(Color.argb(alpha,220,220,220));
        set2.setDrawFilled(true);
        set2.setLineWidth(2.5f);
        set2.setCircleColor(Color.rgb(240, 238, 70));
        set2.setCircleRadius(2f);
        set2.setFillColor(Color.rgb(240, 238, 70));
        set2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set2.setDrawValues(false);
        //set.setValueTextSize(10f);
        //set.setValueTextColor(Color.rgb(240, 238, 70));
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            //Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_red);
            //set.setFillDrawable(drawable);
            set2.setFillColor(Color.argb(alpha,220,220,220));
        }
        else {
            set2.setFillColor(Color.BLACK);
        }

        //set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
        //set.setAxisDependency(YAxis.AxisDependency.RIGHT);

        dataSets.add(set);
        dataSets.add(set2);

        LineData data = new LineData(dataSets);


        return data;
    }


    private BarData generateBarData() {
        int alpha = (int)(0.5 * 255.0f);

        ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> entries2 = new ArrayList<BarEntry>();

        for (int index = 0; index < mWeight.size(); index++) {
            entries1.add(new BarEntry(index, mWeight.get(index)));
        }

        for (int index = 0; index < mCount.size(); index++) {
            entries2.add(new BarEntry(index, mCount.get(index)));
        }


        BarDataSet set1 = new BarDataSet(entries1, "Weight");
        set1.setColor(Color.argb(alpha,151,187,205));
        set1.setDrawValues(false);
        set1.setAxisDependency(YAxis.AxisDependency.RIGHT);

        BarDataSet set2 = new BarDataSet(entries2, "Sets");
        set2.setColors(Color.argb(alpha,220,220,220));
        set2.setDrawValues(false);
        set2.setAxisDependency(YAxis.AxisDependency.RIGHT);

        float groupSpace = 0.06f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.45f; // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"

        BarData d = new BarData(set1,set2);
        d.setBarWidth(barWidth);

        // make this BarData object grouped
        d.groupBars(0, groupSpace, barSpace); // start at x = 0

        return d;
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.week:
                setUpRange(mCurrentRange = WEEK);
                break;
            case R.id.month:
                setUpRange(mCurrentRange = MONTH);
                break;
            case R.id.year:
                setUpRange(mCurrentRange = YEAR);
                break;
        }
    }
}
