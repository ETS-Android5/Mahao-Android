package ke.co.tonyoa.mahao.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.databinding.FragmentMainBinding;
import ke.co.tonyoa.mahao.ui.auth.login.LoginFragment;
import ke.co.tonyoa.mahao.ui.home.HomeFragment;
import ke.co.tonyoa.mahao.ui.more.MoreFragment;
import ke.co.tonyoa.mahao.ui.profile.ProfileFragment;
import ke.co.tonyoa.mahao.ui.properties.PropertiesFragment;

public class MainFragment extends BaseFragment {

    private FragmentMainBinding mFragmentMainBinding;
    private MainViewModel mMainViewModel;
    private HomeFragment.OnShowMoreClickListener mOnShowMoreClickListener = new HomeFragment.OnShowMoreClickListener() {
        @Override
        public void onShowMore(int position) {
            Fragment fragmentByTag = getChildFragmentManager().findFragmentByTag("f1");
            mMainViewModel.setSelectedPosition(1);
            mFragmentMainBinding.bottomNavigationMain.getMenu().findItem(R.id.navigation_properties).setChecked(true);
            if (fragmentByTag instanceof PropertiesFragment){
                ((PropertiesFragment)fragmentByTag).setCurrentItem(position);
            }
            //If fragment is yet to be created wait for one second before trying again
            else if (fragmentByTag == null){
                mFragmentMainBinding.viewpagerMain.postDelayed(()->{
                    onShowMore(position);
                }, 1000);
            }
        }
    };

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentMainBinding = FragmentMainBinding.inflate(inflater, container, false);
        mFragmentMainBinding.bottomNavigationMain.setBackground(null);

        NavBackStackEntry navBackStackEntry = getNavController().getCurrentBackStackEntry();
        if (navBackStackEntry!=null) {
            SavedStateHandle savedStateHandle = navBackStackEntry.getSavedStateHandle();
            savedStateHandle.getLiveData(LoginFragment.LOGIN_SUCCESSFUL)
                    .observe(navBackStackEntry, success -> {
                        //If user presses back and did not successfully log in, close the app
                        if (!(Boolean) success) {
                            requireActivity().finish();
                        }
                    });
        }

        mFragmentMainBinding.viewpagerMain.setUserInputEnabled(false);
        MainFragmentAdapter mainFragmentAdapter= new MainFragmentAdapter(this);
        mFragmentMainBinding.viewpagerMain.setAdapter(mainFragmentAdapter);

        mMainViewModel.getSelectedPosition().observe(getViewLifecycleOwner(),position->{
            mFragmentMainBinding.viewpagerMain.setCurrentItem(position);
        });

        mMainViewModel.getTokenLive().observe(getViewLifecycleOwner(), token->{
            if (token == null){
                navigate(MainFragmentDirections.actionNavigationMainToNavigationLogin());
            }
        });

        mFragmentMainBinding.bottomNavigationMain.setOnItemSelectedListener(item -> {
            int position=0;
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                position = 0;
            } else if (itemId == R.id.navigation_properties) {
                position = 1;
            } else if (itemId == R.id.navigation_profile) {
                position = 2;
            } else if (itemId == R.id.navigation_about) {
                position = 3;
            }
            mMainViewModel.setSelectedPosition(position);
            return true;
        });

        mFragmentMainBinding.viewpagerMain.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                requireActivity().invalidateOptionsMenu();
            }
        });
        mFragmentMainBinding.floatingActionButtonMain.setOnClickListener(v->{
            navigate(MainFragmentDirections.actionNavigationMainToSinglePropertyFragment(null));
        });

        return mFragmentMainBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMainViewModel.getUserProfile().observe(getViewLifecycleOwner(), userAPIResponse->{
            if (userAPIResponse == null || !userAPIResponse.isSuccessful()){
                if (isAdded() && isVisible()) {
                    navigate(MainFragmentDirections.actionNavigationMainToNavigationLogin());
                }
            }
        });
    }

    class MainFragmentAdapter extends FragmentStateAdapter {

        public MainFragmentAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position){
                case 0:
                    return HomeFragment.newInstance(mOnShowMoreClickListener);
                case 1:
                    return new PropertiesFragment();
                case 2:
                    return new ProfileFragment();
                case 3:
                    return new MoreFragment();
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }
}