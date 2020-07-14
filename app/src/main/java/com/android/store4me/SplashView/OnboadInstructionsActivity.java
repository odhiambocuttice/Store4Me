package com.android.store4me.SplashView;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.viewpager.widget.ViewPager;

import com.android.store4me.R;
import com.android.store4me.adapters.SliderAdapter;

public class OnboadInstructionsActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayoutCompat linearLayout;
    private SliderAdapter sliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_onboad_instructions);

        viewPager = (ViewPager) findViewById(R.id.slideViewpager);
        linearLayout = findViewById(R.id.dotslayout);
        sliderAdapter = new SliderAdapter(this);

        viewPager.setAdapter(sliderAdapter);

    }
}
