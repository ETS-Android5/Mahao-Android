package ke.co.tonyoa.mahao.app.repositories;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.ApiManager;
import ke.co.tonyoa.mahao.app.api.requests.CreatePropertyRequest;
import ke.co.tonyoa.mahao.app.api.responses.FavoriteResponse;
import ke.co.tonyoa.mahao.app.api.responses.Feedback;
import ke.co.tonyoa.mahao.app.api.responses.ModifyAmenitiesResponse;
import ke.co.tonyoa.mahao.app.api.responses.Property;
import ke.co.tonyoa.mahao.app.api.responses.PropertyPhoto;
import ke.co.tonyoa.mahao.app.enums.FeedbackType;
import ke.co.tonyoa.mahao.app.enums.SortBy;
import ke.co.tonyoa.mahao.app.paging.PropertiesRepoDataSource;
import ke.co.tonyoa.mahao.app.paging.RepoDataSource;
import ke.co.tonyoa.mahao.app.paging.RepoResult;
import ke.co.tonyoa.mahao.app.sharedprefs.SharedPrefs;
import ke.co.tonyoa.mahao.ui.properties.PropertiesListFragment;

@Singleton
public class PropertiesRepository {
    private final ApiManager mApiManager;
    private final SharedPrefs mSharedPrefs;
    private final Application mContext;

    @Inject
    public PropertiesRepository(ApiManager apiManager, SharedPrefs sharedPrefs, Application context) {
        this.mApiManager = apiManager;
        this.mSharedPrefs = sharedPrefs;
        mContext = context;
    }

