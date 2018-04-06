package cwo.pakidermo.dgom.com.mx.cwo;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ListView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cwo.pakidermo.dgom.com.mx.cwo.adapter.ExerciseListAdapter;
import cwo.pakidermo.dgom.com.mx.cwo.app.AppConstantes;
import cwo.pakidermo.dgom.com.mx.cwo.db.DatabaseHelper;
import cwo.pakidermo.dgom.com.mx.cwo.db.UserDataController;
import cwo.pakidermo.dgom.com.mx.cwo.to.Exercise;
import cwo.pakidermo.dgom.com.mx.cwo.to.ExerciseDataType;
import cwo.pakidermo.dgom.com.mx.cwo.to.ExerciseSeparator;
import cwo.pakidermo.dgom.com.mx.cwo.to.ExerciseTitle;

public class ExerciseListActivity extends AppCompatActivity {

    private static final String TAG = "ExerciseListActivity";
    ListView listExercise;

    //Analiticos
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejercicio_list);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        listExercise = (ListView) findViewById(R.id.list_exercise);

        List<ExerciseDataType> data = new ArrayList<ExerciseDataType>();

        //Carga los datos de la base de datos
        fillData(data);

        //Crea los separadores
        fillDataSeparators(data);

        //Agrega el titulo
        fillTitleData(data);



        ExerciseListAdapter adapter = new ExerciseListAdapter(this,android.R.layout.simple_list_item_1, data);

        listExercise.setAdapter(adapter);

        final View rootScrollView = listExercise.getRootView();
        
        listExercise.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = rootScrollView.getScrollY(); // For ScrollView
                int scrollX = rootScrollView.getScrollX(); // For Hor
                Log.d(TAG, "Scroll " + scrollY);
            }
        });

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    /**
     *
     * @param data
     */
    private void fillTitleData(List<ExerciseDataType> data){
        ExerciseTitle e = new ExerciseTitle();
        e.setDataType(Exercise.TYPE_TITLE);
        e.setChallenges(countExercisesByType(AppConstantes.CHALLENGES_TYPE, data));
        e.setOffice(countExercisesByType(AppConstantes.OFFICE_WORKOUT_TYPE, data));
        e.setTactfit(countExercisesByType(AppConstantes.TACTFIT_TYPE, data));
        e.setStreching(countExercisesByType(AppConstantes.STRECH_TYPE, data));
        e.setTotal(countExercisesAll(data));
        data.add(0,e);
    }


    /**
     *
     * @param data
     */
    private void fillDataSeparators(List<ExerciseDataType> data){

        int month = -1;
        int year = -1;

        Calendar cal = Calendar.getInstance();
        for(int i=0; i < data.size(); i++){

            if(data.get(i).getDataType() != Exercise.TYPE_EXERCISE){
                continue;
            }

            cal.setTimeInMillis(((Exercise)data.get(i)).getDate());
            if(cal.get(Calendar.YEAR) != year || cal.get(Calendar.MONTH) != month){

                month = cal.get(Calendar.MONTH);
                year = cal.get(Calendar.YEAR);

                int count = countExercisesByDate(year,month,data);

                ExerciseSeparator e = new ExerciseSeparator();
                e.setDataType(Exercise.TYPE_SEPARATOR);
                e.setCount(count);
                String monthStr = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                e.setDate(monthStr + " " + year);
                data.add(i,e);

            }
        }
    }

    private int countExercisesAll( List<ExerciseDataType> data){
        int count = 0;
        Calendar cal = Calendar.getInstance();
        for(int i=0; i < data.size(); i++) {

            if (data.get(i).getDataType() != Exercise.TYPE_EXERCISE) {
                continue;
            }
            count++;
        }
        return count;
    }

    private int countExercisesByType(int type, List<ExerciseDataType> data){
        int count = 0;
        Calendar cal = Calendar.getInstance();
        for(int i=0; i < data.size(); i++) {

            if (data.get(i).getDataType() != Exercise.TYPE_EXERCISE) {
                continue;
            }
            if(((Exercise)data.get(i)).getExerciseType() == type) {
                count++;
            }
        }
        return count;
    }

    private int countExercisesByDate(int year, int month, List<ExerciseDataType> data){
        int count = 0;
        Calendar cal = Calendar.getInstance();
        for(int i=0; i < data.size(); i++) {

            if (data.get(i).getDataType() != Exercise.TYPE_EXERCISE) {
                continue;
            }

            cal.setTimeInMillis(((Exercise)data.get(i)).getDate());
            if(cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) == month) {
                count++;
            }
        }
        return count;
    }

    private void fillData(List<ExerciseDataType> data){
        UserDataController udc = new UserDataController(this);
        List<Exercise> exList = udc.getExerciseData();
        data.addAll(exList);

    }
}
