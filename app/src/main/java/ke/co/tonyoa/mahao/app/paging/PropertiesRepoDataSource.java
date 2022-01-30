package ke.co.tonyoa.mahao.app.paging;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.ApiManager;
import ke.co.tonyoa.mahao.app.api.responses.Property;
import ke.co.tonyoa.mahao.app.enums.SortBy;
import ke.co.tonyoa.mahao.ui.properties.PropertiesListFragment;

public class PropertiesRepoDataSource extends RepoDataSource<Property> {

    private PropertiesListFragment.PropertyListType mPropertyListType;
    private final SortBy mSortBy;
    private final LatLng mLatLngSort;
    private final String mQuery;
    private final Integer mMinBed;
    private final Integer mMaxBed;
    private final Integer mMinBath;
    private final Integer mMaxBath;
    private final Float mMinPrice;
    private final Float mMaxPrice;
    private final LatLng mLatLngFilter;
    private final Integer mFilterRadius;
    private final Boolean mIsVerified;
    private final Boolean mIsEnabled;
    private final List<Integer> mCategories;
    private final List<Integer> mAmenities;
    private final Integer mCategoryFilter;

    public PropertiesRepoDataSource(Context context, ApiManager apiManager,
                                    PropertiesListFragment.PropertyListType propertyListType) {
        this(context, apiManager, propertyListType, null, null, null, null,
                null, null, null, null, null, null,
                null, null, null, null, null, null);
    }

    public PropertiesRepoDataSource(Context context, ApiManager apiManager,
                                    PropertiesListFragment.PropertyListType propertyListType,
                                    Integer categoryFilter) {
        this(context, apiManager, propertyListType, null, null, null, null,
                null, null, null, null, null, null,
                null, null, null, null, null, categoryFilter);
    }

    public PropertiesRepoDataSource(Context context, ApiManager apiManager, PropertiesListFragment.PropertyListType propertyListType,
                                    SortBy sortBy, LatLng latLngSort,
                                    String query, Integer minBed, Integer maxBed, Integer minBath,
                                    Integer maxBath, Float minPrice, Float maxPrice,
                                    LatLng latLngFilter, Integer filterRadius, Boolean isVerified,
                                    Boolean isEnabled, List<Integer> categories, List<Integer> amenities,
                                    Integer categoryFilter){
        super(context, apiManager);
        mPropertyListType = propertyListType;
        mSortBy = sortBy;
        mLatLngSort = latLngSort;
        mQuery = query;
        mMinBed = minBed;
        mMaxBed = maxBed;
        mMinBath = minBath;
        mMaxBath = maxBath;
        mMinPrice = minPrice;
        mMaxPrice = maxPrice;
        mLatLngFilter = latLngFilter;
        mFilterRadius = filterRadius;
        mIsVerified = isVerified;
        mIsEnabled = isEnabled;
        mCategories = categories;
        mAmenities = amenities;
        mCategoryFilter = categoryFilter;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params,
                            @NonNull final LoadInitialCallback<Integer, Property> callback) {
        final int currentPage = 1;

        //setting load state so that the UI can know that progress of data fetching
        mLoadState.postValue(LOADING_ONGOING);

        //Simple retrofit call to get the first page of data
        try {
            int numPagesInitialLoad = 2;
            APIResponse<List<Property>> listAPIResponse = getListAPIResponse(currentPage,
                    RepoDataSource.DEFAULT_PAGE_LIMIT* numPagesInitialLoad);

            if (listAPIResponse != null && listAPIResponse.isSuccessful()){
                //IMPORTANT: once the first page is load we increment the page count and pass it to callback.onResult()
                callback.onResult(listAPIResponse.body(),null,
                        currentPage+numPagesInitialLoad);
                //setting load state so that the UI can know data fetching is successful
                mLoadState.postValue(LOADING_SUCCESS);
            }
            else {
                //since no data was fetched we pass empty list and dont increment the page number
                //so that it can retry the fetching of 1st page
                callback.onResult(new ArrayList<>(), null, currentPage);

                //setting load state so that the UI can know data fetching failed
                mLoadState.postValue(LOADING_FAILED);

                mNetworkErrors.postValue(listAPIResponse!=null?listAPIResponse.errorMessage(mContext) :
                        mContext.getString(R.string.unknown_error));
            }
        } catch (IOException e) {
            e.printStackTrace();
            callback.onResult(new ArrayList<>(), null, currentPage);
            mLoadState.postValue(LOADING_FAILED);
            mNetworkErrors.postValue(e.getMessage());
        }
    }

