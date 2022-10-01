package android.example.health.medicineBox

import android.app.DatePickerDialog
import android.content.Intent
import android.example.health.Constants.GlobalData
import android.example.health.R
import android.example.health.networkUtils.CheckNetworkConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class MedicineDetailsActivity : AppCompatActivity() {
    private lateinit var medicineName: TextInputEditText
    private lateinit var medicineMRP: TextInputEditText
    private lateinit var medicineExpiryDate: TextInputEditText
    private lateinit var expiryCalendarImage: AppCompatImageView
    private lateinit var medicineManufactureDate: TextInputEditText
    private lateinit var manufactureCalendarImage: AppCompatImageView
    private lateinit var medicineManufacturer: TextInputEditText
    private lateinit var btnNext: MaterialButton

    private var mLastClickTime: Long = 0
    private val mClickWaitTime: Long = 3000
    private val TAG = MedicineDetailsActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicine_details)
        medicineName = findViewById<View>(R.id.medicine_details_name) as TextInputEditText
        medicineMRP = findViewById<View>(R.id.medicine_details_mrp) as TextInputEditText
        medicineExpiryDate = findViewById<View>(R.id.medicine_details_expiry_date) as TextInputEditText
        expiryCalendarImage = findViewById<View>(R.id.medicine_details_expiry_calendar_image) as AppCompatImageView
        medicineManufactureDate = findViewById<View>(R.id.medicine_details_manufacture_date) as TextInputEditText
        manufactureCalendarImage = findViewById<View>(R.id.medicine_details_manufacture_calendar_image) as AppCompatImageView
        medicineManufacturer = findViewById<View>(R.id.medicine_details_manufacturer) as TextInputEditText
        btnNext = findViewById<View>(R.id.medicine_details_next_btn) as MaterialButton

        expiryCalendarImage.setOnClickListener { // TODO Auto-generated method stub
            val mcurrentDate = Calendar.getInstance()
            val year = mcurrentDate[Calendar.YEAR]
            val month = mcurrentDate[Calendar.MONTH]
            val day = mcurrentDate[Calendar.DAY_OF_MONTH]
            val mDatePicker = DatePickerDialog(
                this@MedicineDetailsActivity,
                { datepicker, selectedYear, selectedMonth, selectedDay -> medicineExpiryDate.setText(selectedYear.toString() + "-" + (selectedMonth + 1) + "-" + selectedDay) },
                year,
                month,
                day
            )
            mDatePicker.setTitle("Select date")
            mDatePicker.show()
            val minDate = Calendar.getInstance()
            minDate[Calendar.DAY_OF_MONTH] = day
            minDate[Calendar.MONTH] = month
            minDate[Calendar.YEAR] = year
            mDatePicker.datePicker.minDate = minDate.timeInMillis
        }

        manufactureCalendarImage.setOnClickListener { // TODO Auto-generated method stub
            val mcurrentDate = Calendar.getInstance()
            val year = mcurrentDate[Calendar.YEAR]
            val month = mcurrentDate[Calendar.MONTH]
            val day = mcurrentDate[Calendar.DAY_OF_MONTH]
            val mDatePicker = DatePickerDialog(
                this@MedicineDetailsActivity,
                { datepicker, selectedYear, selectedMonth, selectedDay -> medicineManufactureDate.setText(selectedYear.toString() + "-" + (selectedMonth + 1) + "-" + selectedDay) },
                year,
                month,
                day
            )
            mDatePicker.setTitle("Select date")
            mDatePicker.show()
            val minDate = Calendar.getInstance()
            minDate[Calendar.DAY_OF_MONTH] = day
            minDate[Calendar.MONTH] = month
            minDate[Calendar.YEAR] = year
            mDatePicker.datePicker.maxDate = minDate.timeInMillis
        }

        btnNext.setOnClickListener(View.OnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < mClickWaitTime) {
                return@OnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            if (CheckNetworkConnection.isNetworkAvailable(this@MedicineDetailsActivity)) {
                if (isAllFieldValidated()) {
                    val contactIntent = Intent(this@MedicineDetailsActivity, ContactDetailsActivity::class.java)
                    try {
                        contactIntent.putExtra("medicineName", medicineName.text.toString().trim())
                        contactIntent.putExtra("medicineMRP", medicineMRP.text.toString().trim())
                        contactIntent.putExtra("medicineExpiryDate", medicineExpiryDate.text.toString().trim())
                        contactIntent.putExtra("medicineManufactureDate", medicineManufactureDate.text.toString().trim())
                        contactIntent.putExtra("medicineManufacturer", medicineManufacturer.text.toString().trim())
                        startActivity(contactIntent)
                    } catch (e: Exception) {
                        GlobalData.showSnackbar(
                            this@MedicineDetailsActivity,
                            findViewById<View>(android.R.id.content),
                            "Something went wrong!!",
                            GlobalData.ColorType.ERROR
                        )
                        e.printStackTrace()
                    }
                }

            } else {
                GlobalData.showSnackbar(
                    this@MedicineDetailsActivity,
                    findViewById(android.R.id.content),
                    "No Internet Connection",
                    GlobalData.ColorType.ERROR
                )
            }
        })
    }

    private fun isAllFieldValidated(): Boolean {
        var isAllFieldValid = true
        if (medicineName.text.toString().trim { it <= ' ' } == "") {
            GlobalData.showSnackbar(
                this@MedicineDetailsActivity,
                findViewById<View>(android.R.id.content),
                "Enter Medicine Name",
                GlobalData.ColorType.ERROR
            )
            isAllFieldValid = false
        } else if (medicineMRP.text.toString().trim { it <= ' ' } == "") {
            GlobalData.showSnackbar(
                this@MedicineDetailsActivity,
                findViewById<View>(android.R.id.content),
                "Enter Medicine MRP",
                GlobalData.ColorType.ERROR
            )
            isAllFieldValid = false
        } else if (medicineExpiryDate.text.toString().trim { it <= ' ' } == "") {
            GlobalData.showSnackbar(
                this@MedicineDetailsActivity,
                findViewById<View>(android.R.id.content),
                "Enter Medicine Expiry Date",
                GlobalData.ColorType.ERROR
            )
            isAllFieldValid = false
        } else if (medicineManufactureDate.text.toString().trim { it <= ' ' } == "") {
            GlobalData.showSnackbar(
                this@MedicineDetailsActivity,
                findViewById<View>(android.R.id.content),
                "Enter Medicine Manufactured Date",
                GlobalData.ColorType.ERROR
            )
            isAllFieldValid = false
        } else if (medicineManufacturer.text.toString().trim { it <= ' ' } == "") {
            GlobalData.showSnackbar(
                this@MedicineDetailsActivity,
                findViewById<View>(android.R.id.content),
                "Enter Manufacturer Name",
                GlobalData.ColorType.ERROR
            )
            isAllFieldValid = false
        }
        return isAllFieldValid
    }
}