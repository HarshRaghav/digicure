package android.example.health.medicineBox

import android.example.health.R
import android.example.health.daos.MedicineDAO
import android.example.health.models.MedicineBox
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson

class MedicineBoxMedicineInformationActivity : AppCompatActivity()  {

    private lateinit var medicineName: AppCompatTextView
    private lateinit var medicineMRP: AppCompatTextView
    private lateinit var medicineExpiryDate: AppCompatTextView
    private lateinit var medicineManufactureDate: AppCompatTextView
    private lateinit var medicineManufacturer: AppCompatTextView
    private lateinit var medicineImage: AppCompatImageView
    private lateinit var medicineDAO: MedicineBox


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.medicine_box_medicine_details)
        medicineName = findViewById<View>(R.id.medicine_name) as AppCompatTextView
        medicineMRP = findViewById<View>(R.id.medicine_cost) as AppCompatTextView
        medicineExpiryDate =
            findViewById<View>(R.id.medicine_expiry_date) as AppCompatTextView
        medicineManufactureDate =
            findViewById<View>(R.id.medicine_manufacture_date) as AppCompatTextView
        medicineManufacturer =
            findViewById<View>(R.id.medicine_manufacturer) as AppCompatTextView
        medicineImage = findViewById<View>(R.id.medicine_image) as AppCompatImageView

        val intent = intent.extras

        if (intent != null) {
            if(intent.containsKey("medicineData")){
                val strDAO : String = intent.get("medicineData") as String
                medicineDAO = Gson().fromJson(strDAO, MedicineBox :: class.java)

            }
        }

        medicineName.text = medicineDAO.medicineName
        //Glide.with(holder.medicineImage.context).load(model.createdBy.imageUrl).circleCrop().into(holder.medicineImage)
        medicineMRP.text = medicineDAO.medicineMRP
        medicineManufactureDate.text = medicineDAO.medicineManufactureDate
        medicineExpiryDate.text = medicineDAO.medicineExpiryDate
        medicineManufacturer.text = medicineDAO.medicineManufacturer

    }

}