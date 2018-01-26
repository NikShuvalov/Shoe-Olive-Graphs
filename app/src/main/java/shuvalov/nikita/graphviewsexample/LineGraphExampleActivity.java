package shuvalov.nikita.graphviewsexample;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import shuvalov.nikita.line_graph_view.LineGraphView;

public class LineGraphExampleActivity extends AppCompatActivity implements View.OnClickListener{
    private Button mChangeButton, mAxisToggleButton;
    private FrameLayout mGraphContainer;
    private LineGraphView mLineGraphView;
    private TextView mChartInfo;


    private static final int NUMBER_OF_VALUES = 50;
    private static final int VALUE_CEILING = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph_example);
        findViews();
        init();
    }

    private void findViews(){
        mGraphContainer = findViewById(R.id.graph_container);
        mChangeButton = findViewById(R.id.change_data_button);
        mAxisToggleButton = findViewById(R.id.axis_toggle_button);
        mChartInfo = findViewById(R.id.graph_type_text);
    }

    private void init(){
        List<Float> dummyData = createRandomDummyData();
        mLineGraphView = new LineGraphView.Builder(dummyData)
                .includeAxes()
                .useProgressBased()
                .build(this);
        mGraphContainer.addView(mLineGraphView);
        mGraphContainer.setOnClickListener(this);
        mChangeButton.setOnClickListener(this);
        mAxisToggleButton.setOnClickListener(this);
        refreshInfoText();
    }

    private void refreshInfoText(){
        String infoText = "Chart set to:\n" + (mLineGraphView.isProgressBased() ? "Progressive" : "Instance") + "\n\nClick the chart to toggle";
        mChartInfo.setText(infoText);
    }

    private List<Float> createRandomDummyData(){
        List<Float> dummyData = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_VALUES; i++){
            dummyData.add((float)(Math.random() * VALUE_CEILING));
        }
        return dummyData;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.graph_container:
                mLineGraphView.setProgressBased(!mLineGraphView.isProgressBased());
                break;
            case R.id.change_data_button:
                mLineGraphView.setValues(createRandomDummyData());
                break;
            case R.id.axis_toggle_button:
                mLineGraphView.setShowAxes(!mLineGraphView.isShowingAxes());
                break;
        }
        mLineGraphView.invalidate();
        refreshInfoText();
    }
}
