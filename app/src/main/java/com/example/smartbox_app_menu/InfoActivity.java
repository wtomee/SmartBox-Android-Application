package com.example.smartbox_app_menu;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_info);

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.doboz)
                .setDescription("Applikáció a GAIN SmartBoxhoz")
                .addItem(new Element().setTitle("Verzió 1.0"))
                .addGroup("Kérdésed van?")
                .addEmail("wurtht@gain.uni-sopron.hu","E-mail")
                .addGitHub("wtomee","GitHub")
                .addItem(createCopyright())
                .create();

        setContentView(aboutPage);
    }

    private Element createCopyright() {
        Element copyright = new Element();
        final String copyrightString = String.format("© %d SmartBox app by Würth Tamás", Calendar.getInstance().get(Calendar.YEAR));
        copyright.setTitle(copyrightString);
        copyright.setGravity(Gravity.CENTER);

        copyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InfoActivity.this, copyrightString, Toast.LENGTH_SHORT).show();
            }
        });
        return copyright;

    }

}
