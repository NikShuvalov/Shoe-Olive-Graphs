package shuvalov.nikita.line_graph_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * Created by NikitaShuvalov on 6/14/17.
 */

//FixMe: Negative Y Values leads to graphline flowing UNDER the bounds the graph
public class LineGraphView extends View {
    private List<LineGraphable> mGraphables;
    private Paint mLinePaint, mFillPaint, mWhitePaint;
    private Paint mLegendGoalPaint, mLegendActualPaint, mGraphOutlinePaint;
    private Path mLinePath;
    private Path mXDividerPath, mYDividerPath;
    private Rect mLineGraphRect, mLegendRect;
    private Paint mTextPaint, mAxisPaint, mAxisLabelPaint;
    private Paint mDividerPaint;
    private float mAxisTextSize;
    private double mPeakValue;
    private double mYPerPixel;

    private boolean mXValuesAreDivided;

//==================================== Builder Set Variables ========================
    private int mFillColor, mLineColor;
    private boolean isProgressBased, mShowAxes, mAutoX; //Determines whether the graph will be progress based or instance based. In other words, if the values accumulate or they are what they are.

    //======================================= General Setup =========================================
    private LineGraphView(Context context,
                          List<LineGraphable> graphables,
                          int fillColor,
                          int lineColor,
                          boolean progressBased,
                          boolean showAxes,
                          boolean autoX) {
        super(context);
        mAutoX = autoX;
        mFillColor = fillColor;
        mLineColor = lineColor;
        isProgressBased = progressBased;
        mShowAxes = showAxes;

        mGraphables = graphables;
        mLineGraphRect= new Rect();
        mLegendRect = new Rect();
        preparePaints();
    }

    private void preparePaints() {
        mGraphOutlinePaint = new Paint();
        mGraphOutlinePaint.setStyle(Paint.Style.STROKE);
        mGraphOutlinePaint.setColor(Color.BLACK);
        mGraphOutlinePaint.setStrokeWidth(4f);

        mDividerPaint = new Paint();
        mDividerPaint.setStyle(Paint.Style.STROKE);
        mDividerPaint.setColor(Color.BLACK);
        mDividerPaint.setStrokeWidth(2f);
        mDividerPaint.setPathEffect(new DashPathEffect(new float[]{10,5},0));

        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(4f);

        mFillPaint = new Paint();
        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setColor(mFillColor);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);

        mAxisPaint = new Paint();
        mAxisPaint.setColor(Color.BLACK);

        mAxisLabelPaint  =new Paint();
        mAxisLabelPaint.setColor(Color.BLACK);

        mWhitePaint = new Paint();
        mWhitePaint.setColor(Color.WHITE);

        mLegendGoalPaint = new Paint();
        mLegendGoalPaint.setARGB(255, 200, 200, 255);
        mLegendGoalPaint.setStrokeWidth(4f);
        mLegendGoalPaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0f));

        mLegendActualPaint = new Paint();
        mLegendActualPaint.setARGB(125, 0, 0, 255);
        mLegendActualPaint.setStrokeWidth(4f);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        setChartRect(left, top, right, bottom);
        initLegendRect();
        initPaths();
    }

    private void setChartRect(int left, int top, int right, int bottom){
        int width = right - left;
        int height = bottom - top;
        int centerY = (top+bottom)/2;
        if (height > width) {
            height = (int) (width / 1.78);
            mLineGraphRect.set(right/10, centerY - height/2, (int)(right*.9), centerY + height/2);
        }else{
            height = (int)(height*.8);
            mLineGraphRect.set(right/10, centerY - height/2, (int)(right*.9), centerY + (int)(height/2.2));
        }
        setTextSize(width,height);
    }

    private void setTextSize(int width, int height){
        float textSize = width/20;
        mAxisTextSize = height/30f;
        mTextPaint.setTextSize(textSize);
        mAxisPaint.setTextSize(mAxisTextSize);
        mLegendGoalPaint.setTextSize(mAxisTextSize);
        mLegendActualPaint.setTextSize(mAxisTextSize);
        mAxisLabelPaint.setTextSize(mAxisTextSize*2);
    }

    private void initLegendRect(){
        int left = mLineGraphRect.left + (int)(mLineGraphRect.width()*.03);
        int top = mLineGraphRect.top + (int)(mLineGraphRect.height()*.03);
        int right = (int)(left + mLineGraphRect.width()*.2);
        int bottom = (int)( top + mLineGraphRect.height()*.2);
        mLegendRect.set(left, top, right, bottom);
    }

    //==================================== Set Methods =============================================

    public void setGraphables(List<LineGraphable> dataValues){
        if(dataValues != null && dataValues.size() > 1)Collections.sort(dataValues);
        mGraphables = dataValues;
        initPaths();
    }

    public void setProgressBased(boolean isProgressBased){
        this.isProgressBased = isProgressBased;
        initPaths();
    }

    public void setShowAxes(boolean showAxes) {
        mShowAxes = showAxes;
        initPaths();
    }

