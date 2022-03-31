package com.example.driverdector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private ColumnChart test1;
    //int values[]={56,134,57,93};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test1=findViewById(R.id.chart1);


    }
}