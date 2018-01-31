package com.example.aggelos.tempcontrol.TempControl;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.aggelos.tempcontrol.MQTTServer.OpenWeatherMap;
import com.example.aggelos.tempcontrol.MQTTServer.ThingSpeakChannel;
import com.example.aggelos.tempcontrol.MQTTServer.ThingSpeakLineChart;
import com.example.aggelos.tempcontrol.R;
import com.macroyau.thingspeakandroid.model.Feed;

import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class Dashboard extends Fragment{

    private View _view;

    private FloatingActionButton dashboard_chart_btn;

    private final String READ_APIKEY = "AHVAOB5BO0DYLP90";
    private final int CHANNEL_NUM = 376531;

    public Dashboard() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        initializeChartBtn();

        getInTemperature();
        getOutTemperature();

        updateTemp();

        return _view;
    }

    private void initializeChartBtn(){
        dashboard_chart_btn = _view.findViewById(R.id.dashboard_chart_btn);
        dashboard_chart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChartDialog(view);}
        });
    }

    private void updateTemp(){
        final Handler handler = new Handler();
        int UPDATE_DELAY = 15 * 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getInTemperature();
                getOutTemperature();
                handler.postDelayed(this, 10000);
            }
        }, UPDATE_DELAY);
    }

    private void getInTemperature(){
        ThingSpeakChannel tsChannel = new ThingSpeakChannel(CHANNEL_NUM, READ_APIKEY);
        tsChannel.setFeedUpdateListener(new ThingSpeakChannel.FeedEntryUpdateListener() {
            @Override
            public void onFeedUpdated(long channelId, long entryId, Feed feed) {
                TextView in_temp_lbl = _view.findViewById(R.id.in_temp_lbl);
                in_temp_lbl.setText(feed.getField1().toString().substring(0, 4) + " °C");
            }
        });
        tsChannel.loadChannelFeed();
        tsChannel.loadLastEntryInChannelFeed();
    }

    private void getOutTemperature(){
        final String CITY_OUT = "Cluj-Napoca";
        final String READ_APIKEY_OUT = "d4faf390b65735df4c02021008fcf40e";

        OpenWeatherMap openWeatherMap = new OpenWeatherMap();
        String jsonTemp = "http://api.openweathermap.org/data/2.5/weather?q=" + CITY_OUT + "&units=metric&mode=json&appid=" + READ_APIKEY_OUT;
        double temp = openWeatherMap.ParseJson(jsonTemp);
        TextView out_temp_lbl = _view.findViewById(R.id.out_temp_lbl);
        if (temp != 0.0){
            out_temp_lbl.setText(Double.toString(temp).substring(0, 4) + " °C");
            return;
        }
        out_temp_lbl.setText("0.0 °C");
    }

    private void showChartDialog(View view){
        startAnimation();

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        View mView = getLayoutInflater().inflate(R.layout.chat_view, null);
        builder.setView(mView);
        builder.create();
        final AlertDialog alertDialog = builder.show();

        final LineChartView chartView = mView.findViewById(R.id.chart);

        int READ_FIELD = 1;
        ThingSpeakLineChart tsChart = new ThingSpeakLineChart(CHANNEL_NUM, READ_FIELD, READ_APIKEY);
        chartView.setZoomEnabled(false);

        tsChart.setNumberOfEntries(12);
        tsChart.setValueAxisLabelInterval(1);
        tsChart.setDateAxisLabelInterval(2);
        tsChart.useSpline(true);
        tsChart.setLineColor(Color.parseColor("#D32F2F"));
        tsChart.setAxisColor(Color.parseColor("#455a64"));

        tsChart.setListener(new ThingSpeakLineChart.ChartDataUpdateListener() {
            @Override
            public void onChartDataUpdated(long channelId, int fieldId, String title, LineChartData lineChartData, Viewport maxViewport, Viewport initialViewport) {
                chartView.setLineChartData(lineChartData);
                chartView.setMaximumViewport(maxViewport);
                chartView.setCurrentViewport(initialViewport);
            }
        });
        tsChart.loadChartData();
    }

    private void startAnimation(){
        Animation show_button = AnimationUtils.loadAnimation(getActivity()
                .getBaseContext().getApplicationContext(), R.anim.float_button_start);
        dashboard_chart_btn.startAnimation(show_button);
    }
}
