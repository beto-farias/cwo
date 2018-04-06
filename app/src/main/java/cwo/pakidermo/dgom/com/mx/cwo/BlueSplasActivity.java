package cwo.pakidermo.dgom.com.mx.cwo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import cwo.pakidermo.dgom.com.mx.cwo.db.UserDataController;

public class BlueSplasActivity extends Activity {

    private long SPLASH_SCREEN_TIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_blue_splas);


        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                //TODO verificar si el usuario esta logeado

                //Carga los datos de ejercicios desde firebase
                UserDataController udc = new UserDataController(BlueSplasActivity.this.getApplicationContext());
                udc.getFireBaseUserExercises();

                Intent i = new Intent(BlueSplasActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        }, SPLASH_SCREEN_TIME);
    }
}
