package cwo.pakidermo.dgom.com.mx.cwo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDate;

import cwo.pakidermo.dgom.com.mx.cwo.app.AppConstantes;

/**
 * Created by beto on 21/01/18.
 */

public class DaysExcercisesUtils {

    private static final String TAG = "DaysExcercisesUtils";

    private static final String PREFS_LAST_EXERCICE_DATE = "PREFS_LAST_EXERCICE_DATE";
    private static final String PREFS_START_EXERCICE_DATE = "PREFS_START_EXERCICE_DATE";


    public static int numberOfHoursSinceLastExercice(Context context){
        SharedPreferences settings = context.getSharedPreferences(AppConstantes.PREFS_NAME, 0);
        //Fecha de hoy
        long today = System.currentTimeMillis();

        //Tiempo del inicio de la ronda
        long startTime = settings.getLong(PREFS_START_EXERCICE_DATE, -1);

        //Tiempo del ultimo ejercicio de la ronda
        long lastTime = settings.getLong(PREFS_LAST_EXERCICE_DATE, -1);

        Log.d(TAG, "Today: " + today + " startTime: " + startTime + " lastTime: " + lastTime);
        Log.d(TAG, "Today: " + new LocalDate(today) + " startTime: " + new LocalDate(startTime) + " lastTime: " + new LocalDate(lastTime));
        int hours = Hours.hoursBetween( new LocalDate(lastTime), new LocalDate(today)).getHours();

        Log.d(TAG,"Horas transcurridas desde el ultimo ejercicio: " + hours);

        return hours;


    }

    public static int numberOfDaysInRow(Context context){
        SharedPreferences settings = context.getSharedPreferences(AppConstantes.PREFS_NAME, 0);
        //Fecha de hoy
        long today = System.currentTimeMillis();

        //Tiempo del inicio de la ronda
        long startTime = settings.getLong(PREFS_START_EXERCICE_DATE, -1);

        //Tiempo del ultimo ejercicio de la ronda
        long lastTime = settings.getLong(PREFS_LAST_EXERCICE_DATE, -1);

        Log.d(TAG, "Today: " + today + " startTime: " + startTime + " lastTime: " + lastTime);
        Log.d(TAG, "Today: " + new LocalDate(today) + " startTime: " + new LocalDate(startTime) + " lastTime: " + new LocalDate(lastTime));


        //Si no hay un startdate, se está iniciando el ejercicio
        if(startTime == -1){
            Log.d(TAG, "No startDate");
            return -1;
        }


        int days = Days.daysBetween( new LocalDate(lastTime), new LocalDate(today)).getDays();

        //Verifica que la cantidad de dias entre lastTime y hoy no sean mayor a 1, ya que reinicia el contador
        Log.d(TAG, "Diferencia en dia, del último ejercicio: " + days);
        if(days > 1){
            Log.d(TAG, "Se reinicia el contador por falta de ejercicio");
            return -1;
        }

        days = Days.daysBetween( new LocalDate(startTime), new LocalDate(today)).getDays();

        Log.d(TAG, "Dias en row: " + days);

        return days;
    }

    public static void exerciceFinished(Context context){
        Log.d(TAG,"Ejercicio terminado");
        if(numberOfDaysInRow(context) == -1){
            //Pone la fecha actual como inicio del ejercicio
            setStartExerciceDateNow(context);
        }

        //actualiza la fecha del ultim ejercicio
        setLastExerciceDateNow(context);
    }

    public static long getStartExerciceDate(Context context){
        SharedPreferences settings = context.getSharedPreferences(AppConstantes.PREFS_NAME, 0);
        //Tiempo del inicio de la ronda
        long startTime = settings.getLong(PREFS_START_EXERCICE_DATE, -1);
        return startTime;
    }


    public static long getLastExerciceDate(Context context){
    SharedPreferences settings = context.getSharedPreferences(AppConstantes.PREFS_NAME, 0);

    //Tiempo del ultimo ejercicio de la ronda
    long lastTime = settings.getLong(PREFS_LAST_EXERCICE_DATE, -1);

    return lastTime;
}

    private static void setStartExerciceDateNow(Context context){
        Log.d(TAG,"Iniciando la fecha de inicio del ejercio a hoy");
        SharedPreferences settings = context.getSharedPreferences(AppConstantes.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(PREFS_START_EXERCICE_DATE, System.currentTimeMillis());
        editor.commit();
    }

    private static void setLastExerciceDateNow(Context context){
        Log.d(TAG,"Actualizando la fecha de ultimo ejercio a hoy");
        SharedPreferences settings = context.getSharedPreferences(AppConstantes.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(PREFS_LAST_EXERCICE_DATE, System.currentTimeMillis());
        editor.commit();
    }


    public static void setTempData(Context context){
        SharedPreferences settings = context.getSharedPreferences(AppConstantes.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        long oneDay = 1000 * 60 * 60 * 24;

        editor.putLong(PREFS_LAST_EXERCICE_DATE, System.currentTimeMillis()- oneDay);
        editor.putLong(PREFS_START_EXERCICE_DATE, System.currentTimeMillis() - (10 * oneDay));
        editor.commit();
    }

}
