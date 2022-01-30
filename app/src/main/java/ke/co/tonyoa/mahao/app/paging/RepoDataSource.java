package ke.co.tonyoa.mahao.app.paging;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;
import androidx.paging.PagedList;

import org.jetbrains.annotations.NotNull;

import ke.co.tonyoa.mahao.app.api.ApiManager;

public abstract class RepoDataSource<T> extends PageKeyedDataSource<Integer, T> {

    public static final int LOADING_ONGOING = 1;
    public static final int LOADING_SUCCESS = 2;
    public static final int LOADING_FAILED = 3;

    public static final int DEFAULT_PAGE_LIMIT = 20;
    public static final int DEFAULT_PREFETCH_DISTANCE = 10;

    protected final Context mContext;
    protected final ApiManager mApiManager;
    protected final MutableLiveData<String> mNetworkErrors;
    protected MutableLiveData<Integer> mLoadState;

    public RepoDataSource(Context context, ApiManager apiManager) {
        mContext = context;
        mApiManager = apiManager;
        mNetworkErrors = new MutableLiveData<>();
        mLoadState = new MutableLiveData<>();
    }

    public LiveData<String> getNetworkErrors() {
        return this.mNetworkErrors;
    }

    public LiveData<Integer> getLoadState(){
        return this.mLoadState;
    }

    @NotNull
    public static PagedList.Config getDefaultPagedListConfig() {
        return new PagedList.Config.Builder()
                .setInitialLoadSizeHint(DEFAULT_PAGE_LIMIT*2)
                .setPageSize(RepoDataSource.DEFAULT_PAGE_LIMIT)
                .setPrefetchDistance(DEFAULT_PREFETCH_DISTANCE)
                .setEnablePlaceholders(false)
                .build();
    }
}
