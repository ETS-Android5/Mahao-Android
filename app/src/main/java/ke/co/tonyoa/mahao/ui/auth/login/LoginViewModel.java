package ke.co.tonyoa.mahao.ui.auth.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import javax.inject.Inject;

import ke.co.tonyoa.mahao.app.MahaoApplication;
import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.responses.LoginResponse;
import ke.co.tonyoa.mahao.app.repositories.AuthRepository;

public class LoginViewModel extends AndroidViewModel {

    @Inject
    AuthRepository mAuthRepository;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
    }

    public LiveData<APIResponse<LoginResponse>> login(String email, String password){
        return mAuthRepository.login(email, password);
    }

}
