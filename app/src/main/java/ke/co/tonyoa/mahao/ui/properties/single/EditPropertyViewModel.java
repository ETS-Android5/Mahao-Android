package ke.co.tonyoa.mahao.ui.properties.single;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import javax.inject.Inject;

import ke.co.tonyoa.mahao.app.MahaoApplication;
import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.responses.PropertyCategory;
import ke.co.tonyoa.mahao.app.repositories.PropertyCategoriesRepository;

public class EditPropertyViewModel extends AndroidViewModel {

    private MutableLiveData<Uri> mThumbnailUri = new MutableLiveData<>();
    private MutableLiveData<PropertyCategory> mSelectedPropertyCategory = new MutableLiveData<>();
    @Inject
    PropertyCategoriesRepository mPropertyCategoriesRepository;

    public EditPropertyViewModel(@NonNull Application application) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
    }

    public void setThumbnailUri(Uri uri){
        mThumbnailUri.postValue(uri);
    }

    public LiveData<Uri> getThumbnailUri(){
        return mThumbnailUri;
    }

    public LiveData<APIResponse<List<PropertyCategory>>> getPropertyCategories(){
        return mPropertyCategoriesRepository.getPropertyCategories();
    }

    public void setSelectedPropertyCategory(PropertyCategory propertyCategory){
        mSelectedPropertyCategory.postValue(propertyCategory);
    }

    public LiveData<PropertyCategory> getSelectedPropertyCategory(){
        return mSelectedPropertyCategory;
    }
}
