package cwo.pakidermo.dgom.com.mx.cwo;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cwo.pakidermo.dgom.com.mx.cwo.app.AppConstantes;


public class WelcomeActivity extends Activity {

    private FirebaseAuth mAuth;
    private static final String TAG ="WelcomeActivity";
    private LoginButton buttonFacebookLogin;
    private TextView txtCrearCuenta;
    private TextView txtTengoCuenta;

    //Facebook
    private CallbackManager callbackManager;

    private VideoView vvBackground;
    int onStartCount = 0;
    Animation animation;

    //Analiticos
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);

        onStartCount = 1;
        if (savedInstanceState == null) // 1st time
        {
            this.overridePendingTransition(R.anim.anim_slide_in_left,
                    R.anim.anim_slide_out_left);
        } else // already created so reverse animation
        {
            onStartCount = 2;
        }



        //animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.bottom2up);

        vvBackground = (VideoView) findViewById(R.id.vv_background);
        buttonFacebookLogin = (LoginButton) findViewById(R.id.button_facebook_login);
        txtCrearCuenta = (TextView) findViewById(R.id.txt_crear_cuenta);
        txtTengoCuenta = (TextView) findViewById(R.id.txt_tengo_cuenta);

        mAuth = FirebaseAuth.getInstance();
        initFacebook();


        initBackgroundVideo();




        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF,50.0f,
                Animation.RELATIVE_TO_SELF, -0.0f);

        slide.setDuration(2000);
        slide.setFillBefore(false);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        slide.setDetachWallpaper(true);
        slide.setZAdjustment(10);
        //Inicia la animación
        buttonFacebookLogin.startAnimation(slide);
        txtCrearCuenta.startAnimation(slide);
        txtTengoCuenta.startAnimation(slide);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }


    private void initBackgroundVideo(){
        //Video Loop
        vvBackground.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.login_screen_video);

        vvBackground.setVideoURI(uri);
        vvBackground.requestFocus();
        vvBackground.start();
    }

    public void crearCuentaAction(View v){
        Intent i = new Intent(WelcomeActivity.this, SignUpActivity.class);
        startActivity(i);
        //finish();
    }

    public void showLoginAction(View v){
        Intent i = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(i);
        //finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



    private void initFacebook(){

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.button_facebook_login);
        loginButton.setReadPermissions("email", "public_profile");

        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            AppConstantes.FIREBASE_USER = user;

                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(WelcomeActivity.this, "Error de autenticación, " + task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                            AppConstantes.FIREBASE_USER = null;
                        }

                        // ...
                    }
                });
    }

    private void updateUI(){

        if(AppConstantes.FIREBASE_USER != null) {
            Intent i = new Intent(WelcomeActivity.this, BlueSplasActivity.class);
            startActivity(i);
            finish();
        }
    }
}
