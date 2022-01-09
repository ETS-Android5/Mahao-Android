package ke.co.tonyoa.mahao.ui.profile.amenities;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

import ke.co.tonyoa.mahao.app.MahaoApplication;
import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.responses.Amenity;
import ke.co.tonyoa.mahao.app.repositories.AmenitiesRepository;

public class AmenitiesViewModel extends AndroidViewModel {

    @Inject
    AmenitiesRepository mAmenitiesRepository;

    public AmenitiesViewModel(@NonNull Application application) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
    }

    public LiveData<APIResponse<List<Amenity>>> getAmenities(){
        return mAmenitiesRepository.getAmenities();
    }
}
