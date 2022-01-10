package ke.co.tonyoa.mahao.ui.properties.single;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

import ke.co.tonyoa.mahao.app.MahaoApplication;
import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.responses.Amenity;
import ke.co.tonyoa.mahao.app.api.responses.ModifyAmenitiesResponse;
import ke.co.tonyoa.mahao.app.repositories.AmenitiesRepository;
import ke.co.tonyoa.mahao.app.repositories.PropertiesRepository;

public class PropertyAmenitySelectViewModel extends AndroidViewModel {

    @Inject
    AmenitiesRepository mAmenitiesRepository;
    @Inject
    PropertiesRepository mPropertiesRepository;

    public PropertyAmenitySelectViewModel(@NonNull Application application) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
    }

    public LiveData<APIResponse<List<Amenity>>> getAmenities(){
        return mAmenitiesRepository.getAmenities();
    }

    public LiveData<APIResponse<List<ModifyAmenitiesResponse>>> modifyAmenities(int propertyId, List<Integer> added, List<Integer> removed){
        return mPropertiesRepository.modifyPropertyAmenities(propertyId, added, removed);
    }


}