// ================================ Getters ==================================================

    public boolean isProgressBased(){
        return this.isProgressBased;
    }

    public boolean isShowingAxes() {
        return mShowAxes;
    }


// ======================================= Lines/Paths ======================================
    private void initPaths(){
        if(mGraphables ==null || mGraphables.size() < 2) {
            return;
        }

        mLinePath = new Path();
        mLinePath.moveTo(mLineGraphRect.left, mLineGraphRect.bottom);
        checkIfXZeroIsNeeded();
        if(this.isProgressBased){
            useProgressBased();
        }else{
            useInstanceBased();
        }

        if(mXValuesAreDivided){
            createXDividerLinePath();
        }
        mLinePath.lineTo(mLineGraphRect.right, mLineGraphRect.bottom);
        mLinePath.close();
    }

    private void useProgressBased(){
        getPeakValue(true);
        plotProgressLineGraph();
    }

    private void useInstanceBased(){
        getPeakValue(false);
        plotInstanceLineGraph();
    }

    private void plotProgressLineGraph(){
        List<PointF> points = new ArrayList<>();
        createPointsWithYValues(points);
        attachXValuesToPoints(points);
        for(PointF p : points){
            mLinePath.lineTo(p.x, p.y);
        }
        mLinePath.lineTo(mLineGraphRect.right, mLineGraphRect.bottom);
    }

    private void plotInstanceLineGraph(){
        List<PointF> points = new ArrayList<>();
        createPointsWithYValues(points);
        attachXValuesToPoints(points);
        for(PointF p :points){
            mLinePath.lineTo(p.x, p.y);
        }
        mLinePath.lineTo(mLineGraphRect.right, mLineGraphRect.bottom);
    }

    private void createPointsWithYValues(List<PointF> points) {
        double runningTotal = 0;
        for (LineGraphable graphable : mGraphables) {
            PointF point = new PointF();
            if(!isProgressBased) {
                point.y = mLineGraphRect.bottom - (float) (mYPerPixel * graphable.getYValue().doubleValue());
                points.add(point);
            }else{
                runningTotal += graphable.getYValue().doubleValue();
                point.y = mLineGraphRect.bottom - (float)(mYPerPixel * runningTotal);
                points.add(point);
            }
        }
    }

    private void attachXValuesToPoints(List<PointF> points){
        float xPerPixel = xPerPixel(mLineGraphRect.width());
        float minValue = mGraphables.get(0).getXValue().floatValue();
        for(int i =0 ; i < mGraphables.size(); i++){
            LineGraphable graphable = mGraphables.get(i);
            points.get(i).x = mAutoX ? mLineGraphRect.left + xPerPixel * i:
                    mLineGraphRect.left + (graphable.getXValue().floatValue() - minValue) * xPerPixel;
        }
    }

    private void createXDividerLinePath(){
        float minValue = mGraphables.get(0).getXValue().floatValue();
        float xZero = mLineGraphRect.left + (xPerPixel(mLineGraphRect.width()) * Math.abs(minValue));
        mXDividerPath = new Path();
        mXDividerPath.moveTo(xZero, mLineGraphRect.bottom);
        mXDividerPath.lineTo(xZero, mLineGraphRect.top);

    }


