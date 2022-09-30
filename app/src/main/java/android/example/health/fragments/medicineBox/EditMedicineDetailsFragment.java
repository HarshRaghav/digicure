package android.example.health.fragments.medicineBox;

import android.content.Intent;
import android.example.health.Constants.GlobalData;
import android.example.health.HomeScreenActivity;
import android.example.health.R;
import android.example.health.daos.MedicineDAO;
import android.example.health.models.MedicineBox;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

public class EditMedicineDetailsFragment extends Fragment {

    private View rootView;
    private TextInputEditText medicineName;
    private TextInputEditText medicineCost;
    private TextInputEditText medicineExpiryDate;
    private TextInputEditText medicineManufactureDate;
    private TextInputEditText medicineManufacturer;
    private MedicineBox currentItem;
    private MaterialButton editButton;
    private long mLastClickTime = 0;
    private long mClickWaitTime = 3000;
    private String id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.medicine_box_edit_medicine_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
        medicineName = rootView.findViewById(R.id.medicine_details_name);
        medicineCost = rootView.findViewById(R.id.medicine_details_mrp);
        medicineExpiryDate = rootView.findViewById(R.id.medicine_details_expiry_date);
        medicineManufactureDate = rootView.findViewById(R.id.medicine_details_manufacture_date);
        medicineManufacturer = rootView.findViewById(R.id.medicine_details_manufacturer);
        editButton = rootView.findViewById(R.id.medicine_details_save_btn);
        Bundle value = getArguments();
        if(value != null && value.containsKey("medicineData")){
            Gson mGson = new Gson();
            String strDAO = value.getString("medicineData");
            currentItem = mGson.fromJson(strDAO, MedicineBox.class);
            id = value.getString("id");
            medicineName.setText(currentItem.getMedicineName());
            medicineCost.setText(currentItem.getMedicineMRP());
            medicineExpiryDate.setText(currentItem.getMedicineExpiryDate());
            medicineManufactureDate.setText(currentItem.getMedicineManufactureDate());
            medicineManufacturer.setText(currentItem.getMedicineManufacturer());

        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLastClickTime = SystemClock.elapsedRealtime();
                if(mLastClickTime < mClickWaitTime){
                    return;
                }
                if(isAllFieldValidated()) {
                    GlobalData.getApp().showProgressDialog(getActivity(), "Updating", "Please wait....");
                    MedicineDAO medicineDAO = new MedicineDAO();
                    medicineDAO.getPostC().document(id).update("medicineName", medicineName.getText().toString());
                    medicineDAO.getPostC().document(id).update("medicineMRP", medicineCost.getText().toString());
                    medicineDAO.getPostC().document(id).update("medicineManufacturer", medicineManufacturer.getText().toString());
                    GlobalData.getApp().dismissProgressDialog();
                    Intent intent = new Intent(getActivity(), HomeScreenActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    private boolean isAllFieldValidated() {
        boolean isAllFieldValid = true;
        if (medicineName.getText().toString().trim().equalsIgnoreCase("")) {
            GlobalData.showSnackbar( getActivity(), rootView, "Enter Medicine Name", GlobalData.ColorType.ERROR);
            isAllFieldValid = false;
        }
        else if (medicineCost.getText().toString().trim().equalsIgnoreCase("")) {
            GlobalData.showSnackbar( getActivity(), rootView, "Enter Medicine Cost", GlobalData.ColorType.ERROR);
            isAllFieldValid = false;
        }
        else if (medicineManufacturer.getText().toString().trim().equalsIgnoreCase("")) {
            GlobalData.showSnackbar( getActivity(), rootView, "Enter Manufacturer Name", GlobalData.ColorType.ERROR);
            isAllFieldValid = false;
        }
        return isAllFieldValid;
    }


}
