package org.cambridge.eltpoc.api;

import org.cambridge.eltpoc.model.CLMSClassList;
import org.cambridge.eltpoc.model.CLMSContentScore;
import org.cambridge.eltpoc.model.CLMSCourseList;
import org.cambridge.eltpoc.model.CLMSUnitLessonScore;
import org.cambridge.eltpoc.model.CLMSUser;

import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.Callback;
import retrofit.http.Path;

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
    CLMSCourseList getCoursesList(@Header("Authorization") String tokenAccess);

    @GET("/v1.0/courses")
    void getCoursesList(@Header("Authorization") String tokenAccess,
                        Callback<CLMSCourseList> clmsCourseListCallback);

    @GET("/v1.0/classes")
    CLMSClassList getClassesList(@Header("Authorization") String tokenAccess);

    @GET("/v1.0/classes")
    void getClassesList(@Header("Authorization") String tokenAccess,
                        Callback<CLMSClassList> clmsClassListCallback);

    @GET("/v1.0/classes/{classId}/users/{userId}/gradebook/unit-scores")
    CLMSUnitLessonScore getUnitScore(@Header("Authorization") String tokenAccess,
                               @Path("classId") int classId,
                               @Path("userId") int userId);

    @GET("/v1.0/classes/{classId}/users/{userId}/gradebook/unit-scores")
    void getUnitScore(@Header("Authorization") String tokenAccess,
                               @Path("classId") int classId,
                               @Path("userId") int userId,
                      Callback<CLMSUnitLessonScore> clmsUnitScoreCallback);

    @GET("/v1.0/classes/{classId}/users/{userId}/gradebook/unit-scores/{unitId}/lesson-scores ")
    CLMSUnitLessonScore getLessonScore(@Header("Authorization") String tokenAcess,
                            @Path("classId") int classId,
                            @Path("userId") int userId,
                            @Path("unitId") int unitId);

    @GET("/v1.0/classes/{classId}/users/{userId}/gradebook/unit-scores/{unitId}/lesson-scores ")
    void getLessonScore(@Header("Authorization") String tokenAcess,
                        @Path("classId") int classId,
                        @Path("userId") int userId,
                        @Path("unitId") int unitId,
                        Callback<CLMSUnitLessonScore> clmsUnitLessonScoreCallback);

    @GET("/v1.0/classes/{classId}/users/{userId}/gradebook/unit-scores/{unitId}/lesson-scores/{lessonId}/content-scores")
    CLMSContentScore getContentScore(@Header("Authorization") String tokenAccess,
                             @Path("classId") int classId,
                             @Path("userId") int userId,
                             @Path("unitId") int unitId,
                             @Path("lessonId") int lessonId);

    @GET("/v1.0/classes/{classId}/users/{userId}/gradebook/unit-scores/{unitId}/lesson-scores/{lessonId}/content-scores")
    void getContentScore(@Header("Authorization") String tokenAccess,
                                     @Path("classId") int classId,
                                     @Path("userId") int userId,
                                     @Path("unitId") int unitId,
                                     @Path("lessonId") int lessonId,
                         Callback<CLMSContentScore> clmsContentScoreCallback);

    @GET("/v1.0/about")
    CLMSUser getAboutInfo(@Header("Authorization") String accessToken, CLMSUser user);
}
