package ke.co.tonyoa.mahao.ui.profile.categories.single;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class EditCategoryViewModel extends AndroidViewModel {

    private MutableLiveData<Uri> mThumbnailUri = new MutableLiveData<>();

    public EditCategoryViewModel(@NonNull Application application) {
        super(application);
    }

    public void setThumbnailUri(Uri uri){
        mThumbnailUri.postValue(uri);
    }

    public LiveData<Uri> getThumbnailUri(){
        return mThumbnailUri;
    }
}
