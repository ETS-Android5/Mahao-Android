package ke.co.tonyoa.mahao.app.repositories;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.ApiManager;
import ke.co.tonyoa.mahao.app.api.responses.FavoriteResponse;
import ke.co.tonyoa.mahao.app.api.responses.Feedback;
import ke.co.tonyoa.mahao.app.api.responses.ModifyAmenitiesResponse;
import ke.co.tonyoa.mahao.app.api.responses.Property;
import ke.co.tonyoa.mahao.app.api.responses.PropertyPhoto;
import ke.co.tonyoa.mahao.app.enums.FeedbackType;
import ke.co.tonyoa.mahao.app.enums.SortBy;
import ke.co.tonyoa.mahao.app.sharedprefs.SharedPrefs;

@Singleton
public class PropertiesRepository {
    private final ApiManager apiManager;
    private final SharedPrefs mSharedPrefs;
    private Application mContext;

    @Inject
    public PropertiesRepository(ApiManager apiManager, SharedPrefs sharedPrefs, Application context) {
        this.apiManager = apiManager;
        this.mSharedPrefs = sharedPrefs;
        mContext = context;
    }

    public LiveData<APIResponse<List<Property>>> getProperties(SortBy sortBy, LatLng latLngSort,
                                                               String query, Integer minBed, Integer maxBed, Integer minBath,
                                                               Integer maxBath, Float minPrice, Float maxPrice,
                                                               LatLng latLngFilter, Integer filterRadius, Boolean isVerified,
                                                               Boolean isEnabled, List<Integer> categories, List<Integer> amenities) {
        MutableLiveData<APIResponse<List<Property>>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<List<Property>> response = apiManager.getProperties(0, 5000,
                        sortBy, latLngSort, query, minBed, maxBed, minBath, maxBath, minPrice, maxPrice,
                        latLngFilter, filterRadius, isVerified, isEnabled, categories, amenities);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<List<Property>>> getUserProperties() {
        MutableLiveData<APIResponse<List<Property>>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<List<Property>> response = apiManager.getUserProperties(0, 5000);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<List<Property>>> getFavoriteProperties() {
        MutableLiveData<APIResponse<List<Property>>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<List<Property>> response = apiManager.getFavoriteProperties(0, 5000);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<List<Property>>> getLatestProperties(Integer category) {
        MutableLiveData<APIResponse<List<Property>>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<List<Property>> response = apiManager.getLatestProperties(category, 0, 5000);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<List<Property>>> getPopularProperties(Integer category) {
        MutableLiveData<APIResponse<List<Property>>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<List<Property>> response = apiManager.getPopularProperties(category, 0, 5000);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<List<Property>>> getRecommendedProperties(Integer category) {
        MutableLiveData<APIResponse<List<Property>>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<List<Property>> response = apiManager.getRecommendedProperties(category, 0, 5000);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<List<Property>>> getSimilarProperties(int propertyId, Integer category) {
        MutableLiveData<APIResponse<List<Property>>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<List<Property>> response = apiManager.getSimilarProperties(propertyId,
                        category, 0, 5000);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<Property>> getPropertyById(int propertyId) {
        MutableLiveData<APIResponse<Property>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<Property> response = apiManager.getPropertyById(propertyId);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<Property>> createProperty(Uri featureImage, int propertyCategoryId, String title,
                                                          String description, int numBed, int numBath, String locationName,
                                                          float price, LatLng latLng, boolean isEnabled, boolean isVerified) {
        MutableLiveData<APIResponse<Property>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<Property> response = apiManager.createProperty(featureImage, propertyCategoryId,
                        title, description, numBed, numBath, locationName, price, latLng, isEnabled,
                        isVerified);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<Property>> updateProperty(int propertyId, Uri featureImage,
                                                          int propertyCategoryId, String title,
                                                          String description, int numBed, int numBath,
                                                          String locationName, float price, LatLng latLng,
                                                          boolean isEnabled, boolean isVerified) {
        MutableLiveData<APIResponse<Property>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<Property> response = apiManager.updateProperty(propertyId, featureImage,
                        propertyCategoryId, title, description, numBed, numBath, locationName,
                        price, latLng, isEnabled, isVerified);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<Property>> deleteProperty(int propertyId) {
        MutableLiveData<APIResponse<Property>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<Property> response = apiManager.deletePropertyById(propertyId);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<ModifyAmenitiesResponse>> modifyPropertyAmenities(int propertyId,
                                                                                  List<Integer> added,
                                                                                  List<Integer> removed){
        MutableLiveData<APIResponse<ModifyAmenitiesResponse>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<ModifyAmenitiesResponse> response = apiManager.modifyPropertyAmenities(propertyId,
                        added, removed);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<FavoriteResponse>> addFavorite(int propertyId) {
        MutableLiveData<APIResponse<FavoriteResponse>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<FavoriteResponse> response = apiManager.addFavorite(propertyId);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<FavoriteResponse>> removeFavorite(int propertyId) {
        MutableLiveData<APIResponse<FavoriteResponse>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<FavoriteResponse> response = apiManager.removeFavorite(propertyId);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<Feedback>> addFeedback(int propertyId, FeedbackType feedbackType) {
        MutableLiveData<APIResponse<Feedback>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<Feedback> response = apiManager.addFeedback(propertyId, feedbackType);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<List<PropertyPhoto>>> addPropertyPhotos(int propertyId, List<Uri> photos) {
        MutableLiveData<APIResponse<List<PropertyPhoto>>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<List<PropertyPhoto>> response = apiManager.addPropertyPhotos(propertyId, photos);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<PropertyPhoto>> removePropertyPhoto(int propertyId, int propertyPhotoId) {
        MutableLiveData<APIResponse<PropertyPhoto>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<PropertyPhoto> response = apiManager.removePropertyPhoto(propertyId, propertyPhotoId);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

}