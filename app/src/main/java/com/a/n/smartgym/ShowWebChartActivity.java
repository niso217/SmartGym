package com.a.n.smartgym;

/**
 * Created by nirb on 17/05/2017.
 */

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
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
import com.a.n.smartgym.repo.ExerciseRepo;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@SuppressLint("SetJavaScriptEnabled")
public class ShowWebChartActivity extends ActionBarActivity {

    WebView webView;
    int num1, num2, num3, num4, num5;

    Spinner spCharts;
    List<Integer> count;
    List<Integer> weight;
    List<String> labels;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_webchart);

        ArrayList<LastExercise> lastExercise2 = new ExerciseRepo().getLastSummary("", "");

        labels = new ArrayList<>();
        weight = new ArrayList<>();
        count = new ArrayList<>();


        Iterator<LastExercise> iterator = lastExercise2.iterator();
        while (iterator.hasNext()) {
            LastExercise current = iterator.next();
            labels.add(current.getName());
            weight.add(current.getWeight());
            count.add(current.getCount());

        }


//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item, listCharts);
        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      //  spCharts.setAdapter(dataAdapter);
//        spCharts.setOnItemSelectedListener(new OnItemSelectedListener(){
//
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view,
//                                       int position, long id) {
//                String chartHtml = listHtml.get(parent.getSelectedItemPosition());
//                webView.loadUrl("file:///android_asset/chartjs.html");
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // TODO Auto-generated method stub
//
//            }});

        webView = (WebView)findViewById(R.id.web);
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
        webView.loadUrl("file:///android_asset/chartjs.html");

    }

    public class WebAppInterface {

        @JavascriptInterface
        public String  getLabels() {
            //return labels.toArray(new String[labels.size()]);
            String x [] = labels.toArray(new String[labels.size()]);
            return new JSONArray(Arrays.asList(x)).toString();
            //return   "{'nir','nir','nir','3','ee'}";
        }

        @JavascriptInterface
        public List<Integer> getWeights() {
            return weight;
        }

        @JavascriptInterface
        public List<Integer> getCounts() {
            return count;
        }

    }

}
