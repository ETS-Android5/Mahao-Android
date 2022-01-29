package ke.co.tonyoa.mahao.ui.profile.profile;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import javax.inject.Inject;

import ke.co.tonyoa.mahao.app.MahaoApplication;
import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.responses.User;
import ke.co.tonyoa.mahao.app.repositories.AuthRepository;

public class EditProfileViewModel extends AndroidViewModel {

    @Inject
    AuthRepository mAuthRepository;
    private final LiveData<APIResponse<User>> mUser;

    public EditProfileViewModel(@NonNull Application application) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
        mUser = mAuthRepository.getUser();
    }

    public LiveData<APIResponse<User>> getProfile(){
        return mUser;
    }

    public LiveData<APIResponse<User>> updateProfile(String firstName, String lastName, String phone, String location){
        return mAuthRepository.updateUser(firstName, lastName, phone, location, null);
    }

    public LiveData<APIResponse<User>> updateProfilePicture(Uri profilePicture){
        return mAuthRepository.updateProfilePicture(profilePicture);
    }
}