    public LiveData<APIResponse<List<Property>>> getProperties(int page, int limit, SortBy sortBy, LatLng latLngSort,
                                                               String query, Integer minBed, Integer maxBed, Integer minBath,
                                                               Integer maxBath, Float minPrice, Float maxPrice,
                                                               LatLng latLngFilter, Integer filterRadius, Boolean isVerified,
                                                               Boolean isEnabled, List<Integer> categories, List<Integer> amenities) {
        MutableLiveData<APIResponse<List<Property>>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<List<Property>> response = mApiManager.getProperties(page, limit,
                        sortBy, latLngSort, query, minBed, maxBed, minBath, maxBath, minPrice, maxPrice,
                        latLngFilter, filterRadius, isVerified, isEnabled, categories, amenities);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<RepoResult<Property>> getPropertiesPaged(SortBy sortBy, LatLng latLngSort,
                                                             String query, Integer minBed, Integer maxBed, Integer minBath,
                                                             Integer maxBath, Float minPrice, Float maxPrice,
                                                             LatLng latLngFilter, Integer filterRadius, Boolean isVerified,
                                                             Boolean isEnabled, List<Integer> categories, List<Integer> amenities){
        PropertiesRepoDataSource.PropertiesRepoDataSourceFactory propertiesRepoDataSourceFactory =
                new PropertiesRepoDataSource.PropertiesRepoDataSourceFactory(mContext, mApiManager,
                        PropertiesListFragment.PropertyListType.ALL, sortBy, latLngSort, query, minBed,
                        maxBed, minBath, maxBath, minPrice, maxPrice, latLngFilter, filterRadius, isVerified,
                        isEnabled, categories, amenities, null);
        LiveData<PagedList<Property>> pagedListLiveData = new LivePagedListBuilder<>(propertiesRepoDataSourceFactory,
                RepoDataSource.getDefaultPagedListConfig())
                .setFetchExecutor(Executors.newFixedThreadPool(2))
                .build();
        MutableLiveData<RepoResult<Property>> repoResultMutableLiveData = new MutableLiveData<>();
        RepoResult<Property> repoResult = new RepoResult<>(pagedListLiveData,
                Transformations.switchMap(propertiesRepoDataSourceFactory.getPropertiesRepoDataSource(),
                        PropertiesRepoDataSource::getLoadState),
                Transformations.switchMap(propertiesRepoDataSourceFactory.getPropertiesRepoDataSource(),
                        PropertiesRepoDataSource::getNetworkErrors));
        repoResultMutableLiveData.postValue(repoResult);
        return repoResultMutableLiveData;
    }

    public LiveData<APIResponse<List<Property>>> getUserProperties(int page, int limit) {
        MutableLiveData<APIResponse<List<Property>>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<List<Property>> response = mApiManager.getUserProperties(page, limit);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<RepoResult<Property>> getUserPropertiesPaged(){
        PropertiesRepoDataSource.PropertiesRepoDataSourceFactory propertiesRepoDataSourceFactory =
                new PropertiesRepoDataSource.PropertiesRepoDataSourceFactory(mContext, mApiManager,
                        PropertiesListFragment.PropertyListType.PERSONAL);
        LiveData<PagedList<Property>> pagedListLiveData = new LivePagedListBuilder<>(propertiesRepoDataSourceFactory,
                RepoDataSource.getDefaultPagedListConfig())
                .setFetchExecutor(Executors.newFixedThreadPool(2))
                .build();
        MutableLiveData<RepoResult<Property>> repoResultMutableLiveData = new MutableLiveData<>();
        RepoResult<Property> repoResult = new RepoResult<>(pagedListLiveData,
                Transformations.switchMap(propertiesRepoDataSourceFactory.getPropertiesRepoDataSource(),
                        PropertiesRepoDataSource::getLoadState),
                Transformations.switchMap(propertiesRepoDataSourceFactory.getPropertiesRepoDataSource(),
                        PropertiesRepoDataSource::getNetworkErrors));
        repoResultMutableLiveData.postValue(repoResult);
        return repoResultMutableLiveData;
    }

    public LiveData<APIResponse<List<Property>>> getFavoriteProperties(int page, int limit) {
        MutableLiveData<APIResponse<List<Property>>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<List<Property>> response = mApiManager.getFavoriteProperties(page, limit);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<RepoResult<Property>> getFavoritePropertiesPaged(){
        PropertiesRepoDataSource.PropertiesRepoDataSourceFactory propertiesRepoDataSourceFactory =
                new PropertiesRepoDataSource.PropertiesRepoDataSourceFactory(mContext, mApiManager,
                        PropertiesListFragment.PropertyListType.FAVORITE);
        LiveData<PagedList<Property>> pagedListLiveData = new LivePagedListBuilder<>(propertiesRepoDataSourceFactory,
                RepoDataSource.getDefaultPagedListConfig())
                .setFetchExecutor(Executors.newFixedThreadPool(2))
                .build();
        MutableLiveData<RepoResult<Property>> repoResultMutableLiveData = new MutableLiveData<>();
        RepoResult<Property> repoResult = new RepoResult<>(pagedListLiveData,
                Transformations.switchMap(propertiesRepoDataSourceFactory.getPropertiesRepoDataSource(),
                        PropertiesRepoDataSource::getLoadState),
                Transformations.switchMap(propertiesRepoDataSourceFactory.getPropertiesRepoDataSource(),
                        PropertiesRepoDataSource::getNetworkErrors));
        repoResultMutableLiveData.postValue(repoResult);
        return repoResultMutableLiveData;
    }

    public LiveData<APIResponse<List<Property>>> getLatestProperties(Integer category, int page, int limit) {
        MutableLiveData<APIResponse<List<Property>>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<List<Property>> response = mApiManager.getLatestProperties(category, page, limit);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<RepoResult<Property>> getLatestPropertiesPaged(Integer category){
        PropertiesRepoDataSource.PropertiesRepoDataSourceFactory propertiesRepoDataSourceFactory =
                new PropertiesRepoDataSource.PropertiesRepoDataSourceFactory(mContext, mApiManager,
                        PropertiesListFragment.PropertyListType.LATEST, category);
        LiveData<PagedList<Property>> pagedListLiveData = new LivePagedListBuilder<>(propertiesRepoDataSourceFactory,
                RepoDataSource.getDefaultPagedListConfig())
                .setFetchExecutor(Executors.newFixedThreadPool(2))
                .build();
        MutableLiveData<RepoResult<Property>> repoResultMutableLiveData = new MutableLiveData<>();
        RepoResult<Property> repoResult = new RepoResult<>(pagedListLiveData,
                Transformations.switchMap(propertiesRepoDataSourceFactory.getPropertiesRepoDataSource(),
                        PropertiesRepoDataSource::getLoadState),
                Transformations.switchMap(propertiesRepoDataSourceFactory.getPropertiesRepoDataSource(),
                        PropertiesRepoDataSource::getNetworkErrors));
        repoResultMutableLiveData.postValue(repoResult);
        return repoResultMutableLiveData;
    }

    public LiveData<APIResponse<List<Property>>> getPopularProperties(Integer category, int page, int limit) {
        MutableLiveData<APIResponse<List<Property>>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<List<Property>> response = mApiManager.getPopularProperties(category, page, limit);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<RepoResult<Property>> getPopularPropertiesPaged(Integer category){
        PropertiesRepoDataSource.PropertiesRepoDataSourceFactory propertiesRepoDataSourceFactory =
                new PropertiesRepoDataSource.PropertiesRepoDataSourceFactory(mContext, mApiManager,
                        PropertiesListFragment.PropertyListType.POPULAR, category);
        LiveData<PagedList<Property>> pagedListLiveData = new LivePagedListBuilder<>(propertiesRepoDataSourceFactory,
                RepoDataSource.getDefaultPagedListConfig())
                .setFetchExecutor(Executors.newFixedThreadPool(2))
                .build();
        MutableLiveData<RepoResult<Property>> repoResultMutableLiveData = new MutableLiveData<>();
        RepoResult<Property> repoResult = new RepoResult<>(pagedListLiveData,
                Transformations.switchMap(propertiesRepoDataSourceFactory.getPropertiesRepoDataSource(),
                        PropertiesRepoDataSource::getLoadState),
                Transformations.switchMap(propertiesRepoDataSourceFactory.getPropertiesRepoDataSource(),
                        PropertiesRepoDataSource::getNetworkErrors));
        repoResultMutableLiveData.postValue(repoResult);
        return repoResultMutableLiveData;
    }

    public LiveData<APIResponse<List<Property>>> getRecommendedProperties(Integer category, int page, int limit) {
        MutableLiveData<APIResponse<List<Property>>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<List<Property>> response = mApiManager.getRecommendedProperties(category, page, limit);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<RepoResult<Property>> getRecommendedPropertiesPaged(Integer category){
        PropertiesRepoDataSource.PropertiesRepoDataSourceFactory propertiesRepoDataSourceFactory =
                new PropertiesRepoDataSource.PropertiesRepoDataSourceFactory(mContext, mApiManager,
                        PropertiesListFragment.PropertyListType.RECOMMENDED, category);
        LiveData<PagedList<Property>> pagedListLiveData = new LivePagedListBuilder<>(propertiesRepoDataSourceFactory,
                RepoDataSource.getDefaultPagedListConfig())
                .setFetchExecutor(Executors.newFixedThreadPool(2))
                .build();
        MutableLiveData<RepoResult<Property>> repoResultMutableLiveData = new MutableLiveData<>();
        RepoResult<Property> repoResult = new RepoResult<>(pagedListLiveData,
                Transformations.switchMap(propertiesRepoDataSourceFactory.getPropertiesRepoDataSource(),
                        PropertiesRepoDataSource::getLoadState),
                Transformations.switchMap(propertiesRepoDataSourceFactory.getPropertiesRepoDataSource(),
                        PropertiesRepoDataSource::getNetworkErrors));
        repoResultMutableLiveData.postValue(repoResult);
        return repoResultMutableLiveData;
    }

    public LiveData<APIResponse<List<Property>>> getSimilarProperties(int propertyId, Integer category,
                                                                      int page, int limit) {
        MutableLiveData<APIResponse<List<Property>>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<List<Property>> response = mApiManager.getSimilarProperties(propertyId,
                        category, page, limit);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<Property>> getPropertyById(int propertyId) {
        MutableLiveData<APIResponse<Property>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<Property> response = mApiManager.getPropertyById(propertyId);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<Property>> createProperty(Uri featureImage, int propertyCategoryId, String title,
                                                          String description, int numBed, int numBath, String locationName,
                                                          float price, LatLng latLng, boolean isEnabled, boolean isVerified) {
        MutableLiveData<APIResponse<Property>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<Property> response = mApiManager.createProperty(featureImage, propertyCategoryId,
                        title, description, numBed, numBath, locationName, price, latLng, isEnabled,
                        isVerified);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<Property>> createProperty(CreatePropertyRequest createPropertyRequest) {
        MutableLiveData<APIResponse<Property>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<Property> response = mApiManager.createProperty(createPropertyRequest);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<Property>> updateProperty(int propertyId, Uri featureImage,
                                                          int propertyCategoryId, String title,
                                                          String description, int numBed, int numBath,
                                                          String locationName, float price, LatLng latLng,
                                                          boolean isEnabled, boolean isVerified) {
        MutableLiveData<APIResponse<Property>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<Property> response = mApiManager.updateProperty(propertyId, featureImage,
                        propertyCategoryId, title, description, numBed, numBath, locationName,
                        price, latLng, isEnabled, isVerified);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<Property>> deleteProperty(int propertyId) {
        MutableLiveData<APIResponse<Property>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<Property> response = mApiManager.deletePropertyById(propertyId);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<List<ModifyAmenitiesResponse>>> modifyPropertyAmenities(int propertyId,
                                                                                  List<Integer> added,
                                                                                  List<Integer> removed){
        MutableLiveData<APIResponse<List<ModifyAmenitiesResponse>>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<List<ModifyAmenitiesResponse>> response = mApiManager.modifyPropertyAmenities(propertyId,
                        added, removed);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<FavoriteResponse>> addFavorite(int propertyId) {
        MutableLiveData<APIResponse<FavoriteResponse>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<FavoriteResponse> response = mApiManager.addFavorite(propertyId);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<FavoriteResponse>> removeFavorite(int propertyId) {
        MutableLiveData<APIResponse<FavoriteResponse>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<FavoriteResponse> response = mApiManager.removeFavorite(propertyId);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<Feedback>> addFeedback(int propertyId, FeedbackType feedbackType) {
        MutableLiveData<APIResponse<Feedback>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<Feedback> response = mApiManager.addFeedback(propertyId, feedbackType);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<List<PropertyPhoto>>> addPropertyPhotos(int propertyId, List<Uri> photos) {
        MutableLiveData<APIResponse<List<PropertyPhoto>>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<List<PropertyPhoto>> response = mApiManager.addPropertyPhotos(propertyId, photos);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<PropertyPhoto>> removePropertyPhoto(int propertyId, int propertyPhotoId) {
        MutableLiveData<APIResponse<PropertyPhoto>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<PropertyPhoto> response = mApiManager.removePropertyPhoto(propertyId, propertyPhotoId);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

}