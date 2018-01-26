package shuvalov.nikita.piechartview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NikitaShuvalov on 6/13/17.
 */

public class PieChartView extends View {
    private List<Float> mValues;
    private List<Integer> mColors;
    private float mTotalValue;
    private RectF mCircle;
    private Paint mLinePaint;
    private ArrayList<Paint> mColorPaints;

    /**
     *
     * @param values The values to be used in creating the pie chart
     * @param colors The colors used to distinguish each value. Leaving blank will create a list of shades of gray
     *
     */
    public PieChartView(Context context, List<Float> values, @Nullable List<Integer> colors) {
        super(context);
        mValues = values;
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
        mTotalValue = 0;
        for(Float v: mValues){
            mTotalValue += v;
        }
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
        for(int i = 0; i < mValues.size(); i ++){
            float v = mValues.get(i);
            float percent = v/ mTotalValue;
            float arc =percent * 360f;
            canvas.drawArc(mCircle, startAngle,arc, true, mColorPaints.get(i % mColorPaints.size()));
            canvas.drawArc(mCircle, startAngle,arc, true, mLinePaint);
            startAngle+= arc;
        }
    }

    public void setValues(List<Float> values){
        mValues = values;
        calculateTotal();
    }

    public void setColors(List<Integer> colors){
        mColors = colors;
        prepColors();
    }
}
