
package ke.co.tonyoa.mahao.app.api.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class PropertyAmenity implements Serializable {

    @SerializedName("property_id")
    @Expose
    private Integer propertyId;
    @SerializedName("amenity_id")
    @Expose
    private Integer amenityId;
    @SerializedName("created_at")
    @Expose
    private Date createdAt;
    @SerializedName("amenity")
    @Expose
    private Amenity amenity;

    public Integer getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Integer propertyId) {
        this.propertyId = propertyId;
    }

    public Integer getAmenityId() {
        return amenityId;
    }

    public void setAmenityId(Integer amenityId) {
        this.amenityId = amenityId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Amenity getAmenity() {
        return amenity;
    }

    public void setAmenity(Amenity amenity) {
        this.amenity = amenity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyAmenity that = (PropertyAmenity) o;
        return Objects.equals(propertyId, that.propertyId) && Objects.equals(amenityId, that.amenityId) &&
                Objects.equals(createdAt, that.createdAt) && Objects.equals(amenity, that.amenity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyId, amenityId, createdAt, amenity);
    }
}
