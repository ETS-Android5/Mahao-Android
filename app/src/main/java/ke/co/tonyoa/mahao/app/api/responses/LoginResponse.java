package ke.co.tonyoa.mahao.app.api.responses;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("token_object")
    private Token mToken;
    @SerializedName("user")
    private User mUser;

    public Token getToken() {
        return mToken;
    }

    public void setToken(Token token) {
        mToken = token;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }
}
