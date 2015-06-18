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
    private String kind;
    private long id;
    private String displayName;

    @Ignore
    @Expose
    @SerializedName("access_token")
    private String access_token;
    @Ignore
    @Expose
    @SerializedName("expires_in")
    private int expires_in;
    @Ignore
    @Expose
    @SerializedName("token_type")
    private String token_type;
    @Ignore
    @Expose
    @SerializedName("scope")
    private String scope;
    @Ignore
    @Expose
    @SerializedName("refresh_token")
    private String refresh_token;

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

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
}
