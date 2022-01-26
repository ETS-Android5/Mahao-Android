package ke.co.tonyoa.mahao.ui.properties;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class PropertiesListViewModelFactory implements ViewModelProvider.Factory {

    private Application mApplication;
    private PropertiesListFragment.PropertyListType mPropertyListType;
    private LiveData<PropertiesViewModel.FilterAndSort> mFilterAndSortLiveData;

    public PropertiesListViewModelFactory(@NonNull Application application,
                                          PropertiesListFragment.PropertyListType propertyListType,
                                          LiveData<PropertiesViewModel.FilterAndSort> filterAndSortLiveData){
        mApplication = application;
        mPropertyListType = propertyListType;
        mFilterAndSortLiveData = filterAndSortLiveData;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) new PropertiesListViewModel(mApplication, mPropertyListType, mFilterAndSortLiveData);
    }
}
