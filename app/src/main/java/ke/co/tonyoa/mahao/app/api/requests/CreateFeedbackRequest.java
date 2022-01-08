package ke.co.tonyoa.mahao.app.api.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateFeedbackRequest {

    @SerializedName("feedback_type")
    @Expose
    private String feedbackType;

    public CreateFeedbackRequest(String feedbackType) {
        this.feedbackType = feedbackType;
    }

    public String getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(String feedbackType) {
        this.feedbackType = feedbackType;
    }

}
