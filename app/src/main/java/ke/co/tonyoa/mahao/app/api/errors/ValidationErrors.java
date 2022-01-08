package ke.co.tonyoa.mahao.app.api.errors;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ValidationErrors implements Serializable {
    @SerializedName("detail")
    public ValidationError[] detail;

    public static class ValidationError implements Serializable {
        @SerializedName("loc")
        public String[] loc;
        @SerializedName("msg")
        public String msg;
        @SerializedName("type")
        public String type;
    }
}
