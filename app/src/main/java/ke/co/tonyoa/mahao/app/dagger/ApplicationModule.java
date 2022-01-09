package ke.co.tonyoa.mahao.app.dagger;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private Application mApplication;

    public ApplicationModule(Application application){
        mApplication = application;
    }

    @Provides
    @Singleton
    public Application getApplication(){
        return mApplication;
    }
}
