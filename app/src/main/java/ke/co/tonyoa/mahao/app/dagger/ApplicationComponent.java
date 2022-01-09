package ke.co.tonyoa.mahao.app.dagger;

import javax.inject.Singleton;

import dagger.Component;
import ke.co.tonyoa.mahao.ui.auth.forgot.ForgotPasswordViewModel;
import ke.co.tonyoa.mahao.ui.auth.login.LoginViewModel;
import ke.co.tonyoa.mahao.ui.auth.register.RegisterViewModel;
import ke.co.tonyoa.mahao.ui.home.HomeViewModel;
import ke.co.tonyoa.mahao.ui.main.MainViewModel;
import ke.co.tonyoa.mahao.ui.profile.ProfileViewModel;
import ke.co.tonyoa.mahao.ui.properties.PropertiesListViewModel;
import ke.co.tonyoa.mahao.ui.properties.PropertiesViewModel;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {
    void inject(MainViewModel mainViewModel);

    void inject(LoginViewModel loginViewModel);

    void inject(RegisterViewModel registerViewModel);

    void inject(ForgotPasswordViewModel forgotPasswordViewModel);

    void inject(HomeViewModel homeViewModel);

    void inject(PropertiesListViewModel propertiesListViewModel);

    void inject(PropertiesViewModel propertiesViewModel);

    void inject(ProfileViewModel profileViewModel);
}
