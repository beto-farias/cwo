package cwo.pakidermo.dgom.com.mx.cwo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import cwo.pakidermo.dgom.com.mx.cwo.app.AppConstantes;

/**
 * Created by beto on 25/01/18.
 */

public class OfferScreenUtils {

    private static final String TAG = "OfferScreenUtils";
    private static final String PREFS_OFFER_SCREEN_SHOW_DATE = "PREFS_OFFER_SCREEN_SHOW_DATE" ;
    private static final long ONE_DAY = 1000 * 60 * 60 * 24; //Duracion de un dia en milisegundos

    /**
     * Marca que se mostro la pantalla
     * @param context
     */
    public static void setShownOfferScreen(Context context){
        Log.d(TAG,"Pantalla de oferta mostrada");
        SharedPreferences settings = context.getSharedPreferences(AppConstantes.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(PREFS_OFFER_SCREEN_SHOW_DATE, System.currentTimeMillis());
        editor.commit();
    }


    public static long getLastShwedOfferScreen(Context context){
        SharedPreferences settings = context.getSharedPreferences(AppConstantes.PREFS_NAME, 0);
        //Tiempo la ultima vez que se mostro la pantalla
        long time = settings.getLong(PREFS_OFFER_SCREEN_SHOW_DATE, -1);
        return time;
    }

    public static boolean showOfferScreen(Context context){
        SharedPreferences settings = context.getSharedPreferences(AppConstantes.PREFS_NAME, 0);
        //Tiempo la ultima vez que se mostro la pantalla
        long time = settings.getLong(PREFS_OFFER_SCREEN_SHOW_DATE, -1);

        if(time == -1){
            return true;
        }

        if(System.currentTimeMillis() - time > ONE_DAY){
            return true;
        }

        return false;

    }
}
