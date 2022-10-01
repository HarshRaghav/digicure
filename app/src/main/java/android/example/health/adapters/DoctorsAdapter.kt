package android.example.health.adapters

import android.example.health.HomeScreenActivity
import android.example.health.R
import android.example.health.models.Doctor
import android.example.health.models.MedicineBox
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.posttweet.Utils
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.button.MaterialButton

class DoctorsAdapter(options: FirestoreRecyclerOptions<Doctor>, val clicked: onAppointmentClicked, val context : HomeScreenActivity):
    FirestoreRecyclerAdapter<Doctor, DoctorViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val viewHolder =  DoctorViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.doctor_recyclerview_item, parent, false))
        viewHolder.bookAppointmentButton.setOnClickListener{
            clicked.onBookAppointmentClicking(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int, model: Doctor) {
        holder.doctorName.text = model.doctorName
        holder.specialityName.text = model.doctorSpeciality
        holder.experienceLevel.text = model.doctorExperience.toString() + " years"
        holder.consultancyFees.text = context.getString(R.string.rupee_symbol) + " " + model.doctorConsultationFee.toString()

    }
}

class DoctorViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val doctorImage : ImageView = itemView.findViewById(R.id.doctor_image)
    val doctorName : TextView = itemView.findViewById(R.id.doctor_name)
    val specialityName : TextView = itemView.findViewById(R.id.speciality_name)
    val experienceLevel : TextView = itemView.findViewById(R.id.experience_level)
    val consultancyFees : TextView = itemView.findViewById(R.id.consultancy_fees)
    val bookAppointmentButton : MaterialButton = itemView.findViewById(R.id.book_appointment)
}
interface onAppointmentClicked{
    fun onBookAppointmentClicking(id:String)
}