package ke.co.tonyoa.mahao.ui.properties;

import static ke.co.tonyoa.mahao.app.utils.ViewUtils.getChip;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.responses.Amenity;
import ke.co.tonyoa.mahao.app.api.responses.PropertyCategory;
import ke.co.tonyoa.mahao.app.navigation.BaseFragment;
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.FragmentFilterPropertiesBinding;
import ke.co.tonyoa.mahao.ui.common.PickLocationFragment;

public class FilterPropertiesFragment extends BaseFragment {

    private FragmentFilterPropertiesBinding mFragmentFilterPropertiesBinding;
    private FilterPropertiesViewModel mFilterPropertiesViewModel;

    public FilterPropertiesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        PropertyFilter propertyFilter = null;
        if (getArguments() != null) {
            propertyFilter = FilterPropertiesFragmentArgs.fromBundle(getArguments()).getPropertyFilter();
        }
        FilterPropertiesViewModelFactory filterPropertiesViewModelFactory = new FilterPropertiesViewModelFactory(requireActivity().getApplication(),
                propertyFilter);
        mFilterPropertiesViewModel = new ViewModelProvider(this, filterPropertiesViewModelFactory).get(FilterPropertiesViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentFilterPropertiesBinding = FragmentFilterPropertiesBinding.inflate(inflater, container, false);
        setToolbar(mFragmentFilterPropertiesBinding.layoutToolbar.materialToolbarLayoutToolbar);
        setTitle(getString(R.string.filter));

        initializeListeners();
        mFragmentFilterPropertiesBinding.rangeSliderFilterBeds
                .setValues(mFilterPropertiesViewModel.getMinBed()==null?-1f:mFilterPropertiesViewModel.getMinBed(),
                        mFilterPropertiesViewModel.getMaxBed()==null?7f:mFilterPropertiesViewModel.getMaxBed());

        mFragmentFilterPropertiesBinding.rangeSliderFilterBaths
                .setValues(mFilterPropertiesViewModel.getMinBath()==null?-1f:mFilterPropertiesViewModel.getMinBath(),
                        mFilterPropertiesViewModel.getMaxBath()==null?7f:mFilterPropertiesViewModel.getMaxBath());

        mFragmentFilterPropertiesBinding.rangeSliderFilterPrice
                .setValues(mFilterPropertiesViewModel.getMinPrice()==null?-1f:mFilterPropertiesViewModel.getMinPrice(),
                        mFilterPropertiesViewModel.getMaxPrice()==null?100000f:mFilterPropertiesViewModel.getMaxPrice());

        mFragmentFilterPropertiesBinding.textInputEditTextFilterPropertiesLocation
                .setText(mFilterPropertiesViewModel.getLocationName()==null?null:mFilterPropertiesViewModel.getLocationName());
        fetchCategories();
        fetchAmenities();
        return mFragmentFilterPropertiesBinding.getRoot();
    }

