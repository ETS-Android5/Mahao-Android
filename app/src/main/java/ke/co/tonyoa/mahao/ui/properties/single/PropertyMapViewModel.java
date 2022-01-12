package ke.co.tonyoa.mahao.ui.properties.single;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

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

    public PropertyMapViewModel(@NonNull Application application) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
    }

    public LiveData<APIResponse<List<Property>>> getProperties(float lat, float lng, float radius){
        LatLng latLngSort = new LatLng(lat, lng);
        return mPropertiesRepository.getProperties(0, 100, SortBy.DISTANCE, latLngSort,
                null, null, null, null, null, null, null,
                latLngSort, (int)radius, null, null, null, null);
    }

    public LiveData<APIResponse<FavoriteResponse>> addFavorite(int propertyId) {
        return mPropertiesRepository.addFavorite(propertyId);
    }

    public LiveData<APIResponse<FavoriteResponse>> removeFavorite(int propertyId) {
        return mPropertiesRepository.removeFavorite(propertyId);
    }
}
