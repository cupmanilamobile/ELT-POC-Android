package org.cambridge.eltpoc.api;

import org.cambridge.eltpoc.model.CLMSClassList;
import org.cambridge.eltpoc.model.CLMSCourseList;
import org.cambridge.eltpoc.model.CLMSUser;

import java.util.List;
import java.util.Objects;

import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.Callback;

/**
 * Created by jlundang on 6/16/15.
 */
public interface TestHarnessService {
    @FormUrlEncoded
    @POST("/v1.0/authorize")
    void getBearerTokenWithCallback(@Field("grant_type") String grantType,
                                    @Field("client_id") String clientId,
                                    @Field("username") String username,
                                    @Field("password") String password,
                                    Callback<CLMSUser> clmsUserCallback);

    @FormUrlEncoded
    @POST("/v1.0/authorize")
    CLMSUser getBearerToken(@Field("grant_type") String grantType,
                            @Field("client_id") String clientId,
                            @Field("username") String username,
                            @Field("password") String password);

    @GET("/v1.0/courses")
    CLMSCourseList getClassesList(@Header("Authorization") String tokenAccess);

    @GET("/v1.0/courses")
    void getClassesList(@Header("Authorization") String tokenAccess,
                        Callback<CLMSCourseList> clmsCourseListCallback);

    @GET("/v1.0/classes")
    CLMSClassList getCoursesList(@Header("Authorization") String tokenAccess);

    @GET("/v1.0/classes")
    void getCoursesList(@Header("Authorization") String tokenAccess,
                        Callback<CLMSClassList> clmsClassListCallback);

    @GET("/v1.0/about")
    CLMSUser getAboutInfo(@Header("Authorization") String accessToken, CLMSUser user);
}
