package com.example.smartbox_app_menu;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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

public class SoundActivity extends AppCompatActivity {

        MqttAndroidClient client;
        String soundTopic = "feed/sound";
//    TextView subText, subText2;
        LineChart soundLineChart;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sound);

/*
        subText = (TextView) findViewById(R.id.subText);
        subText2 = (TextView) findViewById(R.id.subText2);
*/

            soundLineChart = (LineChart) findViewById(R.id.soundLineChart);


            soundLineChart.setBackgroundColor(Color.WHITE);
            soundLineChart.setNoDataText("Nincs megjelenítendő adat");
            soundLineChart.setNoDataTextColor(Color.RED);
            //chart szélei
            soundLineChart.setDrawGridBackground(true);
            soundLineChart.setDrawBorders(true);
            soundLineChart.setBorderColor(Color.GRAY);
            soundLineChart.setBorderWidth(1);

            //YAxis
            YAxis yAxis = soundLineChart.getAxisLeft();
            yAxis.setTextSize(10f);
            yAxis.setTextColor(Color.BLACK);
            //yAxis.setAxisMinimum(0f); // 0 min
            //yAxis.setAxisMaximum(45f); // 45max
            soundLineChart.getAxisRight().setEnabled(false);

            //XAxis
            XAxis xAxis = soundLineChart.getXAxis();
            xAxis.setDrawLabels(false);
            xAxis.setPosition(XAxis.XAxisPosition.TOP);
            xAxis.setTextSize(10f);
            xAxis.setTextColor(Color.BLACK);
            xAxis.setDrawAxisLine(true);
            xAxis.setDrawGridLines(false);
            //xAxis.setGranularity(1f);


            //description a bal alsó sarokban
            Description description = new Description();
            description.setText("Hangerősség érzéklelő");
            description.setTextColor(Color.GRAY);
            description.setTextSize(13);
            soundLineChart.setDescription(description);

            //Legends
            Legend legend = soundLineChart.getLegend();

            legend.setEnabled(false);//!!!!!
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
                        Toast.makeText(SoundActivity.this, "Kapcsolódva", Toast.LENGTH_LONG).show();
                        subSound();
                        LineData data = new LineData();
                        soundLineChart.setData(data);
                        soundLineChart.invalidate();
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Toast.makeText(SoundActivity.this, "Nem sikerült a kapcsolódás", Toast.LENGTH_LONG).show();

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
                    if (topic.equals(soundTopic)){
//                    subText.setText(new String(message.getPayload()));
                        addEntrySound(Float.valueOf(message.toString()));


                    }

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        }
        public void subSound(){
            try{
                client.subscribe(soundTopic,0)
                ;}
            catch(MqttException e){
                e.printStackTrace();

            }

        }
        public void addEntrySound(float value) {

            LineData dataSound = soundLineChart.getData();


            if (dataSound != null){

                ILineDataSet setSound = dataSound.getDataSetByIndex(0);


                if (setSound == null) {
                    setSound = createSetSound();
                    dataSound.addDataSet(setSound);
                }

                dataSound.addEntry(new Entry(setSound.getEntryCount(),value),0);
                Log.w("soundChart", setSound.getEntryForIndex(setSound.getEntryCount()-1).toString());

                dataSound.notifyDataChanged();

                soundLineChart.notifyDataSetChanged();

                // megjelenítendő entryk
                soundLineChart.setVisibleXRangeMaximum(10);
                // mChart.setVisibleYRange(30, AxisDependency.LEFT);

                // utolsó entryhez mozgatás
                soundLineChart.moveViewTo(setSound.getEntryCount()-1, dataSound.getYMax(), YAxis.AxisDependency.LEFT);

            }
        }

        private LineDataSet createSetSound() {
            LineDataSet setSound = new LineDataSet(null, null);
            setSound.setAxisDependency(YAxis.AxisDependency.LEFT);
            setSound.setColor(Color.parseColor("#eb5ef3"));
            setSound.setLineWidth(5f);
            //setSound.setCircleRadius(4f);
            //setSound.setFillAlpha(65);
            //setSound.setFillColor(Color.rgb(67, 164, 34));
            //setSound.setHighLightColor(Color.rgb(67, 164, 34));
            //setSound.setValueTextColor(Color.rgb(67, 164, 34));
            //setSound.setValueTextSize(9f);
            //setSound.setDrawValues(false);
            //új
            setSound.setDrawCircles(true);
            setSound.setDrawCircleHole(true);
            setSound.setCircleColor(Color.RED);
            setSound.setCircleHoleColor(Color.parseColor("#eb5ef3"));
            setSound.setCircleRadius(11);
            setSound.setCircleHoleRadius(9);

            setSound.setDrawValues(true);
            setSound.setValueTextSize(10);
            setSound.setValueTextColor(Color.BLACK);
            //setSound.enableDashedLine(5,10,0);//szaggatott
            return setSound;
        }

    }
