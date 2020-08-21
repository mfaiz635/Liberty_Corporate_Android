
package libertypassage.com.app.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelInfectedStatuses {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("details")
    @Expose
    private List<DetailInfectedStatus> details = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DetailInfectedStatus> getDetails() {
        return details;
    }

    public void setDetails(List<DetailInfectedStatus> details) {
        this.details = details;
    }

}
