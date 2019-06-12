package com.example.smartbox_app_menu;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.view.View;




public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView temphumCard, flameCard, soundCard, infoCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temphumCard = (CardView) findViewById(R.id.tempCard);
        flameCard = (CardView) findViewById(R.id.flameCard);
        soundCard = (CardView) findViewById(R.id.soundCard);
        infoCard = (CardView) findViewById(R.id.infoCard);

        temphumCard.setOnClickListener(this);
        flameCard.setOnClickListener(this);
        soundCard.setOnClickListener(this);
        infoCard.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        Intent i;

        switch (v.getId()){
            case R.id.tempCard : i = new Intent(this,DHTActivity.class); startActivity(i);break;
            case R.id.flameCard : i = new Intent(this,FlameActivity.class);startActivity(i);break;
            case R.id.soundCard : i = new Intent(this, SoundActivity.class);startActivity(i);break;
            case R.id.infoCard : i = new Intent(this,InfoActivity.class);startActivity(i);break;
            default:break;


        }
    }
}
