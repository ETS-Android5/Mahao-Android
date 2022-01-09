package ke.co.tonyoa.mahao.ui.auth.forgot;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import javax.inject.Inject;

import ke.co.tonyoa.mahao.app.MahaoApplication;
import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.responses.PasswordRecoveryResponse;
import ke.co.tonyoa.mahao.app.repositories.AuthRepository;

public class ForgotPasswordViewModel extends AndroidViewModel {

    @Inject
    AuthRepository mAuthRepository;

    public ForgotPasswordViewModel(@NonNull Application application) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
    }

    public LiveData<APIResponse<PasswordRecoveryResponse>> recoverPassword(String email){
        return mAuthRepository.recoverPassword(email);
    }
}
