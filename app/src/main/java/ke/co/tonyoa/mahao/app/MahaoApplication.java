package ke.co.tonyoa.mahao.app;

import android.app.Application;

import ke.co.tonyoa.mahao.app.dagger.ApplicationComponent;
import ke.co.tonyoa.mahao.app.dagger.ApplicationModule;
import ke.co.tonyoa.mahao.app.dagger.DaggerApplicationComponent;

public class MahaoApplication extends Application {

    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public static MahaoApplication getInstance(Application application) {
        return (MahaoApplication) application;
    }

    public ApplicationComponent getApplicationComponent(){
        return mApplicationComponent;
    }
}
