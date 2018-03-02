package shuvalov.nikita.graphviewsexample;

import shuvalov.nikita.piechartview.PieGraphable;

/**
 * Created by NikitaShuvalov on 3/1/18.
 */

public class MyPieGraphables implements PieGraphable<Float> {
    private Float someValue;
    private String someName;

    public MyPieGraphables(Float someValue, String someName) {
        this.someValue = someValue;
        this.someName = someName;
    }

    @Override
    public Float getValue() {
        return someValue;
    }
}
