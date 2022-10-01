package android.example.health.adapters;

import android.example.health.fragments.healthTracker.HydrationTrackerFragment;
import android.example.health.fragments.healthTracker.SleepTrackerFragment;
import android.example.health.fragments.healthTracker.StepTrackerFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerHealthAdapter  extends FragmentStateAdapter {
    public ViewPagerHealthAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        if(position == 0) {
            StepTrackerFragment stepTrackerFragment = new StepTrackerFragment();

            return stepTrackerFragment;
        }
        else{
            SleepTrackerFragment sleepTrackerFragment = new SleepTrackerFragment();
            return sleepTrackerFragment;

        }
        /*else {
            HydrationTrackerFragment hydrationTrackerFragment = new HydrationTrackerFragment();
            return hydrationTrackerFragment;

        }*/


    }


    @Override
    public int getItemCount() {
        return 2;
    }
}
