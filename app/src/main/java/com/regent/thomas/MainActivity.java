package com.regent.thomas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import com.regent.thomas.PlanCompleteBarView.DataWrapper;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<DataWrapper> mDataWrappers=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final PlanCompleteBarView planCompleteBarView =findViewById(R.id.spcv);
        mDataWrappers = new ArrayList<>();
        mDataWrappers.add(new DataWrapper("11月", 700, 520));
        mDataWrappers.add(new DataWrapper("12月", 720, 300));
        mDataWrappers.add(new DataWrapper("13月", 1000, 410));
        mDataWrappers.add(new DataWrapper("14月", 700, 500));
        mDataWrappers.add(new DataWrapper("15月", 700, 1000));
        mDataWrappers.add(new DataWrapper("16月", 706, 560));
        mDataWrappers.add(new DataWrapper("17月", 500, 580));
        mDataWrappers.add(new DataWrapper("18月", 730, 500));
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                planCompleteBarView.setMaxValue(1000);
                planCompleteBarView.setStep(10);
                planCompleteBarView.setBarstyle(PlanCompleteBarView.BAR_STYLE_TIE);
                planCompleteBarView.setDataWrappers(mDataWrappers);
                planCompleteBarView.invalidate();
            }
        });
    }
}
