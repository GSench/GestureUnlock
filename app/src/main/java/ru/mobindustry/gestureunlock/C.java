package ru.mobindustry.gestureunlock;

import android.content.Context;

/**
 * Created by Григорий on 26.04.2016.
 */
public class C {

    public static final String CONSTANTS = "constants";

    private static final String valuableDelta = "delta";
    private static final String correctPoints = "correct";
    private static final String fault = "fault";

    public static float VALUABLE_DELTA(Context context){
        return context.getSharedPreferences(CONSTANTS, Context.MODE_PRIVATE).getFloat(valuableDelta, 0.1f);
    }

    public static float CORRECT_POINTS(Context context){
        return context.getSharedPreferences(CONSTANTS, Context.MODE_PRIVATE).getFloat(correctPoints, 0.95f);
    }

    public static float FAULT(Context context){
        return context.getSharedPreferences(CONSTANTS, Context.MODE_PRIVATE).getFloat(fault, 0.5f);
    }

    public static void setValuableDelta(float d, Context context){
        context.getSharedPreferences(CONSTANTS, Context.MODE_PRIVATE)
                .edit()
                .putFloat(valuableDelta, d)
                .commit();
    }

    public static void setCorrectPoints(float p, Context context){
        context.getSharedPreferences(CONSTANTS, Context.MODE_PRIVATE)
                .edit()
                .putFloat(correctPoints, p)
                .commit();
    }

    public static void setFault(float f, Context context){
        context.getSharedPreferences(CONSTANTS, Context.MODE_PRIVATE)
                .edit()
                .putFloat(fault, f)
                .commit();
    }

}
