package shuvalov.nikita.graphviewsexample;

import android.support.annotation.NonNull;

import shuvalov.nikita.line_graph_view.LineGraphable;

/**
 * Created by NikitaShuvalov on 3/2/18.
 */

public class MyLineGraphable implements LineGraphable<Integer, Double> {

    private Integer xAxis;
    private Double yAxis;

    public MyLineGraphable(Integer xAxis, Double yAxis) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }

    @Override
    public Integer getXValue() {
        return xAxis;
    }

    @Override
    public Double getYValue() {
        return yAxis;
    }

    @Override
    public int compareTo(@NonNull LineGraphable lineGraphable) {
        return xAxis - lineGraphable.getXValue().intValue();
    }
}