    private APIResponse<List<Property>> getListAPIResponse(int currentPage, int limit) throws IOException {
        APIResponse<List<Property>> listAPIResponse;
        switch (mPropertyListType){
            case LATEST:
                listAPIResponse = mApiManager.getLatestProperties(mCategoryFilter, currentPage,
                        limit);
                break;
            case POPULAR:
                listAPIResponse = mApiManager.getPopularProperties(mCategoryFilter, currentPage,
                        limit);
                break;
            case FAVORITE:
                listAPIResponse = mApiManager.getFavoriteProperties(currentPage,
                        limit);
                break;
            case PERSONAL:
                listAPIResponse = mApiManager.getUserProperties(currentPage,
                        limit);
                break;
            case RECOMMENDED:
                listAPIResponse = mApiManager.getRecommendedProperties(mCategoryFilter, currentPage,
                        limit);
                break;
            case ALL:
            case NEARBY:
            default:
                listAPIResponse = mApiManager.getProperties(currentPage,
                        limit,
                        mSortBy, mLatLngSort, mQuery, mMinBed, mMaxBed, mMinBath, mMaxBath, mMinPrice,
                        mMaxPrice, mLatLngFilter, mFilterRadius, mIsVerified, mIsEnabled, mCategories,
                        mAmenities);
                break;
        }
        return listAPIResponse;
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Property> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Property> callback) {
        // we get the current page from params
        final int currentPage = params.key;

        mLoadState.postValue(LOADING_ONGOING);
        try {
            APIResponse<List<Property>> listAPIResponse = getListAPIResponse(currentPage,
                    RepoDataSource.DEFAULT_PAGE_LIMIT);

            if (listAPIResponse != null && listAPIResponse.isSuccessful()){
                mLoadState.postValue(LOADING_SUCCESS);
                callback.onResult(listAPIResponse.body(), currentPage+1);
            }
            else {
                callback.onResult(new ArrayList<>(), currentPage);
                mLoadState.postValue(LOADING_FAILED);

                mNetworkErrors.postValue(listAPIResponse!=null?listAPIResponse.errorMessage(mContext) :
                        mContext.getString(R.string.unknown_error));
            }
        } catch (IOException e) {
            e.printStackTrace();
            callback.onResult(new ArrayList<>(), currentPage);
            mLoadState.postValue(LOADING_FAILED);
            mNetworkErrors.postValue(e.getMessage());
        }
    }

    public static class PropertiesRepoDataSourceFactory extends Factory<Integer, Property> {

        private final Context mContext;
        private final ApiManager mApiManager;
        private final PropertiesListFragment.PropertyListType mPropertyListType;
        private final SortBy mSortBy;
        private final LatLng mLatLngSort;
        private final String mQuery;
        private final Integer mMinBed;
        private final Integer mMaxBed;
        private final Integer mMinBath;
        private final Integer mMaxBath;
        private final Float mMinPrice;
        private final Float mMaxPrice;
        private final LatLng mLatLngFilter;
        private final Integer mFilterRadius;
        private final Boolean mIsVerified;
        private final Boolean mIsEnabled;
        private final List<Integer> mCategories;
        private final List<Integer> mAmenities;
        private final Integer mCategoryFilter;
        private final MutableLiveData<PropertiesRepoDataSource> mPropertiesRepoDataSource;

        public PropertiesRepoDataSourceFactory(Context context, ApiManager apiManager,
                                               PropertiesListFragment.PropertyListType propertyListType) {
            this(context, apiManager, propertyListType, null, null, null, null,
                    null, null, null, null, null, null,
                    null, null, null, null, null, null);
        }

        public PropertiesRepoDataSourceFactory(Context context, ApiManager apiManager,
                                               PropertiesListFragment.PropertyListType propertyListType,
                                               Integer categoryFilter){
            this(context, apiManager, propertyListType, null, null, null, null,
                    null, null, null, null, null, null,
                    null, null, null, null, null, categoryFilter);
        }

        public PropertiesRepoDataSourceFactory(Context context, ApiManager apiManager, PropertiesListFragment.PropertyListType propertyListType,
                                               SortBy sortBy, LatLng latLngSort,
                                               String query, Integer minBed, Integer maxBed, Integer minBath,
                                               Integer maxBath, Float minPrice, Float maxPrice,
                                               LatLng latLngFilter, Integer filterRadius, Boolean isVerified,
                                               Boolean isEnabled, List<Integer> categories, List<Integer> amenities,
                                               Integer categoryFilter){

            mContext = context;
            mApiManager = apiManager;
            mPropertyListType = propertyListType;
            mSortBy = sortBy;
            mLatLngSort = latLngSort;
            mQuery = query;
            mMinBed = minBed;
            mMaxBed = maxBed;
            mMinBath = minBath;
            mMaxBath = maxBath;
            mMinPrice = minPrice;
            mMaxPrice = maxPrice;
            mLatLngFilter = latLngFilter;
            mFilterRadius = filterRadius;
            mIsVerified = isVerified;
            mIsEnabled = isEnabled;
            mCategories = categories;
            mAmenities = amenities;
            mCategoryFilter = categoryFilter;
            mPropertiesRepoDataSource = new MutableLiveData<>();
        }

        @NonNull
        @Override
        public DataSource<Integer, Property> create() {
            PropertiesRepoDataSource collectorsRepoDataSource = new PropertiesRepoDataSource(mContext, mApiManager,
                    mPropertyListType, mSortBy, mLatLngSort, mQuery, mMinBed, mMaxBed, mMinBath, mMaxBath,
                    mMinPrice, mMaxPrice, mLatLngFilter, mFilterRadius, mIsVerified, mIsEnabled, mCategories,
                    mAmenities, mCategoryFilter);
            mPropertiesRepoDataSource.postValue(collectorsRepoDataSource);
            return collectorsRepoDataSource;
        }

        public LiveData<PropertiesRepoDataSource> getPropertiesRepoDataSource() {
            return mPropertiesRepoDataSource;
        }
    }
}
