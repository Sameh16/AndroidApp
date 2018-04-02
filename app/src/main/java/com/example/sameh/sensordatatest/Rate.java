package com.example.sameh.sensordatatest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.goodiebag.protractorview.ProtractorView;

import me.itangqi.waveloadingview.WaveLoadingView;

public class Rate extends AppCompatActivity {

    Intent gIntent;
    private String driverId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        gIntent = getIntent();
        driverId = gIntent.getStringExtra("dirverId");
        final WaveLoadingView waveLoadingView = findViewById(R.id.view);
        waveLoadingView.setProgressValue(4);
        Button b = findViewById(R.id.button2);
        final EditText text = findViewById(R.id.value);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String va = text.getText().toString();
                int vale = Integer.parseInt(va);
                waveLoadingView.setProgressValue(vale);
            }
        });
    }
}
