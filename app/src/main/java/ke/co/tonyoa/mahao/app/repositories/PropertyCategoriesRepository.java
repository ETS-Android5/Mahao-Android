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
import ke.co.tonyoa.mahao.app.api.responses.PropertyCategory;
import ke.co.tonyoa.mahao.app.paging.RepoDataSource;
import ke.co.tonyoa.mahao.app.paging.RepoResult;
import ke.co.tonyoa.mahao.app.paging.CategoriesRepoDataSource;
import ke.co.tonyoa.mahao.app.sharedprefs.SharedPrefs;

@Singleton
public class PropertyCategoriesRepository {
    private final ApiManager mApiManager;
    private final SharedPrefs mSharedPrefs;
    private final Application mContext;

    @Inject
    public PropertyCategoriesRepository(ApiManager apiManager, SharedPrefs sharedPrefs, Application context) {
        this.mApiManager = apiManager;
        this.mSharedPrefs = sharedPrefs;
        mContext = context;
    }

    public LiveData<APIResponse<List<PropertyCategory>>> getPropertyCategories() {
        MutableLiveData<APIResponse<List<PropertyCategory>>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<List<PropertyCategory>> response = mApiManager.getPropertyCategories(1, 5000);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<RepoResult<PropertyCategory>> getPropertyCategoriesPaged(){
        CategoriesRepoDataSource.PropertyCategoriesRepoDataSourceFactory propertyCategoriesRepoDataSourceFactory =
                new CategoriesRepoDataSource.PropertyCategoriesRepoDataSourceFactory(mContext, mApiManager);
        LiveData<PagedList<PropertyCategory>> pagedListLiveData = new LivePagedListBuilder<>(propertyCategoriesRepoDataSourceFactory,
                RepoDataSource.getDefaultPagedListConfig())
                .setFetchExecutor(Executors.newFixedThreadPool(2))
                .build();
        MutableLiveData<RepoResult<PropertyCategory>> repoResultMutableLiveData = new MutableLiveData<>();
        RepoResult<PropertyCategory> repoResult = new RepoResult<>(pagedListLiveData,
                Transformations.switchMap(propertyCategoriesRepoDataSourceFactory.getPropertyCategoriesRepoDataSource(),
                        CategoriesRepoDataSource::getLoadState),
                Transformations.switchMap(propertyCategoriesRepoDataSourceFactory.getPropertyCategoriesRepoDataSource(),
                        CategoriesRepoDataSource::getNetworkErrors));
        repoResultMutableLiveData.postValue(repoResult);
        return repoResultMutableLiveData;
    }

    public LiveData<APIResponse<PropertyCategory>> getPropertyCategoryById(int amenityId) {
        MutableLiveData<APIResponse<PropertyCategory>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<PropertyCategory> response = mApiManager.getPropertyCategoryById(amenityId);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<PropertyCategory>> createPropertyCategory(Uri icon, String title, String description) {
        MutableLiveData<APIResponse<PropertyCategory>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<PropertyCategory> response = mApiManager.createPropertyCategory(icon, title, description);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<PropertyCategory>> updatePropertyCategory(int propertyCategoryId, Uri icon, String title, String description) {
        MutableLiveData<APIResponse<PropertyCategory>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<PropertyCategory> response = mApiManager.updatePropertyCategory(propertyCategoryId, icon, title, description);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<PropertyCategory>> deletePropertyCategoryById(int propertyCategoryId) {
        MutableLiveData<APIResponse<PropertyCategory>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<PropertyCategory> response = mApiManager.deletePropertyCategoryById(propertyCategoryId);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

}