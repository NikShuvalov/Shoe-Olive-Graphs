package shuvalov.nikita.line_graph_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * Created by NikitaShuvalov on 6/14/17.
 */

public class LineGraphView extends View {
    private List<LineGraphable> mGraphables;
    private Paint mLinePaint, mFillPaint, mWhitePaint;
    private Paint mLegendGoalPaint, mLegendActualPaint, mGraphOutlinePaint;
    private Path mLinePath;
    private Rect mLineGraphRect, mLegendRect;
    private Paint mTextPaint, mAxisPaint, mAxisLabelPaint;
    private float mAxisTextSize;
//    private float mVerticalScale;
    private Number mPeakValue;
    private float mYPerPixel; //FixMe?: Might need to be a double to avoid precision loss

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


// ======================================= Lines/Paths ======================================
    private void initPaths(){
        if(mGraphables ==null || mGraphables.size() < 2) {
            return;
        }

        mLinePath = new Path();
        mLinePath.moveTo(mLineGraphRect.left, mLineGraphRect.bottom);

        if(this.isProgressBased){
            useProgressBased();
        }else{
            useInstanceBased();
        }

        mLinePath.lineTo(mLineGraphRect.right, mLineGraphRect.bottom);
        mLinePath.close();
    }

    private void useProgressBased(){
        getPeakValue(true);
//        determineVerticalScale(mLineGraphRect.height() *.9f);
        float xIntervalSize = xPerPixel(mLineGraphRect.width());
        float value = 0;
        for(int i = 0; i < mGraphables.size(); i++) {
//            value += mGraphables.get(i); //FixMe: Oh god
            mLinePath.lineTo(mLineGraphRect.left + xIntervalSize * (i+1), mLineGraphRect.bottom - (mYPerPixel * value));
        }
    }
    private void useInstanceBased(){
        getPeakValue(false);
        plotInstanceLineGraph();

//        determineVerticalScale(mLineGraphRect.height() * .9f);
//        determineYLabelInterval();
//        float xIntervalSize = xPerPixel(mLineGraphRect.width());
//        float yPerPixel = (float)(mLineGraphRect.height()/(mMaxValue * 1.1f));
//        for(int i = 0; i < mGraphables.size(); i++){
//            float value = mGraphables.get(i); //FixMe: Oh god this too.
//            mLinePath.lineTo(mLineGraphRect.left + xIntervalSize * i, mLineGraphRect.bottom - mYPerPixel * value);
//        }
    }

    private void plotInstanceLineGraph(){
        List<PointF> points = new ArrayList<>();
        createPointsWithYValues(points);
        attachXValuesToPoints(points);
        for(int i = 0; i < points.size(); i++){
            PointF p = points.get(i);
            mLinePath.lineTo(p.x, p.y);
        }
        mLinePath.lineTo(mLineGraphRect.right, mLineGraphRect.bottom);
    }

    private void createPointsWithYValues(List<PointF> points){
        Number ySample = mGraphables.get(0).getYValue();
        if(ySample instanceof Integer){
            for(LineGraphable graphable : mGraphables){
                PointF point = new PointF();
                point.y = mLineGraphRect.bottom - (mYPerPixel * graphable.getYValue().intValue());
                points.add(point);
            }
        }else if(ySample instanceof Float){
            for(LineGraphable graphable : mGraphables){
                PointF point = new PointF();
                point.y = mLineGraphRect.bottom - (mYPerPixel * graphable.getYValue().floatValue());
                points.add(point);
            }
        }else{//Double
            for(LineGraphable graphable : mGraphables){
                PointF point = new PointF();
                point.y = mLineGraphRect.bottom - (mYPerPixel * (float)graphable.getYValue().doubleValue());
                points.add(point);
            }
        }
    }

    private void attachXValuesToPoints(List<PointF> points){
        float xPerPixel = xPerPixel(mLineGraphRect.width());
        Number xSample = mGraphables.get(0).getXValue();
        if(xSample instanceof Integer){
            Integer minValue = mGraphables.get(0).getXValue().intValue();
            for(int i =0 ; i < mGraphables.size(); i++){
                LineGraphable graphable = mGraphables.get(i);
                float x = mLineGraphRect.left + ((graphable.getXValue().intValue() - minValue) * xPerPixel);
                points.get(i).x = mAutoX ? mLineGraphRect.left + xPerPixel * i:
                        x;
            }
        }else if(xSample instanceof Float){
            Float minValue = mGraphables.get(0).getXValue().floatValue();
            for(int i =0 ; i < mGraphables.size(); i++){
                LineGraphable graphable = mGraphables.get(i);
                points.get(i).x = mAutoX ? mLineGraphRect.left + xPerPixel * i:
                        mLineGraphRect.left + ((graphable.getXValue().floatValue() - minValue) * xPerPixel);
            }
        }else if (xSample instanceof Double){
            Double minValue = mGraphables.get(0).getXValue().doubleValue();
            for(int i =0 ; i < mGraphables.size(); i++){
                LineGraphable graphable = mGraphables.get(i);
                points.get(i).x = mAutoX ? mLineGraphRect.left + xPerPixel * i:
                        mLineGraphRect.left + (float)((graphable.getXValue().doubleValue() - minValue) * xPerPixel);
            }
        }
    }

//    private void determineVerticalScale(float maxHeight){
//        mVerticalScale = maxHeight/(float)(mPeakValue instanceof Long ?
//                mPeakValue.longValue() :
//                mPeakValue instanceof Integer ?
//                        mPeakValue.intValue() :
//                        mPeakValue instanceof Double ?
//                                mPeakValue.doubleValue() :
//                                mPeakValue.floatValue());
//    }

