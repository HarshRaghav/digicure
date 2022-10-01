package android.example.health.medicineBox

import android.example.health.Constants.GlobalData
import android.example.health.R
import android.example.health.daos.MedicineDAO
import android.example.health.models.MedicineBox
import android.example.health.networkUtils.CheckNetworkConnection
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import android.content.Intent
import android.net.Uri


class MedicineBoxContactInformationActivity : AppCompatActivity()  {

    private lateinit var contactImage: AppCompatImageView
    private lateinit var contactName: AppCompatTextView
    private lateinit var contactEmail: AppCompatTextView
    private lateinit var contactMobileNumber: AppCompatTextView
    private lateinit var contactCity: AppCompatTextView
    private lateinit var contactState: AppCompatTextView
    private lateinit var contactCountry: AppCompatTextView
    private lateinit var medicineDAO: MedicineBox
    private lateinit var callImage: AppCompatImageView
    private lateinit var emailImage: AppCompatImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.medicine_box_contact_info)
        contactImage = findViewById<View>(R.id.user_image) as AppCompatImageView
        contactName = findViewById<View>(R.id.user_name) as AppCompatTextView
        contactEmail = findViewById<View>(R.id.user_email) as AppCompatTextView
        contactMobileNumber = findViewById<View>(R.id.user_mobile) as AppCompatTextView
        contactCity = findViewById<View>(R.id.user_city) as AppCompatTextView
        contactState = findViewById<View>(R.id.user_state) as AppCompatTextView
        contactCountry = findViewById<View>(R.id.user_country) as AppCompatTextView
        callImage = findViewById<View>(R.id.call_button) as AppCompatImageView
        emailImage = findViewById<View>(R.id.email_button) as AppCompatImageView

        val intent = intent.extras

        if (intent != null) {
            if(intent.containsKey("medicineData")){
                val strDAO : String = intent.get("medicineData") as String
                medicineDAO = Gson().fromJson(strDAO, MedicineBox :: class.java)

            }
        }

        contactName.text = medicineDAO.ContactUserName
        Glide.with(contactImage).load(medicineDAO.createdBy.imageUrl).circleCrop().into(contactImage)
        contactEmail.text = medicineDAO.ContactUserEmailID
        contactMobileNumber.text = medicineDAO.ContactUserMobileNumber
        contactCity.text = medicineDAO.ContactUserCity
        contactState.text = medicineDAO.ContactUserState
        contactCountry.text = medicineDAO.ContactUserCountry

        callImage.setOnClickListener(View.OnClickListener {
            if(medicineDAO.ContactUserMobileNumber != null) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:" + medicineDAO.ContactUserMobileNumber)
                startActivity(intent);

            }
            else{
                GlobalData.showSnackbar(
                    this@MedicineBoxContactInformationActivity,
                    findViewById(android.R.id.content),
                    "Invalid number",
                    GlobalData.ColorType.ERROR
                )

            }
        })

        emailImage.setOnClickListener(View.OnClickListener {
            if(medicineDAO.ContactUserEmailID != null) {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:")
                val addresses = arrayOf(medicineDAO.ContactUserEmailID)
                intent.putExtra(Intent.EXTRA_EMAIL, addresses)
                startActivity(Intent.createChooser(intent, "Choose mail"));

            }
            else{
                GlobalData.showSnackbar(
                    this@MedicineBoxContactInformationActivity,
                    findViewById(android.R.id.content),
                    "Invalid email",
                    GlobalData.ColorType.ERROR
                )

            }
        })

    }

}