package shuvalov.nikita.graphviewsexample;

import shuvalov.nikita.piechartview.PieGraphable;

/**
 * Created by NikitaShuvalov on 3/1/18.
 */

public class MyPieGraphables implements PieGraphable<Long> {
    private Long someValue;
    private String someName;

    public MyPieGraphables(Long someValue, String someName) {
        this.someValue = someValue;
        this.someName = someName;
    }

    @Override
    public Long getValue() {
        return someValue;
    }
}
