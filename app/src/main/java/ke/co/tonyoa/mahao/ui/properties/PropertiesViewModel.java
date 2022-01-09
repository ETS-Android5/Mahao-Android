package ke.co.tonyoa.mahao.ui.properties;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import ke.co.tonyoa.mahao.app.MahaoApplication;

public class PropertiesViewModel extends AndroidViewModel {

    private MutableLiveData<Integer> mSelectedPosition=new MutableLiveData<>();

    public PropertiesViewModel(@NonNull Application application) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
        mSelectedPosition.setValue(1);
    }

    public void setSelectedPosition(int position){
        mSelectedPosition.postValue(position);
    }

    public LiveData<Integer> getSelectedPosition(){
        return mSelectedPosition;
    }

}
