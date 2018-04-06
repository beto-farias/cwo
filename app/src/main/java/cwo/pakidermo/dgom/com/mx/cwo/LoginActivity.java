package cwo.pakidermo.dgom.com.mx.cwo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cwo.pakidermo.dgom.com.mx.cwo.app.AppConstantes;
import cwo.pakidermo.dgom.com.mx.cwo.db.UserDataController;
import cwo.pakidermo.dgom.com.mx.cwo.viewholder.ReoverPasswordActivity;

public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;

    TextView txtEmail;
    TextView txtPassword;


    TextInputLayout inputLayoutEmail;
    TextInputLayout inputLayoutPassword;

    //Analiticos
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmail = (TextView) findViewById(R.id.txt_email);
        txtPassword = (TextView) findViewById(R.id.txt_password);

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);

        mAuth = FirebaseAuth.getInstance();

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    public void loginAction(View v){
        loginUser(txtEmail.getText().toString(),txtPassword.getText().toString());
    }


    private void loginUser(String email,String password){

        boolean error = false;
        if(TextUtils.isEmpty( txtEmail.getText())){
            inputLayoutEmail.setError(getString(R.string.no_email_error));
            error = true;
        }

        if(TextUtils.isEmpty( txtPassword.getText())){
            inputLayoutPassword.setError(getString(R.string.no_password_error));
            error = true;
        }

        if(error){
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            AppConstantes.FIREBASE_USER = user;



                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            AppConstantes.FIREBASE_USER = null;
                            updateUI();
                        }

                        // ...
                    }
                });
    }




    private void updateUI(){

        if(AppConstantes.FIREBASE_USER != null) {
            Intent i = new Intent(LoginActivity.this, BlueSplasActivity.class);
            startActivity(i);
            finish();
        }
    }

    public void showPasswordRecover(View v){
        Intent i = new Intent(LoginActivity.this, ReoverPasswordActivity.class);
        startActivity(i);
    }
}
