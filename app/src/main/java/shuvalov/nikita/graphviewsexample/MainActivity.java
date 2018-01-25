package shuvalov.nikita.graphviewsexample;

import android.content.DialogInterface;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button mChangeButon;
    private FrameLayout mGraphContainer;
    private LineGraphView mLineGraphView;
    private TextView mChartInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        init();
    }

    private void findViews(){
        mGraphContainer = findViewById(R.id.graph_container);
        mChangeButon = findViewById(R.id.change_data_button);
        mChartInfo = findViewById(R.id.graph_type_text);
    }

    private void init(){
        List<Float> dummyData = createRandomDummyData(50, 10);
        mLineGraphView = new LineGraphView.LineGraphViewBuilder(dummyData)
                .setFillColor(Color.BLUE)
                .setLineColor(Color.RED)
                .includeAxes()
                .useProgressBased()
                .build(this);
        mGraphContainer.addView(mLineGraphView);
        mGraphContainer.setOnClickListener(this);
        mChangeButon.setOnClickListener(this);

        refreshInfoText();
    }

    private void refreshInfoText(){
        String infoText = "Chart set to:\n" + (mLineGraphView.isProgressBased() ? "Progressive" : "Instance");
        mChartInfo.setText(infoText);
    }

    private List<Float> createRandomDummyData(int numberOfDataPoints, double ceiling){
        List<Float> dummyData = new ArrayList<>();
        for(int i = 0; i < numberOfDataPoints; i++){
            dummyData.add((float)(Math.random() * ceiling));
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
                mLineGraphView.setValues(createRandomDummyData(50,200));
                break;
        }
        mLineGraphView.invalidate();
        refreshInfoText();
    }
}
