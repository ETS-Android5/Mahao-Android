package ke.co.tonyoa.mahao.ui.profile.amenities.single;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

import ke.co.tonyoa.mahao.app.MahaoApplication;
import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.responses.Amenity;
import ke.co.tonyoa.mahao.app.repositories.AmenitiesRepository;
import ke.co.tonyoa.mahao.app.sharedprefs.SharedPrefs;

public class SingleAmenityViewModel extends AndroidViewModel {

    @Inject
    AmenitiesRepository mAmenitiesRepository;
    @Inject
    SharedPrefs mSharedPrefs;
    private MutableLiveData<Integer> mSelectedPosition=new MutableLiveData<>();

    public SingleAmenityViewModel(@NonNull Application application) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
        mSelectedPosition.setValue(0);
    }

    public void setSelectedPosition(int position){
        mSelectedPosition.postValue(position);
    }

    public LiveData<Integer> getSelectedPosition(){
        return mSelectedPosition;
    }

    public LiveData<APIResponse<Amenity>> saveAmenity(Integer amenityId,
                                                      String title,
                                                      String description,
                                                      Uri icon){
        if (amenityId == null){
            return mAmenitiesRepository.createAmenity(icon, title, description);
        }
        else {
            return mAmenitiesRepository.updateAmenity(amenityId, icon, title, description);
        }
    }

    public LiveData<APIResponse<Amenity>> deleteAmenity(int amenityId){
        return mAmenitiesRepository.deleteAmenityById(amenityId);
    }
}
