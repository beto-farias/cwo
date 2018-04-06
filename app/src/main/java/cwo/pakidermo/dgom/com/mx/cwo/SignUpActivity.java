package cwo.pakidermo.dgom.com.mx.cwo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cwo.pakidermo.dgom.com.mx.cwo.app.AppConstantes;

public class SignUpActivity extends Activity {

    private FirebaseAuth mAuth;
    private static final String TAG ="SignUpActivity";

    TextView txtNombreUsuario;
    TextView txtEmail;
    TextView txtPassword;

    TextInputLayout inputLayoutNombre;
    TextInputLayout inputLayoutEmail;
    TextInputLayout inputLayoutPassword;

    //Analiticos
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sign_up);

        txtNombreUsuario = (TextView) findViewById(R.id.txt_nombre_usuario);
        txtEmail = (TextView) findViewById(R.id.txt_email);
        txtPassword = (TextView) findViewById(R.id.txt_password);

        inputLayoutNombre = (TextInputLayout) findViewById(R.id.input_layout_nombre);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);

        mAuth = FirebaseAuth.getInstance();

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }


    public void createUserAction(View v){

        boolean error = false;
        if(TextUtils.isEmpty( txtEmail.getText())){
            inputLayoutEmail.setError(getString(R.string.no_email_error));
            error = true;
        }

        if(TextUtils.isEmpty( txtNombreUsuario.getText())){
            inputLayoutNombre.setError(getString(R.string.no_user_name_error));
            error = true;
        }

        if(TextUtils.isEmpty( txtPassword.getText())){
            inputLayoutPassword.setError(getString(R.string.no_password_error));
            error = true;
        }

        if(error){
            return;
        }


        createUser(txtEmail.getText().toString(), txtPassword.getText().toString());
    }

    public void loginFacebookAction(View v){
        Intent i = new Intent(SignUpActivity.this, WelcomeActivity.class);
        startActivity(i);
        finish();

    }

    private void createUser(String email,String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            AppConstantes.FIREBASE_USER = user;
                            updateUI();
                        } else {

                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Error de autenticación, " + task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                            AppConstantes.FIREBASE_USER = null;
                        }
                    }
                });
    }

    private void updateUI(){

        if(AppConstantes.FIREBASE_USER != null) {
            Intent i = new Intent(SignUpActivity.this, BlueSplasActivity.class);
            startActivity(i);
            finish();
        }
    }
}
