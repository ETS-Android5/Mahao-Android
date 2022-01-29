package ke.co.tonyoa.mahao.ui.common;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class PropertyMapViewModelFactory implements ViewModelProvider.Factory {

    private Application mApplication;
    private PropertyMapViewModel.LatLngRadius mLatLngRadius;

    public PropertyMapViewModelFactory(Application application, PropertyMapViewModel.LatLngRadius latLngRadius){
        mApplication = application;
        mLatLngRadius = latLngRadius;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) new PropertyMapViewModel(mApplication, mLatLngRadius);
    }
}
