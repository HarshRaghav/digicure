package android.example.health.utils;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

public class UtilsFragmentTransaction {
    public static void addFragmentTransaction(int id, FragmentActivity activity, Fragment fragment, String fragment_tag, Bundle arBundle){
        FragmentTransaction ft_one = activity.getSupportFragmentManager()
                .beginTransaction();
        fragment.setArguments(arBundle);

        ft_one.add(id, fragment, fragment_tag);
        ft_one.addToBackStack(fragment_tag);
        ft_one.commit();
    }

    public static void replaceFragmentTransaction(int id, AppCompatActivity activity, Fragment fragment, Bundle arBundle){
        FragmentTransaction ft_one = activity.getSupportFragmentManager().beginTransaction();
        fragment.setArguments(arBundle);
        ft_one.replace(id, fragment);
        ft_one.commit();
    }

    public static void replaceWithTagFragmentTransaction(int id, FragmentActivity activity,
                                                         Fragment fragment, String fragment_tag, Bundle arBundle){
        FragmentTransaction ft_one = activity.getSupportFragmentManager()
                .beginTransaction();
        fragment.setArguments(arBundle);

        ft_one.replace(id, fragment, fragment_tag);
        ft_one.commit();
    }

    public static void replaceFragmentTransaction(int id, AppCompatActivity activity,
                                                  Fragment fragment, String fragment_tag, Bundle arBundle) {
        FragmentTransaction ft_one = activity.getSupportFragmentManager()
                .beginTransaction();
        fragment.setArguments(arBundle);

        ft_one.replace(id, fragment, fragment_tag);
        ft_one.commit();

    }
}
