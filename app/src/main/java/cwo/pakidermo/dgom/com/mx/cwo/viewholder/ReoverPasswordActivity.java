package cwo.pakidermo.dgom.com.mx.cwo.viewholder;

import android.app.Activity;
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
import com.google.firebase.auth.FirebaseAuth;

import cwo.pakidermo.dgom.com.mx.cwo.R;

public class ReoverPasswordActivity extends Activity {

    private static final String TAG = "ReoverPassword";
    private FirebaseAuth mAuth;

    TextInputLayout inputLayoutEmail;
    TextView txtEmail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reover_password);

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        txtEmail = (TextView) findViewById(R.id.txt_email);


        mAuth = FirebaseAuth.getInstance();
    }

    private void recoverPassword(String emailAddress){

        //https://firebase.google.com/docs/auth/android/manage-users#send_a_password_reset_email
        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            Toast.makeText(getApplicationContext(),R.string.password_recover_mail_send,Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(getApplicationContext(), R.string.password_recover_mail_send_error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    public void showPasswordRecover(View v){
        boolean error = false;
        if(TextUtils.isEmpty( txtEmail.getText())){
            inputLayoutEmail.setError(getString(R.string.no_email_error));
            error = true;
        }

        if(error){
            return;
        }

        recoverPassword(txtEmail.getText().toString());
    }
}
