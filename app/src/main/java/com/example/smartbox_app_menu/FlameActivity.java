package com.example.smartbox_app_menu;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class FlameActivity extends AppCompatActivity {
    MqttAndroidClient client;
    String topicStr = "feed/flame";
    //TextView subText;
    String number;
    TextView flameString;
    LottieAnimationView flameAnimation, eyeAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flame);

        flameAnimation = (LottieAnimationView) findViewById(R.id.flame_animation);
        eyeAnimation = (LottieAnimationView) findViewById(R.id.eye_animation);
        flameString = (TextView) findViewById(R.id.flameString);
        flameString.setTextColor(Color.DKGRAY);

        //subText = (TextView) findViewById(R.id.subText);
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.2.80:1883", clientId);
        MqttConnectOptions options = new MqttConnectOptions();

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(FlameActivity.this, "Connected", Toast.LENGTH_LONG).show();
                    sub();
                    flameString.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(FlameActivity.this, "Not Connected", Toast.LENGTH_LONG).show();
                    flameString.setText(R.string.flameStringOff);
                    flameString.setVisibility(View.VISIBLE);

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
                //subText.setText(new String(message.getPayload()));
                number = new String(message.getPayload());
                //subText.setText(number);
                if (number.contentEquals("1")){
                    eyeAnimation.setVisibility(View.GONE);
                    flameAnimation.setVisibility(View.VISIBLE);



                }
                else {
                    flameAnimation.setVisibility(View.GONE);
                    eyeAnimation.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
    public void sub(){
        try{
            client.subscribe(topicStr,0)
            ;}
        catch(MqttException e){
            e.printStackTrace();

        }

    }

}
