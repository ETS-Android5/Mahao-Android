package ke.co.tonyoa.mahao.app.api.requests;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreatePropertyRequest {

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
    private List<String> location = null;
    @SerializedName("is_enabled")
    @Expose
    private Boolean isEnabled;
    @SerializedName("is_verified")
    @Expose
    private Boolean isVerified;
    @SerializedName("feature_image")
    @Expose
    private String featureImage;

    public CreatePropertyRequest(Integer propertyCategoryId, String title, String description, Integer numBed,
                                 Integer numBath, String locationName, Float price, List<String> location,
                                 Boolean isEnabled, Boolean isVerified, String featureImage) {
        this.propertyCategoryId = propertyCategoryId;
        this.title = title;
        this.description = description;
        this.numBed = numBed;
        this.numBath = numBath;
        this.locationName = locationName;
        this.price = price;
        this.location = location;
        this.isEnabled = isEnabled;
        this.isVerified = isVerified;
        this.featureImage = featureImage;
    }

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

    public List<String> getLocation() {
        return location;
    }

    public void setLocation(List<String> location) {
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

    public String getFeatureImage() {
        return featureImage;
    }

    public void setFeatureImage(String featureImage) {
        this.featureImage = featureImage;
    }

}
