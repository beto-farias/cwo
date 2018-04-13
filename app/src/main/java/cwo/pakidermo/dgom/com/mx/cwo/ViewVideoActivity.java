package cwo.pakidermo.dgom.com.mx.cwo;

//https://stackoverflow.com/questions/11310764/videoview-full-screen-in-android-application

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.util.concurrent.TimeUnit;

import cwo.pakidermo.dgom.com.mx.cwo.app.AppConstantes;
import cwo.pakidermo.dgom.com.mx.cwo.download.util.DownloadReceiver;
import cwo.pakidermo.dgom.com.mx.cwo.to.VideoContent;
import cwo.pakidermo.dgom.com.mx.cwo.utils.FileUtil;

public class ViewVideoActivity extends Activity {


    private static final String TAG ="ViewVideoActivity";

    private VideoContent vc;
    private VideoView videoView;
    private TextView txtVideoTimeRemaning;
    private ImageView imgVideoControl;
    private ImageView imgVideoControlRestart;
    private ImageView imgVideoControlStop;
    private ImageView imgLogo;
    private ImageView imgGotoEnd;

    private TextView txtReanudar;
    private TextView txtRestart;
    private TextView txtStop;

    private ConstraintLayout layoutControls;
    private MyAsync mTask;

    ProgressDialog progDailog;
    //Analiticos
    private FirebaseAnalytics mFirebaseAnalytics;

