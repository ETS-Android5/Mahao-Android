package ke.co.tonyoa.mahao.ui.properties;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SortPropertiesViewModelFactory implements ViewModelProvider.Factory {

    private final PropertiesViewModel.SortBy mSortBy;

    public SortPropertiesViewModelFactory(PropertiesViewModel.SortBy sortBy){
        mSortBy = sortBy;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) new SortPropertiesViewModel(mSortBy);
    }
}
