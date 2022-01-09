package ke.co.tonyoa.mahao.ui.auth.register;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import javax.inject.Inject;

import ke.co.tonyoa.mahao.app.MahaoApplication;
import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.responses.User;
import ke.co.tonyoa.mahao.app.repositories.AuthRepository;

public class RegisterViewModel extends AndroidViewModel {

    @Inject
    AuthRepository mAuthRepository;

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
    }

    public LiveData<APIResponse<User>> register(String firstName, String lastName, String email, String phone,
                                                String location, String password){
        return mAuthRepository.createUser(firstName, lastName, email, phone, location, false, false,
                false, password);
    }
}
