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
import ke.co.tonyoa.mahao.app.api.responses.FavoriteResponse;
import ke.co.tonyoa.mahao.app.api.responses.ModifyAmenitiesResponse;
import ke.co.tonyoa.mahao.app.api.responses.Property;
import ke.co.tonyoa.mahao.app.api.responses.PropertyPhoto;
import ke.co.tonyoa.mahao.app.enums.FeedbackType;
import ke.co.tonyoa.mahao.app.repositories.PropertiesRepository;
import ke.co.tonyoa.mahao.app.sharedprefs.SharedPrefs;

public class SinglePropertyViewModel extends AndroidViewModel {

    @Inject
    PropertiesRepository mPropertiesRepository;
    @Inject
    SharedPrefs mSharedPrefs;
    private MutableLiveData<Integer> mSelectedPosition=new MutableLiveData<>();

    public SinglePropertyViewModel(@NonNull Application application) {
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

    public LiveData<APIResponse<Property>> saveProperty(Integer propertyId, Uri featureImage, int propertyCategoryId, String title,
                                                        String description, int numBed, int numBath,
                                                        String locationName, float price, float latitude,
                                                        float longitude, boolean isEnabled, boolean isVerified){
        if (propertyId == null){
            return mPropertiesRepository.createProperty(featureImage, propertyCategoryId, title, description,
                    numBed, numBath, locationName, price, new LatLng(latitude, longitude),
                    isEnabled, isVerified);
        }
        else {
            return mPropertiesRepository.updateProperty(propertyId, featureImage, propertyCategoryId, title, description,
                    numBed, numBath, locationName, price, new LatLng(latitude, longitude),
                    isEnabled, isVerified);
        }
    }

    public LiveData<APIResponse<List<PropertyPhoto>>> addPropertyPhotos(int propertyId, List<Uri> photos){
        return mPropertiesRepository.addPropertyPhotos(propertyId, photos);
    }

    public LiveData<APIResponse<Property>> deleteProperty(int propertyId){
        return mPropertiesRepository.deleteProperty(propertyId);
    }

    public LiveData<APIResponse<PropertyPhoto>> removePropertyPhoto(int propertyId, int propertyPhotoId){
        return mPropertiesRepository.removePropertyPhoto(propertyId, propertyPhotoId);
    }

    public LiveData<APIResponse<FavoriteResponse>> addFavorite(boolean isFavorite, int propertyId){
        if (isFavorite) {
            return mPropertiesRepository.addFavorite(propertyId);
        }
        else {
            return mPropertiesRepository.removeFavorite(propertyId);
        }
    }

    public LiveData<APIResponse<List<Property>>> getSimilarProperties(int propertyId){
        return mPropertiesRepository.getSimilarProperties(propertyId, null, 0, 10);
    }

    public void addFeedback(int propertyId, FeedbackType feedbackType){
        mPropertiesRepository.addFeedback(propertyId, feedbackType);
    }

    public boolean isAdmin(){
        return mSharedPrefs.isAdmin();
    }

    public String getUserId(){
        return mSharedPrefs.getUserId();
    }
}
