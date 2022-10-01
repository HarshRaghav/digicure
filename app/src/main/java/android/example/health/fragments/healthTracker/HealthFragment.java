package android.example.health.fragments.healthTracker;

import android.example.health.R;
import android.example.health.adapters.ViewPagerHealthAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HealthFragment extends Fragment {

    private ViewPagerHealthAdapter viewPagerHealthAdapter;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private View rootView;
    private TextView title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.health_tracker_health_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView=view;
        title = getActivity().findViewById(R.id.title);
        title.setText(R.string.health_tracker);
        /*Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setNavigationIcon(R.drawable.menu_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        });*/
        viewPagerHealthAdapter = new ViewPagerHealthAdapter(getActivity());
        viewPager2 = (ViewPager2) view.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        String[] tabLabels = {"STEPS", "SLEEP"};
        viewPager2.setAdapter(viewPagerHealthAdapter);
        new TabLayoutMediator(tabLayout,viewPager2,(tab, position) -> tab.setText(tabLabels[position])).attach();
    }

}
