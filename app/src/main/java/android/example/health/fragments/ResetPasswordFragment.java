package android.example.health.fragments;

import android.example.health.Constants.GlobalData;
import android.example.health.R;
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

public class ResetPasswordFragment extends Fragment {

    private TextInputEditText newPassword;
    private TextInputEditText confirmPassword;
    private MaterialButton resetButton;
    private View rootView;
    private long mLastClickTime = 0;
    private long mClickWaitTime = 3000;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.reset_password_fragment, container, false);
    }

    /*@Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
        newPassword = rootView.findViewById(R.id.reset_password);
        confirmPassword = rootView.findViewById(R.id.confirm_password);
        resetButton = rootView.findViewById(R.id.btn_reset_password);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLastClickTime < mClickWaitTime){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if(isAllFieldValidated()){

                }

            }
        });
    }

    private boolean isAllFieldValidated(){
        if(newPassword.getText().toString().equalsIgnoreCase("")){
            GlobalData.showSnackbar(getActivity(), rootView, "Please enter new password.", GlobalData.ColorType.ERROR);
            return false;
        }
        else if(confirmPassword.getText().toString().equalsIgnoreCase("")){
            GlobalData.showSnackbar(getActivity(), rootView, "Please enter new password.", GlobalData.ColorType.ERROR);
            return false;
        }
    }*/
}
