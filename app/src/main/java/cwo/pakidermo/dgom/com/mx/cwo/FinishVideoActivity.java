package cwo.pakidermo.dgom.com.mx.cwo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;

import cwo.pakidermo.dgom.com.mx.cwo.app.AppConstantes;
import cwo.pakidermo.dgom.com.mx.cwo.db.DatabaseHelper;
import cwo.pakidermo.dgom.com.mx.cwo.db.UserDataController;
import cwo.pakidermo.dgom.com.mx.cwo.to.VideoContent;
import cwo.pakidermo.dgom.com.mx.cwo.utils.DaysExcercisesUtils;

public class FinishVideoActivity extends Activity {


    private static final String TAG = "FinishVideoActivity";
    CallbackManager callbackManager;

    private VideoContent vc;

    private TextView txtHowYouFeel;
    private TextView txtSelectedFeeling;
    private Button btnShareFacebook;

    private SmileRating smileRating;


    private long insertId;

    //Analiticos
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_finish_video);

        this.setFinishOnTouchOutside(false);



        vc = (VideoContent) getIntent().getSerializableExtra(AppConstantes.VIDEO_EXTRA);

        txtHowYouFeel = (TextView) findViewById(R.id.txt_how_you_feel);
        txtSelectedFeeling = (TextView) findViewById(R.id.txt_selected_feeling);
        btnShareFacebook = (Button) findViewById(R.id.btn_share_facebook);
        smileRating = (SmileRating) findViewById(R.id.smile_rating);

        //btnShareFacebook.setVisibility(View.INVISIBLE);
        btnShareFacebook.setEnabled(false);


        //Guarda en la base de datos el video visto
        UserDataController udc = new UserDataController(this);
        insertId = udc.insertData(vc,-1);


        //String message = getString(R.string.how_you_feel, vc.getName());
        String message = getString(R.string.how_you_feel);
        txtHowYouFeel.setText(message);


        smileRating.setNameForSmile(BaseRating.TERRIBLE, AppConstantes.VIDEO_FEELINGS[BaseRating.TERRIBLE]);
        smileRating.setNameForSmile(BaseRating.BAD, AppConstantes.VIDEO_FEELINGS[BaseRating.BAD]);
        smileRating.setNameForSmile(BaseRating.OKAY, AppConstantes.VIDEO_FEELINGS[BaseRating.OKAY]);
        smileRating.setNameForSmile(BaseRating.GOOD, AppConstantes.VIDEO_FEELINGS[BaseRating.GOOD]);
        smileRating.setNameForSmile(BaseRating.GREAT, AppConstantes.VIDEO_FEELINGS[BaseRating.GREAT]);



        smileRating.setOnSmileySelectionListener(new SmileRating.OnSmileySelectionListener() {
            @Override
            public void onSmileySelected(@BaseRating.Smiley int smiley, boolean reselected) {
                // reselected is false when user selects different smiley that previously selected one
                // true when the same smiley is selected.
                // Except if it first time, then the value will be false.

                txtSelectedFeeling.setText(AppConstantes.VIDEO_FEELINGS[smiley]);
                btnShareFacebook.setEnabled(true);

                //TODO update feeling

                switch (smiley) {
                    case SmileRating.BAD:
                        Log.i(TAG, "Bad");
                        break;
                    case SmileRating.GOOD:
                        Log.i(TAG, "Good");
                        break;
                    case SmileRating.GREAT:
                        Log.i(TAG, "Great");
                        break;
                    case SmileRating.OKAY:
                        Log.i(TAG, "Okay");
                        break;
                    case SmileRating.TERRIBLE:
                        Log.i(TAG, "Terrible");
                        break;
                }

                //TODO actualizar el feeleng
                Log.d(TAG, "Smiley: " + smiley);
            }
        });


        DaysExcercisesUtils.exerciceFinished(getApplicationContext());

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

    }



    public void postWallAction(View v){
        //https://developers.facebook.com/docs/sharing/android
        Log.d(TAG, "postWallAction");



        String message = "Me senti " + AppConstantes.VIDEO_FEELINGS[(int)smileRating.getRating() - 1];

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("http://www.cwo.com.mx/app"))
                .setQuote("Cw " + vc.getName())
                .setContentDescription(message)
                .build();

        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);

        Log.d(TAG, "Termina");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Intent i = new Intent(FinishVideoActivity.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
}
