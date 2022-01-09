package ke.co.tonyoa.mahao.ui.properties;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayoutMediator;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.databinding.FragmentPropertiesBinding;

public class PropertiesFragment extends BaseFragment {

    private FragmentPropertiesBinding mFragmentPropertiesBinding;
    private PropertiesViewModel mPropertiesViewModel;

    public PropertiesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPropertiesViewModel = new ViewModelProvider(this).get(PropertiesViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentPropertiesBinding = FragmentPropertiesBinding.inflate(inflater, container, false);

        mFragmentPropertiesBinding.viewPagerProperties.setUserInputEnabled(false);
        PropertiesFragmentAdapter propertiesFragmentAdapter = new PropertiesFragmentAdapter(this);
        mFragmentPropertiesBinding.viewPagerProperties.setAdapter(propertiesFragmentAdapter);
        mPropertiesViewModel.getSelectedPosition().observe(getViewLifecycleOwner(),position->{
            mFragmentPropertiesBinding.viewPagerProperties.setCurrentItem(position);
            propertiesFragmentAdapter.notifyDataSetChanged();
        });
        new TabLayoutMediator(mFragmentPropertiesBinding.tabLayoutProperties, mFragmentPropertiesBinding.viewPagerProperties,
                (tab, position) -> {
                    String title = "All";
                    int icon = R.drawable.ic_home_black_24dp;
                    switch (position){
                        case 0:
                            title = "All";
                            icon = R.drawable.ic_home_black_24dp;
                            break;
                        case 1:
                            title = "Recommended";
                            icon = R.drawable.ic_baseline_thumb_up_24;
                            break;
                        case 2:
                            title = "Nearby";
                            icon = R.drawable.ic_baseline_location_on_24;
                            break;
                        case 3:
                            title = "Latest";
                            icon = R.drawable.ic_baseline_access_time_24;
                            break;
                        case 4:
                            title = "Popular";
                            icon = R.drawable.ic_baseline_trending_up_24;
                            break;
                        case 5:
                            title = "Favorite";
                            icon = R.drawable.ic_baseline_favorite_24;
                            break;
                        case 6:
                            title = "Mine";
                            icon = R.drawable.ic_baseline_person_24;
                            break;

                    }
                    tab.setText(title);
                    tab.setIcon(icon);
                }).attach();

        return mFragmentPropertiesBinding.getRoot();
    }

    static class PropertiesFragmentAdapter extends FragmentStateAdapter {

        public PropertiesFragmentAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            PropertiesListFragment.PropertyListType propertyListType;
            switch (position){
                case 1:
                    propertyListType = PropertiesListFragment.PropertyListType.RECOMMENDED;
                    break;
                case 2:
                    propertyListType = PropertiesListFragment.PropertyListType.NEARBY;
                    break;
                case 3:
                    propertyListType = PropertiesListFragment.PropertyListType.LATEST;
                    break;
                case 4:
                    propertyListType = PropertiesListFragment.PropertyListType.POPULAR;
                    break;
                case 5:
                    propertyListType = PropertiesListFragment.PropertyListType.FAVORITE;
                    break;
                case 6:
                    propertyListType = PropertiesListFragment.PropertyListType.PERSONAL;
                    break;
                case 0:
                default:
                    propertyListType = PropertiesListFragment.PropertyListType.ALL;
                    break;
            }
            return PropertiesListFragment.newInstance(propertyListType);
        }

        @Override
        public int getItemCount() {
            return 7;
        }
    }
}