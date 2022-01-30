package ke.co.tonyoa.mahao.app.repositories;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.ApiManager;
import ke.co.tonyoa.mahao.app.api.responses.Amenity;
import ke.co.tonyoa.mahao.app.sharedprefs.SharedPrefs;

@Singleton
public class AmenitiesRepository {
    private final ApiManager apiManager;
    private final SharedPrefs mSharedPrefs;
    private Application mContext;

    @Inject
    public AmenitiesRepository(ApiManager apiManager, SharedPrefs sharedPrefs, Application context) {
        this.apiManager = apiManager;
        this.mSharedPrefs = sharedPrefs;
        mContext = context;
    }

    public LiveData<APIResponse<List<Amenity>>> getAmenities() {
        MutableLiveData<APIResponse<List<Amenity>>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<List<Amenity>> response = apiManager.getAmenities(1, 5000);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<Amenity>> getAmenityById(int amenityId) {
        MutableLiveData<APIResponse<Amenity>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<Amenity> response = apiManager.getAmenityById(amenityId);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<Amenity>> createAmenity(Uri icon, String title, String description) {
        MutableLiveData<APIResponse<Amenity>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<Amenity> response = apiManager.createAmenity(icon, title, description);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<Amenity>> updateAmenity(int amenityId, Uri icon, String title, String description) {
        MutableLiveData<APIResponse<Amenity>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<Amenity> response = apiManager.updateAmenity(amenityId, icon, title, description);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<Amenity>> deleteAmenityById(int amenityId) {
        MutableLiveData<APIResponse<Amenity>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<Amenity> response = apiManager.deleteAmenityById(amenityId);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

}