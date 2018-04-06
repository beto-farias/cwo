package cwo.pakidermo.dgom.com.mx.cwo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import cwo.pakidermo.dgom.com.mx.cwo.utils.AnimateCounter;
import cwo.pakidermo.dgom.com.mx.cwo.utils.DaysExcercisesUtils;

public class DayCounterActivity extends Activity implements AnimateCounter.AnimateCounterListener {

    private TextView txtDayCounter;
    private AnimateCounter animateCounter;
    private boolean cancelled;

    //Analiticos
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_counter);

        txtDayCounter = (TextView) findViewById(R.id.txt_day_counter);

        int days = DaysExcercisesUtils.numberOfDaysInRow(getApplicationContext());

        animateCounter = new AnimateCounter.Builder(txtDayCounter)
                .setCount(0,days)
                .setDuration(1000)
                .build();

        animateCounter.setAnimateCounterListener(this);
        animateCounter.execute();

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    @Override
    public void onAnimateCountEnd() {
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if(cancelled){
                    return;
                }

                Intent i = new Intent(DayCounterActivity.this,MainActivity.class);

                startActivity(i);
                finish();
            }
        }, 500);
    }


    public void continueActivity(View v){
        if(animateCounter != null){
            animateCounter.stop();
        }
        cancelled = true;
        Intent i = new Intent(DayCounterActivity.this,MainActivity.class);

        startActivity(i);
        finish();
    }
}
