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
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.posttweet.Utils;
import com.google.android.material.button.MaterialButton;

import java.util.Collections;
import java.util.List;

public class SlotsAdapter extends RecyclerView.Adapter<SlotsAdapter.SlotsViewHolder> {

    List<String> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    @NonNull
    @Override
    public SlotsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.appointment_slot_item, parent, false);
        SlotsViewHolder holder = new SlotsViewHolder(view);
        return holder;
    }

    public SlotsAdapter(Context context, List<String> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;

    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);

    }

    @Override
    public void onBindViewHolder(@NonNull SlotsViewHolder holder, int position) {
        String current = data.get(position);
        holder.slotTime.setText(current == null ? "NA" : current);
        holder.slotTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSlotItemClick.onSlotButtonClicked(holder.getAdapterPosition());
            }
        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class SlotsViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView slotTime;

        public SlotsViewHolder(View itemView) {
            super(itemView);
            slotTime = itemView.findViewById(R.id.slot_text_view);

        }

    }

    public interface OnSlotItemClick {
        void onSlotButtonClicked(int position);
    }

    private OnSlotItemClick onSlotItemClick;

    public void setOnSlotItemClick(OnSlotItemClick onSlotItemClick) {
        this.onSlotItemClick = onSlotItemClick;
    }

}