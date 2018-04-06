package cwo.pakidermo.dgom.com.mx.cwo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import cwo.pakidermo.dgom.com.mx.cwo.adapter.VideoRecycleAdapter;
import cwo.pakidermo.dgom.com.mx.cwo.app.AppConstantes;
import cwo.pakidermo.dgom.com.mx.cwo.to.VideoContent;
import cwo.pakidermo.dgom.com.mx.cwo.utils.VideoContentUtuls;

public class VideoListActivity extends AppCompatActivity {


    ListView listVideos;
    TextView txtListaVideo;
    private int type = 0;


    RecyclerView recycleView;
    Toolbar toolbar;
    ImageView imgHeader;

    CollapsingToolbarLayout collapsingToolbarLayout;
    //Analiticos
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list_collapsable);


       // listVideos = (ListView) findViewById(R.id.list_videos_diponibles);
       // txtListaVideo = (TextView) findViewById(R.id.txt_lista_video);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recycleView = (RecyclerView) findViewById(R.id.list_videos_diponibles);
        imgHeader = (ImageView) findViewById(R.id.img_header);

        int white = ContextCompat.getColor(this, R.color.white);
       // int brillante = ContextCompat.getColor(this, R.color.color_brillante);

        collapsingToolbarLayout.setExpandedTitleColor(white);
        collapsingToolbarLayout.setCollapsedTitleTextColor(white);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        setupUI();

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }


    private void setupUI(){
        type = getIntent().getIntExtra("list_type",1);
        String nombreEntrenamiento = "";
        int imgResource = R.drawable.banner_office_work_out_2x;
        switch (type){
            case AppConstantes.OFFICE_WORKOUT_TYPE:
                nombreEntrenamiento = getString(R.string.exercice_office_workout);
                //imgResource = R.drawable.banner_office_work_out_2x;
                imgResource = R.drawable.banner_top_office_workout;
                break;
            case AppConstantes.CHALLENGES_TYPE:
                nombreEntrenamiento = getString(R.string.exercice_challenges);
                //imgResource = R.drawable.banner_desafios_cwo_2x;
                imgResource = R.drawable.banner_top_desafios_cwo;
                break;
            case AppConstantes.STRECH_TYPE:
                nombreEntrenamiento = getString(R.string.exercice_streching);
                //imgResource = R.drawable.banner_streching_2x;
                imgResource = R.drawable.banner_top_streching;
                break;
            case AppConstantes.TACTFIT_TYPE:
                nombreEntrenamiento = getString(R.string.exercice_tactfit);
                //imgResource = R.drawable.banner_tacfit_2x;
                imgResource = R.drawable.banner_top_tacfit;
                break;
        }

        //txtListaVideo.setText(nombreEntrenamiento);
        toolbar.setTitle(nombreEntrenamiento);
        imgHeader.setImageResource(imgResource);

        VideoContent vc;
        final List<VideoContent> data = VideoContentUtuls.getContentById(type, AppConstantes.videoData);
        VideoRecycleAdapter adapter = new VideoRecycleAdapter(this, data, new VideoRecycleAdapter.OnItemClickListener() {

            @Override public void onItemClick(VideoContent item) {

                //Toast.makeText(getApplicationContext(), "Item Clicked", Toast.LENGTH_LONG).show();
                Intent intent;
                VideoContent vc = item;
                intent = new Intent(VideoListActivity.this, ViewVideoActivity.class);
                intent.putExtra(AppConstantes.VIDEO_EXTRA, vc);
                startActivity(intent);

            }

        });

        recycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        recycleView.setAdapter(adapter);

        recycleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //VideoListAdapter adapter = new VideoListAdapter(this,  android.R.layout.simple_list_item_1, data);
        //listVideos.setAdapter(adapter);

        /*
        listVideos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent;
                VideoContent vc = data.get(i);
                intent = new Intent(VideoListActivity.this, ViewVideoActivity.class);
                intent.putExtra(AppConstantes.VIDEO_EXTRA, vc);
                startActivity(intent);
            }
        });*/
    }






}
