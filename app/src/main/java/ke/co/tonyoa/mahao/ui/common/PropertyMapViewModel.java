package ke.co.tonyoa.mahao.ui.common;

import static ke.co.tonyoa.mahao.ui.home.HomeViewModel.DEFAULT_COORDINATES;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import javax.inject.Inject;

import ke.co.tonyoa.mahao.app.MahaoApplication;
import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.responses.FavoriteResponse;
import ke.co.tonyoa.mahao.app.api.responses.Property;
import ke.co.tonyoa.mahao.app.enums.SortBy;
import ke.co.tonyoa.mahao.app.repositories.PropertiesRepository;

public class PropertyMapViewModel extends AndroidViewModel {

    @Inject
    PropertiesRepository mPropertiesRepository;
    private MutableLiveData<LatLngRadius> mLatLngRadiusMutableLiveData = new MutableLiveData<>();
    private LiveData<APIResponse<List<Property>>> mProperties;
    private float mLastZoom;

    public PropertyMapViewModel(@NonNull Application application, LatLngRadius latLngRadius) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
        mLatLngRadiusMutableLiveData.setValue(latLngRadius==null?new LatLngRadius((float) DEFAULT_COORDINATES.latitude,
                (float) DEFAULT_COORDINATES.longitude, 5):latLngRadius);
        mLastZoom = latLngRadius==null?12:15;
        mProperties = Transformations.switchMap(mLatLngRadiusMutableLiveData, input -> {
            LatLng latLngSort = new LatLng(input.getLat(), input.getLng());
            return mPropertiesRepository.getProperties(1, 500, SortBy.DISTANCE, latLngSort,
                    null, null, null, null, null, null, null,
                    latLngSort, (int)input.getRadius(), null, null, null, null);
        });
    }

    public LiveData<APIResponse<List<Property>>> getProperties(){
        return mProperties;
    }

    public void setLatLngRadius(float lat, float lng, float radius){
        if (radius<1)
            radius = 1;
        LatLngRadius value = mLatLngRadiusMutableLiveData.getValue();
        if (value != null) {
            if (value.getLat() == lat && value.getLng() == lng && value.getRadius() == radius)
                return;

        }
        value = new LatLngRadius(lat, lng, radius);
        mLatLngRadiusMutableLiveData.postValue(value);
    }

    public LatLngRadius getLatLngRadius() {
        LatLngRadius value = mLatLngRadiusMutableLiveData.getValue();
        return value == null?new LatLngRadius((float) DEFAULT_COORDINATES.latitude,
                (float) DEFAULT_COORDINATES.longitude, 5):value;
    }

    public LiveData<APIResponse<FavoriteResponse>> addFavorite(int propertyId) {
        return mPropertiesRepository.addFavorite(propertyId);
    }

    public LiveData<APIResponse<FavoriteResponse>> removeFavorite(int propertyId) {
        return mPropertiesRepository.removeFavorite(propertyId);
    }

    public float getLastZoom() {
        return mLastZoom;
    }

    public void setLastZoom(float lastZoom) {
        mLastZoom = lastZoom;
    }

    public static class LatLngRadius{
        private float mLat;
        private float mLng;
        private float mRadius;

        public LatLngRadius(float lat, float lng, float radius) {
            mLat = lat;
            mLng = lng;
            mRadius = radius;
        }

        public float getLat() {
            return mLat;
        }

        public void setLat(float lat) {
            mLat = lat;
        }

        public float getLng() {
            return mLng;
        }

        public void setLng(float lng) {
            mLng = lng;
        }

        public float getRadius() {
            return mRadius;
        }

        public void setRadius(float radius) {
            mRadius = radius;
        }
    }
}
