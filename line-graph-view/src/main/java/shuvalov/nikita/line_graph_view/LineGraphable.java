package shuvalov.nikita.line_graph_view;

/**
 * Created by NikitaShuvalov on 3/1/18.
 */


import android.support.annotation.NonNull;

/**
 * Allows objects to be graphed to the LineGraphView.
 * The acceptable Number types for the interface are: Integer, Double, Float
 *
 * NOTE: Linegraphables should be sortable by their x-axis values to avoid unexpected oddities.
 *
 *
 * @param <xAxis> Values to plot along the X-axis. Will be ignored if GraphView's AutoX setting is set to true. The last value will
 * @param <yAxis> Values to plot on the Y-axis.
 */
public interface LineGraphable<xAxis extends Number, yAxis extends Number> extends Comparable<LineGraphable> {
    xAxis getXValue();
    yAxis getYValue();

    @Override
    int compareTo(@NonNull LineGraphable lineGraphable);
}
