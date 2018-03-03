package shuvalov.nikita.graphviewsexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import shuvalov.nikita.line_graph_view.LineGraphView;
import shuvalov.nikita.line_graph_view.LineGraphable;

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
        List<LineGraphable> dummyData = createRandomDummyData();
        mLineGraphView = new LineGraphView.Builder(dummyData)
                .includeAxes()
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

    private List<LineGraphable> createRandomDummyData(){
        List<LineGraphable> dummyData = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        for(int i = 0; i < NUMBER_OF_VALUES; i++){
            dummyData.add(new MyLineGraphable(i * (i + 5), new Random().nextDouble() * 10));
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
                mLineGraphView.setGraphables(createRandomDummyData());
                break;
            case R.id.axis_toggle_button:
                mLineGraphView.setShowAxes(!mLineGraphView.isShowingAxes());
                break;
        }
        mLineGraphView.invalidate();
        refreshInfoText();
    }
}
