package ke.co.tonyoa.mahao.ui.properties.single;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import ke.co.tonyoa.mahao.app.MahaoApplication;
import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.responses.Property;
import ke.co.tonyoa.mahao.app.api.responses.PropertyCategory;
import ke.co.tonyoa.mahao.app.repositories.PropertyCategoriesRepository;

public class EditPropertyViewModel extends AndroidViewModel {

    private MutableLiveData<Uri> mThumbnailUri = new MutableLiveData<>();
    private MutableLiveData<PropertyCategory> mSelectedPropertyCategory = new MutableLiveData<>();
    private MutableLiveData<Property> mPropertyMutableLiveData = new MutableLiveData<>();
    @Inject
    PropertyCategoriesRepository mPropertyCategoriesRepository;

    public EditPropertyViewModel(@NonNull Application application, Property property) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
        mPropertyMutableLiveData.postValue(property);
    }

    public void setThumbnailUri(Uri uri){
        mThumbnailUri.postValue(uri);
    }

    public LiveData<Uri> getThumbnailUri(){
        return mThumbnailUri;
    }

    public LiveData<APIResponse<List<PropertyCategory>>> getPropertyCategories(){
        return mPropertyCategoriesRepository.getPropertyCategories();
    }

    public void setSelectedPropertyCategory(PropertyCategory propertyCategory){
        mSelectedPropertyCategory.postValue(propertyCategory);
    }

    public LiveData<PropertyCategory> getSelectedPropertyCategory(){
        return mSelectedPropertyCategory;
    }

    public LiveData<Property> getProperty() {
        return mPropertyMutableLiveData;
    }

    public void setProperty(Property property){
        mPropertyMutableLiveData.postValue(property);
    }

    public void setLocation(String location, LatLng coordinates){
        Property property = mPropertyMutableLiveData.getValue();
        if (property==null)
            property = new Property();
        property.setLocationName(location);
        property.setLocation(Arrays.asList((float)coordinates.latitude, (float)coordinates.longitude));
        mPropertyMutableLiveData.postValue(property);
    }
}
