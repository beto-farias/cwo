package cwo.pakidermo.dgom.com.mx.cwo.db;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import cwo.pakidermo.dgom.com.mx.cwo.app.AppConstantes;
import cwo.pakidermo.dgom.com.mx.cwo.to.Exercise;
import cwo.pakidermo.dgom.com.mx.cwo.to.VideoContent;

/**
 * Created by notei on 08/02/18.
 */

public class FirebaseHelper {

    private static final String TAG = "FirebaseHelper";

    private DatabaseReference mDatabase;
    private static final Gson gson = new Gson();
    private Context mContext;
    private FirebaseAuth mAuth;
    private String userEmail;

    public FirebaseHelper(Context context){
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("Exercises");
        this.mContext = context;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userEmail = currentUser.getEmail().replaceAll("\\.", "_");
    }


    /**
     * Registra un valor en la base de datos de firebase
     * @param ex ejercicio a ser almacenado
     * @return si fue almacenado correctamente
     */
    protected boolean saveExercise(Exercise ex){
        //String user = AppConstantes.FIREBASE_USER.getEmail().replaceAll("\\.", "_");
        Log.d(TAG, userEmail);
        mDatabase.child(userEmail).push().setValue(ex);
        return true;
    }

    protected void getFireBaseUserExercises(){
        final UserDataController udc = new UserDataController(mContext);



        //final String user = AppConstantes.FIREBASE_USER.getEmail().replaceAll("\\.", "_");
        Log.d(TAG, userEmail);
        final DatabaseReference userExercise = mDatabase.child(userEmail);

        // Read from the database
        userExercise.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(TAG, "Firebase Data change");



                Iterable<DataSnapshot> contactChildren = dataSnapshot.getChildren();
                for (DataSnapshot contact : contactChildren) {
                    Exercise c = contact.getValue(Exercise.class);
                    Log.d(TAG,c.toString());
                    udc.addExerciseFromFireBase(c);
                }

                //Quita el listener una vez que procesa la info
                mDatabase.child(userEmail).removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

}
