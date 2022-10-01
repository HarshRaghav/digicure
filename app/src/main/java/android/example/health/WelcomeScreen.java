package android.example.health;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.browser.customtabs.CustomTabsIntent;

import android.content.Intent;
import android.example.health.Constants.GlobalData;
import android.example.health.networkUtils.CheckNetworkConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import com.google.android.material.button.MaterialButton;

public class WelcomeScreen extends AppCompatActivity {
    private MaterialButton btn_login;
    private AppCompatTextView txt_view_signup;


    private long mLastClickTime = 0;
    private long mClickWaitTime = 3000;
    private String TAG = WelcomeScreen.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_welcome_screen);

        btn_login = (MaterialButton) findViewById(R.id.btn_login);
        txt_view_signup = (AppCompatTextView) findViewById(R.id.txt_new_account);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < mClickWaitTime) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if(CheckNetworkConnection.isNetworkAvailable(WelcomeScreen.this)){
                    Intent intent = new Intent(WelcomeScreen.this, LoginScreen.class);
                    startActivity(intent);
                }else{
                    GlobalData.showSnackbar(WelcomeScreen.this, findViewById(android.R.id.content), "No Internet Connection", GlobalData.ColorType.ERROR);
                }
            }
        });

        txt_view_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < mClickWaitTime) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if(CheckNetworkConnection.isNetworkAvailable(WelcomeScreen.this)){
                    Intent intent = new Intent(WelcomeScreen.this, SignupScreen.class);
                    startActivity(intent);
                }else{
                    GlobalData.showSnackbar(WelcomeScreen.this, findViewById(android.R.id.content), "No Internet Connection", GlobalData.ColorType.ERROR);
                }
            }
        });
    }
}