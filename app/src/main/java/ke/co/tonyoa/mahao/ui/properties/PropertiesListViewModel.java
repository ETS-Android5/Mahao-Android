package ke.co.tonyoa.mahao.ui.properties;

import static ke.co.tonyoa.mahao.ui.home.HomeViewModel.DEFAULT_LIMIT;

import android.app.Application;
import android.content.SharedPreferences;

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

    @Inject
    PropertiesRepository mPropertiesRepository;
    @Inject
    SharedPrefs mSharedPrefs;
    private MutableLiveData<PropertiesListFragment.PropertyListType> mPropertyListMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<LatLng> mLatLngMutableLiveData = new MutableLiveData<>();
    private LiveData<APIResponse<List<Property>>> mPropertiesLiveData;

    private SharedPreferences.OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(SharedPrefs.KEY_LAST_COORDINATES)){
                mLatLngMutableLiveData.postValue(mSharedPrefs.getLastLocation());
            }
        }
    };

    public PropertiesListViewModel(@NonNull Application application) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
        mPropertyListMutableLiveData.postValue(PropertiesListFragment.PropertyListType.ALL);

        mPropertiesLiveData = Transformations.switchMap(mPropertyListMutableLiveData, input -> {
            switch (input){
                case ALL:
                    return mPropertiesRepository.getProperties(0, 2000, null, null, null,
                            null, null, null, null, null, null, null, null,
                            null, null, null, null);
                case LATEST:
                    return mPropertiesRepository.getLatestProperties(null, 0, 2000);
                case NEARBY:
                    LatLng value = mLatLngMutableLiveData.getValue();
                    return mPropertiesRepository.getProperties(0, DEFAULT_LIMIT, SortBy.DISTANCE, value, null, null,
                            null, null, null, null, null, value,
                            5, null, null, null, null);
                case POPULAR:
                    return mPropertiesRepository.getPopularProperties(null, 0, 2000);
                case FAVORITE:
                    return mPropertiesRepository.getFavoriteProperties(0, 2000);
                case PERSONAL:
                    return mPropertiesRepository.getUserProperties(0, 2000);
                case RECOMMENDED:
                    return mPropertiesRepository.getRecommendedProperties(null, 0, 2000);
            }
            return null;
        });

    }

    public void setPropertyList(PropertiesListFragment.PropertyListType propertyListType){
        mPropertyListMutableLiveData.postValue(propertyListType);
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
