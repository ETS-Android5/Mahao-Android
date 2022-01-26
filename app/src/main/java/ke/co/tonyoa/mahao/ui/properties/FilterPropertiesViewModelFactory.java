package ke.co.tonyoa.mahao.ui.properties;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class FilterPropertiesViewModelFactory implements ViewModelProvider.Factory {

    private Application mApplication;
    private FilterPropertiesFragment.PropertyFilter mPropertyFilter;

    public FilterPropertiesViewModelFactory(Application application, FilterPropertiesFragment.PropertyFilter propertyFilter){
        mApplication = application;
        mPropertyFilter = propertyFilter;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) new FilterPropertiesViewModel(mApplication, mPropertyFilter);
    }
}
