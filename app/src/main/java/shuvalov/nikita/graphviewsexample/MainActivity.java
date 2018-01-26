package shuvalov.nikita.graphviewsexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button mLineGraphButton, mPieChartButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        init();
    }

    private void findViews(){
        mPieChartButton = findViewById(R.id.pie_chart_button);
        mLineGraphButton = findViewById(R.id.line_graph_button);
    }

    private void init(){
        mLineGraphButton.setOnClickListener(this);
        mPieChartButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.line_graph_button:
                Intent lineGraphIntent = new Intent(this, LineGraphExampleActivity.class);
                startActivity(lineGraphIntent);
                break;
            case R.id.pie_chart_button:
                Intent pieChartIntent = new Intent(this, PieChartExampleActivity.class);
                startActivity(pieChartIntent);
                break;
        }
    }
}
