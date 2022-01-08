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
import ke.co.tonyoa.mahao.app.api.responses.PropertyCategory;
import ke.co.tonyoa.mahao.app.sharedprefs.SharedPrefs;

@Singleton
public class PropertyCategoriesRepository {
    private final ApiManager apiManager;
    private final SharedPrefs mSharedPrefs;
    private Application mContext;

    @Inject
    public PropertyCategoriesRepository(ApiManager apiManager, SharedPrefs sharedPrefs, Application context) {
        this.apiManager = apiManager;
        this.mSharedPrefs = sharedPrefs;
        mContext = context;
    }

    public LiveData<APIResponse<List<PropertyCategory>>> getPropertyCategories() {
        MutableLiveData<APIResponse<List<PropertyCategory>>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<List<PropertyCategory>> response = apiManager.getPropertyCategories(0, 5000);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<PropertyCategory>> getPropertyCategoryById(int amenityId) {
        MutableLiveData<APIResponse<PropertyCategory>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<PropertyCategory> response = apiManager.getPropertyCategoryById(amenityId);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<PropertyCategory>> createPropertyCategory(Uri icon, String title, String description) {
        MutableLiveData<APIResponse<PropertyCategory>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<PropertyCategory> response = apiManager.createPropertyCategory(icon, title, description);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<PropertyCategory>> updatePropertyCategory(int propertyCategoryId, Uri icon, String title, String description) {
        MutableLiveData<APIResponse<PropertyCategory>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<PropertyCategory> response = apiManager.updatePropertyCategory(propertyCategoryId, icon, title, description);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<PropertyCategory>> deletePropertyCategoryById(int propertyCategoryId) {
        MutableLiveData<APIResponse<PropertyCategory>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<PropertyCategory> response = apiManager.deletePropertyCategoryById(propertyCategoryId);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

}