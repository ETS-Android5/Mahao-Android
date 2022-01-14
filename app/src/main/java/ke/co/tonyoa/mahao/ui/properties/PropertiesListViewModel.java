package ke.co.tonyoa.mahao.ui.properties;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
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

public class PropertiesListViewModel extends AndroidViewModel {

    public static final int LIMIT = 2000;
    @Inject
    PropertiesRepository mPropertiesRepository;
    @Inject
    SharedPrefs mSharedPrefs;
    private MutableLiveData<LatLng> mLatLngMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<String> mUserIdLiveData = new MutableLiveData<>();
    private LiveData<APIResponse<List<Property>>> mPropertiesLiveData;

    private SharedPreferences.OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(SharedPrefs.KEY_LAST_COORDINATES)){
                mLatLngMutableLiveData.postValue(mSharedPrefs.getLastLocation());
            }
            else if (key.equals(SharedPrefs.KEY_USERID)){
                mUserIdLiveData.postValue(mSharedPrefs.getUserId());
            }
        }
    };
    private PropertiesListFragment.PropertyListType mPropertyListType;

    public PropertiesListViewModel(@NonNull Application application, PropertiesListFragment.PropertyListType propertyListType) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
        mPropertyListType = propertyListType;
        mSharedPrefs.registerOnSharedPreferencesListener(mOnSharedPreferenceChangeListener);

        mLatLngMutableLiveData.setValue(mSharedPrefs.getLastLocation());
        mUserIdLiveData.setValue(mSharedPrefs.getUserId());

        // If user changes, fetch data again
        mPropertiesLiveData = Transformations.switchMap(mUserIdLiveData, input -> getProperties());
    }

    private LiveData<APIResponse<List<Property>>> getProperties(){
        switch (mPropertyListType){
            case ALL:
                return mPropertiesRepository.getProperties(0, LIMIT, null, null, null,
                        null, null, null, null, null, null, null, null,
                        null, null, null, null);
            case LATEST:
                return mPropertiesRepository.getLatestProperties(null, 0, LIMIT);
            case NEARBY:
                mLatLngMutableLiveData.setValue(mSharedPrefs.getLastLocation());
                LatLng value = mLatLngMutableLiveData.getValue();
                return mPropertiesRepository.getProperties(0, LIMIT, SortBy.DISTANCE, value, null, null,
                        null, null, null, null, null, value,
                        5, null, null, null, null);
            case POPULAR:
                return mPropertiesRepository.getPopularProperties(null, 0, LIMIT);
            case FAVORITE:
                return mPropertiesRepository.getFavoriteProperties(0, LIMIT);
            case PERSONAL:
                return mPropertiesRepository.getUserProperties(0, LIMIT);
            case RECOMMENDED:
                return mPropertiesRepository.getRecommendedProperties(null, 0, LIMIT);
        }
        return null;
    }

    public LiveData<APIResponse<List<Property>>> getPropertiesLiveData(){
        return mPropertiesLiveData;
    }

    public LiveData<APIResponse<FavoriteResponse>> addFavorite(int propertyId) {
        return mPropertiesRepository.addFavorite(propertyId);
    }

    public LiveData<APIResponse<FavoriteResponse>> removeFavorite(int propertyId) {
        return mPropertiesRepository.removeFavorite(propertyId);
    }

    public void addFeedback(int propertyId, FeedbackType feedbackType) {
        mPropertiesRepository.addFeedback(propertyId, feedbackType);
    }
}
