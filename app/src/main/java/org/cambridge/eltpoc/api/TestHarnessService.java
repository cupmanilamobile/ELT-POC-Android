package org.cambridge.eltpoc.api;

import org.cambridge.eltpoc.model.CLMSUser;

import java.util.List;

import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.Callback;

/**
 * Created by jlundang on 6/16/15.
 */
public interface TestHarnessService {
    @FormUrlEncoded
    @POST("/v1.0/authorize")
    void getBearerToken(@Field("grant_type") String grantType,
                               @Field("client_id") String clientId,
                               @Field("username") String username,
                               @Field("password") String password
                            );

}
