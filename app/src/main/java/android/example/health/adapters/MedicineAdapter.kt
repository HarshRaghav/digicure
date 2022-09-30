package android.example.health.adapters

import android.example.health.R
import android.example.health.models.MedicineBox
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.posttweet.Utils
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MedicineAdapter(options: FirestoreRecyclerOptions<MedicineBox>, val clicked: onClicked):
    FirestoreRecyclerAdapter<MedicineBox, MedicineViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val viewHolder =  MedicineViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.medicine_post_recyclerview_item, parent, false))
        viewHolder.detailsButton.setOnClickListener{
            clicked.onDetailsClicking(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        viewHolder.contactInfoButton.setOnClickListener{
            clicked.onContactInfoClicking(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int, model: MedicineBox) {
        Glide.with(holder.medicineImage.context).load(model.createdBy.imageUrl).circleCrop().into(holder.medicineImage)
        holder.medicineName.text = model.medicineName
        holder.createdAt.text = Utils.getTime(model.createdAt)
        holder.medicineCost.text = model.medicineMRP
        holder.userText.text = model.createdBy.name

    }
}

class MedicineViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val medicineImage : ImageView = itemView.findViewById(R.id.medicineImage)
    val medicineName : TextView = itemView.findViewById(R.id.medicineName)
    val createdAt : TextView = itemView.findViewById(R.id.createdAt)
    val medicineCost : TextView = itemView.findViewById(R.id.medicineCost)
    val userText : TextView = itemView.findViewById(R.id.userName)
    val detailsButton : MaterialButton = itemView.findViewById(R.id.detailsButton)
    val contactInfoButton: MaterialButton = itemView.findViewById(R.id.contactInfoButton)
}
interface onClicked{
    fun onDetailsClicking(id:String)
    fun onContactInfoClicking(id:String)
}