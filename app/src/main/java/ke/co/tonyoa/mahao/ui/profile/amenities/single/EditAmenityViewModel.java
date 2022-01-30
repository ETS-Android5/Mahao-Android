package ke.co.tonyoa.mahao.ui.profile.amenities.single;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class EditAmenityViewModel extends AndroidViewModel {

    private Uri mThumbnailUri;
    private boolean mInitialLoad = true;

    public EditAmenityViewModel(@NonNull Application application) {
        super(application);
    }

    public void setThumbnailUri(Uri uri){
        mThumbnailUri = uri;
    }

    public Uri getThumbnailUri(){
        return mThumbnailUri;
    }

    public boolean isInitialLoad() {
        return mInitialLoad;
    }

    public void setInitialLoad(boolean initialLoad) {
        mInitialLoad = initialLoad;
    }
}
