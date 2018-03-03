package shuvalov.nikita.line_graph_view;

/**
 * Created by NikitaShuvalov on 3/1/18.
 */


/**
 * Allows objects to be graphed to the LineGraphView.
 * The acceptable Number types for the interface are: Integer, Double, Float
 *
 *
 *
 * @param <xAxis> Values to plot along the X-axis. Will be ignored if GraphView's AutoX setting is set to true. The last value will
 * @param <yAxis> Values to plot on the Y-axis.
 */
public interface LineGraphable<xAxis extends Number, yAxis extends Number> {
    xAxis getXValue();
    yAxis getYValue();
}
