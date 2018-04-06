package cwo.pakidermo.dgom.com.mx.cwo.download.util;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cwo.pakidermo.dgom.com.mx.cwo.app.AppConstantes;
import cwo.pakidermo.dgom.com.mx.cwo.to.VideoContent;
import cwo.pakidermo.dgom.com.mx.cwo.utils.FileUtil;

/**
 * Created by beto on 16/01/18.
 */

public class DownloadReceiver extends BroadcastReceiver{

    private static final String TAG = "DownloadReceiver";
    private static Gson gson = new Gson();

    private static DownloadReceiver downloadReceiver;

    private DownloadReceiver(){}

    public  static DownloadReceiver getInstance(){
        Log.d(TAG,"Solicitud de instancia de DownloadReceiver");
        if(downloadReceiver == null){
            Log.d(TAG,"Creando la instancia de DownloadReceiver");
            downloadReceiver = new DownloadReceiver();
        }
        return downloadReceiver;
    }



    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"Descarga completa" );

        //desregistra el receiber
        context.unregisterReceiver(getInstance());


        long refId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
        Log.d(TAG, "ID RECIBIDO: " + refId);
        if(refId == getDownloadID(context)){
            Log.d(TAG,"Corresponde a la descarga de la app =)");
            Toast.makeText(context,"Descarga completa", Toast.LENGTH_LONG).show();



            //Obtiene el json del video descargado
            String json = getDownloadJSON(context);

            //guarda el nuevo video a la lista de descargas
            addJsonVideoDownloaded(context, json);

            //Cifra el video
            VideoContent vc = gson.fromJson(json, VideoContent.class);
            try {
                //FileUtil.cipherVideoFile(vc);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Limpia los datos
            //Remueve el id de descarga
            clearDownloadID(context);
            //Remueve el json de la descarga
            clearDownloadJSON(context);

            //TODO hacer algo con la descarga del video
        }
    }

    //------------------------PREFERENCIAS --------------------------------------


    /**
     * Quita el id de las propiedades
     * @param context
     */
    private void clearDownloadID(Context context){
        Log.d(TAG, "Remueve el id de descarga");
        SharedPreferences settings = context.getSharedPreferences(AppConstantes.PREFS_NAME, 0);
        settings.edit().remove(AppConstantes.DOWNLOAD_ID).commit();
    }

    public static long getDownloadID(Context context){
        SharedPreferences settings = context.getSharedPreferences(AppConstantes.PREFS_NAME, 0);
        long id = settings.getLong(AppConstantes.DOWNLOAD_ID, -1);
        return id;
    }

    public static void setDownloadID(Context context, long id){
        Log.d(TAG,"Configurando video para descarga id: " + id);
        SharedPreferences settings = context.getSharedPreferences(AppConstantes.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(AppConstantes.DOWNLOAD_ID, id);
        editor.commit();
    }




    private void clearDownloadJSON(Context context){
        Log.d(TAG, "Remueve el json de descarga");
        SharedPreferences settings = context.getSharedPreferences(AppConstantes.PREFS_NAME, 0);
        settings.edit().remove(AppConstantes.DOWNLOADING_JSON).commit();
    }

    public static void setDownloadJSON(Context context, String json){
        Log.d(TAG,"Configurando el json para json: " + json);
        SharedPreferences settings = context.getSharedPreferences(AppConstantes.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        //Guarda el Json del archivo que va a descargar
        editor.putString(AppConstantes.DOWNLOADING_JSON , json);
        editor.commit();
    }

    /**
     * Obtiene el json del video que se está descargando
     * @param context
     * @return
     */
    public static String getDownloadJSON(Context context){
        SharedPreferences settings = context.getSharedPreferences(AppConstantes.PREFS_NAME, 0);
        String res = settings.getString(AppConstantes.DOWNLOADING_JSON , "");
        return res;
    }


    public static void addJsonVideoDownloaded(Context context, String json){
        //Pasa el json a VC
        VideoContent vc = gson.fromJson(json, VideoContent.class);

        //Obtiene la lista de videos guardados
        List<VideoContent> lista = getSavedVideoContentLibrary(context);

        //Agrega el video descargado
        lista.add(vc);

        //Actualiza la lista de videos
        saveDownloadJSONLibrary(context, lista);
    }

    public synchronized static void removeVideoDownloadedJson(Context context, VideoContent vc){
        Log.d(TAG, "Remover video: " + vc.getUiid());
        List<VideoContent> lista = getSavedVideoContentLibrary(context);
        lista = new ArrayList<>(lista);
        VideoContent item;
        for (int i = 0; i < lista.size(); i++) {
            item = lista.get(i);
            if(item.getUiid().equalsIgnoreCase(vc.getUiid())){
                //Borra el archivo si es que existe

                lista.remove(i);
                Log.d(TAG, "Video removido con exito");
            }
        }

        saveDownloadJSONLibrary(context,lista);
    }

    private static void saveDownloadJSONLibrary(Context context, List<VideoContent> lista){
        Log.d(TAG,"Guardando la lista de videos descargada: " +  lista);

        String json = gson.toJson(lista.toArray());

        SharedPreferences settings = context.getSharedPreferences(AppConstantes.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        //Guarda el Json de los videos descargados
        editor.putString(AppConstantes.DOWNLOADED_JSON_LIBRARY, json);
        editor.commit();
    }


    /**
     * Recupera la lista de videos descargados
     * @param context
     * @return
     */
    public static List<VideoContent> getSavedVideoContentLibrary(Context context){
        //Obtiene los videos guardados
        SharedPreferences settings = context.getSharedPreferences(AppConstantes.PREFS_NAME, 0);
        String res = settings.getString(AppConstantes.DOWNLOADED_JSON_LIBRARY, "");

        //Deserializa los videos
        VideoContent[] data = gson.fromJson(res,VideoContent[].class);
        List<VideoContent> lista = new ArrayList<VideoContent>();
        //Copia el arreglo a una lista
        if(data != null) {
            lista.addAll(Arrays.asList(data));
        }
        return lista;
    }

    /**
     * Verifica si un video ya ha sido descargado
     * @param context
     * @param uiid
     * @return
     */
    public static boolean isVideoDownloaded(Activity context, String uiid) {
        List<VideoContent> lista = getSavedVideoContentLibrary(context);
        for (VideoContent item:lista) {
            if(item.getUiid().equalsIgnoreCase(uiid)){
                return true;
            }
        }

        return false;
    }


    //--------------------------------------------------------------

    /**
     * Metodo que valida si el usuario tiene o no permisos para escribir en el disco
     * @param activity
     * @return
     */
    public static boolean haveStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error","You have permission");
                return true;
            } else {
                Log.e("Permission error","You have asked for permission");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error","You already have the permission");
            return true;
        }
    }


}
