package shuvalov.nikita.line_graph_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.View;

import java.util.Collections;
import java.util.List;


/**
 * Created by NikitaShuvalov on 6/14/17.
 */

public class LineGraphView extends View {
    private List<Float> mValues;
    private Paint mLinePaint, mFillPaint, mWhitePaint;
    private Paint mLegendGoalPaint, mLegendActualPaint, mGraphOutlinePaint;
    private Path mLinePath;
    private Rect mLineGraphRect, mLegendRect;
    private Paint mTextPaint, mAxisPaint, mAxisLabelPaint;
    private float mAxisTextSize;
    private float mVerticalScale, mYLabelInterval;
    private float mTotalValue;
    private double mMaxValue;

//==================================== Builder Set Variables ==============
    private int mFillColor, mLineColor;
    private boolean isProgressBased, mShowAxes; //Determines whether the graph will be progress based or instance based. In other words, if the values accumulate or they are what they are.

    private LineGraphView(Context context, List<Float> values, int fillColor, int lineColor, boolean progressBased, boolean showAxes) {
        super(context);
        mFillColor = fillColor;
        mLineColor = lineColor;
        isProgressBased = progressBased;
        mShowAxes = showAxes;

        mValues = values;
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

    private void initPaths(){
        if(mValues ==null || mValues.size() < 2) {
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
        determineVerticalScale(mLineGraphRect.height() *.9f);
        float xIntervalSize = mLineGraphRect.width()/ (float)(mValues.size());
        float yPerPixel = mLineGraphRect.height()/(mTotalValue * 1.1f);
        float value = 0;
        for(int i = 0; i < mValues.size(); i++) {
            value += mValues.get(i);
            mLinePath.lineTo(mLineGraphRect.left + xIntervalSize * (i+1), mLineGraphRect.bottom - (yPerPixel * value));
        }
    }

    private void determineVerticalScale(float maxHeight){
        mTotalValue = 0;
        for(float value: mValues){
            mTotalValue+= value;
        }
        mVerticalScale = maxHeight/mTotalValue;
        mYLabelInterval = mTotalValue * 11/40f;
    }

    private void useInstanceBased(){
        mMaxValue =  Collections.max(mValues);
        float xIntervalSize = mLineGraphRect.width()/ (float)(mValues.size()-1);
        float yPerPixel = (float)(mLineGraphRect.height()/(mMaxValue * 1.1f));
        for(int i = 0; i < mValues.size(); i++){
            float value = mValues.get(i);
            mLinePath.lineTo(mLineGraphRect.left + xIntervalSize * i, mLineGraphRect.bottom - yPerPixel * value);
        }
    }


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
        int xIntervalLength = mLineGraphRect.width()/4;
        int left = mLineGraphRect.left;
        int xIntervalValues = mValues.size()/5;
        float x;
        float y1;
        for(int i =0; i < 5; i ++) {
            x = left + (xIntervalLength*i);
            float y0 = mLineGraphRect.bottom;
            if((y1 = mLineGraphRect.bottom + mLineGraphRect.height()/20) >= canvas.getClipBounds().bottom){
                y1 = (mLineGraphRect.bottom*3 + canvas.getClipBounds().bottom)/4;
            }
            canvas.drawLine(x,y0, x,y1,mGraphOutlinePaint);
            canvas.drawText(String.valueOf(xIntervalValues * i), x-xIntervalLength/5f,y1 + (y1-y0), mAxisPaint);
        }

        //Draw y Axis lines & values
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
                mYLabelInterval = mTotalValue * 11/40f;
                String yLabelText = String.valueOf((int)(mYLabelInterval * i));
                canvas.drawText(yLabelText,x1 - (yLabelText.length()*(int)(mAxisTextSize/1.5)),y + mAxisTextSize/2,mAxisPaint);
                float totalYValue = mLineGraphRect.bottom - (mTotalValue*mVerticalScale);
                canvas.drawLine(mLineGraphRect.right, totalYValue, mLineGraphRect.right+20, totalYValue, mLinePaint);
                canvas.drawText(String.valueOf(mTotalValue), mLineGraphRect.right+30, totalYValue, mAxisPaint);
            }else{
                /*
                    Explanation: Why 11/40f?
                    The VALUE amount of the top of the graph should be 11/10 of the Max VALUE since the Max Value will be positioned at 1/1.1 or 10/11 of the height of the graph,
                    AND the INTERVAL AMOUNT should be divided by 4 of that max value. Since that's how many parts we're partitioning it by.
                    Ergo: (11/10)/4 = 11/40f
                 */
                mYLabelInterval = (float)mMaxValue * 11/40f;
                String yLabelText = String.valueOf((int)(mYLabelInterval * i));
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

    public void setValues(List<Float> dataValues){
        mValues = dataValues;
        initPaths();
    }


    public void setProgressBased(boolean isProgressBased){
        this.isProgressBased = isProgressBased;
        initPaths();
    }

    public boolean isProgressBased(){
        return this.isProgressBased;
    }

    public boolean isShowingAxes() {
        return mShowAxes;
    }

    public void setShowAxes(boolean showAxes) {
        mShowAxes = showAxes;
        initPaths();
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

    public static class Builder {
        //Required Params
        private List<Float> mValues;
        private int mFillColor, mLineColor;
        private boolean isProgressBased, useAxes;

        public Builder(List<Float> values){
            mValues = values;
            mFillColor = Color.argb(125, 0, 0, 255);
            mLineColor = Color.BLACK;
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

        public LineGraphView build(Context context) {
            return new LineGraphView(context, mValues, mFillColor, mLineColor, isProgressBased, useAxes);
        }
    }
}