package android.example.health.fragments.medicineBox

import android.example.health.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.example.health.utils.UtilsFragmentTransaction
import androidx.annotation.Nullable

import com.google.android.material.bottomnavigation.BottomNavigationView

class MedicineBoxMainFragment : Fragment() {
    var rootView: View? = null
    var bottomNavigationView: BottomNavigationView? = null

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView = view
        bottomNavigationView =
            rootView!!.findViewById<View>(R.id.bottom_navigation) as BottomNavigationView
        UtilsFragmentTransaction.addFragmentTransaction(
            R.id.course_frame_layout,
            activity, MedicineDonationFragment(),
            MedicineDonationFragment::class.java.getSimpleName(), null
        )
        bottomNavigationView!!.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.MedicineBox -> {
                    UtilsFragmentTransaction.addFragmentTransaction(
                        R.id.course_frame_layout,
                        activity, MedicineDonationFragment(),
                        MedicineDonationFragment::class.java.getSimpleName(), null
                    )
                    return@OnNavigationItemSelectedListener true
                }
                R.id.MyMedicine -> {
                    UtilsFragmentTransaction.addFragmentTransaction(
                        R.id.course_frame_layout,
                        activity, MyMedicinesFragment(),
                        MyMedicinesFragment::class.java.getSimpleName(), null
                    )
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(android.example.health.R.layout.fragment_medicine_box, container, false)
    }
}