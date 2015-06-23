package org.cambridge.eltpoc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mbaltazar on 6/10/15.
 */
public class CLMSUser extends RealmObject {

    @PrimaryKey
    private String username;
    private String password;
    private String kind = "";
    private long id;
    private String displayName = "";

    @Ignore
    @Expose
    @SerializedName("access_token")
    private String accessToken;
    @Ignore
    @Expose
    @SerializedName("expires_in")
    private int expiresIn;
    @Ignore
    @Expose
    @SerializedName("token_type")
    private String tokenType = "";
    @Ignore
    @Expose
    @SerializedName("scope")
    private String scope = "";
    @Ignore
    @Expose
    @SerializedName("refresh_token")
    private String refreshToken;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String access_token) {
        this.accessToken = access_token;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String token_type) {
        this.tokenType = token_type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refresh_token) {
        this.refreshToken = refresh_token;
    }
}
