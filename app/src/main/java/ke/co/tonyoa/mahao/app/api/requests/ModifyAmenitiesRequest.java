package ke.co.tonyoa.mahao.app.api.requests;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModifyAmenitiesRequest {

    @SerializedName("added")
    @Expose
    private List<Integer> added = null;
    @SerializedName("removed")
    @Expose
    private List<Integer> removed = null;

    public ModifyAmenitiesRequest(List<Integer> added, List<Integer> removed) {
        this.added = added;
        this.removed = removed;
    }

    public List<Integer> getAdded() {
        return added;
    }

    public void setAdded(List<Integer> added) {
        this.added = added;
    }

    public List<Integer> getRemoved() {
        return removed;
    }

    public void setRemoved(List<Integer> removed) {
        this.removed = removed;
    }

}
