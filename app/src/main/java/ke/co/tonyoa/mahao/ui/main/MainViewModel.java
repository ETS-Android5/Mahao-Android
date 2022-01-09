package ke.co.tonyoa.mahao.ui.main;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Objects;

import javax.inject.Inject;

import ke.co.tonyoa.mahao.app.MahaoApplication;
import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.responses.User;
import ke.co.tonyoa.mahao.app.repositories.AuthRepository;
import ke.co.tonyoa.mahao.app.sharedprefs.SharedPrefs;

public class MainViewModel extends AndroidViewModel {

    @Inject
    AuthRepository mAuthRepository;
    @Inject
    SharedPrefs mSharedPrefs;
    private MutableLiveData<Integer> mSelectedPosition=new MutableLiveData<>();
    private MutableLiveData<String> mToken = new MutableLiveData<>();
    private SharedPreferences.OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (Objects.equals(key, SharedPrefs.KEY_TOKEN)){
                mToken.setValue(mSharedPrefs.getToken());
            }
        }
    };

    public MainViewModel(@NonNull Application application) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
        mSelectedPosition.setValue(0);
        mToken.postValue(mSharedPrefs.getToken());
        mSharedPrefs.registerOnSharedPreferencesListener(mOnSharedPreferenceChangeListener);
    }


    public void setSelectedPosition(int position){
        mSelectedPosition.postValue(position);
    }

    public LiveData<Integer> getSelectedPosition(){
        return mSelectedPosition;
    }

    public LiveData<APIResponse<User>> getUserProfile(){
        return mAuthRepository.getUser();
    }

    public LiveData<String> getTokenLive(){
        return mToken;
    }
}
