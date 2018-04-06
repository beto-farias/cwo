package cwo.pakidermo.dgom.com.mx.cwo.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cwo.pakidermo.dgom.com.mx.cwo.to.VideoContent;

/**
 * Created by beto on 08/01/18.
 */

public class VideoContentUtuls {
    private static final String TAG = "VideoContentUtuls";

    public static List<VideoContent>getContentById(int id, List <VideoContent>data){
        List<VideoContent> res = new ArrayList();

        for (VideoContent item:data
             ) {
            if(item.getType() == id){
                res.add(item);
            }
        }

        return res;
    }


    public static VideoContent getFeatured(List<VideoContent> data){

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        Log.d(TAG, "Dia de la semana: " + day);

        for (VideoContent item:data
                ) {
            if(item.getFeatured() == 1 && item.getFeatured_day() == day){
                return item;
            }
        }

        return null;
    }



}
