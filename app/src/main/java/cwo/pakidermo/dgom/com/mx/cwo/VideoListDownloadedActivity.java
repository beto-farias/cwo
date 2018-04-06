package cwo.pakidermo.dgom.com.mx.cwo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import cwo.pakidermo.dgom.com.mx.cwo.adapter.VideoListDownloaderAdapter;
import cwo.pakidermo.dgom.com.mx.cwo.app.AppConstantes;
import cwo.pakidermo.dgom.com.mx.cwo.download.util.DownloadReceiver;
import cwo.pakidermo.dgom.com.mx.cwo.to.VideoContent;
import cwo.pakidermo.dgom.com.mx.cwo.to.VideoContentDownloaded;
import cwo.pakidermo.dgom.com.mx.cwo.utils.VideoContentUtuls;

public class VideoListDownloadedActivity extends Activity {

    private ListView listDownloadedVideos;

    //Analiticos
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list_downloaded);


        listDownloadedVideos = (ListView) findViewById(R.id.list_downloaded_videos);


        List<VideoContent> data = new ArrayList<>();


        //Carga los videos almacenados
        List<VideoContent> lista =  DownloadReceiver.getSavedVideoContentLibrary(this);


        VideoContentDownloaded item = new VideoContentDownloaded();
        item.setbTitle(true);
        item.setsTitle(getString(R.string.exercice_office_workout));
        data.add(item);

        data.addAll(VideoContentUtuls.getContentById(R.string.exercice_office_workout, lista));

        item = new VideoContentDownloaded();
        item.setbTitle(true);
        item.setsTitle(getString(R.string.exercice_challenges));
        data.add(item);

        data.addAll( VideoContentUtuls.getContentById(AppConstantes.CHALLENGES_TYPE, lista));


        item = new VideoContentDownloaded();
        item.setbTitle(true);
        item.setsTitle(getString(R.string.exercice_streching));
        data.add(item);

        data.addAll( VideoContentUtuls.getContentById(AppConstantes.STRECH_TYPE, lista));


        item = new VideoContentDownloaded();
        item.setbTitle(true);
        item.setsTitle(getString(R.string.exercice_tactfit));
        data.add(item);
        data.addAll( VideoContentUtuls.getContentById(AppConstantes.TACTFIT_TYPE, lista));


        VideoListDownloaderAdapter adapter = new VideoListDownloaderAdapter(this,  android.R.layout.simple_list_item_1, data);

        listDownloadedVideos.setAdapter(adapter);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }
}