    private boolean isFeaturedVideo = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_video);

        vc = (VideoContent) getIntent().getSerializableExtra(AppConstantes.VIDEO_EXTRA);
        isFeaturedVideo = getIntent().getBooleanExtra("IS_FEATURED_VIDEO", false);


        layoutControls = (ConstraintLayout) findViewById(R.id.layout_controls);
        videoView = (VideoView) findViewById(R.id.video_view);
        imgVideoControl = (ImageView) findViewById(R.id.img_video_control);
        imgVideoControlRestart = (ImageView) findViewById(R.id.img_video_control_restart);
        imgVideoControlStop = (ImageView) findViewById(R.id.img_video_control_stop);
        imgLogo = (ImageView) findViewById(R.id.img_logo);
        txtVideoTimeRemaning = (TextView) findViewById(R.id.txt_video_time_remaning);
        txtReanudar = (TextView) findViewById(R.id.txt_reanudar);
        txtRestart = (TextView) findViewById(R.id.txt_restart);
        txtStop = (TextView) findViewById(R.id.txt_stop);

        imgGotoEnd = (ImageView) findViewById(R.id.img_goto_end);

        txtVideoTimeRemaning.setText(R.string.lbl_empty);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
       // getSupportActionBar().hide();
        setupDebug();
        initVideo();

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Borra el archivo abierto
        //FileUtil.deleteUncepherVideoFile(vc, this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //fullScreen();
            Log.d(TAG,"Full screen landscape");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            //normalScreen();
            Log.d(TAG,"Normal screen Portrait");
        }
    }

    @Override
    protected void onDestroy() {
        if (mTask != null) {
            mTask.cancel(true);
            mTask.stop = true;
        }
        super.onDestroy();

    }

    private void initVideo(){
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        //Para no poner los controles del video
        videoView.setMediaController(null);

        boolean useLocalFile = FileUtil.videoExists(vc,this);

        Log.d(TAG,"Use local file (PATH): " + useLocalFile);



            //if(FileUtil.videoExists(vc)){
        if(useLocalFile){
            String path = FileUtil.getVideoPath(vc, this);
            Log.d(TAG,"El archivo si existe");
            Log.d(TAG, "Stream de video local: " + path );
            try {
                videoView.setVideoURI(Uri.parse(path));
            } catch (Exception e) {
                e.printStackTrace();
                useLocalFile = false;
            }
        }else{
            Log.d(TAG,"El archivo no existe");

            //Borra el archivo de la lista de descargas por que no existe el arhivo
            DownloadReceiver.removeVideoDownloadedJson(this, vc);
            //marca para que use el video de internet
            useLocalFile = false;
        }

        if(!useLocalFile){
            Log.d(TAG, "Stream de video de URL");
            videoView.setVideoURI(Uri.parse(vc.getVideo_url()));
            //videoView.setVideoURI(Uri.parse("http://distribution.bbb3d.renderfarming.net/video/mp4/bbb_sunflower_1080p_60fps_normal.mp4"));
        }




        //MAneja los controles de la pantalla
        showControls(false);

        showProgressDialog();
        mTask = new MyAsync();
        mTask.execute();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            public void onCompletion(MediaPlayer mp)
            {
                Intent i = new Intent(ViewVideoActivity.this, FinishVideoActivity.class);
                i.putExtra(AppConstantes.VIDEO_EXTRA, vc);
                startActivity(i);
                finish();
            }
        });
    }

    private void showProgressDialog(){
        progDailog = ProgressDialog.show(this, getString(R.string.loading_video_title), getString(R.string.loading_video_message), true);

    }





    public void pauseAction(View v){
        if(videoView == null){
            return;
        }

        if(videoView.isPlaying()) {
            videoView.pause();
            showControls(true);
        }else{
            videoView.start();
            showControls(false);
        }
    }

    public void restartAction(View v){
        showProgressDialog();
        videoView.resume();
        videoView.start();
        showControls(false);
    }

    public void stopAction(View v){
        finish();
    }

    public void moveEndAction(View v){
        videoView.seekTo(mTask.duration);
    }


    private void showControls(boolean show){
        if(show){
            imgVideoControl.setImageResource(R.drawable.icon_play_2x);
            imgVideoControlRestart.setVisibility(View.VISIBLE);
            imgVideoControlStop.setVisibility(View.VISIBLE);
            imgLogo.setVisibility(View.VISIBLE);
            layoutControls.setVisibility(View.VISIBLE);
            txtStop.setVisibility(View.VISIBLE);
            txtRestart.setVisibility(View.VISIBLE);
            txtReanudar.setVisibility(View.VISIBLE);

        }else{
            imgVideoControl.setImageResource(R.drawable.icon_pause_2x);
            imgVideoControlRestart.setVisibility(View.GONE);
            imgVideoControlStop.setVisibility(View.GONE);
            imgLogo.setVisibility(View.GONE);
            layoutControls.setVisibility(View.GONE);
            txtStop.setVisibility(View.GONE);
            txtRestart.setVisibility(View.GONE);
            txtReanudar.setVisibility(View.GONE);
        }
    }




    private class MyAsync extends AsyncTask<Void, Integer, Void>
    {
        int duration = 0;
        long current = 0;
        boolean stop = false;
        @Override
        protected Void doInBackground(Void... params) {

            videoView.start();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                public void onPrepared(MediaPlayer mp) {
                    progDailog.dismiss();
                    duration = videoView.getDuration();
                    Log.d(TAG, "Duración: " + duration);
                    System.out.println("Duración: " + duration);

                    String time = String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(duration),
                            TimeUnit.MILLISECONDS.toSeconds(duration) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                    );


                    txtVideoTimeRemaning.setText(time);
                }
            });




                boolean preview = true;

                do {

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                    current = videoView.getCurrentPosition();
                    Log.d(TAG, "duration - " + duration + " current- " + current + " Time preview " + vc.getPreview_time());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String time = String.format("%02d:%02d",
                                    TimeUnit.MILLISECONDS.toMinutes( (duration - current)),
                                    TimeUnit.MILLISECONDS.toSeconds( (duration - current)) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes( (duration - current)))
                            );


                            txtVideoTimeRemaning.setText(time);
                            txtVideoTimeRemaning.setText(time);
                        }
                    });

                    //Si el video es featured no monitorea el tiempo
                    if(!isFeaturedVideo) {
                        //Si no se ha pagado y el video permite prview
                        if (!AppConstantes.subscribed && vc.getPayment_type() == AppConstantes.ACCESS_PRIVATE) {
                            //Verifica que el tiempo de preview no suceda
                            if (current >= (vc.getPreview_time() / 6)) {
                                preview = false;
                                videoView.stopPlayback();
                                stop = true;
                            }
                        }
                    }

                    Log.d(TAG, "Avance: " + current);
                } while ((current <= duration && preview) && !stop);

                stop = true;
                finish();


            //Borra el archivo abierto
           // FileUtil.deleteUncepherVideoFile(vc);

            if(!AppConstantes.subscribed && vc.getPayment_type() == AppConstantes.ACCESS_PRIVATE) {
                //debe mostrar la ventana de pago
                Intent i = new Intent(ViewVideoActivity.this, PaymentActivity.class);
                startActivity(i);
            }


            return null;
        }

    }


    /**
     * Configura la pantalla para el debug
     */
    private void setupDebug(){
        if(!AppConstantes.DEBUG){
            imgGotoEnd.setVisibility(View.GONE);
        }
    }
}
