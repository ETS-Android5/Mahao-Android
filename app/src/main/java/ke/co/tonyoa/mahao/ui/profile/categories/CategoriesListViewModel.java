package ke.co.tonyoa.mahao.ui.profile.categories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.PagedList;

import javax.inject.Inject;

import ke.co.tonyoa.mahao.app.MahaoApplication;
import ke.co.tonyoa.mahao.app.api.responses.PropertyCategory;
import ke.co.tonyoa.mahao.app.paging.RepoResult;
import ke.co.tonyoa.mahao.app.repositories.PropertyCategoriesRepository;

public class CategoriesListViewModel extends AndroidViewModel {

    @Inject
    PropertyCategoriesRepository mPropertyCategoriesRepository;
    private final LiveData<RepoResult<PropertyCategory>> mRepoResult;
    private final LiveData<PagedList<PropertyCategory>> mCategoryList;
    private final LiveData<Integer> mLoadState;
    private final LiveData<String> mErrors;

    public CategoriesListViewModel(@NonNull Application application) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
        mRepoResult =  mPropertyCategoriesRepository.getPropertyCategoriesPaged();
        mCategoryList = Transformations.switchMap(mRepoResult, RepoResult::getData);
        mLoadState = Transformations.switchMap(mRepoResult, RepoResult::getLoadState);
        mErrors = Transformations.switchMap(mRepoResult, RepoResult::getNetworkErrors);
    }

    public LiveData<PagedList<PropertyCategory>> getPropertyCategories(){
        return mCategoryList;
    }

    public LiveData<Integer> getLoadState() {
        return mLoadState;
    }

    public LiveData<String> getErrors() {
        return mErrors;
    }

    public void invalidateList(){
        PagedList<PropertyCategory> value = mCategoryList.getValue();
        if (value != null) {
            value.getDataSource().invalidate();
        }
    }
}
