package ke.co.tonyoa.mahao.app.api;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;

import ke.co.tonyoa.mahao.R;
import ke.co.tonyoa.mahao.app.api.errors.MessageError;
import ke.co.tonyoa.mahao.app.api.errors.ValidationErrors;
import okhttp3.Headers;
import retrofit2.Response;

public class APIResponse<T> {
    private final T body;
    private final Headers headers;
    private String errorBody;
    private final int code;
    private final boolean successful;

    public APIResponse(Response<T> response) {
        if (response != null) {
            this.body = response.body();
            this.code = response.code();
            this.successful = response.isSuccessful();
            this.headers = response.headers();
            try {
                //noinspection ConstantConditions
                this.errorBody = response.errorBody().string();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
                this.errorBody = null;
            }
        } else {
            this.body = null;
            this.code = 0;
            this.successful = false;
            this.headers = null;
            this.errorBody = "No connection to our server";
        }
    }

    public boolean isSuccessful() {
        return successful;
    }

    public Headers headers() {
        return this.headers;
    }

    public T body() {
        return body;
    }

    public int code() {
        return code;
    }

    public String errorBody() {
        return errorBody;
    }

    public boolean serverError() {
        return code >= 500;
    }

    public boolean badRequest() {
        return code == 400;
    }

    public boolean authenticationError() {
        return code == 401;
    }

    public boolean authorizationError() {
        return code == 403;
    }

    @NonNull
    public HashMap<String, String> validationErrors() {
        HashMap<String, String> errorMap = new HashMap<>();
        try {
            Gson gson = new Gson();
            ValidationErrors errors = gson.fromJson(errorBody, ValidationErrors.class);
            for (ValidationErrors.ValidationError error : errors.detail) {
                errorMap.put(error.loc[error.loc.length - 1], error.msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return errorMap;
    }

    public String errorMessage(Context context) {
        if (serverError()) {
            return context.getString(R.string.server_error);
        }
        if (authenticationError()) {
            return context.getString(R.string.authentication_error);
        }
        if (authorizationError()) {
            return context.getString(R.string.authorization_error);
        }
        if (badRequest() && !validationErrors().isEmpty()) {
            if (validationErrors().get("__root__") != null) {
                return validationErrors().get("__root__");
            }
            return context.getString(R.string.validation_error);
        }
        try {
            Gson gson = new Gson();
            MessageError error = gson.fromJson(errorBody, MessageError.class);
            return  error.detail;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return errorBody;
    }
}
