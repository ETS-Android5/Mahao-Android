package ke.co.tonyoa.mahao.ui.properties.single;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ke.co.tonyoa.mahao.app.api.responses.Property;

public class EditPropertyViewModelFactory implements ViewModelProvider.Factory {

    private Application mApplication;
    private Property mProperty;

    public EditPropertyViewModelFactory(Application application, Property property){
        mApplication = application;
        mProperty = property;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) new EditPropertyViewModel(mApplication, mProperty);
    }
}
