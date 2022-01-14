package ke.co.tonyoa.mahao.ui.properties;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.responses.Amenity;
import ke.co.tonyoa.mahao.app.api.responses.PropertyCategory;
import ke.co.tonyoa.mahao.app.utils.ViewUtils;
import ke.co.tonyoa.mahao.databinding.FragmentFilterPropertiesBinding;
import ke.co.tonyoa.mahao.ui.properties.single.PickLocationFragment;

public class FilterPropertiesFragment extends BottomSheetDialogFragment {

    private FragmentFilterPropertiesBinding mFragmentFilterPropertiesBinding;
    private FilterPropertiesViewModel mFilterPropertiesViewModel;
    private PropertyFilter mPropertyFilter;

    public FilterPropertiesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPropertyFilter = FilterPropertiesFragmentArgs.fromBundle(getArguments()).getPropertyFilter();
        }
        mFilterPropertiesViewModel = new ViewModelProvider(this).get(FilterPropertiesViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentFilterPropertiesBinding = FragmentFilterPropertiesBinding.inflate(inflater, container, false);

        initializeListeners();
        if (mPropertyFilter!=null){
            mFragmentFilterPropertiesBinding.rangeSliderFilterBeds
                    .setValues(mPropertyFilter.getMinBed()==null?-1f:mPropertyFilter.getMinBed(),
                            mPropertyFilter.getMaxBed()==null?7f:mPropertyFilter.getMaxBed());

            mFragmentFilterPropertiesBinding.rangeSliderFilterBaths
                    .setValues(mPropertyFilter.getMinBath()==null?-1f:mPropertyFilter.getMinBath(),
                            mPropertyFilter.getMaxBath()==null?7f:mPropertyFilter.getMaxBath());

            mFragmentFilterPropertiesBinding.rangeSliderFilterPrice
                    .setValues(mPropertyFilter.getMinPrice()==null?-1f:mPropertyFilter.getMinPrice(),
                            mPropertyFilter.getMaxPrice()==null?100000f:mPropertyFilter.getMaxPrice());

            mFragmentFilterPropertiesBinding.textInputEditTextFilterPropertiesLocation
                    .setText(mPropertyFilter.getLocationName()==null?null:mPropertyFilter.getLocationName());
        }


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
        mFragmentFilterPropertiesBinding.textInputEditTextFilterPropertiesLocation.setOnClickListener(v->{
            LatLng filterCoordinates = mFilterPropertiesViewModel.getFilterCoordinates();
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main)
                    .navigate(FilterPropertiesFragmentDirections
                            .actionFilterPropertiesFragmentToPickLocationFragment(mFilterPropertiesViewModel.getFilterLocation(),
                                    filterCoordinates==null?null:new float[]{(float) filterCoordinates.latitude,
                                            (float) filterCoordinates.longitude}, (PickLocationFragment.OnLocationSelectedListener) (locationName, latLng) -> {
                                        mFilterPropertiesViewModel.setFilterCoordinates(latLng);
                                        mFilterPropertiesViewModel.setFilterLocation(locationName);
                                    }));
        });
        mFragmentFilterPropertiesBinding.buttonFilterPropertiesCancel.setOnClickListener(v->{
            dismiss();
        });
        mFragmentFilterPropertiesBinding.buttonFilterPropertiesDone.setOnClickListener(v->{
            //TODO: Complete filtering
        });
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

                    for (PropertyCategory propertyCategory:categories){
                        mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesCategories.addView(getChip(propertyCategory.getId(),
                                propertyCategory.getTitle(), propertyCategory,
                                mPropertyFilter!=null && mPropertyFilter.getCategories()!=null && mPropertyFilter.getCategories().contains(propertyCategory.getId())));
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

                    for (Amenity amenity:amenities){
                        mFragmentFilterPropertiesBinding.chipGroupFilterPropertiesAmenities.addView(getChip(amenity.getId(),
                                amenity.getTitle(), amenity,
                                mPropertyFilter!=null && mPropertyFilter.getAmenities()!=null && mPropertyFilter.getAmenities().contains(amenity.getId())));
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

    private Chip getChip(int id, String text, Object tag, boolean isChecked){
        Chip chip = new Chip(requireContext());
        chip.setId(id);
        chip.setText(text);
        chip.setTag(tag);
        chip.setCheckable(true);
        chip.setCheckedIconVisible(false);
        chip.setChecked(isChecked);
        chip.setClickable(true);
        chip.setFocusable(true);
        chip.setEnsureMinTouchTargetSize(false);
        chip.setChipBackgroundColorResource(R.color.chip_color);
        //chip.setChipDrawable(ChipDrawable.createFromAttributes(getContext(), null, 0, R.style.CustomChipChoice));

        chip.setOnCheckedChangeListener((compoundButton, selected) -> {

        });
        return chip;
    }

    public static class PropertyFilter implements Serializable {
        private Integer mMinBed;
        private Integer mMaxBed;
        private Integer mMinBath;
        private Integer mMaxBath;
        private Integer mMinPrice;
        private Integer mMaxPrice;
        private String mLocationName;
        private LatLng mLatLng;
        private List<Integer> mCategories;
        private List<Integer> mAmenities;

        public PropertyFilter(Integer minBed, Integer maxBed, Integer minBath, Integer maxBath,
                              Integer minPrice, Integer maxPrice, String locationName, LatLng latLng,
                              List<Integer> categories, List<Integer> amenities) {
            mMinBed = minBed;
            mMaxBed = maxBed;
            mMinBath = minBath;
            mMaxBath = maxBath;
            mMinPrice = minPrice;
            mMaxPrice = maxPrice;
            mLocationName = locationName;
            mLatLng = latLng;
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

        public Integer getMinPrice() {
            return mMinPrice;
        }

        public void setMinPrice(Integer minPrice) {
            mMinPrice = minPrice;
        }

        public Integer getMaxPrice() {
            return mMaxPrice;
        }

        public void setMaxPrice(Integer maxPrice) {
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

        public List<Integer> getCategories() {
            return mCategories;
        }

        public void setCategories(List<Integer> categories) {
            mCategories = categories;
        }

        public List<Integer> getAmenities() {
            return mAmenities;
        }

        public void setAmenities(List<Integer> amenities) {
            mAmenities = amenities;
        }
    }

    interface OnFilterListener{
        void onFilter();
    }
}