// ========================================= Scaling/Peak ===============================================
    /*
Explanation: Why 11/40f?
The VALUE amount of the top of the graph should be 11/10 of the Max VALUE since the Max Value will be positioned at 1/1.1 or 10/11 of the height of the graph,
AND the INTERVAL AMOUNT should be divided by 4 of that max value. Since that's how many parts we're partitioning it by.
Ergo: (11/10)/4 = 11/40f
*/
    private float determineYLabelInterval(){
        return (float)(mPeakValue * 11/40f);
    }

    private float xPerPixel(float graphWidth){
        if(mAutoX){
            return mLineGraphRect.width()/ (float)(mGraphables.size() - (isProgressBased ? 0 : 1));
        }
        LineGraphable maxGraphable = mGraphables.get(mGraphables.size()-1);
        LineGraphable minGraphable = mGraphables.get(0);
        return (float)(graphWidth / (maxGraphable.getXValue().doubleValue() - minGraphable.getXValue().doubleValue()));
    }

    private void checkIfXZeroIsNeeded() {
        Number min = mGraphables.get(0).getXValue();
        Number max = mGraphables.get(mGraphables.size() -1).getXValue();
        mXValuesAreDivided = min.doubleValue() < 0 && max.doubleValue() > 0;
    }

    private void getPeakValue(boolean isProgressive){
        if(isProgressive){
            determineSumPeakValue();
        }else{
            determineInstancePeakValue();
        }
    }

    private void determineSumPeakValue(){
        if(mGraphables.isEmpty()){
            mPeakValue = 0;
        }else {
            Number sample = mGraphables.get(0).getYValue();
            if (sample instanceof Integer || sample instanceof Float || sample instanceof Double) {
                getTotal(mGraphables);
            }else{
                throw new IllegalArgumentException("LineGraphable only accepts Integer, Double or Float");
            }
        }
    }

    private void determineInstancePeakValue(){
        if(mGraphables.isEmpty()){
            mPeakValue = 0;
        }else {
            Number sample = mGraphables.get(0).getYValue();
            if (sample instanceof Integer || sample instanceof Double || sample instanceof Float) {
                getMax(mGraphables);
            } else {
                throw new IllegalArgumentException("LineGraphable only accepts Integer, Double or Float");
            }
        }
    }

    private void getMax(List<LineGraphable> graphables){
        Double maxValue = Double.MIN_VALUE;
        for(LineGraphable graphable : graphables){
            double yValue = graphable.getYValue().doubleValue();
            if(maxValue < yValue){
                maxValue = yValue;
            }
        }
        mPeakValue = maxValue;
        mYPerPixel = mLineGraphRect.height()/maxValue * .9;
    }

    private void getTotal(List<LineGraphable> graphables){
        double totalValue = 0;
        for(LineGraphable graphable : graphables){
            totalValue += graphable.getYValue().doubleValue();
        }
        mPeakValue = totalValue;
        mYPerPixel = mLineGraphRect.height()/totalValue * .9;
    }



    //===============================  Draw Methods ================================================
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mLinePath !=null) {
            drawGraph(canvas);
            if(mShowAxes) {
                drawAxes(canvas);
            }
//            drawLegend(canvas);
        }else{
            canvas.drawText("Not enough data to graph", canvas.getWidth()/5, canvas.getHeight()/2, mTextPaint);
        }
    }

    private void drawGraph(Canvas canvas){
        canvas.drawPath(mLinePath, mFillPaint);
        canvas.drawPath(mLinePath, mLinePaint);
        canvas.drawRect(mLineGraphRect, mGraphOutlinePaint);
        if(mXValuesAreDivided){
            canvas.drawPath(mXDividerPath, mDividerPaint);
        }
    }

    private void drawAxes(Canvas canvas){
        //Draw x Axis lines & values
        int xDivisions = 4; //ToDo: Make this an option for the user to put in as well. Modularity! This value only affect a few things at the moment.
        float xIntervalLength = mLineGraphRect.width()/4f;
        int left = mLineGraphRect.left;
        List<Float> xAxisMilestones = getXAxisLabelValues(mGraphables, xDivisions);
        float x;
        float y1;
        for(int i =0; i <= xDivisions; i ++) {
            x = left + (xIntervalLength*i);
            float y0 = mLineGraphRect.bottom;
            if((y1 = mLineGraphRect.bottom + mLineGraphRect.height()/20) >= canvas.getClipBounds().bottom){
                y1 = (mLineGraphRect.bottom*3 + canvas.getClipBounds().bottom)/4;
            }
            canvas.drawLine(x,y0, x,y1,mGraphOutlinePaint);
            canvas.drawText(String.valueOf(xAxisMilestones.get(i).intValue()), x-xIntervalLength/5f,y1 + (y1-y0), mAxisPaint);
        }

        //Draw y Axis lines & values
        float yInterval = determineYLabelInterval();
        float yIntervalLength = mLineGraphRect.height()/4f;
        float y;
        float x0;
        float x1;
        for(int i = 0; i < 5; i++){
            y = mLineGraphRect.bottom - (i * yIntervalLength);
            x0 = mLineGraphRect.left;
            x1 = x0 - 20;
            canvas.drawLine(x0, y, x1, y, mGraphOutlinePaint);
            String yLabelText = String.valueOf((int)(yInterval * i));
            canvas.drawText(yLabelText,x1 - (yLabelText.length()*(int)(mAxisTextSize/1.5)),y + mAxisTextSize/2,mAxisPaint);
        }
        if(isProgressBased) { //Draws ending value total
            float finalValue = (float)(mLineGraphRect.bottom - (mPeakValue * mYPerPixel));
            canvas.drawLine(mLineGraphRect.right, finalValue, mLineGraphRect.right+20, finalValue,mLinePaint);
            canvas.drawText(String.valueOf(mPeakValue), mLineGraphRect.right + 30, finalValue, mAxisPaint);
        }
    }

    private List<Float> getXAxisLabelValues(List<LineGraphable> graphables, int dividors){
        double minValue = graphables.get(0).getXValue().doubleValue();
        double maxValue = graphables.get(graphables.size() - 1).getXValue().doubleValue();

        double range = maxValue - minValue;
        double rangeIntervals = range/dividors;
        List<Float> xAxisLabelValues = new ArrayList<>();
        for(int i = 0; i <= dividors; i++){
            xAxisLabelValues.add((float)(minValue + (rangeIntervals * i)));
        }
        return xAxisLabelValues;
    }

