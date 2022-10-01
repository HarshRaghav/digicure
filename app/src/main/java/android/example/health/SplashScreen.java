package android.example.health;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.example.health.Constants.GlobalData;
import android.example.health.networkUtils.CheckNetworkConnection;
import android.example.health.utils.MessagePersistanceManager;
import android.os.Bundle;
import android.os.Handler;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SplashScreen extends AppCompatActivity {
    private Handler mHandler;
    private static final String TAG = SplashScreen.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash_screen);

        mHandler = new Handler();
        if (mHandler != null) {
            mHandler.postDelayed(mRunnable, 3000);
        }
    }


    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (CheckNetworkConnection.isNetworkAvailable(SplashScreen.this)) {
                Intent intent = new Intent(SplashScreen.this,WelcomeScreen.class);
                startActivity(intent);

            } else {
                GlobalData.showSnackbar(SplashScreen.this, findViewById(android.R.id.content), MessagePersistanceManager.getMessagePersistanceManager().noInternetConnectionMessage(SplashScreen.this), GlobalData.ColorType.ERROR);
                retryDialog("Error", MessagePersistanceManager.getMessagePersistanceManager().noInternetConnectionMessage(SplashScreen.this));
            }
        }
    };

    private void retryDialog(String title, String Message) {
        if (!isFinishing()) {
            GlobalData.getApp().showAlertDialogWithOk(this, title, Message, "Retry", R.color.colorPrimary, R.color.white, SweetAlertDialog.NORMAL_TYPE, new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    GlobalData.getApp().dismissProgressDialog();
                    if (mHandler != null) {
                        mHandler.removeCallbacks(mRunnable);
                    }
                    mHandler = new Handler();
                    if (mHandler != null) {
                        mHandler.postDelayed(mRunnable, 3000);
                    }
                }
            });
        }
    }

    private void getAppVersionFromServer() {

    }

    @Override
    protected void onStop() {
        //GlobalData.getApp().cancelPendingRequests();
        super.onStop();
    }
}