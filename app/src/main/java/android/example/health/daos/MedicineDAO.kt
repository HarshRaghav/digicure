package android.example.health.daos

import android.content.Context
import android.example.health.models.MedicineBox
import android.example.health.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class MedicineDAO {

    val db= FirebaseFirestore.getInstance()
    val postC=db.collection("medicines")
    val auth= Firebase.auth

    fun addMedicine(medicineName:String, medicineMRP:String, medicineExpiryDate:String, medicineManufactureDate:String, medicineManufacturer:String, contactName:String, contactMobile:String, contactEmail:String, contactCity:String, contactState:String, contactCountry:String){
        GlobalScope.launch {
            val currentId =auth.currentUser!!.uid
            val userDao=UserDao()
            val user=userDao.getUserById(currentId).await().toObject(User::class.java)!!
            val currentTime=System.currentTimeMillis()
            val post= MedicineBox(medicineName, medicineManufacturer, medicineMRP, medicineManufactureDate, medicineExpiryDate, contactName, contactEmail, contactMobile, contactCity, contactState, contactCountry ,currentTime,user)
            postC.document().set(post)
        }

    }

    fun deleteExpiredMedicinesMedicine(){
        GlobalScope.launch {
            val post : QuerySnapshot? = postC.get().await()
            val documents = post?.documents
            val posts : MutableList<MedicineBox> = postC.get().await().toObjects(MedicineBox::class.java)
            for(i in 0 until posts.size){
                val expiryDate = getMilliFromDate(posts[i].medicineExpiryDate)
                val maxDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                val mMaxYear = maxDate[Calendar.YEAR]
                val mMaxMonth = maxDate[Calendar.MONTH]
                val mMaxDay = maxDate[Calendar.DAY_OF_MONTH]
                val minDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                minDate.set(Calendar.DAY_OF_MONTH, mMaxDay)
                minDate.set(Calendar.MONTH, mMaxMonth)
                minDate.set(Calendar.YEAR, mMaxYear)
                if(expiryDate <= minDate.timeInMillis){
                    if (documents != null) {
                        postC.document(documents[i].id).delete()
                    }
                }
            }

        }
    }

    fun getMedicineById(postId: String): Task<DocumentSnapshot> {
        return postC.document(postId).get()
    }

    fun getMilliFromDate(dateFormat: String?): Long {
        var date = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        try {
            date = formatter.parse(dateFormat)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date.time
    }

}