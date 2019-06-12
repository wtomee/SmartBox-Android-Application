package com.example.smartbox_app_menu;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class DHTActivity extends AppCompatActivity {

    MqttAndroidClient client;
    String tempTopic = "feed/temp";
    String humTopic = "feed/hum";
//    TextView subText, subText2;
    LineChart dhtLineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dht);

/*
        subText = (TextView) findViewById(R.id.subText);
        subText2 = (TextView) findViewById(R.id.subText2);
*/

        dhtLineChart = (LineChart) findViewById(R.id.dhtchart);


        dhtLineChart.setBackgroundColor(Color.WHITE);
        dhtLineChart.setNoDataText("Nincs megjelenítendő adat");
        dhtLineChart.setNoDataTextColor(Color.RED);
        //chart szélei
        dhtLineChart.setDrawGridBackground(true);
        dhtLineChart.setDrawBorders(true);
        dhtLineChart.setBorderColor(Color.GRAY);
        dhtLineChart.setBorderWidth(1);

        //YAxis
        YAxis yAxis =dhtLineChart.getAxisLeft();
        yAxis.setTextSize(10f);
        yAxis.setTextColor(Color.BLACK);
        yAxis.setAxisMinimum(0f); // 0 min
        yAxis.setAxisMaximum(45f); // 45max
        dhtLineChart.getAxisRight().setEnabled(false);

        //XAxis
        XAxis xAxis = dhtLineChart.getXAxis();
        xAxis.setDrawLabels(false);
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        //xAxis.setGranularity(1f);


        //description a bal alsó sarokban
        Description description = new Description();
        description.setText("Hőmérséklet és páratartalom");
        description.setTextColor(Color.GRAY);
        description.setTextSize(13);
        dhtLineChart.setDescription(description);

        //Legends
        Legend legend = dhtLineChart.getLegend();

        legend.setEnabled(true);
        legend.setTextColor(Color.BLACK);
        legend.setTextSize(10);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(10);
        legend.setXEntrySpace(15);//közti spaceing
        legend.setFormToTextSpace(10);//form és a text közti space


        //MQTT
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.2.80:1883", clientId);
        MqttConnectOptions options = new MqttConnectOptions();

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(DHTActivity.this, "Kapcsolódva", Toast.LENGTH_LONG).show();
                    subHum();
                    subTemp();
                    LineData data = new LineData();
                    dhtLineChart.setData(data);
                    dhtLineChart.invalidate();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(DHTActivity.this, "Nem sikerült a kapcsolódás", Toast.LENGTH_LONG).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if (topic.equals(tempTopic)){
//                    subText.setText(new String(message.getPayload()));
                    addEntryTemp(Float.valueOf(message.toString()));


                }
                else if (topic.equals(humTopic)){
//                    subText2.setText(new String(message.getPayload()));
                    addEntryHum(Float.valueOf(message.toString()));
                }


            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
    public void subHum(){
        try{
            client.subscribe(humTopic,0)
            ;}
        catch(MqttException e){
            e.printStackTrace();

        }

    }
    public void subTemp(){
        try{
            client.subscribe(tempTopic,0)
            ;}
        catch(MqttException e){
            e.printStackTrace();

        }

    }
    public void addEntryTemp(float value) {

        LineData dataTemp = dhtLineChart.getData();


        if (dataTemp != null){

            ILineDataSet setTemp = dataTemp.getDataSetByIndex(0);


            if (setTemp == null) {
                setTemp = createSetTemp();
                dataTemp.addDataSet(setTemp);
            }

            dataTemp.addEntry(new Entry(setTemp.getEntryCount(),value),0);
            Log.w("tempChart", setTemp.getEntryForIndex(setTemp.getEntryCount()-1).toString());

            dataTemp.notifyDataChanged();

            dhtLineChart.notifyDataSetChanged();

            // megjelenítendő entryk
            dhtLineChart.setVisibleXRangeMaximum(10);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // utolsó entryhez mozgatás
            dhtLineChart.moveViewTo(setTemp.getEntryCount()-1, dataTemp.getYMax(), YAxis.AxisDependency.LEFT);

        }
    }

    private LineDataSet createSetTemp() {
        LineDataSet setTemp = new LineDataSet(null, "Hőmérséklet");
        setTemp.setAxisDependency(YAxis.AxisDependency.LEFT);
        setTemp.setColor(Color.parseColor("#FF9800"));
        setTemp.setLineWidth(3f);
        //setTemp.setCircleRadius(4f);
        //setTemp.setFillAlpha(65);
        //setTemp.setFillColor(Color.rgb(67, 164, 34));
        //setTemp.setHighLightColor(Color.rgb(67, 164, 34));
        //setTemp.setValueTextColor(Color.rgb(67, 164, 34));
        //setTemp.setValueTextSize(9f);
        //setTemp.setDrawValues(false);
        //új
        setTemp.setDrawCircles(false);
        setTemp.setDrawCircleHole(false);
        setTemp.setCircleColor(Color.RED);
        setTemp.setCircleHoleColor(Color.RED);
        setTemp.setCircleRadius(10);
        setTemp.setCircleHoleRadius(10);

        setTemp.setDrawValues(false);
        setTemp.setValueTextSize(10);
        setTemp.setValueTextColor(Color.BLACK);
        //setTemp.enableDashedLine(5,10,0);//szaggatott
        return setTemp;
    }


    public void addEntryHum(float value) {

        LineData dataHum = dhtLineChart.getData();


        if (dataHum != null){

            ILineDataSet setHum = dataHum.getDataSetByIndex(1);


            if (setHum == null) {
                setHum = createSetHum();
                dataHum.addDataSet(setHum);
            }

            dataHum.addEntry(new Entry(setHum.getEntryCount(),value),1);
            Log.w("humChart", setHum.getEntryForIndex(setHum.getEntryCount()-1).toString());

            dataHum.notifyDataChanged();

            dhtLineChart.notifyDataSetChanged();

            // megjelenítendő entryk
            dhtLineChart.setVisibleXRangeMaximum(10);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // utolsó entryhez mozgatás
            dhtLineChart.moveViewTo(setHum.getEntryCount()-1, dataHum.getYMax(), YAxis.AxisDependency.LEFT);
        }
    }

    private LineDataSet createSetHum() {
        LineDataSet setHum = new LineDataSet(null, "Páratartalom");
        setHum.setAxisDependency(YAxis.AxisDependency.LEFT);
        setHum.setColor(Color.parseColor("#2196F3"));
        setHum.setLineWidth(3f);
        setHum.setDrawCircles(false);
        setHum.setDrawCircleHole(false);
        setHum.setCircleColor(Color.RED);
        setHum.setCircleHoleColor(Color.RED);
        setHum.setCircleRadius(10);
        setHum.setCircleHoleRadius(10);

        setHum.setDrawValues(false);
        setHum.setValueTextSize(10);
        setHum.setValueTextColor(Color.BLACK);
        //setTemp.enableDashedLine(5,10,0);//szaggatott
        return setHum;
    }


}

