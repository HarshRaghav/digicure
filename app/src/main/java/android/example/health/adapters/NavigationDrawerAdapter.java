package android.example.health.adapters;

import android.content.Context;
import android.example.health.R;
import android.example.health.daos.navDrawerItemDAO.NavDrawerItemDAO;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.NavigationDrawerViewHolder> {

    List<NavDrawerItemDAO> navigationDrawerdata = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    public NavigationDrawerAdapter(Context context, List<NavDrawerItemDAO> navigationDrawerdata) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.navigationDrawerdata = navigationDrawerdata;
    }

    public void delete(int position) {
        navigationDrawerdata.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public NavigationDrawerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        NavigationDrawerViewHolder holder = new NavigationDrawerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull  NavigationDrawerAdapter.NavigationDrawerViewHolder holder, int position) {
        NavDrawerItemDAO current = navigationDrawerdata.get(position);
        if(current!=null){
            holder.title.setText(current.getTitle() == null ? "NA" : current.getTitle());
            holder.imageicon.setImageResource(current.getImage());
        }
    }

    @Override
    public int getItemCount() {
        return navigationDrawerdata.size();
    }

    class NavigationDrawerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;

        ImageView imageicon;

        public NavigationDrawerViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.text_view_nav_drawer_title);
            imageicon = (ImageView) itemView.findViewById(R.id.imageView_nav_drawer_icon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onDrawerItemClick != null) {
                onDrawerItemClick.onDrawerItemClicked(getAdapterPosition());
            }
        }
    }

    public interface OnDrawerItemClick {
        void onDrawerItemClicked(int position);
    }

    private OnDrawerItemClick onDrawerItemClick;

    public void setOnDrawerItemClick(OnDrawerItemClick onDrawerItemClick) {
        this.onDrawerItemClick = onDrawerItemClick;
    }
}