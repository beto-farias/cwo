package cwo.pakidermo.dgom.com.mx.cwo.to;

/**
 * Created by beto on 29/01/18.
 */

public class ExerciseDataType {

    public static final int TYPE_TITLE = 0;
    public static final int TYPE_SEPARATOR = 1;
    public static final int TYPE_EXERCISE = 2;


    private int dataType;

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }
}
