package ke.co.tonyoa.mahao.ui.home;

import android.app.Application;
import android.content.SharedPreferences;
import android.location.LocationListener;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

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
import ke.co.tonyoa.mahao.app.utils.LocationUpdateListener;

public class HomeViewModel extends AndroidViewModel {

    public static final LatLng DEFAULT_COORDINATES = new LatLng( -1.091079, 37.011459 );
    public static final int DEFAULT_LIMIT = 14;

    @Inject
    PropertiesRepository mPropertiesRepository;
    @Inject
    SharedPrefs mSharedPrefs;
    @Inject
    LocationUpdateListener mLocationUpdateListener;
    private MutableLiveData<String> mNameLiveData = new MutableLiveData<>();
    private MutableLiveData<String> mProfilePictureLiveData = new MutableLiveData<>();
    private MutableLiveData<LatLng> mLatLngMutableLiveData = new MutableLiveData<>();
    private LiveData<APIResponse<List<Property>>> mNearbyProperties;

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
            else if (key.equals(SharedPrefs.KEY_LAST_COORDINATES)){
                mLatLngMutableLiveData.postValue(mSharedPrefs.getLastLocation());
            }
        }
    };

    public HomeViewModel(@NonNull Application application) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
        mSharedPrefs.registerOnSharedPreferencesListener(mOnSharedPreferenceChangeListener);
        mNameLiveData.postValue(mSharedPrefs.getNames());
        mProfilePictureLiveData.postValue(mSharedPrefs.getProfilePicture());
        mLatLngMutableLiveData.setValue(mSharedPrefs.getLastLocation());
        mNearbyProperties = Transformations.switchMap(mLatLngMutableLiveData, new Function<LatLng, LiveData<APIResponse<List<Property>>>>() {
            @Override
            public LiveData<APIResponse<List<Property>>> apply(LatLng input) {
                return mPropertiesRepository.getProperties(1, DEFAULT_LIMIT, SortBy.DISTANCE, input, null, null,
                        null, null, null, null, null, input,
                        5, null, null, null, null);
            }
        });
    }


    public LiveData<APIResponse<List<Property>>> getRecommendedProperties() {
        return mPropertiesRepository.getRecommendedProperties(null, 1, DEFAULT_LIMIT);
    }

    public LiveData<APIResponse<List<Property>>> getNearbyProperties() {
        mLatLngMutableLiveData.setValue(mLatLngMutableLiveData.getValue());
        return mNearbyProperties;
    }

    public LiveData<APIResponse<List<Property>>> getLatestProperties() {
        return mPropertiesRepository.getLatestProperties(null, 1, DEFAULT_LIMIT);
    }

    public LiveData<APIResponse<List<Property>>> getPopularProperties() {
        return mPropertiesRepository.getPopularProperties(null, 1, DEFAULT_LIMIT);
    }

    public LiveData<APIResponse<List<Property>>> getFavoriteProperties() {
        return mPropertiesRepository.getFavoriteProperties(1, DEFAULT_LIMIT);
    }

    public LiveData<APIResponse<List<Property>>> getPersonalProperties() {
        return mPropertiesRepository.getUserProperties(1, DEFAULT_LIMIT);
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

    public void listenToLocationUpdates(LocationListener locationListener, long updateTime, long updateDistance){
        mLocationUpdateListener.startListening(locationListener, updateTime, updateDistance);
    }

}