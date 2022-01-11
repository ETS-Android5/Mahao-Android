package ke.co.tonyoa.mahao.ui.profile;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

import ke.co.tonyoa.mahao.app.MahaoApplication;
import ke.co.tonyoa.mahao.app.repositories.AuthRepository;
import ke.co.tonyoa.mahao.app.repositories.DummyRepository;
import ke.co.tonyoa.mahao.app.sharedprefs.SharedPrefs;

public class ProfileViewModel extends AndroidViewModel {

    @Inject
    SharedPrefs mSharedPrefs;
    @Inject
    AuthRepository mAuthRepository;
    @Inject
    DummyRepository mDummyRepository;
    private MutableLiveData<String> mNameLiveData = new MutableLiveData<>();
    private MutableLiveData<String> mProfilePictureLiveData = new MutableLiveData<>();
    private MutableLiveData<String> mPhoneLiveData = new MutableLiveData<>();
    private MutableLiveData<String> mEmailLiveData = new MutableLiveData<>();

    private SharedPreferences.OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(SharedPrefs.KEY_NAMES)){
                String fullName = mSharedPrefs.getNames();
                mNameLiveData.postValue(fullName);
            }
            else if (key.equals(SharedPrefs.KEY_PROFILE_PICTURE)){
                mProfilePictureLiveData.postValue(mSharedPrefs.getProfilePicture());
            }
            else if (key.equals(SharedPrefs.KEY_EMAIL)){
                mEmailLiveData.postValue(mSharedPrefs.getEmail());
            }
            else if (key.equals(SharedPrefs.KEY_PHONE)){
                mPhoneLiveData.postValue(mSharedPrefs.getPhone());
            }
        }
    };

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
        mNameLiveData.postValue(mSharedPrefs.getNames());
        mProfilePictureLiveData.postValue(mSharedPrefs.getProfilePicture());
        mEmailLiveData.postValue(mSharedPrefs.getEmail());
        mPhoneLiveData.postValue(mSharedPrefs.getPhone());
    }

    public LiveData<String> getName(){
        return mNameLiveData;
    }

    public LiveData<String> getProfilePicture(){
        return mProfilePictureLiveData;
    }

    public LiveData<String> getEmail(){
        return mEmailLiveData;
    }

    public LiveData<String> getPhone(){
        return mPhoneLiveData;
    }

    public void logOut(){
        mAuthRepository.logout();
    }

    public boolean isAdmin(){
        return mSharedPrefs.isAdmin();
    }

    public void createDummyData(){
        mDummyRepository.createDummyData();
    }

    public void createFeedbacks(){
        mDummyRepository.createFeedbacks();
    }

}
