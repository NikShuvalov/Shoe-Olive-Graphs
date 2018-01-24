package shuvalov.nikita.line_graph_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.View;

import java.util.List;


/**
 * Created by NikitaShuvalov on 6/14/17.
 */

public class LineGraphView extends View {
    private List<Integer> mLinePoints;
    private Paint mGoalLinePaint, mProgressLinePaint, mGoalPaint, mProgressPaint, mWhitePaint;
    private Paint mLegendGoalPaint, mLegendActualPaint;
    private Path mGoalPath, mProgressPath;
    private Rect mLineGraphRect, mLegendRect;
    private float mVerticalScale;
    private Paint mTextPaint, mAxisPaint, mAxisLabelPaint;
    private float mYLabelInterval, mAxisTextSize;
    private int mTotalProgress, mTotalGoal;



    public LineGraphView(Context context, List<Integer> plottedPoints) {
        super(context);
        mLinePoints = plottedPoints;
        mLineGraphRect= new Rect();
        mLegendRect = new Rect();
        preparePaints();
    }

    private void preparePaints() {
        mGoalLinePaint = new Paint();
        mGoalLinePaint.setStyle(Paint.Style.STROKE);
        mGoalLinePaint.setARGB(255, 0, 0, 0);
        mGoalLinePaint.setStrokeWidth(4f);
        mGoalLinePaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0f));

        mProgressLinePaint = new Paint();
        mProgressLinePaint.setStyle(Paint.Style.STROKE);
        mProgressLinePaint.setARGB(255, 0, 0, 0);
        mProgressLinePaint.setStrokeWidth(4f);

        mGoalPaint = new Paint();
        mGoalPaint.setStyle(Paint.Style.FILL);
        mGoalPaint.setARGB(255, 200, 200, 255);

        mProgressPaint = new Paint();
        mProgressPaint.setStyle(Paint.Style.FILL);
        mProgressPaint.setARGB(125, 0, 0, 255);

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

    private void definePaths(){
        int totalGoalTime = 0;
        int totalProgressTime = 0;
        if(mLinePoints ==null || mLinePoints.isEmpty()) {
            return;
        }

//        int startWeek = mLinePoints.get(0).getWeekNum();
//        int endWeek = AppUtils.getCurrentWeekNum();
//        float weekInterval = mLineGraphRect.width()/((endWeek - startWeek) +1f);
        int left = mLineGraphRect.left;
        mGoalPath = new Path();
        mProgressPath = new Path();
        mProgressPath.moveTo(mLineGraphRect.left, mLineGraphRect.bottom);
        mGoalPath.moveTo(mLineGraphRect.left, mLineGraphRect.bottom);

//        int cursorWeek = startWeek;
        int j = 0;
        for(int i = 0; i < mLinePoints.size(); i++){
            Goal goal = mLinePoints.get(i);
            totalGoalTime+=goal.getEndMilli()/60000;
            totalProgressTime+= goal.getCurrentMilli()/60000;

            if(cursorWeek != goal.getWeekNum()){
                mProgressPath.lineTo(left + (weekInterval * j), mLineGraphRect.bottom - (totalProgressTime*mVerticalScale));
                mGoalPath.lineTo(left+(weekInterval*j), mLineGraphRect.bottom - (totalGoalTime * mVerticalScale));

                int previousGoalYear = cursorWeek/100;
                int thisGoalYear = goal.getWeekNum()/100;

                int previousWeekOfYear = cursorWeek%100;
                int thisWeekOfYear = goal.getWeekNum()%100;

                int weekDiff = 0;
                if(previousGoalYear == thisGoalYear){
                    weekDiff = thisWeekOfYear - previousWeekOfYear;
                }

                j+= weekDiff;
                cursorWeek = goal.getWeekNum();
            }
        }

        mProgressPath.lineTo(mLineGraphRect.right, mLineGraphRect.bottom - (totalProgressTime*mVerticalScale));
        mGoalPath.lineTo(mLineGraphRect.right, mLineGraphRect.bottom - (totalGoalTime * mVerticalScale));

        mProgressPath.lineTo(mLineGraphRect.right, mLineGraphRect.bottom);
        mGoalPath.lineTo(mLineGraphRect.right, mLineGraphRect.bottom);

        mProgressPath.close();
        mGoalPath.close();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        setChartRect(left, top, right, bottom);
        setLegendRect();
        definePaths();

    }

    private void setChartRect(int left, int top, int right, int bottom){
        int width = right - left;
        int height = bottom - top;
        int centerY = (top+bottom)/2;
        if (height > width) {
            height = (int) (width / 1.78);
            mLineGraphRect.set(right/10, centerY - height/2, (int)(right*.9), centerY + height/2);
            determineVerticalScale(height *.9f);
        }else{
            height = (int)(height*.8);
            mLineGraphRect.set(right/10, centerY - height/2, (int)(right*.9), centerY + (int)(height/2.2));
            determineVerticalScale(height *.9f);
        }
        setTextSize(width,height);
    }

    private void setLegendRect(){
        int left = mLineGraphRect.left + (int)(mLineGraphRect.width()*.03);
        int top = mLineGraphRect.top + (int)(mLineGraphRect.height()*.03);
        int right = (int)(left + mLineGraphRect.width()*.2);
        int bottom = (int)( top + mLineGraphRect.height()*.2);
        mLegendRect.set(left, top, right, bottom);
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

    private void determineVerticalScale(float maxHeight){
        int totalProgress = 0;
        int totalGoal = 0;
        for(Goal g: mLinePoints){
            totalProgress+= g.getCurrentMilli()/60000;
            totalGoal+= g.getEndMilli()/60000;
        }
        mVerticalScale = totalProgress > totalGoal ?
                maxHeight/totalProgress:
                maxHeight/totalGoal;
        mYLabelInterval = totalProgress > totalGoal ?
                totalProgress * 10/36f :
                totalGoal*10/36f;
        mTotalGoal = totalGoal;
        mTotalProgress = totalProgress;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mGoalPath !=null && mProgressPath!=null) {
            drawGraph(canvas);
            drawAxes(canvas);
            drawLegend(canvas);
        }else{
            canvas.drawText("Not enough data to graph", canvas.getWidth()/5, canvas.getHeight()/2, mTextPaint);
        }
    }

    private void drawGraph(Canvas canvas){
        canvas.drawPath(mGoalPath, mGoalPaint);
        canvas.drawPath(mProgressPath, mProgressPaint);
        canvas.drawPath(mGoalPath, mGoalLinePaint);
        canvas.drawPath(mProgressPath, mProgressLinePaint);
        canvas.drawRect(mLineGraphRect, mProgressLinePaint);
    }

    private void drawAxes(Canvas canvas){
        //Draw x Axis lines & values
        int xIntervalLength = mLineGraphRect.width()/4;
        int left = mLineGraphRect.left;
        int startWeek = mLinePoints.get(0).getWeekNum();
        long startMillis = AppUtils.getWeekStartMillis(startWeek);
        int endWeek = AppUtils.getCurrentWeekNum();
        long endMillis = AppUtils.getWeekEndMillis(endWeek);
        long milliInterval = (endMillis - startMillis)/4;
        float x;
        float y1;
        for(int i =0; i < 5; i ++) {
            x = left + (xIntervalLength*i);
            float y0 = mLineGraphRect.bottom;
            if((y1 = mLineGraphRect.bottom + mLineGraphRect.height()/20) >= canvas.getClipBounds().bottom){
                y1 = (mLineGraphRect.bottom*3 + canvas.getClipBounds().bottom)/4;
            }
            canvas.drawLine(x,y0, x,y1,mProgressLinePaint);
            String date = AppUtils.getDate(startMillis + (milliInterval*i));
            canvas.drawText(date, x-xIntervalLength/5f,y1 + (y1-y0), mAxisPaint);
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
            canvas.drawLine(x0, y, x1, y, mProgressLinePaint);
            String yLabelText = String.valueOf((int)((0+mYLabelInterval*i)/60));
            if(i == 0){
                yLabelText += " hrs";
            }
            canvas.drawText(yLabelText,x1 - (yLabelText.length()*(int)(mAxisTextSize/1.5)),y + mAxisTextSize/2,mAxisPaint);
        }

        //Draw final values
        float yGoal = mLineGraphRect.bottom - (mTotalGoal*mVerticalScale);
        float yProgress = mLineGraphRect.bottom - (mTotalProgress*mVerticalScale);
        canvas.drawLine(mLineGraphRect.right, yGoal, mLineGraphRect.right+20, yGoal,mGoalLinePaint);
        canvas.drawLine(mLineGraphRect.right, yProgress, mLineGraphRect.right+20,yProgress,mProgressLinePaint);
        canvas.drawText(String.valueOf(mTotalGoal/60), mLineGraphRect.right+30, yGoal, mAxisPaint);
        canvas.drawText(String.valueOf(mTotalProgress/60), mLineGraphRect.right+30, yProgress, mAxisPaint);
    }

    private void drawLegend(Canvas canvas){
        canvas.drawRect(mLegendRect,mWhitePaint);
        canvas.drawRect(mLegendRect,mProgressLinePaint);

        float x0 = mLegendRect.right - mLegendRect.width()*.2f;
        float x1 = mLegendRect.right - mLegendRect.width()*.05f;
        float y0 = mLegendRect.top + mLegendRect.height()*.35f;
        float y1 = mLegendRect.top + mLegendRect.height()*.7f;
        Path dashedLine = new Path();
        dashedLine.moveTo(x0, y0);
        dashedLine.lineTo(x1, y0);
        canvas.drawPath(dashedLine, mGoalLinePaint);
        canvas.drawLine(x0, y1, x1, y1, mProgressLinePaint);

//        float textXStart = mLegendRect.left + mLegendRect.width()*.1f ;
//        canvas.drawText("Goal",textXStart, y0, mAxisPaint);
//        canvas.drawText("Actual", textXStart, y1, mAxisPaint);
    }
}
