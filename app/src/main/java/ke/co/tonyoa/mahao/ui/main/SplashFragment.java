package ke.co.tonyoa.mahao.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Timer;
import java.util.TimerTask;

import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.databinding.FragmentSplashBinding;

public class SplashFragment extends BaseFragment {

    private FragmentSplashBinding mFragmentSplashBinding;
    private Timer mTimer;

    public SplashFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentSplashBinding = FragmentSplashBinding.inflate(inflater, container, false);
        return mFragmentSplashBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mTimer!=null)
            mTimer.cancel();
        mTimer = new Timer("launch");
        mTimer.schedule((new TimerTask() {
            @Override
            public void run() {
                mFragmentSplashBinding.animationView.post(()->{
                    NavDirections action = SplashFragmentDirections.actionNavigationSplashToNavigationMain();
                    navigate(action);
                });
            }
        }), 5000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        mTimer=null;
    }
}