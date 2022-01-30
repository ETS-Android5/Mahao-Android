package ke.co.tonyoa.mahao.ui.properties;

import android.app.Application;
import android.content.SharedPreferences;
import android.location.LocationListener;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.PagedList;

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
import ke.co.tonyoa.mahao.app.paging.RepoResult;
import ke.co.tonyoa.mahao.app.repositories.PropertiesRepository;
import ke.co.tonyoa.mahao.app.sharedprefs.SharedPrefs;
import ke.co.tonyoa.mahao.app.utils.LocationUpdateListener;

public class PropertiesListViewModel extends AndroidViewModel {

    @Inject
    PropertiesRepository mPropertiesRepository;
    @Inject
    SharedPrefs mSharedPrefs;
    @Inject
    LocationUpdateListener mLocationUpdateListener;
    private final MutableLiveData<LatLng> mLatLngMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mUserIdLiveData = new MutableLiveData<>();
    private final LiveData<RepoResult<Property>> mRepoResult;
    private final LiveData<PagedList<Property>> mPropertiesLiveData;
    private final LiveData<Integer> mLoadState;
    private final LiveData<String> mErrors;
    private final LiveData<PropertiesViewModel.FilterAndSort> mFilterAndSortLiveData;

    private final SharedPreferences.OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
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
    private final PropertiesListFragment.PropertyListType mPropertyListType;

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
        mRepoResult = Transformations.switchMap(mUserIdLiveData, input -> getRepoResult());
        mPropertiesLiveData = Transformations.switchMap(mRepoResult, RepoResult::getData);
        mLoadState = Transformations.switchMap(mRepoResult, RepoResult::getLoadState);
        mErrors = Transformations.switchMap(mRepoResult, RepoResult::getNetworkErrors);
    }

    private LiveData<RepoResult<Property>> getRepoResult(){
        return Transformations.switchMap(mUserIdLiveData, input -> {
            switch (mPropertyListType){
                case ALL:
                    return Transformations.switchMap(mFilterAndSortLiveData, filter -> {
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
                        if (filter != null){
                            // Sort
                            PropertiesViewModel.SortBy actualSortBy = filter.getSortBy();
                            if (actualSortBy != null) {
                                PropertiesViewModel.SortOrder sortOrder = actualSortBy.getSortOrder();
                                switch (filter.getSortBy().getSortColumn()) {
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

                            FilterPropertiesFragment.PropertyFilter propertyFilter = filter.getPropertyFilter();
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
                        return mPropertiesRepository.getPropertiesPaged(sortBy, latLngSort,
                                query, minBed, maxBed, minBath, maxBath, minPrice,
                                maxPrice, latLngFilter, filterRadius, null, null,
                                categories, amenities);
                    });
                case LATEST:
                    return mPropertiesRepository.getLatestPropertiesPaged(null);
                case NEARBY:
                    return Transformations.switchMap(mLatLngMutableLiveData,
                            latLng -> mPropertiesRepository.getPropertiesPaged(SortBy.DISTANCE, latLng, null, null,
                            null, null, null, null, null, latLng,
                            5, null, null, null, null));
                case POPULAR:
                    return mPropertiesRepository.getPopularPropertiesPaged(null);
                case FAVORITE:
                    return mPropertiesRepository.getFavoritePropertiesPaged();
                case PERSONAL:
                    return mPropertiesRepository.getUserPropertiesPaged();
                case RECOMMENDED:
                    return mPropertiesRepository.getRecommendedPropertiesPaged(null);
            }
            return null;
        });
    }

    public LiveData<PagedList<Property>> getPropertiesLiveData(){
        return mPropertiesLiveData;
    }

    public LiveData<Integer> getLoadState() {
        return mLoadState;
    }

    public LiveData<String> getErrors() {
        return mErrors;
    }

    public void invalidateList(){
        PagedList<Property> value = mPropertiesLiveData.getValue();
        if (value != null) {
            value.getDataSource().invalidate();
        }
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
