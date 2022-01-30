package ke.co.tonyoa.mahao.ui.properties;

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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ke.co.tonyoa.mahao.app.MahaoApplication;
import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.responses.Amenity;
import ke.co.tonyoa.mahao.app.api.responses.FavoriteResponse;
import ke.co.tonyoa.mahao.app.api.responses.Property;
import ke.co.tonyoa.mahao.app.api.responses.PropertyCategory;
import ke.co.tonyoa.mahao.app.enums.FeedbackType;
import ke.co.tonyoa.mahao.app.enums.SortBy;
import ke.co.tonyoa.mahao.app.repositories.PropertiesRepository;
import ke.co.tonyoa.mahao.app.sharedprefs.SharedPrefs;
import ke.co.tonyoa.mahao.app.utils.LocationUpdateListener;

public class PropertiesListViewModel extends AndroidViewModel {

    public static final int LIMIT = 2000;
    @Inject
    PropertiesRepository mPropertiesRepository;
    @Inject
    SharedPrefs mSharedPrefs;
    @Inject
    LocationUpdateListener mLocationUpdateListener;
    private MutableLiveData<LatLng> mLatLngMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<String> mUserIdLiveData = new MutableLiveData<>();
    private LiveData<APIResponse<List<Property>>> mPropertiesLiveData;
    private LiveData<PropertiesViewModel.FilterAndSort> mFilterAndSortLiveData;

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

    public PropertiesListViewModel(@NonNull Application application,
                                   PropertiesListFragment.PropertyListType propertyListType,
                                   LiveData<PropertiesViewModel.FilterAndSort> filterAndSort) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
        mPropertyListType = propertyListType;
        mFilterAndSortLiveData = filterAndSort;
        mSharedPrefs.registerOnSharedPreferencesListener(mOnSharedPreferenceChangeListener);

        mLatLngMutableLiveData.setValue(mSharedPrefs.getLastLocation());
        mUserIdLiveData.setValue(mSharedPrefs.getUserId());

        // If user changes, fetch data again
        mPropertiesLiveData = Transformations.switchMap(mUserIdLiveData, input -> getProperties());
    }

    private LiveData<APIResponse<List<Property>>> getProperties(){
        switch (mPropertyListType){
            case ALL:
                return Transformations.switchMap(mFilterAndSortLiveData, new Function<PropertiesViewModel.FilterAndSort, LiveData<APIResponse<List<Property>>>>() {
                    @Override
                    public LiveData<APIResponse<List<Property>>> apply(PropertiesViewModel.FilterAndSort input) {
                        SortBy sortBy = null;
                        LatLng latLngSort = mSharedPrefs.getLastLocation();
                        String query = null;
                        Integer minBed = null;
                        Integer maxBed = null;
                        Integer minBath = null;
                        Integer maxBath = null;
                        Float minPrice = null;
                        Float maxPrice = null;
                        LatLng latLngFilter = null;
                        Integer filterRadius = null;
                        List<Integer> categories = new ArrayList<>();
                        List<Integer> amenities = new ArrayList<>();
                        if (input != null){
                            // Sort
                            PropertiesViewModel.SortBy actualSortBy = input.getSortBy();
                            if (actualSortBy != null) {
                                PropertiesViewModel.SortOrder sortOrder = actualSortBy.getSortOrder();
                                switch (input.getSortBy().getSortColumn()) {
                                    case TIME:
                                        sortBy = sortOrder == PropertiesViewModel.SortOrder.DESC ? SortBy.NEG_TIME : SortBy.TIME;
                                        break;
                                    case PRICE:
                                        sortBy = sortOrder == PropertiesViewModel.SortOrder.DESC ? SortBy.NEG_PRICE : SortBy.PRICE;
                                        break;
                                    case DISTANCE:
                                        sortBy = sortOrder == PropertiesViewModel.SortOrder.DESC ? SortBy.NEG_DISTANCE : SortBy.DISTANCE;
                                        break;
                                }
                            }

                            FilterPropertiesFragment.PropertyFilter propertyFilter = input.getPropertyFilter();
                            if (propertyFilter != null) {
                                query = propertyFilter.getQuery();
                                minBed = propertyFilter.getMinBed();
                                maxBed = propertyFilter.getMaxBed();
                                minBath = propertyFilter.getMinBath();
                                maxBath = propertyFilter.getMaxBath();
                                minPrice = propertyFilter.getMinPrice();
                                maxPrice = propertyFilter.getMaxPrice();
                                latLngFilter = propertyFilter.getLatLng();
                                filterRadius = propertyFilter.getFilterRadius();

                                if (propertyFilter.getCategories() != null) {
                                    for (PropertyCategory propertyCategory : propertyFilter.getCategories()) {
                                        categories.add(propertyCategory.getId());
                                    }
                                }
                                if (propertyFilter.getAmenities() != null) {
                                    for (Amenity amenity : propertyFilter.getAmenities()) {
                                        amenities.add(amenity.getId());
                                    }
                                }
                            }
                        }
                        return mPropertiesRepository.getProperties(1, LIMIT, sortBy, latLngSort,
                                query, minBed, maxBed, minBath, maxBath, minPrice,
                                maxPrice, latLngFilter, filterRadius, null, null,
                                categories, amenities);
                    }
                });
            case LATEST:
                return mPropertiesRepository.getLatestProperties(null, 1, LIMIT);
            case NEARBY:
                return Transformations.switchMap(mLatLngMutableLiveData, input -> {
                    return mPropertiesRepository.getProperties(1, LIMIT, SortBy.DISTANCE, input, null, null,
                            null, null, null, null, null, input,
                            5, null, null, null, null);
                });
            case POPULAR:
                return mPropertiesRepository.getPopularProperties(null, 1, LIMIT);
            case FAVORITE:
                return mPropertiesRepository.getFavoriteProperties(1, LIMIT);
            case PERSONAL:
                return mPropertiesRepository.getUserProperties(1, LIMIT);
            case RECOMMENDED:
                return mPropertiesRepository.getRecommendedProperties(null, 1, LIMIT);
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

    public void listenToLocationUpdates(LocationListener locationListener, long updateTime, long updateDistance){
        mLocationUpdateListener.startListening(locationListener, updateTime, updateDistance);
    }

}
