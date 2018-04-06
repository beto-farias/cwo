package cwo.pakidermo.dgom.com.mx.cwo.db;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.UUID;

import cwo.pakidermo.dgom.com.mx.cwo.to.Exercise;
import cwo.pakidermo.dgom.com.mx.cwo.to.VideoContent;

/**
 * Created by notei on 09/02/18.
 */

public class UserDataController {

    Context mContext;
    private static final String TAG = "UserDataController";
    DatabaseHelper databaseHelper;

    public UserDataController(Context context){
        this.mContext = context;
        databaseHelper = new DatabaseHelper(mContext);
    }


    public List<Exercise> getExerciseData(){
        List<Exercise> res = databaseHelper.getExerciseData();
        return res;
    }


    /**
     * Guarda los datos en la base de datos y en el firebase
     * @param vc
     * @param feel
     */
    public long insertData(VideoContent vc, int feel){
        boolean sync = false;
        Exercise exercise = new Exercise(vc, feel);
        exercise.setDate(System.currentTimeMillis());
        exercise.setUuid(UUID.randomUUID().toString());
        exercise.setSync(true);
        //Almacena el mismo dato en internet
        try {
            FirebaseHelper firebaseHelper = new FirebaseHelper(mContext);
            sync = firebaseHelper.saveExercise(exercise);
        }catch (Exception ex){
            Log.e(TAG, "Error al guardar en firebase: " + ex.getMessage());
            exercise.setSync(false);
        }


        //Inserta en la base de datos local

        long res = databaseHelper.insertData(exercise);

        return res;
    }

    public void getFireBaseUserExercises(){
        FirebaseHelper firebaseHelper = new FirebaseHelper(mContext);
        firebaseHelper.getFireBaseUserExercises();
    }

    public void syncPendientData(){
        databaseHelper.getDataNotSync();
    }

    public void deleteAllData(){
        databaseHelper.deleteAllData();
    }

    public void addExerciseFromFireBase(Exercise ex) {
        databaseHelper.insertExercise(ex);
    }
}
