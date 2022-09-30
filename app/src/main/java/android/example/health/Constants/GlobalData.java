package android.example.health.Constants;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.example.health.R;
import android.example.health.utils.LocalPersistenceManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDexApplication;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class GlobalData extends MultiDexApplication {
    //private LocationRequest location_request;
    //private ReactiveLocationProvider locationProvider;
    public static String TAG = GlobalData.class.getSimpleName();
    private SweetAlertDialog sweetAlertDialog;


    private SharedPreferences mSharedPreferences;


    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    /*
    private WriteToSdCard mWriteToSdCard = null;

    public WriteToSdCard getWriteToSdCardInstance() {
        if (mWriteToSdCard == null) {
            mWriteToSdCard = new WriteToSdCard(this);
        }
        return mWriteToSdCard;

    }

     */

    public void showProgressDialog(Context mContext, String title, String message) {
        if (mContext == null || ((Activity) mContext).isFinishing()) {
            return;
        }
        if (sweetAlertDialog == null) {
            sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
            if (sweetAlertDialog == null) {
                return;
            } else {
                sweetAlertDialog.getProgressHelper().setBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
                sweetAlertDialog.setTitleText(title);
                sweetAlertDialog.setContentText(message);
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.setCanceledOnTouchOutside(false);
                sweetAlertDialog.show();
            }

        } else {
            sweetAlertDialog.show();
        }

    }

    public void showAlertDialogWithOk(Context mContext, String title, String message, String confirmText, int confirmBGColor, int confirmTextColor, int alertType, SweetAlertDialog.OnSweetClickListener onSweetConfirmClickListener) {

        if (mContext == null || ((Activity) mContext).isFinishing()) {
            return;
        }
        if (sweetAlertDialog == null) {
            sweetAlertDialog = new SweetAlertDialog(mContext, alertType);
            if (sweetAlertDialog == null) {
                return;
            } else {
                sweetAlertDialog.setTitleText(title);
                sweetAlertDialog.setContentText(message);
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.setCanceledOnTouchOutside(false);
                sweetAlertDialog.setConfirmText(confirmText);
                sweetAlertDialog.setConfirmClickListener(onSweetConfirmClickListener);
                sweetAlertDialog.setConfirmButtonTextColor(ContextCompat.getColor(mContext, confirmTextColor));
                sweetAlertDialog.setConfirmButtonBackgroundColor(ContextCompat.getColor(mContext, confirmBGColor));
                sweetAlertDialog.show();


            }

        } else {
            sweetAlertDialog.show();
        }

    }

    public void showAlertDialogWithOkCancel(Context mContext, String title, String message, String confirmText, String cancelText, int confirmBGColor, int cancelBGColor, int alertType, SweetAlertDialog.OnSweetClickListener onSweetConfirmClickListener, SweetAlertDialog.OnSweetClickListener onSweetCancelClickListener) {

        if (mContext == null || ((Activity) mContext).isFinishing()) {
            return;
        }
        if (sweetAlertDialog == null) {
            sweetAlertDialog = new SweetAlertDialog(mContext, alertType);
            if (sweetAlertDialog == null) {
                return;
            } else {
                sweetAlertDialog.setTitleText(title);
                sweetAlertDialog.setContentText(message);
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.setCanceledOnTouchOutside(false);
                sweetAlertDialog.setConfirmText(confirmText);
                sweetAlertDialog.setCancelText(cancelText);
                sweetAlertDialog.setConfirmClickListener(onSweetConfirmClickListener);
                sweetAlertDialog.setCancelClickListener(onSweetCancelClickListener);
                sweetAlertDialog.setConfirmButtonBackgroundColor(ContextCompat.getColor(mContext, confirmBGColor));
                sweetAlertDialog.setCancelButtonBackgroundColor(ContextCompat.getColor(mContext, cancelBGColor));
                sweetAlertDialog.show();
            }

        } else {
            sweetAlertDialog.show();
        }

    }


    public void dismissProgressDialog() {
        if (sweetAlertDialog != null) {
            sweetAlertDialog.dismissWithAnimation();
            sweetAlertDialog = null;
        }

    }


    public static GlobalData app;
    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }


        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
               // FirebaseCrashlytics.getInstance().recordException(ex);
            }
        });
        LocalPersistenceManager.getLocalPersistenceManager().setAppSignature();
    }

    public static GlobalData getApp() {
        return app;
    }

    public RequestQueue getRequestQueue(String url) {
        if (mRequestQueue == null) {
            if (url.contains("https")) {
                mRequestQueue = Volley.newRequestQueue(getApp(), hurlStack);
            } else {
                mRequestQueue = Volley.newRequestQueue(getApp());
            }
        }

        return mRequestQueue;
    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 150);
        toast.show();
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the state_default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue(req.getUrl()).add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue(req.getUrl()).add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    //First Method to invike HTTPS Request using Application Class by using just call nuke() method from  onCreate method of application class.
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++newSslSocketFactory

    public static class NukeSSLCerts {
        protected static final String TAG = "NukeSSLCerts";

        public static void nuke() {
            try {
                TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() {
                                X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                                return myTrustedAnchors;
                            }

                            @Override
                            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            }

                            @Override
                            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            }
                        }
                };

                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                });
            } catch (Exception e) {
            }
        }
    }

    HurlStack hurlStack = new HurlStack() {
        @Override
        protected HttpURLConnection createConnection(URL url) throws IOException {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
            try {
                httpsURLConnection.setSSLSocketFactory(getSSLSocketFactory());
                httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception", "Exception :" + e.getMessage());
            }
            return httpsURLConnection;
        }
    };

    // Let's assume your server app is hosting inside a server machine
    // which has a server certificate in which "Issued to" is "localhost",for example.
    // Then, inside verify method you can verify "localhost".
    // If not, you can temporarily return true
    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                //return true; // verify always returns true, which could cause insecure network traffic due to trusting TLS/SSL server certificates for wrong hostnames
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                boolean isHostnameVerifier = hv.verify("localhost", session);
                Log.e("isHostname", "" + isHostnameVerifier);

                /*  return isHostnameVerifier;*/
                return true;
            }
        };
    }

    private SSLSocketFactory getSSLSocketFactory()
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X.509", "BC");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        InputStream caInput = getResources().openRawResource(R.raw.codeprojectssl); // this cert file stored in \app\src\main\res\raw folder path

        Certificate ca = cf.generateCertificate(caInput);
        caInput.close();

        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("BKS", "BC");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        TrustManager[] wrappedTrustManagers = getWrappedTrustManagers(tmf.getTrustManagers());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, wrappedTrustManagers, null);

        return sslContext.getSocketFactory();
    }

    private TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
        return new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return originalTrustManager.getAcceptedIssuers();
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0) {
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkClientTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkClientTrusted", e.toString());
                        }
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0) {
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkServerTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkServerTrusted", e.toString());
                        }
                    }
                }
        };
    }


    public static void showSnackbar(Context context, View view, String message, ColorType color) {
        if (context == null || ((Activity) context).isFinishing()) {
            return;
        }
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);

        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, getColor(color)));
        TextView tv = (TextView) snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);

        snackbar.show();
    }

    private static int getColor(ColorType color) {
        int color_code = 0;
        switch (color) {
            case SUCCESS:
                color_code = R.color.colorPrimary;
                break;
            case ERROR:
                color_code = R.color.colorPrimary;
                break;
            case UPDATE:
                color_code = R.color.brand_green;
                break;
            case WARNING:
                color_code = R.color.ornageshade;
                break;
        }
        return color_code;
    }

    public enum ColorType {
        SUCCESS,
        ERROR,
        UPDATE,
        WARNING;
    }

    public void hideSoftKeyboard(Activity mActivity) {
        if (mActivity == null || mActivity.isFinishing()) {
            return;
        }
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    public static void hideKeyboardFromActivity(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /*
    public LocationRequest getLocationRequest() {
        if (location_request == null) {
            location_request = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setNumUpdates(1)
                    .setSmallestDisplacement(0)
                    .setInterval(1000);

        }

        return location_request;
    }


    public ReactiveLocationProvider getReactiveLocationProvider() {
        if (locationProvider == null) {
            // locationProvider = new ReactiveLocationProvider(this);
            locationProvider = new ReactiveLocationProvider(getApplicationContext(), ReactiveLocationProviderConfiguration
                    .builder()
                    .setRetryOnConnectionSuspended(true)
                    .build());

        }

        return locationProvider;
    }
    */

    public boolean isLocationServiceEnabled() {
        LocationManager locationManager = null;
        boolean gps_enabled = false, network_enabled = false;

        if (locationManager == null)
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            //do nothing...
        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            //do nothing...
        }

        return gps_enabled || network_enabled;

    }

    public boolean isHighAccuracyLocationModeEnable() {
        boolean isHighAccuracyLocationModeEnable;
        try {
            int locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            if (locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {
                isHighAccuracyLocationModeEnable = true;
            } else {
                isHighAccuracyLocationModeEnable = false;
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            isHighAccuracyLocationModeEnable = false;

        }
        return isHighAccuracyLocationModeEnable;
    }


    public void deleteImageFile(String filePath) {
        File file = new File(filePath);
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    public void deleteLoggerFile(String filePath) {
        File file = new File(filePath);
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    public static boolean deleteFolderContent(File file) {
        if (file.isDirectory()) {
            String[] children = file.list();
            for (int i = 0; i < children.length; i++) {
                deleteFolderContent(new File(file, children[i]));

            }
        }
        return file.delete();
    }

    public boolean isUploadingForgroundServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.e("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.e("isMyServiceRunning?", false + "");
        return false;
    }

    public static String getMd5(String input)
    {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }



}