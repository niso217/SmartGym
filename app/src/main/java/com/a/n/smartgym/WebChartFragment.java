package com.a.n.smartgym;

/**
 * Created by nirb on 17/05/2017.
 */

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.a.n.smartgym.Objects.LastExercise;
import com.a.n.smartgym.Quary.DailyAverage;
import com.a.n.smartgym.Quary.MachineUsage;
import com.a.n.smartgym.Utils.Constants;
import com.a.n.smartgym.repo.ExerciseRepo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@SuppressLint("SetJavaScriptEnabled")
public class WebChartFragment extends Fragment {

    WebView webView;
    int num1, num2, num3, num4, num5;

    Spinner spCharts;
    List<Integer> count;
    List<Integer> weight;
    List<String> labels;
    String title;

    List<String> lbl;
    List<Integer> present;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootFragment = inflater.inflate(R.layout.layout_webchart, null);


        spCharts = (Spinner) rootFragment.findViewById(R.id.spinner2);
        webView = (WebView) rootFragment.findViewById(R.id.web);
        webView.addJavascriptInterface(new WebAppInterface(), "Android");
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);

        final WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setPluginState(WebSettings.PluginState.ON);
        ws.setAllowFileAccess(true);
        ws.setDomStorageEnabled(true);
        ws.setAllowContentAccess(true);
        ws.setAllowFileAccessFromFileURLs(true);
        ws.setAllowUniversalAccessFromFileURLs(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.setWebContentsDebuggingEnabled(true);

        if (getArguments() != null) {
            int type = getArguments().getInt("type", 0);

            switch (type)
            {
                case Constants.TREND:
                    spCharts.setVisibility(View.VISIBLE);
                    ArrayList<String> exercisesList = new ExerciseRepo().getAllExercises("");

                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_spinner_item, exercisesList);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spCharts.setAdapter(dataAdapter);
                    spCharts.setOnItemSelectedListener(new OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view,
                                                   int position, long id) {
                            //String chartHtml = listHtml.get(parent.getSelectedItemPosition());
                            //webView.loadUrl("file:///android_asset/summary.html");
                            title = parent.getSelectedItem().toString();
                            ArrayList<DailyAverage> avg = new ExerciseRepo().getAllDaysAverages2("", title,"-30 days");

                            labels = new ArrayList<>();
                            weight = new ArrayList<>();
                            count = new ArrayList<>();

                            Iterator<DailyAverage> iterator = avg.iterator();
                            while (iterator.hasNext()) {
                                DailyAverage current = iterator.next();

                                labels.add(current.getDate());
                                weight.add(current.getAverage());
                                count.add(current.getCount());

                            }
                            webView.loadUrl("file:///android_asset/daily.html");

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // TODO Auto-generated method stub

                        }
                    });

                    break;
                case Constants.USAGE:
                    spCharts.setVisibility(View.GONE);
                    ArrayList<MachineUsage> usage = new ExerciseRepo().getUsage2("");
                    title = "Muscle Usage Average";

                    lbl = new ArrayList<>();
                    present = new ArrayList<>();

                    Iterator<MachineUsage> iterator = usage.iterator();
                    while (iterator.hasNext()) {
                        MachineUsage current = iterator.next();

                        lbl.add(current.getMuscle());
                        present.add(current.getCounter());

                    }
                    webView.loadUrl("file:///android_asset/division.html");

                    break;
                case Constants.SUMMARY:
                    spCharts.setVisibility(View.GONE);
                    ArrayList<LastExercise> summary = new ExerciseRepo().getLastSummary("", "");
                    if (summary.size()==0) break;
                    title = summary.get(0).getDate();

                    labels = new ArrayList<>();
                    weight = new ArrayList<>();
                    count = new ArrayList<>();

                    Iterator<LastExercise> summary_iterator = summary.iterator();
                    while (summary_iterator.hasNext()) {
                        LastExercise current = summary_iterator.next();

                        labels.add(shortcut(current.getName()));
                        weight.add(current.getWeight());
                        count.add(current.getCount());

                    }
                    webView.loadUrl("file:///android_asset/summary.html");
                    break;
            }
        }

        return rootFragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private String shortcut(String word) {
        String ans = "";
        String[] arr = word.split("(?<=[\\S])[\\S]*\\s*");
        for (String a : arr) {
            ans += a.toUpperCase();
        }
        return ans;
    }

    public class WebAppInterface {

        @JavascriptInterface
        public String getLabels() {
            String x[] = labels.toArray(new String[labels.size()]);
            return new JSONArray(Arrays.asList(x)).toString();
        }

        @JavascriptInterface
        public String getWeights() {
            Integer[] x = weight.toArray(new Integer[weight.size()]);
            return new JSONArray(Arrays.asList(x)).toString();
        }

        @JavascriptInterface
        public String getCounts() {
            Integer[] x = count.toArray(new Integer[count.size()]);
            return new JSONArray(Arrays.asList(x)).toString();
        }

        @JavascriptInterface
        public String getLbl() {
            String x[] = lbl.toArray(new String[lbl.size()]);
            return new JSONArray(Arrays.asList(x)).toString();
        }


        @JavascriptInterface
        public String getPresent() {
            Integer[] x = present.toArray(new Integer[present.size()]);
            return new JSONArray(Arrays.asList(x)).toString();
        }

        @JavascriptInterface
        public String getTitle() {
            return title;
        }

    }

}
