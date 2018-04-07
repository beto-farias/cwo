package cwo.pakidermo.dgom.com.mx.cwo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import cwo.pakidermo.dgom.com.mx.cwo.adapter.ExerciseListAdapter;
import cwo.pakidermo.dgom.com.mx.cwo.app.AppConstantes;
import cwo.pakidermo.dgom.com.mx.cwo.db.DatabaseHelper;
import cwo.pakidermo.dgom.com.mx.cwo.db.UserDataController;
import cwo.pakidermo.dgom.com.mx.cwo.to.VideoContent;
import cwo.pakidermo.dgom.com.mx.cwo.utils.VideoContentUtuls;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private LinearLayout listExercices;
    private VideoContent vcFeatured;
    private ConstraintLayout layoutFeaturedContent;
    private TextView txtContentPremium;

    private ImageView userThumnail;

    private FirebaseAuth mAuth;

    private TextView txtCelebName;
    private ImageView imgIcoPlay;

    private Button btnDebug;

    //Analiticos
    private FirebaseAnalytics mFirebaseAnalytics;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        //Carga los datos de ejercicios desde firebase
        //UserDataController udc = new UserDataController(this);
        //udc.getFireBaseUserExercises();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        layoutFeaturedContent = (ConstraintLayout) findViewById(R.id.layout_featured_content);
        txtContentPremium = (TextView) findViewById(R.id.txt_content_premium);

        txtCelebName = (TextView) findViewById(R.id.txt_celeb_name);
        imgIcoPlay = (ImageView) findViewById(R.id.img_ico_play);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        btnDebug = (Button) findViewById(R.id.btn_debug);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        //TextView text = (TextView) header.findViewById(R.id.textView);
        userThumnail = (ImageView)header.findViewById(R.id.user_thumnail);


        if(AppConstantes.subscribed){
            txtContentPremium.setVisibility(View.GONE);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Listener de los elementos del menu
        navigationView.setNavigationItemSelectedListener(this);
        listExercices = (LinearLayout) findViewById(R.id.list_exercices);

        setupFeatureVideo();

        //Foto del usuario
        if(AppConstantes.FIREBASE_USER.getPhotoUrl() != null){
            Log.d(TAG, AppConstantes.FIREBASE_USER.getPhotoUrl() + "");
            Picasso.with(this)
                    .load(AppConstantes.FIREBASE_USER.getPhotoUrl())
                    .transform(new CropCircleTransformation())
                    .into(userThumnail);
        }

        //Inicializa el modo debug
        setupDebug();

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }



    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.are_you_shure_you_whant_to_exit)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void setupDebug(){
        if(!AppConstantes.DEBUG){
            btnDebug.setVisibility(View.GONE);
        }
    }

    private void setupFeatureVideo(){
        vcFeatured = VideoContentUtuls.getFeatured(AppConstantes.videoData);
        if(vcFeatured == null){
            txtCelebName.setVisibility(View.INVISIBLE);
            imgIcoPlay.setVisibility(View.INVISIBLE);

            Log.d(TAG,"No hay video feature");

        }else{
            Log.d(TAG,"Celebrity: " + vcFeatured.getCelebrity());
            Log.d(TAG,"Poster: " + vcFeatured.getPoster());

            txtCelebName.setText(vcFeatured.getCelebrity());
            Picasso.with(this)
                    .load(vcFeatured.getPoster())
                    .placeholder(R.drawable.img_video_placeholder)
                    .into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Log.d(TAG, "Cargando poster: " + from);
                    layoutFeaturedContent.setBackgroundDrawable(new BitmapDrawable(bitmap));
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Log.d(TAG, "Error con el poster: " + errorDrawable);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    Log.d(TAG,"on Prepare Load: " + placeHolderDrawable);
                }
            });
        }
    }


    public void showDebugAction(View v){
        Intent i = new Intent(MainActivity.this, DebugActivity.class);
        startActivity(i);
    }

    public void showStoreAction(View v){
        Intent i = new Intent(MainActivity.this, PaymentActivity.class);
        startActivity(i);
    }

    /**
     *
     * @param v
     */
    public void playFeatureVideoAction(View v){

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Main Feature video");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Feature Video");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Banner");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        Intent i = new Intent(MainActivity.this, ViewVideoActivity.class);
        i.putExtra(AppConstantes.VIDEO_EXTRA,vcFeatured);
        startActivity(i);
    }


    /**
     *
     * @param v
     */
    public void listOfficeWorkoutVideoAction(View v){

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Main Office workout");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Office workout");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Banner");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


        Intent i = new Intent(MainActivity.this, VideoListActivity.class);
        i.putExtra("list_type", AppConstantes.OFFICE_WORKOUT_TYPE);
        startActivity(i);
    }

    /**
     *
     * @param v
     */
    public void listDesafiosVideoAction(View v){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Main Challenge");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Challenge");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Banner");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        Intent i = new Intent(MainActivity.this, VideoListActivity.class);
        i.putExtra("list_type", AppConstantes.CHALLENGES_TYPE);
        startActivity(i);
    }


    /**
     *
     * @param v
     */
    public void listStrechVideoAction(View v){

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Main Strech");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Strech");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Banner");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        Intent i = new Intent(MainActivity.this, VideoListActivity.class);
        i.putExtra("list_type", AppConstantes.STRECH_TYPE);
        startActivity(i);
    }

    /**
     *
     * @param v
     */
    public void listTacFitVideoAction(View v){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Main Tactfit");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Tactfit");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Banner");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        Intent i = new Intent(MainActivity.this, VideoListActivity.class);
        i.putExtra("list_type", AppConstantes.TACTFIT_TYPE);
        startActivity(i);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Log.d(TAG, "Opcion del menu " + id);
/*
        //TODO desactivado en esta version
        if (id == R.id.nav_config) {
            //TODO config
        } else if (id == R.id.nav_aviso_privacidad) {
            Intent i = new Intent(MainActivity.this, AvisoPrivacidadActivity.class);
            startActivity(i);

        } else*/
         if (id == R.id.nav_cerrar_sesion) {
         logOut();
        }else if (id == R.id.nav_mi_ejercicio) {
            Intent i = new Intent(MainActivity.this, ExerciseListActivity.class);
            startActivity(i);
        }
        /*
        //TODO desactivado en esta version
        else if(id == R.id.nav_downloaded_videos){
            Intent i = new Intent(MainActivity.this, VideoListDownloadedActivity.class);
            startActivity(i);
        }
        */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * Cierra la sesion del usaurio y borra todos los datos que se tengan guardados en el teléfono
     */
    private void logOut(){

        new AlertDialog.Builder(this)
                .setMessage(R.string.are_you_shure_you_whant_to_close_session)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Borra los datos de la base de datos
                        UserDataController userDataController = new UserDataController(MainActivity.this.getApplicationContext());
                        userDataController.deleteAllData();
                        //logout firebase
                        mAuth.signOut();

                        Intent i = new Intent(MainActivity.this, WelcomeActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();


                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();


    }


}
