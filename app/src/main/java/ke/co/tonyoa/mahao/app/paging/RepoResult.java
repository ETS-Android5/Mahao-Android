package ke.co.tonyoa.mahao.app.paging;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

public class RepoResult<T> {

    @NonNull
    private final LiveData<PagedList<T>> mData;
    private final LiveData<Integer> mLoadState;
    private final LiveData<String> mNetworkErrors;

    public RepoResult(@NonNull LiveData<PagedList<T>> data, LiveData<Integer> loadState,
                      LiveData<String> networkErrors) {
        this.mData = data;
        this.mLoadState = loadState;
        this.mNetworkErrors = networkErrors;
    }

    @NonNull
    public final LiveData<PagedList<T>> getData() {
        return this.mData;
    }

    public final LiveData<String> getNetworkErrors() {
        return this.mNetworkErrors;
    }

    public LiveData<Integer> getLoadState() {
        return mLoadState;
    }
}
