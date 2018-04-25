package com.view.inject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import inject.view.com.anotation.BindView;
import inject.view.com.view.api.Inject;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.test)
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Inject.bind(this);
        textView.setText("3");
    }


}