    /*
Explanation: Why 11/40f?
The VALUE amount of the top of the graph should be 11/10 of the Max VALUE since the Max Value will be positioned at 1/1.1 or 10/11 of the height of the graph,
AND the INTERVAL AMOUNT should be divided by 4 of that max value. Since that's how many parts we're partitioning it by.
Ergo: (11/10)/4 = 11/40f
*/
    private float determineYLabelInterval(){
        return (float)(mPeakValue instanceof Integer ?
                            mPeakValue.intValue() :
                            mPeakValue instanceof Double ?
                                    mPeakValue.doubleValue() :
                                    mPeakValue.floatValue()) * 11/40f;
    }

    private float xPerPixel(float graphWidth){
        if(mAutoX){
            return mLineGraphRect.width()/ (float)(mGraphables.size() - (isProgressBased ? 0 : 1));
        }
        LineGraphable maxGraphable = mGraphables.get(mGraphables.size()-1);
        LineGraphable minGraphable = mGraphables.get(0);
        Number sample = maxGraphable.getXValue();
        return graphWidth / (float)(sample instanceof Integer ?
                                maxGraphable.getXValue().intValue() - minGraphable.getXValue().intValue() :
                                sample instanceof Float ?
                                        maxGraphable.getXValue().floatValue() - minGraphable.getXValue().floatValue() :
                                        maxGraphable.getXValue().doubleValue() - minGraphable.getXValue().doubleValue());
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
            if (sample instanceof Integer) {
                getIntegerTotal(mGraphables);
            } else if (sample instanceof Double) {
                getDoubleTotal(mGraphables);
            } else if (sample instanceof Float) {
                getFloatTotal(mGraphables);
            } else {
                throw new IllegalArgumentException("LineGraphable only accepts Integer, Double or Float");
            }
        }
    }

    private void determineInstancePeakValue(){
        if(mGraphables.isEmpty()){
            mPeakValue = 0;
        }else {
            Number sample = mGraphables.get(0).getYValue();
            if (sample instanceof Integer) {
                getMaxInteger(mGraphables);
            } else if (sample instanceof Double) {
                getMaxDouble(mGraphables);
            } else if (sample instanceof Float) {
                getMaxFloat(mGraphables);
            } else {
                throw new IllegalArgumentException("LineGraphable only accepts Integer, Double or Float");
            }
        }
    }

    private void getMaxInteger(List<LineGraphable> graphables){
        int maxValue = Integer.MIN_VALUE;
        for(LineGraphable graphable : graphables){
            int xValue = graphable.getYValue().intValue();
            if(maxValue < xValue){
                maxValue = xValue;
            }
        }
        mPeakValue = maxValue;
        mYPerPixel = mLineGraphRect.height()/maxValue *.9f;
    }

    private void getMaxDouble(List<LineGraphable> graphables){
        Double maxValue = Double.MIN_VALUE;
        for(LineGraphable graphable : graphables){
            double xValue = graphable.getYValue().doubleValue();
            if(maxValue < xValue){
                maxValue = xValue;
            }
        }
        mPeakValue = maxValue;
        mYPerPixel = (float)(mLineGraphRect.height()/maxValue * .9f);
    }

    private void getMaxFloat(List<LineGraphable> graphables){
        float maxValue = Float.MIN_VALUE;
        for(LineGraphable graphable : graphables){
            float xValue = graphable.getYValue().floatValue();
            if(maxValue < xValue){
                maxValue = xValue;
            }
        }
        mPeakValue = maxValue;
        mYPerPixel = mLineGraphRect.height()/maxValue * .9f;
    }

    private void getIntegerTotal(List<LineGraphable> graphables){
        Integer totalValue = 0;
        for(LineGraphable graphable : graphables){
            totalValue += graphable.getYValue().intValue();
        }
        mPeakValue = totalValue;
        mYPerPixel = ((float)mLineGraphRect.height()/totalValue) * 1.1f;
    }

    private void getDoubleTotal(List<LineGraphable> graphables){
        Double totalValue = 0.0;
        for(LineGraphable graphable : graphables){
            totalValue += graphable.getYValue().doubleValue();
        }
        mPeakValue = totalValue;
        mYPerPixel = (float)(mLineGraphRect.height()/totalValue * 1.1);
    }


    private void getFloatTotal(List<LineGraphable> graphables){
        Float totalValue = 0.0f;
        for(LineGraphable graphable : graphables){
            totalValue += graphable.getYValue().floatValue();
        }
        mPeakValue = totalValue;
        mYPerPixel = (float)(mLineGraphRect.height()/totalValue * 1.1);
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
    }

    private void drawAxes(Canvas canvas){
        //Draw x Axis lines & values
        int xDivisions = 4;
        float xIntervalLength = mLineGraphRect.width()/4f;
        int left = mLineGraphRect.left;
        List<Float> xAxisMilestones = getXAxisLabelValues(mGraphables, xDivisions);
        float xIntervalValues = mGraphables.size()/4f; //ToDo: Should start at min, and end at max. Divided by 4.
        float x;
        float y1;
        //FixMe: Right now line graph x-Axis is just marked like an ID or by index of graphable items.
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
            if(isProgressBased){
                String yLabelText = String.valueOf((int)(yInterval * i));
                canvas.drawText(yLabelText,x1 - (yLabelText.length()*(int)(mAxisTextSize/1.5)),y + mAxisTextSize/2,mAxisPaint);

                //FixMe: If I'm going to do the total value on the right end of the graph, this only needs to be drawn once.
//                float totalYValue = mLineGraphRect.bottom - (mTotalValue * mVerticalScale);
//                canvas.drawLine(mLineGraphRect.right, totalYValue, mLineGraphRect.right+20, totalYValue, mLinePaint);
//                canvas.drawText(String.valueOf(mTotalValue), mLineGraphRect.right+30, totalYValue, mAxisPaint);
            }else{
                String yLabelText = String.valueOf((int)(yInterval * i));
                canvas.drawText(yLabelText,x1 - (yLabelText.length()*(int)(mAxisTextSize/1.5)),y + mAxisTextSize/2,mAxisPaint);
            }
        }


        //Draw final values
