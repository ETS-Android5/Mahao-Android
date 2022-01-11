package ke.co.tonyoa.mahao.app.sharedprefs;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SharedPrefs {

    public static final String KEY_TOKEN="KEY_TOKEN";
    public static final String KEY_NAMES="KEY_NAMES";
    public static final String KEY_EMAIL="KEY_EMAIL";
    public static final String KEY_USERID="KEY_USERID";
    public static final String KEY_ADMIN="KEY_ADMIN";
    public static final String KEY_PROFILE_PICTURE = "KEY_PROFILE_PICTURE";
    public static final String KEY_PHONE = "KEY_PHONE";

    private final Context mContext;
    private final SharedPreferences mSharedPreferences;

    @Inject
    public SharedPrefs(Application context){
        mContext = context;
        mSharedPreferences= PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public void saveString(String key, String value){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value).apply();
    }

    public String getString(String key, String defaultValue){
        return mSharedPreferences.getString(key, defaultValue);
    }

    public void saveInt(String key, int value){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value).apply();
    }

    public int getInt(String key, int defaultValue){
        return mSharedPreferences.getInt(key, defaultValue);
    }

    public void saveBoolean(String key, boolean value){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultValue){
        return mSharedPreferences.getBoolean(key, defaultValue);
    }

    public void saveNames(String firstName, String lastName){
        saveString(KEY_NAMES, firstName+" "+lastName);
    }

    public String getNames(){
        return getString(KEY_NAMES, null);
    }

    public void saveToken(String token){
        saveString(KEY_TOKEN, token);
    }

    public String getToken(){
        return getString(KEY_TOKEN, null);
    }

    public void saveEmail(String email){
        saveString(KEY_EMAIL, email);
    }

    public String getEmail(){
        return getString(KEY_EMAIL, null);
    }

    public void savePhone(String phone){
        saveString(KEY_PHONE, phone);
    }

    public String getPhone(){
        return getString(KEY_PHONE, null);
    }

    public void saveUserId(String userId){
        saveString(KEY_USERID, userId);
    }

    public String getUserId(){
        return getString(KEY_USERID, null);
    }

    public void saveProfilePicture(String profilePicture){
        saveString(KEY_PROFILE_PICTURE, profilePicture);
    }

    public String getProfilePicture(){
        return getString(KEY_PROFILE_PICTURE, null);
    }

    public void saveAdmin(boolean isAdmin){
        saveBoolean(KEY_ADMIN, isAdmin);
    }

    public boolean isAdmin(){
        return getBoolean(KEY_ADMIN, false);
    }

    public void addToList(String listName, String value){
        Set<String> stringSet = mSharedPreferences.getStringSet(listName, new HashSet<>());
        List<String> values = new ArrayList<>(stringSet);
        values.add(value);
        mSharedPreferences.edit().putStringSet(listName, new HashSet<>(values)).apply();
    }

    public List<String> getList(String listName){
        return new ArrayList<>(mSharedPreferences.getStringSet(listName, new HashSet<>()));
    }

    public void registerOnSharedPreferencesListener(SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

}
