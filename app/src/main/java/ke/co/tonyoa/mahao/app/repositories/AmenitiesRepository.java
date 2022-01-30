package ke.co.tonyoa.mahao.app.repositories;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.ApiManager;
import ke.co.tonyoa.mahao.app.api.responses.Amenity;
import ke.co.tonyoa.mahao.app.paging.AmenitiesRepoDataSource;
import ke.co.tonyoa.mahao.app.paging.RepoDataSource;
import ke.co.tonyoa.mahao.app.paging.RepoResult;
import ke.co.tonyoa.mahao.app.sharedprefs.SharedPrefs;

@Singleton
public class AmenitiesRepository {
    private final ApiManager mApiManager;
    private final SharedPrefs mSharedPrefs;
    private final Application mContext;

    @Inject
    public AmenitiesRepository(ApiManager apiManager, SharedPrefs sharedPrefs, Application context) {
        this.mApiManager = apiManager;
        this.mSharedPrefs = sharedPrefs;
        mContext = context;
    }

    public LiveData<APIResponse<List<Amenity>>> getAmenities() {
        MutableLiveData<APIResponse<List<Amenity>>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<List<Amenity>> response = mApiManager.getAmenities(1, 5000);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<RepoResult<Amenity>> getAmenitiesPaged(){
        AmenitiesRepoDataSource.AmenitiesRepoDataSourceFactory amenitiesRepoDataSourceFactory =
                new AmenitiesRepoDataSource.AmenitiesRepoDataSourceFactory(mContext, mApiManager);
        LiveData<PagedList<Amenity>> pagedListLiveData = new LivePagedListBuilder<>(amenitiesRepoDataSourceFactory,
                RepoDataSource.getDefaultPagedListConfig())
                .setFetchExecutor(Executors.newFixedThreadPool(2))
                .build();
        MutableLiveData<RepoResult<Amenity>> repoResultMutableLiveData = new MutableLiveData<>();
        RepoResult<Amenity> repoResult = new RepoResult<>(pagedListLiveData,
                Transformations.switchMap(amenitiesRepoDataSourceFactory.getAmenitiesRepoDataSource(),
                        AmenitiesRepoDataSource::getLoadState),
                Transformations.switchMap(amenitiesRepoDataSourceFactory.getAmenitiesRepoDataSource(),
                        AmenitiesRepoDataSource::getNetworkErrors));
        repoResultMutableLiveData.postValue(repoResult);
        return repoResultMutableLiveData;
    }

    public LiveData<APIResponse<Amenity>> getAmenityById(int amenityId) {
        MutableLiveData<APIResponse<Amenity>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<Amenity> response = mApiManager.getAmenityById(amenityId);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<Amenity>> createAmenity(Uri icon, String title, String description) {
        MutableLiveData<APIResponse<Amenity>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<Amenity> response = mApiManager.createAmenity(icon, title, description);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<Amenity>> updateAmenity(int amenityId, Uri icon, String title, String description) {
        MutableLiveData<APIResponse<Amenity>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<Amenity> response = mApiManager.updateAmenity(amenityId, icon, title, description);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<Amenity>> deleteAmenityById(int amenityId) {
        MutableLiveData<APIResponse<Amenity>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<Amenity> response = mApiManager.deleteAmenityById(amenityId);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

}