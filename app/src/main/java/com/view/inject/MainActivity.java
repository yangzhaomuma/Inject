package com.view.inject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import inject.view.com.anotation.BindView;

public class MainActivity extends AppCompatActivity {

    @BindView(100)
    int a = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


}
