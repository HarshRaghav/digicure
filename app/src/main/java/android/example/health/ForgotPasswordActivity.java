package android.example.health;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.example.health.Constants.GlobalData;
import android.example.health.networkUtils.CheckNetworkConnection;
import android.example.health.utils.MessagePersistanceManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity {
    private TextInputEditText edit_text_email_id;
    private MaterialButton sendPassword;

    private String EmailRegex;
    private Pattern EmailPattern;

    private long mLastClickTime = 0;
    private long mClickWaitTime = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        edit_text_email_id=(TextInputEditText) findViewById(R.id.forgot_password_Email_id);
        sendPassword = (MaterialButton) findViewById(R.id.btn_send_forgot_password);

        sendPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SystemClock.elapsedRealtime() - mLastClickTime<mClickWaitTime){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if(CheckNetworkConnection.isNetworkAvailable(ForgotPasswordActivity.this)){
                    if(isAllFieldValidated()){
                        sendLinkToEmail();

                    }
                }else{
                    GlobalData.showSnackbar(ForgotPasswordActivity.this, findViewById(android.R.id.content),"No Internet Connection", GlobalData.ColorType.ERROR);
                }
            }
        });
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (CheckNetworkConnection.isNetworkAvailable(ForgotPasswordActivity.this)) {
                Intent forgotPassword =  new Intent(ForgotPasswordActivity.this , LoginScreen.class);
                startActivity(forgotPassword);

            } else {
                GlobalData.showSnackbar(ForgotPasswordActivity.this, findViewById(android.R.id.content), MessagePersistanceManager.getMessagePersistanceManager().noInternetConnectionMessage(ForgotPasswordActivity.this), GlobalData.ColorType.ERROR);
                //retryDialog("Error", MessagePersistanceManager.getMessagePersistanceManager().noInternetConnectionMessage(SplashScreen.this));
            }
        }
    };

    private void sendLinkToEmail() {

        FirebaseAuth.getInstance().setLanguageCode("en"); // Set to English
        FirebaseAuth.getInstance()
                .sendPasswordResetEmail(edit_text_email_id.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            GlobalData.showSnackbar(ForgotPasswordActivity.this, findViewById(android.R.id.content),"Link send to your email successfully", GlobalData.ColorType.ERROR);
                            Handler mHandler = new Handler();
                            if (mHandler != null) {
                                mHandler.postDelayed(mRunnable, 3000);
                            }

                        }
                        else if(task.getException().toString().equalsIgnoreCase("com.google.firebase.auth.FirebaseAuthInvalidUserException: There is no user record corresponding to this identifier. The user may have been deleted.")){
                            GlobalData.showSnackbar(ForgotPasswordActivity.this, findViewById(android.R.id.content),"No such user exist.", GlobalData.ColorType.ERROR);

                        }
                        else {
                            GlobalData.showSnackbar(ForgotPasswordActivity.this, findViewById(android.R.id.content),"Please try again", GlobalData.ColorType.ERROR);

                        }
                    }
                });
        // Receive response from Firebase Console

    }

    private boolean isAllFieldValidated(){
        EmailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        EmailPattern = Pattern.compile(EmailRegex);
        boolean isAllFieldValid =true;
        if(edit_text_email_id.getText().toString().trim().equals("")){
            GlobalData.showSnackbar(ForgotPasswordActivity.this , findViewById(android.R.id.content),"Enter Email ID",GlobalData.ColorType.ERROR);
            isAllFieldValid=false;
        }else if(!EmailPattern.matcher(edit_text_email_id.getText().toString().trim()).matches()) {
            GlobalData.showSnackbar(ForgotPasswordActivity.this , findViewById(android.R.id.content),"Entered Email ID is not in proper format",GlobalData.ColorType.ERROR);
            isAllFieldValid=false;
        }
        return isAllFieldValid;
    }
}