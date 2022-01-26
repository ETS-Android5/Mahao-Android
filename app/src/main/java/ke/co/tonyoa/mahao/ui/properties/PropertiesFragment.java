package ke.co.tonyoa.mahao.ui.properties;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.chip.Chip;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.responses.Amenity;
import ke.co.tonyoa.mahao.app.api.responses.PropertyCategory;
import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.FragmentPropertiesBinding;
import ke.co.tonyoa.mahao.ui.main.MainFragmentDirections;

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
            setMarginTop(position);
        });
        mPropertiesViewModel.getFilterAndSort().observe(getViewLifecycleOwner(), this::setupFilterViews);
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

        mFragmentPropertiesBinding.viewPagerProperties.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mPropertiesViewModel.setSelectedPosition(position);
            }
        });
        mFragmentPropertiesBinding.imageButtonPropertiesFilter.setOnClickListener(v->{
            PropertiesViewModel.FilterAndSort filterAndSort = mPropertiesViewModel.getFilterAndSort().getValue();
            navigate(MainFragmentDirections.actionNavigationMainToFilterPropertiesFragment(filterAndSort==null?null:filterAndSort.getPropertyFilter()));
        });
        mFragmentPropertiesBinding.imageButtonPropertiesSort.setOnClickListener(v->{
            PropertiesViewModel.FilterAndSort filterAndSort = mPropertiesViewModel.getFilterAndSort().getValue();
            navigate(MainFragmentDirections.actionNavigationMainToSortPropertiesFragment(filterAndSort==null?null:filterAndSort.getSortBy()));
        });
        mFragmentPropertiesBinding.autoCompleteTextViewPropertiesSearch.addTextChangedListener(new ViewUtils.TextValidator() {
            @Override
            public void validate(CharSequence text) {
                mPropertiesViewModel.setQuery(text.toString());
            }
        });
        NavBackStackEntry currentBackStackEntry = getNavController().getCurrentBackStackEntry();
        if (currentBackStackEntry != null){
            currentBackStackEntry.getSavedStateHandle().getLiveData("filter").observe(getViewLifecycleOwner(), result->{
                if (result instanceof FilterPropertiesFragment.PropertyFilter){
                    FilterPropertiesFragment.PropertyFilter propertyFilter = (FilterPropertiesFragment.PropertyFilter) result;
                    mPropertiesViewModel.setFilter(propertyFilter);
                }
            });
            currentBackStackEntry.getSavedStateHandle().getLiveData("order").observe(getViewLifecycleOwner(), result->{
                if (result instanceof PropertiesViewModel.SortBy){
                    PropertiesViewModel.SortBy sortBy = (PropertiesViewModel.SortBy) result;
                    mPropertiesViewModel.setSort(sortBy);
                }
            });
        }

        return mFragmentPropertiesBinding.getRoot();
    }

    private void setMarginTop(Integer position) {
        mFragmentPropertiesBinding.linearLayoutPropertiesSearchFilter.setVisibility(position ==0?View.VISIBLE:View.GONE);
        ViewGroup.MarginLayoutParams tabLayoutParams = (ViewGroup.MarginLayoutParams)mFragmentPropertiesBinding.tabLayoutProperties.getLayoutParams();
        int marginTop;
        if (position == 0) {
            int dimenRes;
            if (mFragmentPropertiesBinding.chipGroupPropertiesFilters.getChildCount() > 0) {
                dimenRes = R.dimen.size_112;
            }
            else {
                dimenRes = R.dimen.size_56;
            }
            marginTop = getResources().getDimensionPixelSize(dimenRes);
        }
        else {
            marginTop = 0;
        }
        tabLayoutParams.setMargins(0, marginTop, 0, 0);
    }

    private void setupFilterViews(PropertiesViewModel.FilterAndSort filterAndSort) {
        mFragmentPropertiesBinding.chipGroupPropertiesFilters.removeAllViews();
        if (filterAndSort != null){
            FilterPropertiesFragment.PropertyFilter propertyFilter = filterAndSort.getPropertyFilter();
            if (propertyFilter != null){

                Integer minBed = propertyFilter.getMinBed();
                if (minBed!=null){
                    int minBedId = 1;
                    Chip chipMinBed = ViewUtils.getChip(minBedId, "Beds>="+minBed,
                            minBed, false, null,
                            v->{
                                mFragmentPropertiesBinding.chipGroupPropertiesFilters.removeView(
                                        mFragmentPropertiesBinding.chipGroupPropertiesFilters.findViewById(minBedId)
                                );
                                propertyFilter.setMinBed(null);
                                mPropertiesViewModel.setFilter(propertyFilter);
                            }, requireContext());
                    mFragmentPropertiesBinding.chipGroupPropertiesFilters.addView(chipMinBed);
                }
                Integer maxBed = propertyFilter.getMaxBed();
                if (maxBed!=null){
                    int maxBedId =2;
                    Chip chipMaxBed = ViewUtils.getChip(maxBedId, "Beds<="+maxBed,
                            maxBed, false, null,
                            v->{
                                mFragmentPropertiesBinding.chipGroupPropertiesFilters.removeView(
                                        mFragmentPropertiesBinding.chipGroupPropertiesFilters.findViewById(maxBedId)
                                );
                                propertyFilter.setMaxBed(null);
                                mPropertiesViewModel.setFilter(propertyFilter);
                            }, requireContext());
                    mFragmentPropertiesBinding.chipGroupPropertiesFilters.addView(chipMaxBed);
                }

                Integer minBath = propertyFilter.getMinBath();
                if (minBath!=null){
                    int minBathId = 3;
                    Chip chipMinBath = ViewUtils.getChip(minBathId, "Baths>="+minBath,
                            minBath, false, null,
                            v->{
                                mFragmentPropertiesBinding.chipGroupPropertiesFilters.removeView(
                                        mFragmentPropertiesBinding.chipGroupPropertiesFilters.findViewById(minBathId)
                                );
                                propertyFilter.setMinBath(null);
                                mPropertiesViewModel.setFilter(propertyFilter);
                            }, requireContext());
                    mFragmentPropertiesBinding.chipGroupPropertiesFilters.addView(chipMinBath);
                }

                Integer maxBath = propertyFilter.getMaxBath();
                if (maxBath!=null){
                    int maxBathId = 4;
                    Chip chipMaxBath = ViewUtils.getChip(maxBathId, "Baths<="+maxBath,
                            maxBath, false, null,
                            v->{
                                mFragmentPropertiesBinding.chipGroupPropertiesFilters.removeView(
                                        mFragmentPropertiesBinding.chipGroupPropertiesFilters.findViewById(maxBathId)
                                );
                                propertyFilter.setMaxBath(null);
                                mPropertiesViewModel.setFilter(propertyFilter);
                            }, requireContext());
                    mFragmentPropertiesBinding.chipGroupPropertiesFilters.addView(chipMaxBath);
                }

                Float minPrice = propertyFilter.getMinPrice();
                if (minPrice!=null){
                    int minPriceId = 5;
                    Chip chipMinPrice = ViewUtils.getChip(minPriceId, "Price>="+minPrice,
                            minPrice, false, null,
                            v->{
                                mFragmentPropertiesBinding.chipGroupPropertiesFilters.removeView(
                                        mFragmentPropertiesBinding.chipGroupPropertiesFilters.findViewById(minPriceId)
                                );
                                propertyFilter.setMinPrice(null);
                                mPropertiesViewModel.setFilter(propertyFilter);
                            }, requireContext());
                    mFragmentPropertiesBinding.chipGroupPropertiesFilters.addView(chipMinPrice);
                }

                Float maxPrice = propertyFilter.getMaxPrice();
                if (maxPrice!=null){
                    int maxPriceId = 6;
                    Chip chipMaxPrice = ViewUtils.getChip(maxPriceId, "Price<="+maxPrice,
                            maxPrice, false, null,
                            v->{
                                mFragmentPropertiesBinding.chipGroupPropertiesFilters.removeView(
                                        mFragmentPropertiesBinding.chipGroupPropertiesFilters.findViewById(maxPriceId)
                                );
                                propertyFilter.setMaxPrice(null);
                                mPropertiesViewModel.setFilter(propertyFilter);
                            }, requireContext());
                    mFragmentPropertiesBinding.chipGroupPropertiesFilters.addView(chipMaxPrice);
                }

                String locationName = propertyFilter.getLocationName();
                LatLng latLng = propertyFilter.getLatLng();
                if (locationName != null && latLng != null){
                    int locationId = 7;
                    Chip chipLocation = ViewUtils.getChip(locationId, "Location="+locationName,
                            latLng, false, null,
                            v->{
                                mFragmentPropertiesBinding.chipGroupPropertiesFilters.removeView(
                                        mFragmentPropertiesBinding.chipGroupPropertiesFilters.findViewById(locationId)
                                );
                                propertyFilter.setLocationName(null);
                                propertyFilter.setLatLng(null);
                                mPropertiesViewModel.setFilter(propertyFilter);
                            }, requireContext());
                    mFragmentPropertiesBinding.chipGroupPropertiesFilters.addView(chipLocation);
                }

                List<PropertyCategory> categories = propertyFilter.getCategories();
                if (categories != null){
                    for (PropertyCategory propertyCategory:categories){
                        Chip chipCategory = ViewUtils.getChip(propertyCategory.getId()+1000,
                                propertyCategory.getTitle(),
                                propertyCategory, false, null,
                                v->{
                                    mFragmentPropertiesBinding.chipGroupPropertiesFilters.removeView(
                                            mFragmentPropertiesBinding.chipGroupPropertiesFilters.findViewWithTag(propertyCategory)
                                    );
                                    List<PropertyCategory> categoryList = new ArrayList<>(categories);
                                    categoryList.remove(propertyCategory);
                                    propertyFilter.setCategories(categoryList);
                                    mPropertiesViewModel.setFilter(propertyFilter);
                                }, requireContext());
                        mFragmentPropertiesBinding.chipGroupPropertiesFilters.addView(chipCategory);
                    }
                }

                List<Amenity> amenities = propertyFilter.getAmenities();
                if (amenities != null){
                    for (Amenity amenity :amenities){
                        Chip chipAmenity = ViewUtils.getChip(amenity.getId()+2000,
                                amenity.getTitle(),
                                amenity, false, null,
                                v->{
                                    mFragmentPropertiesBinding.chipGroupPropertiesFilters.removeView(
                                            mFragmentPropertiesBinding.chipGroupPropertiesFilters.findViewWithTag(amenity)
                                    );
                                    List<Amenity> amenityList = new ArrayList<>(amenities);
                                    amenityList.remove(amenity);
                                    propertyFilter.setAmenities(amenityList);
                                    mPropertiesViewModel.setFilter(propertyFilter);
                                }, requireContext());
                        mFragmentPropertiesBinding.chipGroupPropertiesFilters.addView(chipAmenity);
                    }
                }

                if (mFragmentPropertiesBinding.chipGroupPropertiesFilters.getChildCount()>0) {
                    // Chip to open filter fragment
                    Chip chipAdd = ViewUtils.getChip(999, "Add +", null, false,
                            null, null, requireContext());
                    chipAdd.setOnClickListener(v -> {
                        mFragmentPropertiesBinding.imageButtonPropertiesFilter.performClick();
                    });
                    chipAdd.setChipBackgroundColorResource(R.color.color_primary);
                    mFragmentPropertiesBinding.chipGroupPropertiesFilters.addView(chipAdd);
                }
            }
        }

        setMarginTop(mPropertiesViewModel.getSelectedPosition().getValue());
    }

    public void setCurrentItem(int currentItem){
        mPropertiesViewModel.setSelectedPosition(currentItem);
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