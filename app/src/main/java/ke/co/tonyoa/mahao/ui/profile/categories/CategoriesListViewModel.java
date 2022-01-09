package ke.co.tonyoa.mahao.ui.profile.categories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

import ke.co.tonyoa.mahao.app.MahaoApplication;
import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.responses.PropertyCategory;
import ke.co.tonyoa.mahao.app.repositories.PropertyCategoriesRepository;

public class CategoriesListViewModel extends AndroidViewModel {

    @Inject
    PropertyCategoriesRepository mPropertyCategoriesRepository;

    public CategoriesListViewModel(@NonNull Application application) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
    }

    public LiveData<APIResponse<List<PropertyCategory>>> getPropertyCategories(){
        return mPropertyCategoriesRepository.getPropertyCategories();
    }
}
