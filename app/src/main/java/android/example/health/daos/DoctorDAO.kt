package android.example.health.daos

import android.example.health.models.Doctor
import android.example.health.models.MedicineBox
import android.example.health.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DoctorDAO {

    val db= FirebaseFirestore.getInstance()
    val doctors=db.collection("doctors")
    val auth= Firebase.auth

    fun bookAppointment(){

    }

    fun getDoctorByID(doctorID: String): Task<DocumentSnapshot> {
        return doctors.document(doctorID).get()
    }

    fun updateSlots(postId : String, slot : String){
        GlobalScope.launch{
            val post=getDoctorByID(postId).await().toObject(Doctor::class.java)!!
            val isLike=post.slots.contains(slot)
            if(isLike){
                post.slots.remove(slot)
            }
            doctors.document(postId).set(post)
        }
    }

}