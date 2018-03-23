package shuvalov.nikita.graphviewsexample;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import shuvalov.nikita.piechartview.PieChartView;
import shuvalov.nikita.piechartview.PieGraphable;

public class PieChartExampleActivity extends AppCompatActivity implements View.OnClickListener{
    private Button mValueButton, mColorButton;
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
        mPieChartView = findViewById(R.id.pie_chart_view);
    }

    private void initViews(){
        mPieChartView.setGraphables(createRandomDummyData());
        mPieChartView.setColors(generateRandomColors());
        mValueButton.setOnClickListener(this);
        mColorButton.setOnClickListener(this);
    }

    private List<PieGraphable> createRandomDummyData(){
        List<PieGraphable> dummyData = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_VALUES; i++){
            dummyData.add(new MyPieGraphables(Long.MAX_VALUE, "Hello world"));
        }
        return dummyData;
    }

    private List<Integer> generateRandomColors(){
        List<Integer> colors = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_VALUES; i++){
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
                mPieChartView.setColors(generateRandomColors());
                break;
            case R.id.change_values_button:
                mPieChartView.setGraphables(createRandomDummyData());
                break;
        }
        mPieChartView.invalidate();
    }
}
