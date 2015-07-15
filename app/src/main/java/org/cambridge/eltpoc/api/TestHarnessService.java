package org.cambridge.eltpoc.api;

import org.cambridge.eltpoc.model.CLMSClassList;
import org.cambridge.eltpoc.model.CLMSContentScore;
import org.cambridge.eltpoc.model.CLMSContentScoreList;
import org.cambridge.eltpoc.model.CLMSContentURL;
import org.cambridge.eltpoc.model.CLMSCourseList;
import org.cambridge.eltpoc.model.CLMSLessonScoreList;
import org.cambridge.eltpoc.model.CLMSUnitScore;
import org.cambridge.eltpoc.model.CLMSUnitScoreList;
import org.cambridge.eltpoc.model.CLMSUser;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
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
    CLMSUnitScore getUnitScore(@Header("Authorization") String tokenAccess,
                               @Path("classId") int classId,
                               @Path("userId") int userId);

    @GET("/v1.0/classes/{classId}/users/{userId}/gradebook/unit-scores")
    void getUnitScoreList(@Header("Authorization") String tokenAccess,
                               @Path("classId") int classId,
                               @Path("userId") long userId,
                      Callback<CLMSUnitScoreList> clmsUnitScoreCallback);

    @GET("/v1.0/classes/{classId}/users/{userId}/gradebook/unit-scores/{unitId}/lesson-scores ")
    CLMSUnitScore getLessonScore(@Header("Authorization") String tokenAcess,
                            @Path("classId") int classId,
                            @Path("userId") long userId,
                            @Path("unitId") int unitId);

    @GET("/v1.0/classes/{classId}/users/{userId}/gradebook/unit-scores/{unitId}/lesson-scores ")
    void getLessonScoreList(@Header("Authorization") String tokenAcess,
                        @Path("classId") int classId,
                        @Path("userId") long userId,
                        @Path("unitId") int unitId,
                        Callback<CLMSLessonScoreList> clmsUnitLessonScoreCallback);

//    @GET("/v1.0/classes/{classId}/users/{userId}/gradebook/unit-scores/{unitId}/lesson-scores ")
//    void getLessonScoreList(@Header("Authorization") String tokenAcess,
//                            @Path("classId") int classId,
//                            @Path("userId") long userId,
//                            @Path("unitId") int unitId,
//                            Callback<Object> object);

    @GET("/v1.0/classes/{classId}/users/{userId}/gradebook/unit-scores/{unitId}/lesson-scores/{lessonId}/content-scores")
    CLMSContentScore getContentScore(@Header("Authorization") String tokenAccess,
                             @Path("classId") int classId,
                             @Path("userId") long userId,
                             @Path("unitId") int unitId,
                             @Path("lessonId") int lessonId);

    @GET("/v1.0/classes/{classId}/users/{userId}/gradebook/unit-scores/{unitId}/lesson-scores/{lessonId}/content-scores")
    void getContentScoreList(@Header("Authorization") String tokenAccess,
                                     @Path("classId") int classId,
                                     @Path("userId") long userId,
                                     @Path("unitId") int unitId,
                                     @Path("lessonId") int lessonId,
                         Callback<CLMSContentScoreList> clmsContentScoreCallback);

    @GET("/v1.0/courses/{courseId}/units/{unitId}/lessons/{lessonId}/content/{contentId}?download=true")
    void getContentUrl(@Header("Authorization") String tokenAccess,
                             @Path("courseId") int courseId,
                             @Path("unitId") int unitId,
                             @Path("lessonId") int lessonId,
                             @Path("contentId") int contentId,
                             Callback<CLMSContentURL> clmsContentUrlCallback);

    @GET("/v1.0/about")
    void getAboutInfo(@Header("Authorization") String accessToken, Callback<CLMSUser> user);

    @FormUrlEncoded
    @PUT("/v1.0/classes/{classId}/users/{userId}/gradebook/unit-scores/{unitId}/lesson-scores/{lessonId}/content-scores/{contentId}")
    void updateContentScore(@Header("Authorization") String accessToken,
                            @Path("classId") int courseId,
                            @Path("userId") long userId,
                            @Path("unitId") int unitId,
                            @Path("lessonId") int lessonId,
                            @Path("contentId") int contentId,
                            @Field("score") int score,
                            @Field("progress") int progress,
                            @Field("lastaccess") long lastaccess,
                            Callback<CLMSContentScoreList> clmsContentScoreListCallback);
}
