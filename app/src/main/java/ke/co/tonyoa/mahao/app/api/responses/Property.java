package ke.co.tonyoa.mahao.app.api.responses;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Property implements Serializable {

    @SerializedName("property_category_id")
    @Expose
    private Integer propertyCategoryId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("num_bed")
    @Expose
    private Integer numBed;
    @SerializedName("num_bath")
    @Expose
    private Integer numBath;
    @SerializedName("location_name")
    @Expose
    private String locationName;
    @SerializedName("price")
    @Expose
    private Float price;
    @SerializedName("location")
    @Expose
    private List<Float> location = null;
    @SerializedName("is_enabled")
    @Expose
    private Boolean isEnabled;
    @SerializedName("is_verified")
    @Expose
    private Boolean isVerified;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("owner_id")
    @Expose
    private Integer ownerId;
    @SerializedName("feature_image")
    @Expose
    private String featureImage;
    @SerializedName("created_at")
    @Expose
    private Date createdAt;
    @SerializedName("is_favorite")
    @Expose
    private Boolean isFavorite;
    @SerializedName("owner")
    @Expose
    private User owner;
    @SerializedName("property_category")
    @Expose
    private PropertyCategory propertyCategory;
    @SerializedName("property_amenities")
    @Expose
    private List<PropertyAmenity> propertyAmenities = null;
    @SerializedName("property_photos")
    @Expose
    private List<PropertyPhoto> propertyPhotos = null;

    public Integer getPropertyCategoryId() {
        return propertyCategoryId;
    }

    public void setPropertyCategoryId(Integer propertyCategoryId) {
        this.propertyCategoryId = propertyCategoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNumBed() {
        return numBed;
    }

    public void setNumBed(Integer numBed) {
        this.numBed = numBed;
    }

    public Integer getNumBath() {
        return numBath;
    }

    public void setNumBath(Integer numBath) {
        this.numBath = numBath;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public List<Float> getLocation() {
        return location;
    }

    public void setLocation(List<Float> location) {
        this.location = location;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public String getFeatureImage() {
        return featureImage;
    }

    public void setFeatureImage(String featureImage) {
        this.featureImage = featureImage;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public PropertyCategory getPropertyCategory() {
        return propertyCategory;
    }

    public void setPropertyCategory(PropertyCategory propertyCategory) {
        this.propertyCategory = propertyCategory;
    }

    public List<PropertyAmenity> getPropertyAmenities() {
        return propertyAmenities;
    }

    public void setPropertyAmenities(List<PropertyAmenity> propertyAmenities) {
        this.propertyAmenities = propertyAmenities;
    }

    public List<PropertyPhoto> getPropertyPhotos() {
        return propertyPhotos;
    }

    public void setPropertyPhotos(List<PropertyPhoto> propertyPhotos) {
        this.propertyPhotos = propertyPhotos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property property = (Property) o;
        return Objects.equals(propertyCategoryId, property.propertyCategoryId) &&
                Objects.equals(title, property.title) && Objects.equals(description, property.description) &&
                Objects.equals(numBed, property.numBed) && Objects.equals(numBath, property.numBath) &&
                Objects.equals(locationName, property.locationName) && Objects.equals(price, property.price) &&
                Objects.equals(location, property.location) && Objects.equals(isEnabled, property.isEnabled) &&
                Objects.equals(isVerified, property.isVerified) && Objects.equals(id, property.id) &&
                Objects.equals(ownerId, property.ownerId) && Objects.equals(featureImage, property.featureImage) &&
                Objects.equals(createdAt, property.createdAt) && Objects.equals(isFavorite, property.isFavorite) &&
                Objects.equals(owner, property.owner) && Objects.equals(propertyCategory, property.propertyCategory) &&
                Objects.equals(propertyAmenities, property.propertyAmenities) && Objects.equals(propertyPhotos, property.propertyPhotos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyCategoryId, title, description, numBed, numBath, locationName,
                price, location, isEnabled, isVerified, id, ownerId, featureImage, createdAt, isFavorite,
                owner, propertyCategory, propertyAmenities, propertyPhotos);
    }
}
