package ke.co.tonyoa.mahao.ui.properties;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class PropertiesListViewModelFactory implements ViewModelProvider.Factory {

    private Application mApplication;
    private PropertiesListFragment.PropertyListType mPropertyListType;

    public PropertiesListViewModelFactory(@NonNull Application application, PropertiesListFragment.PropertyListType propertyListType){
        mApplication = application;
        mPropertyListType = propertyListType;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) new PropertiesListViewModel(mApplication, mPropertyListType);
    }
}
