package ke.co.tonyoa.mahao.app.repositories;

import android.app.Application;

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
import ke.co.tonyoa.mahao.app.api.responses.User;
import ke.co.tonyoa.mahao.app.paging.RepoDataSource;
import ke.co.tonyoa.mahao.app.paging.RepoResult;
import ke.co.tonyoa.mahao.app.paging.UsersRepoDataSource;
import ke.co.tonyoa.mahao.app.sharedprefs.SharedPrefs;

@Singleton
public class UsersRepository {
    private final ApiManager mApiManager;
    private final SharedPrefs mSharedPrefs;
    private final Application mContext;

    @Inject
    public UsersRepository(ApiManager apiManager, SharedPrefs sharedPrefs, Application context) {
        this.mApiManager = apiManager;
        this.mSharedPrefs = sharedPrefs;
        mContext = context;
    }

    public LiveData<APIResponse<List<User>>> getUsers() {
        MutableLiveData<APIResponse<List<User>>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<List<User>> response = mApiManager.getUsers(1, 5000);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }


    public LiveData<RepoResult<User>> getUsersPaged(){
        UsersRepoDataSource.UsersRepoDataSourceFactory usersRepoDataSourceFactory =
                new UsersRepoDataSource.UsersRepoDataSourceFactory(mContext, mApiManager);
        LiveData<PagedList<User>> pagedListLiveData = new LivePagedListBuilder<>(usersRepoDataSourceFactory,
                RepoDataSource.getDefaultPagedListConfig())
                .setFetchExecutor(Executors.newFixedThreadPool(2))
                .build();
        MutableLiveData<RepoResult<User>> repoResultMutableLiveData = new MutableLiveData<>();
        RepoResult<User> repoResult = new RepoResult<>(pagedListLiveData,
                Transformations.switchMap(usersRepoDataSourceFactory.getUsersRepoDataSource(),
                        UsersRepoDataSource::getLoadState),
                Transformations.switchMap(usersRepoDataSourceFactory.getUsersRepoDataSource(),
                        UsersRepoDataSource::getNetworkErrors));
        repoResultMutableLiveData.postValue(repoResult);
        return repoResultMutableLiveData;
    }

    public LiveData<APIResponse<User>> getUserById(int userId) {
        MutableLiveData<APIResponse<User>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<User> response = mApiManager.getUserById(userId);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

}