package android.example.health;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.app.PendingIntent;
import android.content.Intent;
import android.example.health.Constants.GlobalData;
import android.example.health.daos.DoctorDAO;
import android.example.health.networkUtils.CheckNetworkConnection;
import android.example.health.utils.LocalPersistenceManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;

import com.google.android.material.button.MaterialButton;

public class SuccessfulPaymentActivity extends AppCompatActivity {
    AppCompatTextView OrderIDTextView;
    AppCompatTextView AmountPaidTextView;
    AppCompatTextView SlotTextView;
    MaterialButton HomeButton;
    private String TAG = SuccessfulPaymentActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful_payment);

        OrderIDTextView = (AppCompatTextView) findViewById(R.id.txt_view_order_id);
        AmountPaidTextView = (AppCompatTextView) findViewById(R.id.txt_view_amount_paid);
        SlotTextView = (AppCompatTextView) findViewById(R.id.txt_view_slot);
        HomeButton = (MaterialButton) findViewById(R.id.idBtnMyAccount);

        OrderIDTextView.setText(LocalPersistenceManager.getLocalPersistenceManager().getTransactionID()!=null?LocalPersistenceManager.getLocalPersistenceManager().getTransactionID():"NA");
        AmountPaidTextView.setText((LocalPersistenceManager.getLocalPersistenceManager().getDoctorConsultationFees()+"").equalsIgnoreCase("")?"NA":LocalPersistenceManager.getLocalPersistenceManager().getDoctorConsultationFees()+"");
        SlotTextView.setText(LocalPersistenceManager.getLocalPersistenceManager().getSlot()!=null?LocalPersistenceManager.getLocalPersistenceManager().getSlot() : "NA");
        DoctorDAO doctorDAO = new DoctorDAO();
        doctorDAO.updateSlots(LocalPersistenceManager.getLocalPersistenceManager().getId(), LocalPersistenceManager.getLocalPersistenceManager().getSlot());

        HomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsManager smsManager=SmsManager.getDefault();
                smsManager.sendTextMessage("6397440584",null,"Hi, your appointment is booked at" + LocalPersistenceManager.getLocalPersistenceManager().getSlot() + "!! Please call " + LocalPersistenceManager.getLocalPersistenceManager().getPhoneNumber().toString() + " at your respective slot time. Thankyou.",null,null);
                if (CheckNetworkConnection.isNetworkAvailable(SuccessfulPaymentActivity.this)) {
                    Intent MyAccountIntent = new Intent(SuccessfulPaymentActivity.this, HomeScreenActivity.class);
                    MyAccountIntent.putExtra("appoint", true);
                    try {
                        startActivity(MyAccountIntent);
                    } catch (Exception e) {
                        GlobalData.showSnackbar(SuccessfulPaymentActivity.this, findViewById(android.R.id.content), "Something went wrong!!", GlobalData.ColorType.ERROR);
                        e.printStackTrace();
                    }
                } else {
                    GlobalData.showSnackbar(SuccessfulPaymentActivity.this, findViewById(android.R.id.content), getResources().getString(R.string.no_internet_connection), GlobalData.ColorType.ERROR);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (CheckNetworkConnection.isNetworkAvailable(SuccessfulPaymentActivity.this)) {
            Intent intent = new Intent(SuccessfulPaymentActivity.this, HomeScreenActivity.class);
            try {
                startActivity(intent);
            } catch (Exception e) {
                GlobalData.showSnackbar(SuccessfulPaymentActivity.this, findViewById(R.id.content), "Something went wrong!!", GlobalData.ColorType.ERROR);
                e.printStackTrace();
            }
        } else {
            GlobalData.showSnackbar(SuccessfulPaymentActivity.this, findViewById(R.id.content), "No Internet Connection", GlobalData.ColorType.ERROR);
        }
    }
}