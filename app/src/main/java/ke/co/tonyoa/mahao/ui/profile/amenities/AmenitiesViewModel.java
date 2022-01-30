package ke.co.tonyoa.mahao.ui.profile.amenities;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.PagedList;

import javax.inject.Inject;

import ke.co.tonyoa.mahao.app.MahaoApplication;
import ke.co.tonyoa.mahao.app.api.responses.Amenity;
import ke.co.tonyoa.mahao.app.paging.RepoResult;
import ke.co.tonyoa.mahao.app.repositories.AmenitiesRepository;

public class AmenitiesViewModel extends AndroidViewModel {

    @Inject
    AmenitiesRepository mAmenitiesRepository;
    private final LiveData<RepoResult<Amenity>> mRepoResult;
    private final LiveData<PagedList<Amenity>> mAmenityList;
    private final LiveData<Integer> mLoadState;
    private final LiveData<String> mErrors;

    public AmenitiesViewModel(@NonNull Application application) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
        mRepoResult = mAmenitiesRepository.getAmenitiesPaged();
        mAmenityList = Transformations.switchMap(mRepoResult, RepoResult::getData);
        mLoadState = Transformations.switchMap(mRepoResult, RepoResult::getLoadState);
        mErrors = Transformations.switchMap(mRepoResult, RepoResult::getNetworkErrors);
    }

    public LiveData<PagedList<Amenity>> getAmenities(){
        return mAmenityList;
    }

    public LiveData<Integer> getLoadState() {
        return mLoadState;
    }

    public LiveData<String> getErrors() {
        return mErrors;
    }

    public void invalidateList(){
        PagedList<Amenity> value = mAmenityList.getValue();
        if (value != null) {
            value.getDataSource().invalidate();
        }
    }
}
