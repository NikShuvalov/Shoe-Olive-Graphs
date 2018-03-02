package shuvalov.nikita.piechartview;

/**
 * Created by NikitaShuvalov on 3/1/18.
 */


/**
 * Interface that lets objects be graphed.
 *
 * @param <T> Long, Integer, Double and Float are the only acceptable types
 */
public interface PieGraphable<T extends Number> {
    T getValue();

}
