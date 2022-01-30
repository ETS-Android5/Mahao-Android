package ke.co.tonyoa.mahao.ui.profile.users;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.PagedList;

import javax.inject.Inject;

import ke.co.tonyoa.mahao.app.MahaoApplication;
import ke.co.tonyoa.mahao.app.api.responses.User;
import ke.co.tonyoa.mahao.app.paging.RepoResult;
import ke.co.tonyoa.mahao.app.repositories.UsersRepository;

public class UsersListViewModel extends AndroidViewModel {

    @Inject
    UsersRepository mUsersRepository;
    private final LiveData<RepoResult<User>> mRepoResult;
    private final LiveData<PagedList<User>> mUserList;
    private final LiveData<Integer> mLoadState;
    private final LiveData<String> mErrors;

    public UsersListViewModel(@NonNull Application application) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
        mRepoResult =  mUsersRepository.getUsersPaged();
        mUserList = Transformations.switchMap(mRepoResult, RepoResult::getData);
        mLoadState = Transformations.switchMap(mRepoResult, RepoResult::getLoadState);
        mErrors = Transformations.switchMap(mRepoResult, RepoResult::getNetworkErrors);
    }

    public LiveData<PagedList<User>> getUsers(){
        return mUserList;
    }


    public LiveData<Integer> getLoadState() {
        return mLoadState;
    }

    public LiveData<String> getErrors() {
        return mErrors;
    }

    public void invalidateList(){
        PagedList<User> value = mUserList.getValue();
        if (value != null) {
            value.getDataSource().invalidate();
        }
    }
}
