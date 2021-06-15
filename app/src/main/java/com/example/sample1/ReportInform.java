package com.example.sample1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class ReportInform extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_inform);


        final Button mapButton = (Button)findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportInform.this, Maps.class);
                ReportInform.this.startActivity(intent);
            }
        });

        final Button endButton = (Button)findViewById(R.id.endButton);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.Glevel_Clear();
                MainActivity.Gposition_Clear();
                MainActivity.Gdisease_Clear();
                finish();
            }
        });

        final TextView level = (TextView) findViewById(R.id.level);
        final TextView disease = (TextView) findViewById(R.id.disease);
        final TextView position = (TextView) findViewById(R.id.position);

        level.setText("환자 응급도: " + MainActivity.Glevel_Get());
        disease.setText("환자 상태: " + MainActivity.Gdisease_Get());
        position.setText("환자 위치: " + MainActivity.Gposition_Get());
    }
}