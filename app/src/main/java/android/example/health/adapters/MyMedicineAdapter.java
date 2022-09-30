package android.example.health.adapters;

import android.content.Context;
import android.example.health.R;
import android.example.health.models.MedicineBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.posttweet.Utils;
import com.google.android.material.button.MaterialButton;

import java.util.Collections;
import java.util.List;

public class MyMedicineAdapter extends RecyclerView.Adapter<MyMedicineAdapter.MyMedicineViewHolder> {

    List<MedicineBox> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    @NonNull
    @Override
    public MyMedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.my_medicine_post_recyclerview_item, parent, false);
        MyMedicineViewHolder holder = new MyMedicineViewHolder(view);
        return holder;
    }

    public MyMedicineAdapter(Context context, List<MedicineBox> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;

    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);

    }

    @Override
    public void onBindViewHolder(@NonNull MyMedicineViewHolder holder, int position) {
        MedicineBox current = data.get(position);
        holder.medicineName.setText(current.getMedicineName() == null ? "NA" : current.getMedicineName());
        holder.createdAt.setText(Utils.Companion.getTime(current.getCreatedAt()));
        holder.medicineCost.setText(current.getMedicineMRP() == null ? "NA" : current.getMedicineMRP());
        Glide.with(holder.medicineImage.getContext()).load(current.getCreatedBy().getImageUrl()).circleCrop().into(holder.medicineImage);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMedicineItemClick.onDeleteItemClicked(holder.getAdapterPosition());
            }
        });
        holder.contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMedicineItemClick.onContactButtonClicked(holder.getAdapterPosition());
            }
        });
        holder.medicineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMedicineItemClick.onMedicineButtonClicked(holder.getAdapterPosition());
            }
        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyMedicineViewHolder extends RecyclerView.ViewHolder {
        TextView medicineName;
        TextView createdAt;
        TextView medicineCost;
        ImageView medicineImage;
        ImageView deleteButton;
        MaterialButton contactButton;
        MaterialButton medicineButton;

        public MyMedicineViewHolder(View itemView) {
            super(itemView);
            medicineImage = itemView.findViewById(R.id.medicineImage);
            medicineName = itemView.findViewById(R.id.medicineName);
            createdAt = itemView.findViewById(R.id.createdAt);
            medicineCost = itemView.findViewById(R.id.medicineCost);
            deleteButton = itemView.findViewById(R.id.delete_button);
            contactButton = itemView.findViewById(R.id.contactInfoButton);
            medicineButton = itemView.findViewById(R.id.detailsButton);
        }

    }

    public interface OnMedicineItemClick {
        void onDeleteItemClicked(int position);
        void onContactButtonClicked(int position);
        void onMedicineButtonClicked(int position);
    }

    private OnMedicineItemClick onMedicineItemClick;

    public void setOnMedicineItemClick(OnMedicineItemClick onMedicineItemClick) {
        this.onMedicineItemClick = onMedicineItemClick;
    }

}
