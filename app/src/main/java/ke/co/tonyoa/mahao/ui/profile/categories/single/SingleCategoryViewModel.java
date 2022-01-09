package ke.co.tonyoa.mahao.ui.profile.categories.single;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

import ke.co.tonyoa.mahao.app.MahaoApplication;
import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.responses.PropertyCategory;
import ke.co.tonyoa.mahao.app.repositories.PropertyCategoriesRepository;
import ke.co.tonyoa.mahao.app.sharedprefs.SharedPrefs;

public class SingleCategoryViewModel extends AndroidViewModel {

    @Inject
    PropertyCategoriesRepository mPropertyCategoriesRepository;
    @Inject
    SharedPrefs mSharedPrefs;
    private MutableLiveData<Integer> mSelectedPosition=new MutableLiveData<>();

    public SingleCategoryViewModel(@NonNull Application application) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
        mSelectedPosition.setValue(0);
    }

    public void setSelectedPosition(int position){
        mSelectedPosition.postValue(position);
    }

    public LiveData<Integer> getSelectedPosition(){
        return mSelectedPosition;
    }

    public LiveData<APIResponse<PropertyCategory>> savePropertyCategory(Integer propertyCategoryId,
                                                                        String title,
                                                                        String description,
                                                                        Uri icon){
        if (propertyCategoryId == null){
            return mPropertyCategoriesRepository.createPropertyCategory(icon, title, description);
        }
        else {
            return mPropertyCategoriesRepository.updatePropertyCategory(propertyCategoryId, icon, title, description);
        }
    }

    public LiveData<APIResponse<PropertyCategory>> deletePropertyCategory(int propertyCategoryId){
        return mPropertyCategoriesRepository.deletePropertyCategoryById(propertyCategoryId);
    }
}