    private void initializeListeners(){
        mFragmentFilterPropertiesBinding.rangeSliderFilterBeds.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            setRangeText(mFragmentFilterPropertiesBinding.textViewFilterPropertiesMinBed,
                    values.get(0), -1, 7);
            setRangeText(mFragmentFilterPropertiesBinding.textViewFilterPropertiesMaxBed,
                    values.get(1), -1, 7);
        });
        mFragmentFilterPropertiesBinding.rangeSliderFilterBaths.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            setRangeText(mFragmentFilterPropertiesBinding.textViewFilterPropertiesMinBath,
                    values.get(0), -1, 7);
            setRangeText(mFragmentFilterPropertiesBinding.textViewFilterPropertiesMaxBath,
                    values.get(1), -1, 7);
        });
        mFragmentFilterPropertiesBinding.rangeSliderFilterPrice.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            setRangeText(mFragmentFilterPropertiesBinding.textViewFilterPropertiesMinPrice,
                    values.get(0), -1, 100000);
            setRangeText(mFragmentFilterPropertiesBinding.textViewFilterPropertiesMaxPrice,
                    values.get(1), -1, 100000);
        });
        mFragmentFilterPropertiesBinding.textInputEditTextFilterPropertiesLocation.setInputType(InputType.TYPE_NULL);
        mFragmentFilterPropertiesBinding.textInputEditTextFilterPropertiesLocation.setOnFocusChangeListener((v, hasFocus)->{
            if (hasFocus)
                mFragmentFilterPropertiesBinding.textInputEditTextFilterPropertiesLocation.performClick();
        });
        mFragmentFilterPropertiesBinding.textViewFilterPropertiesLocationRadius
                .setText(getString(R.string.d_filter_radius, mFragmentFilterPropertiesBinding.sliderFilterLocationRadius.getValue()));
        mFragmentFilterPropertiesBinding.sliderFilterLocationRadius.addOnChangeListener((slider, value, fromUser) -> {
            mFragmentFilterPropertiesBinding.textViewFilterPropertiesLocationRadius
                    .setText(getString(R.string.d_filter_radius, value));
        });
        mFragmentFilterPropertiesBinding.imageButtonFilterPropertiesCancelLocation.setOnClickListener(v->{
            mFilterPropertiesViewModel.setLocationName(null);
            mFilterPropertiesViewModel.setLatLng(null);
            mFragmentFilterPropertiesBinding.textInputEditTextFilterPropertiesLocation.setText(null);
        });

        NavBackStackEntry currentBackStackEntry = getNavController().getCurrentBackStackEntry();
        if (currentBackStackEntry!=null) {
            MutableLiveData<Object> locationLiveData = currentBackStackEntry.getSavedStateHandle().getLiveData("location");
            locationLiveData.observe(getViewLifecycleOwner(), result->{
                if (result instanceof PickLocationFragment.LocationWithLatLng){
                    PickLocationFragment.LocationWithLatLng locationWithLatLng = (PickLocationFragment.LocationWithLatLng) result;
                    String locationName = locationWithLatLng.getLocation();
                    mFragmentFilterPropertiesBinding.textInputEditTextFilterPropertiesLocation.setText(locationName);
                    mFilterPropertiesViewModel.setLatLng(new LatLng(locationWithLatLng.getLat(),
                            locationWithLatLng.getLng()));
                    mFilterPropertiesViewModel.setLocationName(locationName);
                }
            });
        }

        mFragmentFilterPropertiesBinding.textInputEditTextFilterPropertiesLocation.setOnClickListener(v->{
            LatLng filterCoordinates = mFilterPropertiesViewModel.getLatLng();
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main)
                    .navigate(FilterPropertiesFragmentDirections
                            .actionFilterPropertiesFragmentToPickLocationFragment(mFilterPropertiesViewModel.getLocationName(),
                                    filterCoordinates==null?null:new float[]{(float) filterCoordinates.latitude,
                                            (float) filterCoordinates.longitude}));
        });
        mFragmentFilterPropertiesBinding.buttonFilterPropertiesCancel.setOnClickListener(v->{
            navigateBack();
        });
        mFragmentFilterPropertiesBinding.buttonFilterPropertiesDone.setOnClickListener(v->{
            saveCurrentValues();

            PropertyFilter propertyFilter = new PropertyFilter(mFilterPropertiesViewModel.getMinBed(),
                    mFilterPropertiesViewModel.getMaxBed(), mFilterPropertiesViewModel.getMinBath(),
                    mFilterPropertiesViewModel.getMaxBath(), mFilterPropertiesViewModel.getMinPrice(),
                    mFilterPropertiesViewModel.getMaxPrice(), mFilterPropertiesViewModel.getLocationName(),
                    mFilterPropertiesViewModel.getLatLng(), mFilterPropertiesViewModel.getFilterRadius(), new ArrayList<>(mFilterPropertiesViewModel.getSelectedPropertyCategories()),
                    new ArrayList<>(mFilterPropertiesViewModel.getSelectedAmenities()));
            NavBackStackEntry previousBackStackEntry = getNavController().getPreviousBackStackEntry();
            if (previousBackStackEntry != null) {
                previousBackStackEntry.getSavedStateHandle().set("filter", propertyFilter);
            }
            navigateBack();
        });
    }

    private void saveCurrentValues(){
        String minBedText = ViewUtils.getText(mFragmentFilterPropertiesBinding.textViewFilterPropertiesMinBed);
        Integer minBed = getIntFromText(minBedText);
        String maxBedText = ViewUtils.getText(mFragmentFilterPropertiesBinding.textViewFilterPropertiesMaxBed);
        Integer maxBed = getIntFromText(maxBedText);
        String minBathText = ViewUtils.getText(mFragmentFilterPropertiesBinding.textViewFilterPropertiesMinBath);
        Integer minBath = getIntFromText(minBathText);
        String maxBathText = ViewUtils.getText(mFragmentFilterPropertiesBinding.textViewFilterPropertiesMaxBath);
        Integer maxBath = getIntFromText(maxBathText);
        String minPriceText = ViewUtils.getText(mFragmentFilterPropertiesBinding.textViewFilterPropertiesMinPrice);
        Float minPrice = getFloatFromText(minPriceText);
        String maxPriceText = ViewUtils.getText(mFragmentFilterPropertiesBinding.textViewFilterPropertiesMaxPrice);
        Float maxPrice = getFloatFromText(maxPriceText);
        Integer filterRadius = (int) mFragmentFilterPropertiesBinding.sliderFilterLocationRadius.getValue();

        int numSelectedCategories = mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesSelectedCategories.getChildCount();
        Set<PropertyCategory> selectedCategories = new HashSet<>();
        for (int index=0; index<numSelectedCategories; index++) {
            View chip = mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesSelectedCategories.getChildAt(index);
            if (chip.getTag() instanceof PropertyCategory){
                selectedCategories.add((PropertyCategory) chip.getTag());
            }
        }
        mFilterPropertiesViewModel.setSelectedPropertyCategories(selectedCategories);

        int numSelectedAmenities = mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesSelectedAmenities.getChildCount();
        Set<Amenity> selectedAmenities = new HashSet<>();
        for (int index = 0; index<numSelectedAmenities; index++){
            View chip = mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesSelectedAmenities.getChildAt(index);
            if (chip.getTag() instanceof Amenity){
                selectedAmenities.add((Amenity) chip.getTag());
            }
        }
        mFilterPropertiesViewModel.setSelectedAmenities(selectedAmenities);

        mFilterPropertiesViewModel.setMinBed(minBed);
        mFilterPropertiesViewModel.setMaxBed(maxBed);
        mFilterPropertiesViewModel.setMinBath(minBath);
        mFilterPropertiesViewModel.setMaxBath(maxBath);
        mFilterPropertiesViewModel.setMinPrice(minPrice);
        mFilterPropertiesViewModel.setMaxPrice(maxPrice);
        mFilterPropertiesViewModel.setFilterRadius(filterRadius);
    }

    @Override
    public void onPause() {
        super.onPause();
        saveCurrentValues();
    }

    private Integer getIntFromText(String text){
        Integer val = null;
        try {
            if (text.contains("."))
                text = text.split("\\.")[0];
            val = Integer.parseInt(text);
        }
        catch (NumberFormatException numberFormatException){
            numberFormatException.printStackTrace();
        }
        return val;
    }

    public Float getFloatFromText(String text){
        Float val = null;
        try {
            val = Float.parseFloat(text);
        }
        catch (NumberFormatException numberFormatException){
            numberFormatException.printStackTrace();
        }
        return val;
    }

    private void setRangeText(TextView textView, float value, float min, float max){
        textView.setText(value==min?"None":value==max?"Infinite":value+"");
    }

    private void fetchCategories(){
        ViewUtils.load(mFragmentFilterPropertiesBinding.linearLayoutFilterPropertiesLoadingCategories,
                Arrays.asList(mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesCategories), true);
        mFilterPropertiesViewModel.getPropertyCategories().observe(getViewLifecycleOwner(), listAPIResponse -> {
            ViewUtils.load(mFragmentFilterPropertiesBinding.linearLayoutFilterPropertiesLoadingCategories,
                    Arrays.asList(mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesCategories), false);

            int categoryCount = 0;
            if (listAPIResponse!=null && listAPIResponse.isSuccessful()){
                List<PropertyCategory> categories = listAPIResponse.body();
                if (categories!=null) {
                    categoryCount = categories.size();

                    mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesCategories.removeAllViews();
                    mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesSelectedCategories.removeAllViews();
                    for (PropertyCategory propertyCategory:categories){
                        boolean isChecked = mFilterPropertiesViewModel.getSelectedPropertyCategories().contains(propertyCategory);
                        mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesCategories.addView(getChip(propertyCategory.getId(),
                                propertyCategory.getTitle(), propertyCategory,
                                isChecked, (buttonView, buttonIsChecked) -> {
                                    selectChip(propertyCategory, buttonIsChecked, mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesSelectedCategories, mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesCategories);
                                }, null, requireContext()));
                        // If is checked, add it to the list of checked categories
                        if (isChecked){
                            selectChip(propertyCategory, true, mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesSelectedCategories, mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesCategories);
                        }
                    }
                }
            }
            else {
                Toast.makeText(requireContext(),
                        (listAPIResponse==null || listAPIResponse.errorMessage(requireContext())==null)?
                                getString(R.string.unknown_error):
                                listAPIResponse.errorMessage(requireContext()),
                        Toast.LENGTH_SHORT).show();
                categoryCount = mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesCategories.getChildCount();
            }

            if (categoryCount>0){
                mFragmentFilterPropertiesBinding.linearLayoutFilterPropertiesEmptyCategories.setVisibility(View.GONE);
                mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesCategories.setVisibility(View.VISIBLE);
            }
            else {
                mFragmentFilterPropertiesBinding.linearLayoutFilterPropertiesEmptyCategories.setVisibility(View.VISIBLE);
                mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesCategories.setVisibility(View.GONE);
            }

        });
    }

    private void selectChip(Object tag, boolean isChecked,
                            ChipGroup chipGroupSelected, ChipGroup chipGroupAll) {
        if (isChecked) {
            int id;
            String title;
            if (tag instanceof PropertyCategory){
                PropertyCategory propertyCategory = (PropertyCategory) tag;
                id = propertyCategory.getId();
                title = propertyCategory.getTitle();
            }
            else if (tag instanceof Amenity){
                Amenity amenity = (Amenity) tag;
                id = amenity.getId();
                title = amenity.getTitle();
            }
            else {
                return;
            }
            chipGroupSelected.addView(getChip(id,
                    title, tag, true, null,
                    v ->{
                        View checkedChip = chipGroupAll
                                .findViewWithTag(tag);
                        if (checkedChip != null){
                            ((Chip)checkedChip).setChecked(false);
                        }

                        chipGroupSelected.removeView(chipGroupSelected.findViewWithTag(tag));
                    }, requireContext()));
        }
        else {
            chipGroupSelected.removeView(chipGroupSelected.findViewWithTag(tag));
        }
    }

    private void fetchAmenities(){
        ViewUtils.load(mFragmentFilterPropertiesBinding.linearLayoutFilterPropertiesLoadingAmenities,
                Arrays.asList(mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesAmenities), true);
        mFilterPropertiesViewModel.getAmenities().observe(getViewLifecycleOwner(), listAPIResponse -> {
            ViewUtils.load(mFragmentFilterPropertiesBinding.linearLayoutFilterPropertiesLoadingAmenities,
                    Arrays.asList(mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesAmenities), false);

            int amenityCount = 0;
            if (listAPIResponse!=null && listAPIResponse.isSuccessful()){
                List<Amenity> amenities = listAPIResponse.body();
                if (amenities!=null) {
                    amenityCount = amenities.size();

                    mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesAmenities.removeAllViews();
                    mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesSelectedAmenities.removeAllViews();
                    for (Amenity amenity:amenities){
                        boolean isChecked = mFilterPropertiesViewModel.getSelectedAmenities().contains(amenity);
                        mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesAmenities.addView(getChip(amenity.getId(),
                                amenity.getTitle(), amenity,
                                isChecked, ((buttonView, buttonIsChecked) -> {
                                    selectChip(amenity, buttonIsChecked, mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesSelectedAmenities, mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesAmenities);
                                }), null, requireContext()));

                        // If is checked, add it to the list of checked amenities
                        if (isChecked){
                            selectChip(amenity, true, mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesSelectedAmenities, mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesAmenities);
                        }
                    }
                }
            }
            else {
                Toast.makeText(requireContext(),
                        (listAPIResponse==null || listAPIResponse.errorMessage(requireContext())==null)?
                                getString(R.string.unknown_error):
                                listAPIResponse.errorMessage(requireContext()),
                        Toast.LENGTH_SHORT).show();
                amenityCount = mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesAmenities.getChildCount();
            }

            if (amenityCount>0){
                mFragmentFilterPropertiesBinding.linearLayoutFilterPropertiesEmptyAmenities.setVisibility(View.GONE);
                mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesAmenities.setVisibility(View.VISIBLE);
            }
            else {
                mFragmentFilterPropertiesBinding.linearLayoutFilterPropertiesEmptyAmenities.setVisibility(View.VISIBLE);
                mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesAmenities.setVisibility(View.GONE);
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            navigateUp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PropertyFilter implements Serializable {
        private String mQuery;
        private Integer mMinBed;
        private Integer mMaxBed;
        private Integer mMinBath;
        private Integer mMaxBath;
        private Float mMinPrice;
        private Float mMaxPrice;
        private String mLocationName;
        private LatLng mLatLng;
        private Integer mFilterRadius;
        private List<PropertyCategory> mCategories;
        private List<Amenity> mAmenities;

        public PropertyFilter(){

        }

        public PropertyFilter(Integer minBed, Integer maxBed, Integer minBath, Integer maxBath,
                              Float minPrice, Float maxPrice, String locationName, LatLng latLng,
                              Integer filterRadius, List<PropertyCategory> categories, List<Amenity> amenities) {
            mMinBed = minBed;
            mMaxBed = maxBed;
            mMinBath = minBath;
            mMaxBath = maxBath;
            mMinPrice = minPrice;
            mMaxPrice = maxPrice;
            mLocationName = locationName;
            mLatLng = latLng;
            mFilterRadius = filterRadius;
            mCategories = categories;
            mAmenities = amenities;
        }

        public Integer getMinBed() {
            return mMinBed;
        }

        public void setMinBed(Integer minBed) {
            mMinBed = minBed;
        }

        public Integer getMaxBed() {
            return mMaxBed;
        }

        public void setMaxBed(Integer maxBed) {
            mMaxBed = maxBed;
        }

        public Integer getMinBath() {
            return mMinBath;
        }

        public void setMinBath(Integer minBath) {
            mMinBath = minBath;
        }

        public Integer getMaxBath() {
            return mMaxBath;
        }

        public void setMaxBath(Integer maxBath) {
            mMaxBath = maxBath;
        }

        public Float getMinPrice() {
            return mMinPrice;
        }

        public void setMinPrice(Float minPrice) {
            mMinPrice = minPrice;
        }

        public Float getMaxPrice() {
            return mMaxPrice;
        }

        public void setMaxPrice(Float maxPrice) {
            mMaxPrice = maxPrice;
        }

        public String getLocationName() {
            return mLocationName;
        }

        public void setLocationName(String locationName) {
            mLocationName = locationName;
        }

        public LatLng getLatLng() {
            return mLatLng;
        }

        public void setLatLng(LatLng latLng) {
            mLatLng = latLng;
        }

        public Integer getFilterRadius() {
            return mFilterRadius;
        }

        public void setFilterRadius(Integer filterRadius) {
            mFilterRadius = filterRadius;
        }

        public List<PropertyCategory> getCategories() {
            return mCategories;
        }

        public void setCategories(List<PropertyCategory> categories) {
            mCategories = categories;
        }

        public List<Amenity> getAmenities() {
            return mAmenities;
        }

        public void setAmenities(List<Amenity> amenities) {
            mAmenities = amenities;
        }

        public String getQuery() {
            return mQuery;
        }

        public void setQuery(String query) {
            mQuery = query;
        }

        @Override
        public String toString() {
            return "PropertyFilter{" +
                    "mMinBed=" + mMinBed +
                    ", mMaxBed=" + mMaxBed +
                    ", mMinBath=" + mMinBath +
                    ", mMaxBath=" + mMaxBath +
                    ", mMinPrice=" + mMinPrice +
                    ", mMaxPrice=" + mMaxPrice +
                    ", mLocationName='" + mLocationName + '\'' +
                    ", mLatLng=" + mLatLng +
                    ", mCategories=" + mCategories +
                    ", mAmenities=" + mAmenities +
                    ", mQuery="+mQuery+
                    ", mFilterRadius="+mFilterRadius+
                    '}';
        }

    }

}