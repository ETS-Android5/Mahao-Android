package ke.co.tonyoa.mahao.app.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.ApiManager;
import ke.co.tonyoa.mahao.app.api.responses.LoginResponse;
import ke.co.tonyoa.mahao.app.api.responses.PasswordRecoveryResponse;
import ke.co.tonyoa.mahao.app.api.responses.ResetPasswordResponse;
import ke.co.tonyoa.mahao.app.api.responses.User;
import ke.co.tonyoa.mahao.app.sharedprefs.SharedPrefs;

@Singleton
public class UsersRepository {
    private final ApiManager apiManager;
    private final SharedPrefs mSharedPrefs;
    private Application mContext;

    @Inject
    public UsersRepository(ApiManager apiManager, SharedPrefs sharedPrefs, Application context) {
        this.apiManager = apiManager;
        this.mSharedPrefs = sharedPrefs;
        mContext = context;
    }

    public LiveData<APIResponse<List<User>>> getUsers() {
        MutableLiveData<APIResponse<List<User>>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<List<User>> response = apiManager.getUsers(0, 5000);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<User>> getUserById(int userId) {
        MutableLiveData<APIResponse<User>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<User> response = apiManager.getUserById(userId);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

}