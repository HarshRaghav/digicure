package android.example.health.fragments.medicineBox

import android.content.Intent
import android.example.health.Constants.GlobalData
import android.example.health.R
import android.example.health.adapters.MedicineAdapter
import android.example.health.adapters.onClicked
import android.example.health.daos.MedicineDAO
import android.example.health.medicineBox.MedicineBoxContactInformationActivity
import android.example.health.medicineBox.MedicineBoxMedicineInformationActivity
import android.example.health.medicineBox.MedicineDetailsActivity
import android.example.health.models.MedicineBox
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.gson.Gson




class MedicineDonationFragment : Fragment(), onClicked {

    private lateinit var medicineDAO: MedicineDAO
    private lateinit var medicineAdapter: MedicineAdapter
    private var mLastClickTime : Long = 0
    private var mClickWaitTime : Long = 3000
    private lateinit var createPost : FloatingActionButton;
    private lateinit var medicineRecyclerView : RecyclerView;
    private lateinit var rootView : View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //rootView = requireView()
        return inflater.inflate(R.layout.medicine_post_recycler_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createPost = requireView().findViewById<View>(R.id.addMedicine) as FloatingActionButton
        medicineRecyclerView = requireView().findViewById<View>(R.id.recyclerViewMedicines) as RecyclerView

        createPost.setOnClickListener(View.OnClickListener {
            if(SystemClock.elapsedRealtime() - mLastClickTime<mClickWaitTime){
                return@OnClickListener;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            val intent= Intent(activity, MedicineDetailsActivity::class.java)
            startActivity(intent)
        })
        medicineDAO = MedicineDAO()
        GlobalData.app.showProgressDialog(activity, "Loading", "Please wait....")
        medicineDAO.deleteExpiredMedicinesMedicine()
        setUpRecyclerView()
        GlobalData.app.dismissProgressDialog()

    }

    private fun setUpRecyclerView(){
        val medicineCollection= medicineDAO.postC
        val query=medicineCollection.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions=
            FirestoreRecyclerOptions.Builder<MedicineBox>().setQuery(query, MedicineBox::class.java).build()
        medicineAdapter = MedicineAdapter(recyclerViewOptions,this)
        medicineRecyclerView.adapter= medicineAdapter
        medicineRecyclerView.layoutManager= LinearLayoutManager(activity)

    }

    override fun onStart() {
        super.onStart()
        medicineAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        medicineAdapter.stopListening()
    }

    override fun onDetailsClicking(id: String) {
        GlobalScope.launch{
            val medicine = medicineDAO.getMedicineById(id).await().toObject(MedicineBox::class.java)!!
            val intent = Intent(activity, MedicineBoxMedicineInformationActivity ::class.java)
            intent.putExtra("medicineData", Gson().toJson(medicine))
            startActivity(intent)

        }

    }

    override fun onContactInfoClicking(id: String) {

        GlobalScope.launch{
            val medicine = medicineDAO.getMedicineById(id).await().toObject(MedicineBox::class.java)!!
            val intent = Intent(activity, MedicineBoxContactInformationActivity ::class.java)
            intent.putExtra("medicineData", Gson().toJson(medicine))
            startActivity(intent)

        }

    }

}