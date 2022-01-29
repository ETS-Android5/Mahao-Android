package ke.co.tonyoa.mahao.app.repositories;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;

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
public class AuthRepository {
    private final ApiManager apiManager;
    private final SharedPrefs mSharedPrefs;
    private Application mContext;

    @Inject
    public AuthRepository(ApiManager apiManager, SharedPrefs sharedPrefs, Application context) {
        this.apiManager = apiManager;
        this.mSharedPrefs = sharedPrefs;
        mContext = context;
    }

    public LiveData<APIResponse<LoginResponse>> login(String username, String password) {
        MutableLiveData<APIResponse<LoginResponse>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<LoginResponse> response = apiManager.login(username, password);
                if (response!=null && response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    saveLoggedInUser(loginResponse);
                }
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    private void saveLoggedInUser(LoginResponse loginResponse) throws IOException {
        if (loginResponse!=null) {
            mSharedPrefs.saveToken(loginResponse.getToken().getAccessToken());
            User user = loginResponse.getUser();
            mSharedPrefs.saveAdmin(user.getIsSuperuser());
            mSharedPrefs.saveEmail(user.getEmail());
            mSharedPrefs.savePhone(user.getPhone());
            mSharedPrefs.saveNames(user.getFirstName(), user.getLastName());
            mSharedPrefs.saveProfilePicture(user.getProfilePicture());
            mSharedPrefs.saveUserId(user.getId()+"");
        }
    }

    public LiveData<APIResponse<User>> getUser() {
        MutableLiveData<APIResponse<User>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<User> response = apiManager.getUserProfile();
                if (response!=null && !response.isSuccessful()){
                    logout();
                }
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public void logout() {
        mSharedPrefs.saveToken(null);
        mSharedPrefs.saveAdmin(false);
        mSharedPrefs.saveEmail(null);
        mSharedPrefs.savePhone(null);
        mSharedPrefs.saveNames(null, null);
        mSharedPrefs.saveProfilePicture(null);
        mSharedPrefs.saveUserId(null);
    }

    public MutableLiveData<APIResponse<ResetPasswordResponse>> resetPassword(String token, String email){
        MutableLiveData<APIResponse<ResetPasswordResponse>> liveData = new MutableLiveData<>();
        ApiManager.execute(()->{
            try {
                APIResponse<ResetPasswordResponse> response = apiManager.resetPassword(token, email);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public MutableLiveData<APIResponse<PasswordRecoveryResponse>> recoverPassword(String email){

        MutableLiveData<APIResponse<PasswordRecoveryResponse>> liveData = new MutableLiveData<>();
        ApiManager.execute(()->{
            try {
                APIResponse<PasswordRecoveryResponse> response = apiManager.recoverPassword(email);
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<User>> createUser(String firstName, String lastName, String email, String phone,
                                                  String location, boolean isVerified, boolean isActive,
                                                  boolean isSuperUser, String password) {
        MutableLiveData<APIResponse<User>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<User> response;
                if (mSharedPrefs.getToken()!=null && mSharedPrefs.isAdmin()) {
                    response = apiManager.createUser(firstName, lastName, email, phone,
                            location, isVerified, isActive, isSuperUser, password);
                }
                else {
                    response = apiManager.register(firstName, lastName, email, phone,
                            location, isVerified, isActive, isSuperUser, password);
                }
                liveData.postValue(response);
            } catch (IOException ioException) {
                ioException.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<User>> updateUser(String firstName, String lastName, String phone,
                                                  String location, String password) {
        MutableLiveData<APIResponse<User>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<User> response = apiManager.updateUser(firstName, lastName,
                        phone, location, password);
                if (response != null && response.isSuccessful()){
                    User body = response.body();
                    mSharedPrefs.saveNames(firstName, lastName);
                    mSharedPrefs.savePhone(phone);
                    mSharedPrefs.saveProfilePicture(body.getProfilePicture());
                }
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<APIResponse<User>> updateProfilePicture(Uri profilePicture) {
        MutableLiveData<APIResponse<User>> liveData = new MutableLiveData<>();
        ApiManager.execute(() -> {
            try {
                APIResponse<User> response = apiManager.updateProfilePicture(profilePicture);
                if (response != null && response.isSuccessful()){
                    User body = response.body();
                    mSharedPrefs.saveProfilePicture(body.getProfilePicture());
                }
                liveData.postValue(response);
            } catch (IOException e) {
                e.printStackTrace();
                liveData.postValue(null);
            }
        });
        return liveData;
    }

}