//        float yGoal = mLineGraphRect.bottom - (mTotalValue*mVerticalScale);
////        float yProgress = mLineGraphRect.bottom - (mTotalProgress*mVerticalScale);
//        canvas.drawLine(mLineGraphRect.right, yGoal, mLineGraphRect.right+20, yGoal,mLinePaint);
////        canvas.drawLine(mLineGraphRect.right, yProgress, mLineGraphRect.right+20,yProgress,mLinePaint);
//        canvas.drawText(String.valueOf(mTotalValue), mLineGraphRect.right+30, yGoal, mAxisPaint);
////        canvas.drawText(String.valueOf(mTotalProgress/60), mLineGraphRect.right+30, yProgress, mAxisPaint);
    }

    private List<Float> getXAxisLabelValues(List<LineGraphable> graphables, int dividors){
        float minValue = graphables.get(0).getXValue().floatValue();
//        float minValue = (float)(minNumber instanceof Integer ?
//                minNumber.intValue() :
//                minNumber instanceof Double ?
//                        minNumber.doubleValue() :
//                        minNumber.floatValue());
        int lastIndex = graphables.size() - 1;
        float maxValue = graphables.get(graphables.size() - 1).getXValue().floatValue();
//        float maxValue = (float)(maxNumber instanceof Integer ?
//                maxNumber.intValue() :
//                maxNumber instanceof Double ?
//                        maxNumber.doubleValue() :
//                        maxNumber.floatValue());
        float range = maxValue - minValue;
        float rangeIntervals = range/dividors;
        List<Float> xAxisLabelValues = new ArrayList<>();
        for(int i = 0; i <= dividors; i++){
            xAxisLabelValues.add(minValue + (rangeIntervals * i));
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



    //==================================== Set Methods =============================================

    public void setGraphables(List<LineGraphable> dataValues){
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


    public static class Builder {
        //Required Params
        private List<LineGraphable> mGraphables;
        private int mFillColor, mLineColor;
        private boolean isProgressBased, useAxes, autoX;

        /**
         *
         * @param graphables Should be sorted by xValue if autoX isn't set
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

        public boolean isAutoX() {
            return autoX;
        }

        public Builder setAutoX(boolean autoX) {
            this.autoX = autoX;
            return this;
        }

        public LineGraphView build(Context context) {
            return new LineGraphView(context, mGraphables, mFillColor, mLineColor, isProgressBased, useAxes, autoX);
        }
    }
}