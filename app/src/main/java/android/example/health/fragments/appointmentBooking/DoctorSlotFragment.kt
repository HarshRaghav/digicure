package android.example.health.fragments.appointmentBooking

import android.content.Intent
import android.example.health.Constants.GlobalData
import android.example.health.MainActivity
import android.example.health.PaymentActivity
import android.example.health.R
import android.example.health.adapters.MyMedicineAdapter
import android.example.health.adapters.SlotsAdapter
import android.example.health.daos.DoctorDAO
import android.example.health.models.Doctor
import android.example.health.models.MedicineBox
import android.example.health.networkUtils.CheckNetworkConnection
import android.example.health.utils.LocalPersistenceManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class DoctorSlotFragment : Fragment(), SlotsAdapter.OnSlotItemClick{

    private var mLastClickTime : Long = 0
    private var mClickWaitTime : Long = 3000
    private lateinit var slotsRecyclerView : RecyclerView;
    private lateinit var rootView : View
    private lateinit var doctorDetails : Doctor
    private lateinit var doctorImage : AppCompatImageView
    private lateinit var doctorName : AppCompatTextView
    private lateinit var doctorSpeciality : AppCompatTextView
    private lateinit var doctorYears : AppCompatTextView
    private lateinit var slotsAdapter : SlotsAdapter
    private lateinit var id : String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //rootView = requireView()
        return inflater.inflate(R.layout.activity_appointment_time_slots, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        slotsRecyclerView = requireView().findViewById<View>(R.id.slots_recycler_view) as RecyclerView
        doctorImage = requireView().findViewById<View>(R.id.doctor_image) as AppCompatImageView
        doctorName = requireView().findViewById<View>(R.id.doctor_name) as AppCompatTextView
        doctorSpeciality = requireView().findViewById<View>(R.id.doctor_speciality) as AppCompatTextView
        doctorYears = requireView().findViewById<View>(R.id.doctor_experience) as AppCompatTextView

        val value = arguments
        if (value != null && value.containsKey("doctorData")) {
            val strDAO = value.getString("doctorData")
            id = value.getString("id").toString()
            doctorDetails = Gson().fromJson(strDAO, Doctor :: class.java)
        }

        doctorName.text = doctorDetails.doctorName
        doctorSpeciality.text = doctorDetails.doctorSpeciality
        doctorYears.text = doctorDetails.doctorExperience.toString() + " years"
        setUpRecyclerView()

    }

    private fun setUpRecyclerView(){
        slotsAdapter = SlotsAdapter(activity, doctorDetails.slots)
        slotsRecyclerView.adapter = slotsAdapter
        slotsRecyclerView.layoutManager = GridLayoutManager(activity, 3)
        slotsAdapter.setOnSlotItemClick(this@DoctorSlotFragment)

    }

    override fun onSlotButtonClicked(position: Int) {
        LocalPersistenceManager.getLocalPersistenceManager().doctorConsultationFees=doctorDetails.doctorConsultationFee
        LocalPersistenceManager.getLocalPersistenceManager().slot = doctorDetails.slots[position]
        LocalPersistenceManager.getLocalPersistenceManager().id = id
        LocalPersistenceManager.getLocalPersistenceManager().phoneNumber = doctorDetails.phoneNumber
        if (CheckNetworkConnection.isNetworkAvailable(requireActivity())) {
            val intent = Intent(requireActivity(), PaymentActivity::class.java)
            try {
                startActivity(intent)
            } catch (e: Exception) {
                GlobalData.showSnackbar(
                    requireActivity(),
                    requireView().findViewById<View>(R.id.content),
                    "Something went wrong!!",
                    GlobalData.ColorType.ERROR
                )
                e.printStackTrace()
            }
        } else {
            GlobalData.showSnackbar(
                requireActivity(),
                requireView().findViewById<View>(R.id.content),
                "No Internet Connection",
                GlobalData.ColorType.ERROR
            )
        }
    }

}