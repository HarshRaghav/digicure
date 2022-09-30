package android.example.health.utils;

import android.example.health.BuildConfig;
import android.example.health.Constants.AppConstants;
import android.example.health.Constants.GlobalData;
import android.provider.Settings;

import com.google.gson.Gson;

public class LocalPersistenceManager {
    private static LocalPersistenceManager localPersistenceManager;

    public static LocalPersistenceManager getLocalPersistenceManager(){
        if(localPersistenceManager == null){
            localPersistenceManager = new LocalPersistenceManager();
        }
        return localPersistenceManager;
    }

    public void setAppSignature() {
        String rawSignature = GlobalData.getMd5("Health"+"#"+ BuildConfig.VERSION_NAME);

        GlobalData.getApp().getSharedPreferences().edit().putString(AppConstants.PREF_KEY_ENCRYPTED_APP_SIGNATURE,rawSignature).commit();
    }
    public String getAppSignature() {
        return GlobalData.getApp().getSharedPreferences().getString(AppConstants.PREF_KEY_ENCRYPTED_APP_SIGNATURE, "");
    }

    public void setStartSleepData(String data) {
        GlobalData.getApp().getSharedPreferences().edit().putString(AppConstants.START_SLEEP_DATA, data).commit();
    }
    public String getStartSleepData() {
        return GlobalData.getApp().getSharedPreferences().getString(AppConstants.START_SLEEP_DATA, "");
    }

    public void setTodaySleepData(Long data) {
        GlobalData.getApp().getSharedPreferences().edit().putLong(AppConstants.TODAY_SLEEP_DATA, data).commit();
    }
    public Long getTodaySleepData() {
        return GlobalData.getApp().getSharedPreferences().getLong(AppConstants.TODAY_SLEEP_DATA, 0);
    }

    public void setSleepData(long data) {
        GlobalData.getApp().getSharedPreferences().edit().putLong(AppConstants.SLEEP_DATA, data).commit();
    }
    public long getSleepData() {
        return GlobalData.getApp().getSharedPreferences().getLong(AppConstants.SLEEP_DATA, 0);
    }

    public void setUserLoginStatus(boolean status){
        GlobalData.getApp().getSharedPreferences().edit().putBoolean(AppConstants.PREF_KEY_USER_LOGIN_STATUS,status).commit();
    }
    public boolean getUserLoginStatus(){
        return GlobalData.getApp().getSharedPreferences().getBoolean(AppConstants.PREF_KEY_USER_LOGIN_STATUS,false);
    }

    public void setUserName(String name){
        GlobalData.getApp().getSharedPreferences().edit().putString(AppConstants.PREF_KEY_USER_NAME,name).commit();
    }
    public String getUserName() {
        return GlobalData.getApp().getSharedPreferences().getString(AppConstants.PREF_KEY_USER_NAME, "");
    }

    public void setUserProfile(String profileLink){
        GlobalData.getApp().getSharedPreferences().edit().putString(AppConstants.PREF_KEY_USER_PROFILE_IMAGE,profileLink).commit();
    }
    public String getUserProfile() {
        return GlobalData.getApp().getSharedPreferences().getString(AppConstants.PREF_KEY_USER_PROFILE_IMAGE, "");
    }

    public void setUserEmail(String email) {
        GlobalData.getApp().getSharedPreferences().edit().putString(AppConstants.PREF_KEY_USER_EMAIL,email).commit();
    }
    public String getUserEmail() {
        return GlobalData.getApp().getSharedPreferences().getString(AppConstants.PREF_KEY_USER_EMAIL, "");
    }

    public void setUserMobile(String mobile){
        GlobalData.getApp().getSharedPreferences().edit().putString(AppConstants.PREF_KEY_USER_MOBILE,mobile).commit();
    }
    public String getUserMobile() {
        return GlobalData.getApp().getSharedPreferences().getString(AppConstants.PREF_KEY_USER_MOBILE, "");
    }

    public void setDoctorConsultationFees(int Fees){
        GlobalData.getApp().getSharedPreferences().edit().putInt(AppConstants.PREF_KEY_DOCTOR_CONSULTATION_FEES,Fees).commit();
    }
    public int getDoctorConsultationFees(){
        return GlobalData.getApp().getSharedPreferences().getInt(AppConstants.PREF_KEY_DOCTOR_CONSULTATION_FEES,0);
    }

    public void setSlot(String slot){
        GlobalData.getApp().getSharedPreferences().edit().putString(AppConstants.PREF_KEY_APPOINTMENT_SLOT, slot).commit();
    }
    public String getSlot() {
        return GlobalData.getApp().getSharedPreferences().getString(AppConstants.PREF_KEY_APPOINTMENT_SLOT, "");
    }

    public void setId(String id){
        GlobalData.getApp().getSharedPreferences().edit().putString(AppConstants.PREF_KEY_APPOINTMENT_ID, id).commit();
    }
    public String getId() {
        return GlobalData.getApp().getSharedPreferences().getString(AppConstants.PREF_KEY_APPOINTMENT_ID, "");
    }

    public void setPhoneNumber(Long number){
        GlobalData.getApp().getSharedPreferences().edit().putLong(AppConstants.PREF_KEY_PHONE_NUMBER, number).commit();
    }
    public Long getPhoneNumber() {
        return GlobalData.getApp().getSharedPreferences().getLong(AppConstants.PREF_KEY_PHONE_NUMBER, 0);
    }

    public void setTransactionID(String TransactionID){
        GlobalData.getApp().getSharedPreferences().edit().putString(AppConstants.PREF_KEY_TRANSACTION_ID,TransactionID).commit();
    }
    public String getTransactionID() {
        return GlobalData.getApp().getSharedPreferences().getString(AppConstants.PREF_KEY_TRANSACTION_ID, "");
    }

    public void setAddressID(String AddressID){
        GlobalData.getApp().getSharedPreferences().edit().putString(AppConstants.PREF_KEY_CHECKOUT_ADDRESS_ID,AddressID).commit();
    }
    public String getAddressID() {
        return GlobalData.getApp().getSharedPreferences().getString(AppConstants.PREF_KEY_CHECKOUT_ADDRESS_ID, "");
    }

    public void setFCMRegistrationID(String registrationID){
        GlobalData.getApp().getSharedPreferences().edit().putString(AppConstants.PREF_KEY_FCM_REGISTRATION_ID, registrationID).commit();
    }
    public String getFCMRegistrationID(){
        return GlobalData.getApp().getSharedPreferences().getString(AppConstants.PREF_KEY_FCM_REGISTRATION_ID,"");
    }

    public void setHeader(String signature) {

        try {
            GlobalData.getApp().getSharedPreferences().edit().putString(AppConstants.PREF_KEY_HEADER, signature).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getHeader() {

        try {
            return GlobalData.getApp().getSharedPreferences().getString(AppConstants.PREF_KEY_HEADER, "");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setLoginStatus(boolean status){
        try{
            GlobalData.getApp().getSharedPreferences().edit().putBoolean(AppConstants.PREF_KEY_LOGIN_STATUS,status).commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public boolean getLoginStatus(){
        try{
            return GlobalData.getApp().getSharedPreferences().getBoolean(AppConstants.PREF_KEY_LOGIN_STATUS,false);
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public void logoutAllUser(){
        LocalPersistenceManager.getLocalPersistenceManager().setLoginStatus(false);
    }
}
