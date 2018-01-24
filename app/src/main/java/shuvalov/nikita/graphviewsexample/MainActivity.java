package shuvalov.nikita.graphviewsexample;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import shuvalov.nikita.line_graph_view.LineGraphView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout graphContainer = findViewById(R.id.graph_container);
        boolean random = true;
        List<Float> dummyData = random ? createRandomDummyData(25, 2000):
                createLinearDummyData(50, 2000);

        graphContainer.addView(new LineGraphView.LineGraphViewBuilder(dummyData)
                .setFillColor(Color.GREEN)
                .setLineColor(Color.RED)
                .build(this));
    }

    private List<Float> createRandomDummyData(int numberOfDataPoints, double ceiling){
        List<Float> dummyData = new ArrayList<>();
        for(int i = 0; i < numberOfDataPoints; i++){
            dummyData.add((float)(Math.random() * ceiling));
        }
        return dummyData;
    }

    private List<Float> createLinearDummyData(int numberOfDataPoints, double ceiling){
        List<Float> dummyData = new ArrayList<>();
        float increaseAmount = (float)ceiling/(float)numberOfDataPoints;
        for(int i = 0; i < numberOfDataPoints; i++){
            dummyData.add(i* increaseAmount);
        }
        return dummyData;
    }
}
