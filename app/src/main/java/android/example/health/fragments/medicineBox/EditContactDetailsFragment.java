package android.example.health.fragments.medicineBox;

import android.content.Intent;
import android.example.health.Constants.GlobalData;
import android.example.health.HomeScreenActivity;
import android.example.health.R;
import android.example.health.daos.MedicineDAO;
import android.example.health.models.MedicineBox;
import android.example.health.utils.UtilsFragmentTransaction;
import android.media.Image;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;

import java.util.List;

public class EditContactDetailsFragment extends Fragment {

    private View rootView;
    private TextInputEditText contactName;
    private TextInputEditText contactEmail;
    private TextInputEditText contactMobileNumber;
    private TextInputEditText contactCity;
    private TextInputEditText contactState;
    private TextInputEditText contactCountry;
    private MedicineBox currentItem;
    private MaterialButton editButton;
    private long mLastClickTime = 0;
    private long mClickWaitTime = 3000;
    private String id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.medicine_box_edit_contact_details, container, false);
    }

   @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
       super.onViewCreated(view, savedInstanceState);
       rootView = view;
       contactName = rootView.findViewById(R.id.contact_details_name);
       contactEmail = rootView.findViewById(R.id.contact_details_email);
       contactMobileNumber = rootView.findViewById(R.id.contact_details_mobile);
       contactCity = rootView.findViewById(R.id.contact_details_city);
       contactState = rootView.findViewById(R.id.contact_details_state);
       contactCountry = rootView.findViewById(R.id.contact_details_country);
       editButton = rootView.findViewById(R.id.contact_details_save_btn);
       Bundle value = getArguments();
       if(value != null && value.containsKey("medicineData")){
           Gson mGson = new Gson();
           String strDAO = value.getString("medicineData");
           currentItem = mGson.fromJson(strDAO, MedicineBox.class);
           id = value.getString("id");
           contactName.setText(currentItem.getContactUserName());
           contactEmail.setText(currentItem.getContactUserEmailID());
           contactMobileNumber.setText(currentItem.getContactUserMobileNumber());
           contactCity.setText(currentItem.getContactUserCity());
           contactState.setText(currentItem.getContactUserState());
           contactCountry.setText(currentItem.getContactUserCountry());

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
                   medicineDAO.getPostC().document(id).update("contactUserName", contactName.getText().toString());
                   medicineDAO.getPostC().document(id).update("contactUserEmailID", contactEmail.getText().toString());
                   medicineDAO.getPostC().document(id).update("contactUserMobileNumber", contactMobileNumber.getText().toString());
                   medicineDAO.getPostC().document(id).update("contactUserCity", contactCity.getText().toString());
                   medicineDAO.getPostC().document(id).update("contactUserState", contactState.getText().toString());
                   medicineDAO.getPostC().document(id).update("contactUserCountry", contactCountry.getText().toString());
                   GlobalData.getApp().dismissProgressDialog();
                   Intent intent = new Intent(getActivity(), HomeScreenActivity.class);
                   startActivity(intent);
               }
           }
       });


   }

    private boolean isAllFieldValidated() {
        boolean isAllFieldValid = true;
        if (contactName.getText().toString().trim().equalsIgnoreCase("")) {
            GlobalData.showSnackbar( getActivity(), rootView, "Enter Contact Name", GlobalData.ColorType.ERROR);
            isAllFieldValid = false;
        }
        else if (contactEmail.getText().toString().trim().equalsIgnoreCase("")) {
            GlobalData.showSnackbar( getActivity(), rootView, "Enter Contact Email", GlobalData.ColorType.ERROR);
            isAllFieldValid = false;
        }
        else if (contactMobileNumber.getText().toString().trim().equalsIgnoreCase("")) {
            GlobalData.showSnackbar( getActivity(), rootView, "Enter Contact Mobile Number", GlobalData.ColorType.ERROR);
            isAllFieldValid = false;
        }
        else if (contactCity.getText().toString().trim().equalsIgnoreCase("")) {
            GlobalData.showSnackbar( getActivity(), rootView, "Enter Contact City", GlobalData.ColorType.ERROR);
            isAllFieldValid = false;
        }
        else if (contactState.getText().toString().trim().equalsIgnoreCase("")) {
            GlobalData.showSnackbar( getActivity(), rootView, "Enter Contact State", GlobalData.ColorType.ERROR);
            isAllFieldValid = false;
        }
        else if (contactCountry.getText().toString().trim().equalsIgnoreCase("")) {
            GlobalData.showSnackbar( getActivity(), rootView, "Enter Contact Country", GlobalData.ColorType.ERROR);
            isAllFieldValid = false;
        }
        return isAllFieldValid;
    }


}