//    private void drawLegend(Canvas canvas){
//        canvas.drawRect(mLegendRect,mWhitePaint);
//        canvas.drawRect(mLegendRect,mGraphOutlinePaint);
//
//        float x0 = mLegendRect.right - mLegendRect.width()*.2f;
//        float x1 = mLegendRect.right - mLegendRect.width()*.05f;
//        float y0 = mLegendRect.top + mLegendRect.height()*.35f;
//        float y1 = mLegendRect.top + mLegendRect.height()*.7f;
//        Path dashedLine = new Path();
//        dashedLine.moveTo(x0, y0);
//        dashedLine.lineTo(x1, y0);
//        canvas.drawPath(dashedLine, mGoalLinePaint);
//        canvas.drawLine(x0, y1, x1, y1, mLinePaint);
//    }



    //ToDo: Allow user to set axis values with Decimals
    //ToDo: Allow user to have their x axis start at 0 as opposed to using min-value in graphable range as start.
    //ToDo: Handle negative values along y-axis
    public static class Builder {
        private List<LineGraphable> mGraphables;
        private int mFillColor, mLineColor;
        private boolean isProgressBased, useAxes, autoX;

        /**
         * The builder will sort the graphables upon building by the xaxis values as they should be ordered. (No need to pre-sort)
         *
         * Builder options:
         * FillColor - Fill color of the graph
         * LineColor - Line color of the graph
         * IncludeAxes - Whether to include labels on the y and x-axes
         * AutoX - Set true if you wish the graphView to set the x-axis increments to a uniform value based on size rather than the x values in your LineGraphable.
         *
         * @param graphables
         */
        public Builder(List<LineGraphable> graphables){
            mGraphables = graphables;
            mFillColor = Color.argb(50, 0, 0, 255);
            mLineColor = Color.argb(125,0,0,0);
            isProgressBased = false;
        }

        public Builder setFillColor(int color){
            mFillColor = color;
            return this;
        }

        public Builder setLineColor(int color){
            mLineColor = color;
            return this;
        }

        public Builder useProgressBased(){
            isProgressBased = true;
            return this;
        }

        public Builder includeAxes(){
            useAxes = true;
            return this;
        }

        public Builder setAutoX() {
            this.autoX = true;
            return this;
        }

        public LineGraphView build(Context context) {
            if(mGraphables != null && mGraphables.size() > 1) Collections.sort(mGraphables);
            return new LineGraphView(context, mGraphables, mFillColor, mLineColor, isProgressBased, useAxes, autoX);
        }
    }
}