package android.example.health.fragments.medicineBox

import android.example.health.Constants.GlobalData
import android.example.health.R
import android.example.health.adapters.MyMedicineAdapter
import android.example.health.daos.MedicineDAO
import android.example.health.daos.UserDao
import android.example.health.models.MedicineBox
import android.example.health.models.User
import android.example.health.utils.UtilsFragmentTransaction
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.posttweet.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MyMedicinesFragment : Fragment(),
    MyMedicineAdapter.OnMedicineItemClick {
    private lateinit var rootView: View
    private lateinit var medicineDAO: MedicineDAO
    private lateinit var medicineAdapter: MyMedicineAdapter
    private lateinit var medicineRecyclerView : RecyclerView;
    private lateinit var document : MutableList<DocumentSnapshot>
    private var medicines : ArrayList<MedicineBox> = ArrayList()
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(android.example.health.R.layout.my_medicine_recycler_view, container, false)
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        medicineRecyclerView = requireView().findViewById<View>(R.id.recyclerViewMedicines) as RecyclerView
        progressBar = requireView().findViewById<View>(R.id.progressBar) as ProgressBar
        medicineDAO = MedicineDAO()
        setUpRecyclerView()

    }

    private fun setUpRecyclerView(){
        GlobalScope.launch {
            progressBar.visibility = View.VISIBLE
            val currentId = Firebase.auth.currentUser!!.uid
            val userDao= UserDao()
            val user=userDao.getUserById(currentId).await().toObject(User::class.java)!!
            val post : QuerySnapshot? = medicineDAO.postC.get().await()
            val documents = post?.documents
            val posts : MutableList<MedicineBox> = medicineDAO.postC.get().await().toObjects(MedicineBox::class.java)
            document = ArrayList()
            for(i in 0 until posts.size){
                if(posts[i].createdBy.id == user.id){
                    medicines.add(posts[i])
                    if (documents != null) {
                        document.add(documents[i])
                    }
                }
            }
            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
                medicineAdapter = MyMedicineAdapter(activity, medicines)
                medicineRecyclerView.adapter = medicineAdapter
                medicineRecyclerView.layoutManager = LinearLayoutManager(activity)
                medicineAdapter.setOnMedicineItemClick(this@MyMedicinesFragment)
            }
        }

    }

    override fun onDeleteItemClicked(position: Int) {
        GlobalData.getApp().showAlertDialogWithOkCancel(activity,
            "Are you sure you want to delete ?",
            "",
            "OK",
            "Cancel",
            R.color.colorPrimary,
            R.color.lightGrey,
            SweetAlertDialog.WARNING_TYPE,
            {
                medicineAdapter.delete(position)
                medicineDAO.postC.document(document[position].id).delete()
                GlobalData.getApp().dismissProgressDialog()
            },
            { GlobalData.getApp().dismissProgressDialog() }
        )


    }

    override fun onContactButtonClicked(position: Int) {
        val value = Bundle()
        val gson = Gson()
        value.putString("medicineData", gson.toJson(medicines[position]))
        value.putString("id", document[position].id)
        UtilsFragmentTransaction.addFragmentTransaction(
            R.id.fragment_container,
            activity, EditContactDetailsFragment(),
            EditContactDetailsFragment::class.java.simpleName, value
        )

    }

    override fun onMedicineButtonClicked(position: Int) {
        val value = Bundle()
        val gson = Gson()
        value.putString("medicineData", gson.toJson(medicines[position]))
        value.putString("id", document[position].id)
        UtilsFragmentTransaction.addFragmentTransaction(
            R.id.fragment_container,
            activity, EditMedicineDetailsFragment(),
            EditMedicineDetailsFragment::class.java.simpleName, value
        )

    }

}