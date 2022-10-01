package android.example.health.fragments.appointmentBooking

import android.content.Intent
import android.example.health.Constants.GlobalData
import android.example.health.HomeScreenActivity
import android.example.health.R
import android.example.health.adapters.DoctorsAdapter
import android.example.health.adapters.MedicineAdapter
import android.example.health.adapters.onAppointmentClicked
import android.example.health.adapters.onClicked
import android.example.health.daos.DoctorDAO
import android.example.health.daos.MedicineDAO
import android.example.health.fragments.medicineBox.EditMedicineDetailsFragment
import android.example.health.medicineBox.MedicineBoxMedicineInformationActivity
import android.example.health.medicineBox.MedicineDetailsActivity
import android.example.health.models.Doctor
import android.example.health.models.MedicineBox
import android.example.health.utils.UtilsFragmentTransaction
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DoctorsFragment : Fragment(), onAppointmentClicked {

    private lateinit var doctorDAO: DoctorDAO
    private lateinit var doctorsAdapter: DoctorsAdapter
    private var mLastClickTime : Long = 0
    private var mClickWaitTime : Long = 3000
    private lateinit var doctorsRecyclerView : RecyclerView;
    private lateinit var rootView : View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //rootView = requireView()
        return inflater.inflate(R.layout.doctors_recyclerview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doctorsRecyclerView = requireView().findViewById<View>(R.id.recyclerViewDoctors) as RecyclerView
        doctorDAO = DoctorDAO()
        GlobalData.app.showProgressDialog(activity, "Loading", "Please wait....")
        setUpRecyclerView()
        GlobalData.app.dismissProgressDialog()

    }

    private fun setUpRecyclerView(){
        val doctorsCollection= doctorDAO.doctors
        val query=doctorsCollection.orderBy("doctorExperience", Query.Direction.DESCENDING)
        val recyclerViewOptions=
            FirestoreRecyclerOptions.Builder<Doctor>().setQuery(query, Doctor::class.java).build()
        doctorsAdapter = DoctorsAdapter(recyclerViewOptions,this, activity as HomeScreenActivity)
        doctorsRecyclerView.adapter= doctorsAdapter
        doctorsRecyclerView.layoutManager= LinearLayoutManager(activity)

    }

    override fun onStart() {
        super.onStart()
        doctorsAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        doctorsAdapter.stopListening()
    }

    override fun onBookAppointmentClicking(id: String) {
        GlobalScope.launch{
            val doctor = doctorDAO.getDoctorByID(id).await().toObject(Doctor::class.java)!!
            val value = Bundle()
            val gson = Gson()
            value.putString("doctorData", gson.toJson(doctor))
            value.putString("id", id)
            UtilsFragmentTransaction.addFragmentTransaction(
                R.id.fragment_container,
                activity, DoctorSlotFragment(),
                DoctorSlotFragment::class.java.simpleName, value
            )

        }
    }
}