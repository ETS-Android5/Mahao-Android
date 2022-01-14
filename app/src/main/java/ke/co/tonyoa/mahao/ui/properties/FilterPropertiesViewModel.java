package ke.co.tonyoa.mahao.ui.properties;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import javax.inject.Inject;

import ke.co.tonyoa.mahao.app.MahaoApplication;
import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.responses.Amenity;
import ke.co.tonyoa.mahao.app.api.responses.PropertyCategory;
import ke.co.tonyoa.mahao.app.repositories.AmenitiesRepository;
import ke.co.tonyoa.mahao.app.repositories.PropertyCategoriesRepository;

public class FilterPropertiesViewModel extends AndroidViewModel {

    @Inject
    PropertyCategoriesRepository mPropertyCategoriesRepository;
    @Inject
    AmenitiesRepository mAmenitiesRepository;
    private String mFilterLocation;
    private LatLng mFilterCoordinates;

    public FilterPropertiesViewModel(@NonNull Application application) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
    }

    public LiveData<APIResponse<List<PropertyCategory>>> getPropertyCategories(){
        return mPropertyCategoriesRepository.getPropertyCategories();
    }

    public LiveData<APIResponse<List<Amenity>>> getAmenities(){
        return mAmenitiesRepository.getAmenities();
    }

    public String getFilterLocation() {
        return mFilterLocation;
    }

    public LatLng getFilterCoordinates() {
        return mFilterCoordinates;
    }

    public void setFilterLocation(String filterLocation) {
        mFilterLocation = filterLocation;
    }

    public void setFilterCoordinates(LatLng filterCoordinates) {
        mFilterCoordinates = filterCoordinates;
    }
}
