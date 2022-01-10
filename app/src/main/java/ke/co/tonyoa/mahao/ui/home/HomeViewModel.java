package ke.co.tonyoa.mahao.ui.home;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import javax.inject.Inject;

import ke.co.tonyoa.mahao.app.MahaoApplication;
import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.responses.FavoriteResponse;
import ke.co.tonyoa.mahao.app.api.responses.Property;
import ke.co.tonyoa.mahao.app.enums.FeedbackType;
import ke.co.tonyoa.mahao.app.enums.SortBy;
import ke.co.tonyoa.mahao.app.repositories.PropertiesRepository;
import ke.co.tonyoa.mahao.app.sharedprefs.SharedPrefs;

public class HomeViewModel extends AndroidViewModel {

    public static final LatLng DEFAULT_COORDINATES = new LatLng(1.0912, 37.0117);
    public static final int DEFAULT_LIMIT = 14;

    @Inject
    PropertiesRepository mPropertiesRepository;
    @Inject
    SharedPrefs mSharedPrefs;
    private MutableLiveData<String> mNameLiveData = new MutableLiveData<>();
    private MutableLiveData<String> mProfilePictureLiveData = new MutableLiveData<>();

    private SharedPreferences.OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(SharedPrefs.KEY_NAMES)){
                String fullName = mSharedPrefs.getNames();
                String[] names = fullName.replace("  ", " ").split(" ");
                mNameLiveData.postValue(names.length>0 ?names[0]:fullName);
            }
            else if (key.equals(SharedPrefs.KEY_PROFILE_PICTURE)){
                mProfilePictureLiveData.postValue(mSharedPrefs.getProfilePicture());
            }
        }
    };

    public HomeViewModel(@NonNull Application application) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
        mSharedPrefs.registerOnSharedPreferencesListener(mOnSharedPreferenceChangeListener);
        mNameLiveData.postValue(mSharedPrefs.getNames());
        mProfilePictureLiveData.postValue(mSharedPrefs.getProfilePicture());
    }


    public LiveData<APIResponse<List<Property>>> getRecommendedProperties() {
        return mPropertiesRepository.getRecommendedProperties(null, 0, DEFAULT_LIMIT);
    }

    public LiveData<APIResponse<List<Property>>> getNearbyProperties() {
        return mPropertiesRepository.getProperties(0, DEFAULT_LIMIT, SortBy.DISTANCE, DEFAULT_COORDINATES, null, null,
                null, null, null, null, null, DEFAULT_COORDINATES,
                20, null, null, null, null);
    }

    public LiveData<APIResponse<List<Property>>> getLatestProperties() {
        return mPropertiesRepository.getLatestProperties(null, 0, DEFAULT_LIMIT);
    }

    public LiveData<APIResponse<List<Property>>> getPopularProperties() {
        return mPropertiesRepository.getPopularProperties(null, 0, DEFAULT_LIMIT);
    }

    public LiveData<APIResponse<List<Property>>> getFavoriteProperties() {
        return mPropertiesRepository.getFavoriteProperties(0, DEFAULT_LIMIT);
    }

    public LiveData<APIResponse<List<Property>>> getPersonalProperties() {
        return mPropertiesRepository.getUserProperties(0, DEFAULT_LIMIT);
    }

    public LiveData<APIResponse<FavoriteResponse>> addFavorite(int propertyId){
        return mPropertiesRepository.addFavorite(propertyId);
    }

    public LiveData<APIResponse<FavoriteResponse>> removeFavorite(int propertyId){
        return mPropertiesRepository.removeFavorite(propertyId);
    }

    public LiveData<String> getName(){
        return mNameLiveData;
    }

    public LiveData<String> getProfilePicture(){
        return mProfilePictureLiveData;
    }

    public void addFeedback(int propertyId, FeedbackType feedbackType){
        mPropertiesRepository.addFeedback(propertyId, feedbackType);
    }

}