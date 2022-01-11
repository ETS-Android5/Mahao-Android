package ke.co.tonyoa.mahao.ui.profile.users;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

import ke.co.tonyoa.mahao.app.MahaoApplication;
import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.responses.User;
import ke.co.tonyoa.mahao.app.repositories.UsersRepository;

public class UsersListViewModel extends AndroidViewModel {

    @Inject
    UsersRepository mUsersRepository;

    public UsersListViewModel(@NonNull Application application) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);
    }

    public LiveData<APIResponse<List<User>>> getUsers(){
        return mUsersRepository.getUsers();
    }
}
