package android.example.health.medicineBox

import android.content.Intent
import android.example.health.Constants.GlobalData
import android.example.health.HomeScreenActivity
import android.example.health.R
import android.example.health.daos.MedicineDAO
import android.example.health.networkUtils.CheckNetworkConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.View
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ContactDetailsActivity : AppCompatActivity() {
    private lateinit var contactName: TextInputEditText
    private lateinit var contactEmail: TextInputEditText
    private lateinit var contactMobileNumber: TextInputEditText
    private lateinit var contactCity: TextInputEditText
    private lateinit var contactState: TextInputEditText
    private lateinit var contactCountry: TextInputEditText
    private lateinit var btnSave: MaterialButton
    private lateinit var medicineName:String
    private lateinit var medicineMRP:String
    private lateinit var medicineExpiryDate:String
    private lateinit var medicineManufactureDate:String
    private lateinit var medicineManufacturer:String
    private var mLastClickTime: Long = 0
    private val mClickWaitTime: Long = 3000
    private lateinit var medicineDao :MedicineDAO
    private val TAG = ContactDetailsActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_details)
        val intent =intent
        medicineName = intent.getStringExtra("medicineName").toString()
        medicineMRP = intent.getStringExtra("medicineMRP").toString()
        medicineExpiryDate = intent.getStringExtra("medicineExpiryDate").toString()
        medicineManufactureDate = intent.getStringExtra("medicineManufactureDate").toString()
        medicineManufacturer = intent.getStringExtra("medicineManufacturer").toString()

        contactName = findViewById<View>(R.id.contact_details_name) as TextInputEditText
        contactEmail = findViewById<View>(R.id.contact_details_email) as TextInputEditText
        contactMobileNumber = findViewById<View>(R.id.contact_details_mobile) as TextInputEditText
        contactCity = findViewById<View>(R.id.contact_details_city) as TextInputEditText
        contactState = findViewById<View>(R.id.contact_details_state) as TextInputEditText
        contactCountry = findViewById<View>(R.id.contact_details_country) as TextInputEditText
        btnSave = findViewById<View>(R.id.contact_details_save_btn) as MaterialButton
        btnSave.setOnClickListener(View.OnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < mClickWaitTime) {
                return@OnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (CheckNetworkConnection.isNetworkAvailable(this@ContactDetailsActivity)) {
                if (isAllFieldValidated()) {
                    medicineDao = MedicineDAO()
                    medicineDao.addMedicine(medicineName, medicineMRP, medicineExpiryDate, medicineManufactureDate, medicineManufacturer, contactName.text.toString(), contactMobileNumber.text.toString(), contactEmail.text.toString(), contactCity.text.toString(), contactState.text.toString(), contactCountry.text.toString())
                    val mHandler : Handler  = Handler()
                    if (mHandler != null) {
                        mHandler.postDelayed(mRunnable, 2000)
                    }

                }

            } else {
                GlobalData.showSnackbar(
                    this@ContactDetailsActivity,
                    findViewById(android.R.id.content),
                    "No Internet Connection",
                    GlobalData.ColorType.ERROR
                )
            }
        })
    }

    var mRunnable = Runnable {

        val intent = Intent(this, HomeScreenActivity :: class.java)
        startActivity(intent)

    }

    private fun isAllFieldValidated(): Boolean {
        var isAllFieldValid = true
        if (contactName.text.toString().trim { it <= ' ' } == "") {
            GlobalData.showSnackbar(
                this@ContactDetailsActivity,
                findViewById<View>(android.R.id.content),
                "Enter Contact Name",
                GlobalData.ColorType.ERROR
            )
            isAllFieldValid = false
        }
        else if (contactEmail.text.toString().trim { it <= ' ' } == "") {
            GlobalData.showSnackbar(
                this@ContactDetailsActivity,
                findViewById<View>(android.R.id.content),
                "Enter Contact Email",
                GlobalData.ColorType.ERROR
            )
            isAllFieldValid = false
        }
        else if (contactMobileNumber.text.toString().trim { it <= ' ' } == "") {
            GlobalData.showSnackbar(
                this@ContactDetailsActivity,
                findViewById<View>(android.R.id.content),
                "Enter Contact Mobile Number",
                GlobalData.ColorType.ERROR
            )
            isAllFieldValid = false
        }
        else if (contactCity.text.toString().trim { it <= ' ' } == "") {
            GlobalData.showSnackbar(
                this@ContactDetailsActivity,
                findViewById<View>(android.R.id.content),
                "Enter Contact City",
                GlobalData.ColorType.ERROR
            )
            isAllFieldValid = false
        }
        else if (contactState.text.toString().trim { it <= ' ' } == "") {
            GlobalData.showSnackbar(
                this@ContactDetailsActivity,
                findViewById<View>(android.R.id.content),
                "Enter Contact State",
                GlobalData.ColorType.ERROR
            )
            isAllFieldValid = false
        }
        else if (contactCountry.text.toString().trim { it <= ' ' } == "") {
            GlobalData.showSnackbar(
                this@ContactDetailsActivity,
                findViewById<View>(android.R.id.content),
                "Enter Contact Country",
                GlobalData.ColorType.ERROR
            )
            isAllFieldValid = false
        }
        return isAllFieldValid
    }
}