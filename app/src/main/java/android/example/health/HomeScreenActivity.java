package android.example.health;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.example.health.Constants.GlobalData;
import android.example.health.adapters.NavigationDrawerAdapter;
import android.example.health.daos.navDrawerItemDAO.NavDrawerItemDAO;
import android.example.health.fragments.BlankFragment;
import android.example.health.fragments.appointmentBooking.DoctorsFragment;
import android.example.health.fragments.covid19.Covid19FAQSFragment;
import android.example.health.fragments.healthTracker.HealthFragment;
import android.example.health.fragments.illnessPrediction.IllnessPredictionModelFragment;
import android.example.health.fragments.medicineBox.MedicineBoxMainFragment;
import android.example.health.networkUtils.CheckNetworkConnection;
import android.example.health.utils.LocalPersistenceManager;
import android.example.health.utils.UtilsFragmentTransaction;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HomeScreenActivity extends AppCompatActivity implements NavigationDrawerAdapter.OnDrawerItemClick {
    private static final int MY_PERMISSIONS_REQUEST_SMS = 1;
    private RecyclerView nav_drawer_recycler_view;
    private NavigationDrawerAdapter mNavigationDrawerAdapter;
    private List<NavDrawerItemDAO> navigationDrawerData;
    private DrawerLayout drawerLayout;
    private TextView text_view_app_version;
    private AppCompatTextView title;
    private AppCompatTextView txt_view_user_name;
    private AppCompatImageView img_view_user_image;
    private AppCompatTextView txt_view_user_id;
    private String TAG = HomeScreenActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            String[] p = {"Manifest.permission.SEND_SMS"};
            ActivityCompat.requestPermissions(this,
                    p,
                    MY_PERMISSIONS_REQUEST_SMS);
        }
        title = (AppCompatTextView) findViewById(R.id.title);
        txt_view_user_name=(AppCompatTextView) findViewById(R.id.txt_view_user_name);
        img_view_user_image = (AppCompatImageView)findViewById(R.id.img_view_user_image);
        txt_view_user_id = (AppCompatTextView) findViewById(R.id.txt_view_user_id);
        text_view_app_version = (TextView) findViewById(R.id.text_view_app_version);
        nav_drawer_recycler_view = (RecyclerView) findViewById(R.id.nav_drawer_user_app_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        nav_drawer_recycler_view.setLayoutManager(mLayoutManager);
        nav_drawer_recycler_view.setItemAnimator(new DefaultItemAnimator());
        addNavigationDrawerData();

        if(mNavigationDrawerAdapter == null){
            mNavigationDrawerAdapter = new NavigationDrawerAdapter(HomeScreenActivity.this,navigationDrawerData);
            mNavigationDrawerAdapter.setOnDrawerItemClick(this);
            nav_drawer_recycler_view.setAdapter(mNavigationDrawerAdapter);
        }else{
            mNavigationDrawerAdapter.notifyDataSetChanged();
        }
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout, toolbar, R.string.drawer_open,R.string.drawer_close){};
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if(getIntent()!= null && getIntent().getExtras() != null && getIntent().getExtras().getBoolean("appoint")){
            UtilsFragmentTransaction.replaceFragmentTransaction(R.id.fragment_container, this, new DoctorsFragment(),null);
        }
        else {
            UtilsFragmentTransaction.replaceFragmentTransaction(R.id.fragment_container, this, new MedicineBoxMainFragment(), null);
        }
        getSupportActionBar().setTitle("");
        title.setText(R.string.app_name);
        txt_view_user_name.setText(LocalPersistenceManager.getLocalPersistenceManager().getUserName());
        Glide.with(img_view_user_image).load(LocalPersistenceManager.getLocalPersistenceManager().getUserProfile()).placeholder(R.drawable.user_icon).into(img_view_user_image);

        //txt_view_user_id.setText("( "+LocalPersistenceManager.getLocalPersistenceManager().getUserDetails().getUserID()+ " )");
    }


    @Override
    public void onDrawerItemClicked(int position){
        String title = getString(R.string.app_name);
        boolean isTitleChangedRequired = false;
        switch(position){
            case 0:
                UtilsFragmentTransaction.replaceFragmentTransaction(R.id.fragment_container, this, new DoctorsFragment(),null);
                title="";
                isTitleChangedRequired = true;
                this.title.setText(R.string.appointment_booking);
                break;

            case 1:
                title="";
                isTitleChangedRequired = true;
                this.title.setText(R.string.medicine_box);
                UtilsFragmentTransaction.replaceFragmentTransaction(R.id.fragment_container, this, new MedicineBoxMainFragment(),null);
                break;

            case 2:
                title="";
                isTitleChangedRequired = true;
                this.title.setText(R.string.illness_prediction);
                UtilsFragmentTransaction.replaceFragmentTransaction(R.id.fragment_container, this, new IllnessPredictionModelFragment(),null);
                break;

            case 3:
                title="";
                isTitleChangedRequired = true;
                this.title.setText(R.string.covid_faqs);
                UtilsFragmentTransaction.replaceFragmentTransaction(R.id.fragment_container, this, new Covid19FAQSFragment(),null);
                break;

            case 4:
                title="";
                isTitleChangedRequired = true;
                this.title.setText(R.string.health_tracker);
                UtilsFragmentTransaction.replaceFragmentTransaction(R.id.fragment_container, this, new HealthFragment(),null);
                break;

            case 5:
                UtilsFragmentTransaction.replaceFragmentTransaction(R.id.fragment_container, this, new BlankFragment(),null);
                title="";
                isTitleChangedRequired = true;
                //this.title.setText(R.string.nav_item_about_us);
                break;

            case 6:
                UtilsFragmentTransaction.replaceFragmentTransaction(R.id.fragment_container, this, new BlankFragment(),null);
                title="";
                isTitleChangedRequired = true;
                //this.title.setText(R.string.nav_item_privacy_policy);
                break;

            case 7:
                isTitleChangedRequired = false;
                logoutDialog("Alert", "Are you sure you want to logout?");
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void addNavigationDrawerData(){
        if(navigationDrawerData==null){
            navigationDrawerData = new ArrayList<>();
        }
        navigationDrawerData.removeAll(navigationDrawerData);
        int image[] = {R.drawable.appointment_booking, R.drawable.medicine_box , R.drawable.illness_prediction , R.drawable.covid_faqs , R.drawable.instructor_icon , R.drawable.faq_icon , R.drawable.aboutus_icon, R.drawable.logout_icon};
        String title[] = getResources().getStringArray(R.array.nav_drawer_labels);
        for(int i = 0; i < image.length;i++){
            NavDrawerItemDAO navDrawerItemDAO = new NavDrawerItemDAO();
            navDrawerItemDAO.setImage(image[i]);
            navDrawerItemDAO.setTitle(title[i]);
            navigationDrawerData.add(navDrawerItemDAO);
        }
    }

    @Override
    public void onBackPressed(){
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            GlobalData.getApp().dismissProgressDialog();
            exitDialog("Alert", "Are you sure you want to exit?");
        }
    }

    private void logoutDialog(String title, String message) {
        if (!isFinishing()) {

            GlobalData.getApp().showAlertDialogWithOkCancel(HomeScreenActivity.this, title, message, "OK", "Cancel", R.color.colorPrimary, R.color.lightGrey, SweetAlertDialog.NORMAL_TYPE, new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    GlobalData.getApp().dismissProgressDialog();
                    if (CheckNetworkConnection.isNetworkAvailable(HomeScreenActivity.this)) {
                        LocalPersistenceManager.getLocalPersistenceManager().logoutAllUser();
                        LocalPersistenceManager.getLocalPersistenceManager().setUserLoginStatus(false);
                        Intent intent = new Intent(HomeScreenActivity.this, WelcomeScreen.class);
                        try{
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }catch (Exception e) {
                            GlobalData.showSnackbar(HomeScreenActivity.this, findViewById(R.id.content), "Something went wrong!!", GlobalData.ColorType.ERROR);
                            e.printStackTrace();
                        }
                    } else {
                        GlobalData.showSnackbar(HomeScreenActivity.this, findViewById(android.R.id.content), getResources().getString(R.string.no_internet_connection), GlobalData.ColorType.ERROR);
                    }
                }
            }, new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    GlobalData.getApp().dismissProgressDialog();
                }
            });
        }
    }

    private void exitDialog(String title, String message){
        if(!isFinishing()){
            GlobalData.getApp().showAlertDialogWithOkCancel(HomeScreenActivity.this, title, message, "OK", "Cancel", R.color.colorPrimary, R.color.lightGrey, SweetAlertDialog.NORMAL_TYPE, new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    GlobalData.getApp().dismissProgressDialog();
                    HomeScreenActivity.this.finish();
                    System.exit(0);
                }
            }, new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    GlobalData.getApp().dismissProgressDialog();
                }
            });
        }
    }
}