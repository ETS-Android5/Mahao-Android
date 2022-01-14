package ke.co.tonyoa.mahao.ui.properties.single;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import javax.inject.Inject;

import ke.co.tonyoa.mahao.app.MahaoApplication;
import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.responses.Property;
import ke.co.tonyoa.mahao.app.api.responses.PropertyCategory;
import ke.co.tonyoa.mahao.app.repositories.PropertyCategoriesRepository;

public class EditPropertyViewModel extends AndroidViewModel {

    private Uri mThumbnailUri;
    private PropertyCategory mSelectedPropertyCategory;
    private String mLocationName;
    private LatLng mCoordinates;
    private MutableLiveData<Property> mPropertyMutableLiveData = new MutableLiveData<>();
    @Inject
    PropertyCategoriesRepository mPropertyCategoriesRepository;

    public EditPropertyViewModel(@NonNull Application application, Property property) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
        mPropertyMutableLiveData.postValue(property);
    }

    public void setThumbnailUri(Uri uri){
        mThumbnailUri=uri;
    }

    public Uri getThumbnailUri(){
        return mThumbnailUri;
    }

    public LiveData<APIResponse<List<PropertyCategory>>> getPropertyCategories(){
        return mPropertyCategoriesRepository.getPropertyCategories();
    }

    public void setSelectedPropertyCategory(PropertyCategory propertyCategory){
        mSelectedPropertyCategory=propertyCategory;
    }

    public PropertyCategory getSelectedPropertyCategory(){
        return mSelectedPropertyCategory;
    }

    public LiveData<Property> getProperty() {
        return mPropertyMutableLiveData;
    }

    public void setProperty(Property property){
        mPropertyMutableLiveData.postValue(property);
    }

    public void setLocation(String location, LatLng coordinates){
        mLocationName = location;
        mCoordinates = coordinates;
    }

    public String getLocationName() {
        return mLocationName;
    }

    public LatLng getCoordinates() {
        return mCoordinates;
    }
}
