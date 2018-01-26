package shuvalov.nikita.graphviewsexample;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import shuvalov.nikita.piechartview.PieChartView;

public class PieChartExampleActivity extends AppCompatActivity implements View.OnClickListener{
    private Button mValueButton, mColorButton;
    private FrameLayout mContainer;
    private PieChartView mPieChartView;

    private static final int NUMBER_OF_VALUES = 8;
    private static final int VALUE_CEILING = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart_example);
        findViews();
        initViews();
    }

    private void findViews(){
        mValueButton = findViewById(R.id.change_values_button);
        mColorButton = findViewById(R.id.change_colors_button);
        mContainer = findViewById(R.id.pie_chart_container);
    }

    private void initViews(){
        int n = NUMBER_OF_VALUES;
        mPieChartView = new PieChartView(this, createRandomDummyData(n, VALUE_CEILING),generateRandomColors(n));
        mContainer.addView(mPieChartView);

        mValueButton.setOnClickListener(this);
        mColorButton.setOnClickListener(this);
    }

    private List<Float> createRandomDummyData(int numberOfDataPoints, double ceiling){
        List<Float> dummyData = new ArrayList<>();
        for(int i = 0; i < numberOfDataPoints; i++){
            dummyData.add((float)(Math.random() * ceiling));
        }
        return dummyData;
    }

    private List<Integer> generateRandomColors(int n){
        List<Integer> colors = new ArrayList<>();
        for(int i = 0; i < n; i++){
            int r = (int)(Math.random() * 255);
            int g = (int)(Math.random() * 255);
            int b = (int)(Math.random() * 255);

            colors.add(Color.rgb(r, g,b));
        }
        return colors;
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.change_colors_button:
                mPieChartView.setColors(generateRandomColors(NUMBER_OF_VALUES));
                break;
            case R.id.change_values_button:
                mPieChartView.setValues(createRandomDummyData(NUMBER_OF_VALUES, VALUE_CEILING));
                break;
        }
        mPieChartView.invalidate();
    }
}
