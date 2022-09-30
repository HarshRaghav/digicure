package android.example.health;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.example.health.Constants.GlobalData;
import android.example.health.networkUtils.CheckNetworkConnection;
import android.example.health.utils.LocalPersistenceManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {
    private MaterialButton payBtn;
    private AppCompatTextView consultancyFeesTextView;
    private AppCompatTextView slotTextView;
    private String TAG = PaymentActivity.class.getSimpleName();

    private long mLastClickTime = 0;
    private long mClickWaitTime = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        payBtn = (MaterialButton) findViewById(R.id.idBtnPay);
        consultancyFeesTextView = (AppCompatTextView)findViewById(R.id.txt_view_consultancy_fees);
        slotTextView =(AppCompatTextView)findViewById(R.id.txt_view_slot);

        consultancyFeesTextView.setText(LocalPersistenceManager.getLocalPersistenceManager().getDoctorConsultationFees()+"");
        slotTextView.setText(LocalPersistenceManager.getLocalPersistenceManager().getSlot());
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < mClickWaitTime) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                String samount = ""+LocalPersistenceManager.getLocalPersistenceManager().getDoctorConsultationFees();
                int amount = Math.round(Float.parseFloat(samount) * 100);
                if(CheckNetworkConnection.isNetworkAvailable(PaymentActivity.this)){
                    payForCourse(amount);
                }else{
                    GlobalData.showSnackbar(PaymentActivity.this, findViewById(android.R.id.content), "No Internet Connection", GlobalData.ColorType.ERROR);
                }

            }
        });
    }
    @Override
    public void onPaymentSuccess(String s) {
        LocalPersistenceManager.getLocalPersistenceManager().setTransactionID(s);
        Intent paymentSuccessIntent =  new Intent(PaymentActivity.this , SuccessfulPaymentActivity.class);
        startActivity(paymentSuccessIntent);
        Toast.makeText(this, "Payment is successful : " + s, Toast.LENGTH_SHORT).show();
        Log.e("onPaymentSuccess","Result :"+s);
    }

    @SuppressWarnings("unused")
    @Override
    public void onPaymentError(int i, String s) {
        LocalPersistenceManager.getLocalPersistenceManager().setTransactionID(s);
        Intent paymentFailedIntent =  new Intent(PaymentActivity.this , PaymentActivity.class);
        startActivity(paymentFailedIntent);
        Toast.makeText(this, "Payment Failed due to error : " + s, Toast.LENGTH_SHORT).show();
        Log.e("onPaymentError","Result :"+s);
    }


    private void payForCourse(int amount){
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_frQHgt1x2HEtaW");
        checkout.setImage(R.drawable.app_icon);

        JSONObject params = new JSONObject();
        try {
            params.put("name", "ConsultancyFees");
            params.put("theme.color", "");
            params.put("currency", "INR");
            params.put("amount", amount);
            params.put("prefill.contact", LocalPersistenceManager.getLocalPersistenceManager().getUserMobile());
            params.put("prefill.email", LocalPersistenceManager.getLocalPersistenceManager().getUserEmail());
            checkout.open(PaymentActivity.this, params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}