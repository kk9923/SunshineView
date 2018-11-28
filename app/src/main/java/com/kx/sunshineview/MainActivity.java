package com.kx.sunshineview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private SunshineView mSunshineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSunshineView = findViewById(R.id.sunshineView);

    }

    public void Reload(View view) {
        mSunshineView.reLoad();
    }
}
