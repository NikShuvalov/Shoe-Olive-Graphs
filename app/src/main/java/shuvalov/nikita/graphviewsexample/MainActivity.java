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
        List<Float> dummyData = createRandomDummyData(50, 10);

        graphContainer.addView(new LineGraphView.LineGraphViewBuilder(dummyData)
                .setFillColor(Color.BLUE)
                .setLineColor(Color.RED)
                .includeAxes()
                .useProgressBased()
                .build(this));
    }

    private List<Float> createRandomDummyData(int numberOfDataPoints, double ceiling){
        List<Float> dummyData = new ArrayList<>();
        for(int i = 0; i < numberOfDataPoints; i++){
            dummyData.add((float)(Math.random() * ceiling));
        }
        return dummyData;
    }

}
