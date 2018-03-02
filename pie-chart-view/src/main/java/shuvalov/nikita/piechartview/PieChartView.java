package shuvalov.nikita.piechartview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import static android.content.ContentValues.TAG;

/**
 * Created by NikitaShuvalov on 6/13/17.
 */

public class PieChartView extends View {
    private List<PieGraphable> mGraphables;
    private List<Integer> mColors;
    private Number mTotalValue;
    private RectF mCircle;
    private Paint mLinePaint;
    private ArrayList<Paint> mColorPaints;


    /**
     *
     * @param graphables Graphable objects that will be graphed
     * @param colors The colors used to distinguish each value. Leaving blank will create a list of shades of gray
     *
     */
    public PieChartView(Context context, List<PieGraphable> graphables, @Nullable List<Integer> colors) {
        super(context);
        mGraphables = graphables;
        mColors = colors;
        init();
    }

    private void init(){
        mCircle = new RectF();
        calculateTotal();

        mLinePaint = new Paint();
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(4f);
        prepColors();
    }

    private void calculateTotal(){
        if(mGraphables.isEmpty()){
            mTotalValue = 0;
        }else {
            Number sample = mGraphables.get(0).getValue();
            if (sample instanceof Long) {
                getLongTotal(mGraphables);
            } else if (sample instanceof Integer) {
                getIntegerTotal(mGraphables);
            } else if (sample instanceof Double) {
                getDoubleTotal(mGraphables);
            } else if (sample instanceof Float) {
                getFloatTotal(mGraphables);
            } else {
                throw new IllegalArgumentException("PieGraphable only accepts Long, Integer, Double or Float");
            }
        }
    }

    private void getLongTotal(List<PieGraphable> graphables){
        Long totalValue = 0L;
        for(PieGraphable graphable : graphables){
            totalValue += graphable.getValue().longValue();
        }
        Log.d(TAG, "getLongTotal: " + totalValue);
        mTotalValue = totalValue;
    }

    private void getIntegerTotal(List<PieGraphable> graphables){
        Integer totalValue = 0;
        for(PieGraphable graphable : graphables){
            totalValue += graphable.getValue().intValue();
        }
        Log.d(TAG, "getIntegerTotal: " + totalValue);
        mTotalValue = totalValue;
    }

    private void getDoubleTotal(List<PieGraphable> graphables){
        Double totalValue = 0.0;
        for(PieGraphable graphable : graphables){
            totalValue += graphable.getValue().doubleValue();
        }
        Log.d(TAG, "getDoubleTotal: " + totalValue);
        mTotalValue = totalValue;
    }


    private void getFloatTotal(List<PieGraphable> graphables){
        Float totalValue = 0.0f;
        for(PieGraphable graphable : graphables){
            totalValue += graphable.getValue().floatValue();
        }
        Log.d(TAG, "getFloatTotal: " + totalValue);
        mTotalValue = totalValue;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        float centerX = (left + right) /2;
        float centerY = (top + bottom) / 2;
        float width = right - left;
        float height = bottom - top;
        float radius = width > height ?
                height * .45f :
                width*.45f;
        mCircle.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
    }

    private void prepColors(){
        mColorPaints = new ArrayList<>();
        if(mColors != null && !mColors.isEmpty()) {
            for (int color : mColors) {
                Paint p = new Paint();
                p.setStyle(Paint.Style.FILL);
                p.setColor(color);
                mColorPaints.add(p);
            }
        }else{
            for(int i = 0; i < 9; i++){
                Paint p = new Paint();
                p.setStyle(Paint.Style.FILL);
                int v = i %2 == 0 ?
                        (i+1) *(15+i) : 255 - (i+1) *(15+i);
                p.setColor(Color.rgb(v, v, v));
                mColorPaints.add(p);
            }
        }
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float startAngle = 0;
        for(int i = 0; i < mGraphables.size(); i ++){
            float percent = getPercentage(mGraphables.get(i));
            Log.d(TAG, "onDraw: percent" + percent);
            float arc =percent * 360f;
            canvas.drawArc(mCircle, startAngle,arc, true, mColorPaints.get(i % mColorPaints.size()));
            canvas.drawArc(mCircle, startAngle,arc, true, mLinePaint);
            startAngle+= arc;
        }
    }

    private float getPercentage(PieGraphable pieGraphable){
        Number val = pieGraphable.getValue();
        if(val instanceof Integer){
            return (float)pieGraphable.getValue().intValue()/mTotalValue.intValue();
        }else if(val instanceof Long){
            return (float)((double)pieGraphable.getValue().longValue()/mTotalValue.longValue());
        }else if(val instanceof Double){
            return (float)(pieGraphable.getValue().doubleValue()/mTotalValue.doubleValue());
        }else if(val instanceof Float){
            return pieGraphable.getValue().floatValue()/mTotalValue.floatValue();

        }
        return -1.0f;
    }

    public void setGraphables(List<PieGraphable> graphables){
        mGraphables = graphables;
        calculateTotal();
    }

    public void setColors(List<Integer> colors){
        mColors = colors;
        prepColors();
    }
}
