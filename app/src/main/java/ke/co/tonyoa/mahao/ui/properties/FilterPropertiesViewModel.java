package ke.co.tonyoa.mahao.ui.properties;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import ke.co.tonyoa.mahao.app.MahaoApplication;
import ke.co.tonyoa.mahao.app.api.APIResponse;
import ke.co.tonyoa.mahao.app.api.responses.Amenity;
import ke.co.tonyoa.mahao.app.api.responses.PropertyCategory;
import ke.co.tonyoa.mahao.app.repositories.AmenitiesRepository;
import ke.co.tonyoa.mahao.app.repositories.PropertyCategoriesRepository;

public class FilterPropertiesViewModel extends AndroidViewModel {

    @Inject
    PropertyCategoriesRepository mPropertyCategoriesRepository;
    @Inject
    AmenitiesRepository mAmenitiesRepository;
    private String mLocationName;
    private LatLng mLatLng;
    private Integer mFilterRadius;
    private Set<PropertyCategory> mSelectedPropertyCategories = new HashSet<PropertyCategory>();
    private Set<Amenity> mSelectedAmenities = new HashSet<Amenity>();
    private Integer mMinBed;
    private Integer mMaxBed;
    private Integer mMinBath;
    private Integer mMaxBath;
    private Float mMinPrice;
    private Float mMaxPrice;

    private LiveData<APIResponse<List<PropertyCategory>>> mPropertyCategories;
    private LiveData<APIResponse<List<Amenity>>> mAmenities;

    public FilterPropertiesViewModel(@NonNull Application application, FilterPropertiesFragment.PropertyFilter propertyFilter) {
        super(application);
        MahaoApplication.getInstance(application).getApplicationComponent().inject(this);

        if (propertyFilter != null) {
            mMinBed = propertyFilter.getMinBed();
            mMaxBed = propertyFilter.getMaxBed();
            mMinBath = propertyFilter.getMinBath();
            mMaxBath = propertyFilter.getMaxBath();
            mMinPrice = propertyFilter.getMinPrice();
            mMaxPrice = propertyFilter.getMaxPrice();
            if (propertyFilter.getCategories() != null)
                mSelectedPropertyCategories.addAll(propertyFilter.getCategories());
            if (propertyFilter.getAmenities() != null)
                mSelectedAmenities.addAll(propertyFilter.getAmenities());
            mLocationName = propertyFilter.getLocationName();
            mLatLng = propertyFilter.getLatLng();
            mFilterRadius = propertyFilter.getFilterRadius();
        }
        mPropertyCategories = mPropertyCategoriesRepository.getPropertyCategories();
        mAmenities = mAmenitiesRepository.getAmenities();
    }

    public LiveData<APIResponse<List<PropertyCategory>>> getPropertyCategories(){
        return mPropertyCategories;
    }

    public LiveData<APIResponse<List<Amenity>>> getAmenities(){
        return mAmenities;
    }

    public String getLocationName() {
        return mLocationName;
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    public void setLocationName(String locationName) {
        mLocationName = locationName;
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

    public void selectPropertyCategory(PropertyCategory propertyCategory){
        mSelectedPropertyCategories.add(propertyCategory);
    }

    public void removePropertyCategory(PropertyCategory propertyCategory){
        mSelectedPropertyCategories.remove(propertyCategory);
    }

    public Set<PropertyCategory> getSelectedPropertyCategories(){
        return mSelectedPropertyCategories;
    }

    public void selectAmenity(Amenity amenity){
        mSelectedAmenities.add(amenity);
    }

    public void removeAmenity(Amenity amenity){
        mSelectedAmenities.remove(amenity);
    }

    public Set<Amenity> getSelectedAmenities(){
        return mSelectedAmenities;
    }

    public void setSelectedPropertyCategories(Set<PropertyCategory> selectedPropertyCategories) {
        mSelectedPropertyCategories = selectedPropertyCategories;
    }

    public void setSelectedAmenities(Set<Amenity> selectedAmenities) {
        mSelectedAmenities = selectedAmenities;
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
}
