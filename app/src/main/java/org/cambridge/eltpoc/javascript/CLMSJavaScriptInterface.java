package org.cambridge.eltpoc.javascript;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.MediaController;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.cambridge.eltpoc.ELTApplication;
import org.cambridge.eltpoc.LoginActivity;
import org.cambridge.eltpoc.R;
import org.cambridge.eltpoc.api.ClassDeserializer;
import org.cambridge.eltpoc.api.ContentScoreDeserializer;
import org.cambridge.eltpoc.api.ContentURLDeserializer;
import org.cambridge.eltpoc.api.CourseDeserializer;
import org.cambridge.eltpoc.api.LessonScoreDeserializer;
import org.cambridge.eltpoc.api.TestHarnessService;
import org.cambridge.eltpoc.api.UnitScoreDeserializer;
import org.cambridge.eltpoc.api.UserDeserializer;
import org.cambridge.eltpoc.connections.DownloadAsync;
import org.cambridge.eltpoc.connections.HTTPConnectionPost;
import org.cambridge.eltpoc.model.CLMSClass;
import org.cambridge.eltpoc.model.CLMSClassList;
import org.cambridge.eltpoc.model.CLMSContentScore;
import org.cambridge.eltpoc.model.CLMSContentScoreList;
import org.cambridge.eltpoc.model.CLMSContentURL;
import org.cambridge.eltpoc.model.CLMSCourse;
import org.cambridge.eltpoc.model.CLMSCourseList;
import org.cambridge.eltpoc.model.CLMSLessonScore;
import org.cambridge.eltpoc.model.CLMSLessonScoreList;
import org.cambridge.eltpoc.model.CLMSModel;
import org.cambridge.eltpoc.model.CLMSUnitScore;
import org.cambridge.eltpoc.model.CLMSUnitScoreList;
import org.cambridge.eltpoc.model.CLMSUser;
import org.cambridge.eltpoc.util.DialogUtils;
import org.cambridge.eltpoc.util.Misc;
import org.cambridge.eltpoc.util.RealmTransactionUtils;
import org.cambridge.eltpoc.util.SharedPreferencesUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import io.realm.RealmObject;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by mbaltazar on 6/10/15.
 */
public class CLMSJavaScriptInterface {
    private Activity activity;
    private MediaController mediaController;
    private TestHarnessService testHarnessService;
    private final String END_POINT = "http://content-poc-api.cambridgelms.org";
    private Gson defaultGson;

    private CLMSModel webModel;

    public CLMSJavaScriptInterface(Activity activity, CLMSModel webModel) {
        this.activity = activity;
        defaultGson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
        this.webModel = webModel;
    }

    @JavascriptInterface
    public void saveAuthenticationLogin(String jsonAuth, String username, String password) {
        Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaringClass().equals(RealmObject.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();
        CLMSUser user = gson.fromJson(jsonAuth, CLMSUser.class);
        user.setUsername(username);
        user.setPassword(password);
        ELTApplication.getInstance().setCurrentUser(user);

        updateUser(user.getAccessToken(), user);
        saveCourseList(user.getAccessToken());
        saveClassList(user.getAccessToken());
    }

    @JavascriptInterface
    public void updateUser(String authentication, final CLMSUser user) {
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .registerTypeAdapter(CLMSUser.class, new UserDeserializer())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(END_POINT)
                .setConverter(new GsonConverter(gson))
                .build();

        testHarnessService = restAdapter.create(TestHarnessService.class);
        testHarnessService.getAboutInfo("Bearer " + authentication, new Callback<CLMSUser>() {
            @Override
            public void success(CLMSUser clmsUser, Response response) {
                user.setDisplayName(clmsUser.getDisplayName());
                user.setId(clmsUser.getId());
                RealmTransactionUtils.saveUser(activity, user);
                ELTApplication.getInstance().setCurrentUser(user);
                SharedPreferencesUtils.updateLoggedInUser(activity, user.getUsername(), user.getPassword(),
                        user.getDisplayName(), user.getId());
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getMessage() != null)
                    Log.e("getUser()", error.getMessage());
            }
        });
    }

    @JavascriptInterface
    public void saveCourseList(String authentication) {
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .registerTypeAdapter(CLMSCourse.class, new CourseDeserializer())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(END_POINT)
                .setConverter(new GsonConverter(gson))
                .build();

        testHarnessService = restAdapter.create(TestHarnessService.class);
        testHarnessService.getCoursesList("Bearer " + authentication, new Callback<CLMSCourseList>() {
            @Override
            public void success(CLMSCourseList clmsCourseList, Response response) {
                RealmTransactionUtils.saveCourseList(activity, clmsCourseList);
                ELTApplication.getInstance().getCourseListObserver().setCourseList(clmsCourseList);
                ELTApplication.getInstance().getCourseListObserver().notifyObservers();
                ELTApplication.getInstance().getWebModel().setIsCourseRetrieved(true);
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getMessage() != null)
                    Log.e("getCourseList()", error.getMessage());
            }
        });
    }

    @JavascriptInterface
    public void saveClassList(String authentication) {
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .registerTypeAdapter(CLMSClass.class, new ClassDeserializer())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(END_POINT)
                .setConverter(new GsonConverter(gson))
                .build();

        testHarnessService = restAdapter.create(TestHarnessService.class);
        testHarnessService.getClassesList("Bearer " + authentication, new Callback<CLMSClassList>() {
            @Override
            public void success(CLMSClassList clmsClassList, Response response) {
                // Save class lists here!
                for (CLMSClass clmsClass : clmsClassList.getClassLists()) {
                    clmsClass.setUniqueId(ELTApplication.getInstance().getCurrentUser().getUsername() +
                            clmsClass.getId());
                    RealmTransactionUtils.saveClass(activity, clmsClass, true);
                }
                ELTApplication.getInstance().getClassListObserver().setClassList(clmsClassList);
                ELTApplication.getInstance().getClassListObserver().notifyObservers();
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getMessage() != null)
                    Log.e("getClassList()", error.getMessage());
            }
        });
    }

    @JavascriptInterface
    public void saveUnitScoreList(final String tokenAccess, final int classId, final long userId) {

        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .registerTypeAdapter(CLMSUnitScore.class, new UnitScoreDeserializer())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(END_POINT)
                .setConverter(new GsonConverter(gson))
                .build();

        testHarnessService = restAdapter.create(TestHarnessService.class);
        testHarnessService.getUnitScoreList("Bearer " + tokenAccess, classId, userId, new Callback<CLMSUnitScoreList>() {
            @Override
            public void success(CLMSUnitScoreList clmsUnitScoreList, Response response) {
                if (clmsUnitScoreList != null && clmsUnitScoreList.getUnitScoreList() != null) {
                    int count = 0;
                    for (CLMSUnitScore score : clmsUnitScoreList.getUnitScoreList()) {
//                        System.out.println("UNIT SCORE: " + score.getUnitName());
                        score.setUniqueId(ELTApplication.getInstance().getCurrentUser().getUsername() + score.getId());
                        score.setClassId(classId);
                        RealmTransactionUtils.saveUnitScore(activity, score, true);
                        saveLessonScoreList(tokenAccess, classId, userId, score.getId(),
                                count == clmsUnitScoreList.getUnitScoreList().size() - 1);
                        ++count;
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getMessage() != null)
                    Log.e("getUnitScore()", error.getMessage());
            }
        });
    }

    @JavascriptInterface
    public void saveLessonScoreList(final String tokenAccess, final int classId, final long userId, final int unitId,
                                    final boolean isLastContent) {
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .registerTypeAdapter(CLMSLessonScore.class, new LessonScoreDeserializer())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(END_POINT)
                .setConverter(new GsonConverter(gson))
                .build();

        testHarnessService = restAdapter.create(TestHarnessService.class);
        testHarnessService.getLessonScoreList("Bearer " + tokenAccess, classId, userId, unitId, new Callback<CLMSLessonScoreList>() {

            @Override
            public void success(CLMSLessonScoreList clmsLessonScoreList, Response response) {
                if (clmsLessonScoreList.getLessonScoreList() != null) {
                    for (CLMSLessonScore score : clmsLessonScoreList.getLessonScoreList()) {
//                        System.out.println("LESSON SCORE: " + score.getLessonName());
                        score.setUnitId(unitId);
                        score.setClassId(classId);
                        score.setUniqueId(ELTApplication.getInstance().getCurrentUser().getUsername() + score.getId());
                        RealmTransactionUtils.saveLessonScore(activity, score, true);
                        saveContentScoreList(tokenAccess, classId, userId, unitId, score.getId(), isLastContent);
                    }
                }
                saveContentScoreList(tokenAccess, classId, userId, unitId, 0, isLastContent);
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getMessage() != null)
                    Log.e("getLessonScore()", error.getMessage());
                if (isLastContent) {
                    ArrayList<CLMSUnitScore> unitScores =
                            RealmTransactionUtils.getUnitScores(activity, classId);
                    ELTApplication.getInstance().getUnitScoreListObserver().setScoreList(unitScores);
                    ELTApplication.getInstance().getUnitScoreListObserver().notifyObservers();
                }
            }
        });
    }

    @JavascriptInterface
    public void saveContentScoreList(String tokenAccess, final int classId, long userId,
                                     final int unitId, final int lessonId, final boolean isLastContent) {
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .registerTypeAdapter(CLMSContentScore.class, new ContentScoreDeserializer())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(END_POINT)
                .setConverter(new GsonConverter(gson))
                .build();

        testHarnessService = restAdapter.create(TestHarnessService.class);
        testHarnessService.getContentScoreList("Bearer " + tokenAccess, classId, userId, unitId, lessonId, new Callback<CLMSContentScoreList>() {

            @Override
            public void success(CLMSContentScoreList clmsContentScoreList, Response response) {
                if (clmsContentScoreList.getContentScoreList() != null) {
                    for (CLMSContentScore score : clmsContentScoreList.getContentScoreList()) {
                        score.setClassId(classId);
                        score.setUnitId(unitId);
                        score.setLessonId(lessonId);
                        score.setUniqueId(ELTApplication.getInstance().getCurrentUser().getUsername() + score.getId());
                        RealmTransactionUtils.saveContentScore(activity, score, true);
                    }
                }
                if (lessonId == 0 && clmsContentScoreList.getContentScoreList() != null &&
                        clmsContentScoreList.getContentScoreList().size() > 0) {
                    CLMSLessonScore score = new CLMSLessonScore();
                    score.setUnitId(unitId);
                    score.setCalcProgress(0);
                    score.setClassId(classId);
                    score.setId(0);
                    score.setLessonName("CONTENTS");
                    score.setUniqueId(ELTApplication.getInstance().getCurrentUser().getUsername()
                            + score.getId() + score.getUnitId());
                    RealmTransactionUtils.saveLessonScore(activity, score, true);
                }
                if (isLastContent) {
                    ArrayList<CLMSUnitScore> unitScores =
                            RealmTransactionUtils.getUnitScores(activity, classId);
                    ELTApplication.getInstance().getUnitScoreListObserver().setScoreList(unitScores);
                    ELTApplication.getInstance().getUnitScoreListObserver().notifyObservers();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getMessage() != null)
                    Log.e("getContentScore()", error.getMessage());
                if (isLastContent) {
                    ArrayList<CLMSUnitScore> unitScores =
                            RealmTransactionUtils.getUnitScores(activity, classId);
                    ELTApplication.getInstance().getUnitScoreListObserver().setScoreList(unitScores);
                    ELTApplication.getInstance().getUnitScoreListObserver().notifyObservers();
                }
            }
        });
    }

    @JavascriptInterface
    public void downloadContent(final int courseId, final int classId,
                                final int unitId, final int lessonId, final int contentId) {
        String authentication = ELTApplication.getInstance().getCurrentUser().getAccessToken();
        final CLMSContentScore contentScore = RealmTransactionUtils.getContentScore(activity, classId,
                unitId, lessonId, contentId);
        if (contentScore != null && contentScore.getDownloadedFile() != null &&
                !contentScore.getDownloadedFile().isEmpty()) {
            ELTApplication.getInstance().getLinkModel().setWebLink("file:///"+
                    contentScore.getDownloadedFile() + "/index.html");
            ELTApplication.getInstance().getLinkModel().setClassName(contentScore.getContentName());
            ELTApplication.getInstance().getLinkModel().notifyObservers();
        } else {
            Gson gson = new GsonBuilder()
                    .setExclusionStrategies(new ExclusionStrategy() {
                        @Override
                        public boolean shouldSkipField(FieldAttributes f) {
                            return f.getDeclaringClass().equals(RealmObject.class);
                        }

                        @Override
                        public boolean shouldSkipClass(Class<?> clazz) {
                            return false;
                        }
                    })
                    .registerTypeAdapter(CLMSContentURL.class, new ContentURLDeserializer())
                    .create();

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(END_POINT)
                    .setConverter(new GsonConverter(gson))
                    .build();

            testHarnessService = restAdapter.create(TestHarnessService.class);
            testHarnessService.getContentUrl("Bearer " + authentication, courseId, unitId, lessonId,
                    contentId, new Callback<CLMSContentURL>() {
                        @Override
                        public void success(CLMSContentURL clmsContentURL, Response response) {
                            downloadContent(clmsContentURL.getUrl(), courseId, classId,
                                    unitId, lessonId, contentId);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            if (error.getMessage() != null) {
                                Log.e("getContentUrl()", error.getMessage());
                                DialogUtils.createDialog(activity, "ERROR", "The content " +
                                        "does not exist!");
                            }
                        }
                    });
        }
    }

    @JavascriptInterface
    public void downloadContent(String url,
                                int courseId, int classId,
                                int unitId, int lessonId, int contentId) {
        //SAMPLE: "http://content-poc.cambridgelms.org/touchstone2/p/sites/default
        // /files/html_content_zip/UN_UVM_OWB_1B_LS_U06_E10_HTML5_GMV01.zip
        CLMSContentScore contentScore = RealmTransactionUtils.getContentScore(activity, classId,
                unitId, lessonId, contentId);
        String[] str = url.split("/");
        DownloadAsync downloadAsync = new DownloadAsync(activity, contentScore, url,
                activity.getFilesDir().getAbsolutePath() , str[str.length - 1]);
        downloadAsync.execute();
    }

    public void authenticateLogin(final String user, final String password) throws MalformedURLException {
        //HTTP Connection Approach
        ContentValues values = new ContentValues();
        values.put("grant_type", "password");
        values.put("client_id", "app");
        values.put("username", user);
        values.put("password", password);

        String errorTitle = "Authentication Failed";
        String urlString = "http://content-poc-api.cambridgelms.org/v1.0/authorize";
        HTTPConnectionPost post = new HTTPConnectionPost(activity, new URL(urlString), values,
                errorTitle);
        post.setOnPostCompletedListener(new HTTPConnectionPost.OnPostCompletedListener() {
            @Override
            public void onPostCompleted(String response, boolean isFailed) {
                JSONObject object = null;
                try {
                    object = (JSONObject) new JSONTokener(response).nextValue();
                    if (!object.isNull("access_token")) {
                        if (object.optString("access_token") != null) {
                            SharedPreferencesUtils.updateLoggedInUser(activity, user, password, "", 0);
                            saveAuthenticationLogin(response, user, password);
                        }
                    }
                    if (!object.isNull("error_message")) {
                        if (object.optString("error_message") != null) {
                            isFailed = true;
                            webModel.setErrorMessage(object.getString("error_message"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (webModel != null) {
                    webModel.setHasError(isFailed);
                    webModel.notifyObservers();
                }
            }
        });
        post.execute();
    }

    @JavascriptInterface
    public void signOutUser() {
        SharedPreferencesUtils.updateLoggedInUser(activity, "", "", "", 0);
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

    @JavascriptInterface
    public void print(String num) {
        System.out.println("THIS IS A TEST: " + num);
    }

    @JavascriptInterface
    public void updateClassName(final String className, final int courseId, final int classId) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ELTApplication.getInstance().getLinkModel().setClassName(className);
                webModel.setIsLoading(true);
                webModel.notifyObservers();
//                webModel.setIsLoading(false);
                ELTApplication.getInstance().getUnitScoreListObserver().setCourseId(courseId);
                ArrayList<CLMSUnitScore> unitScores = RealmTransactionUtils.getUnitScores(activity, classId);
                if (unitScores.size() == 0) {
                    CLMSUser user = ELTApplication.getInstance().getCurrentUser();
                    saveUnitScoreList(user.getAccessToken(), classId, user.getId());
                } else {
                    ELTApplication.getInstance().getUnitScoreListObserver().setScoreList(unitScores);
                    ELTApplication.getInstance().getUnitScoreListObserver().notifyObservers();
                }
            }
        });
    }

    @JavascriptInterface
    public boolean hasInternetConnection() {
        return Misc.hasInternetConnection(activity);
    }

    @JavascriptInterface
    public void updateContentScores() {
        ArrayList<CLMSContentScore> contentScores = ELTApplication.getInstance()
                .getContentScoreListObserver().getScoreList();
        CLMSUser user = ELTApplication.getInstance().getCurrentUser();
        int count = 0;
//        for(CLMSContentScore contentScore : contentScores) {
            //updateUser(user.getRefreshToken(), user);
            updateContentScore(user.getAccessToken(), user.getId(), contentScores.get(0), 0, 0,
                    Calendar.getInstance().getTimeInMillis()/1000L,
                    count == contentScores.size()-1);
            ++count;
//        }
    }

    @JavascriptInterface
    public void updateContentScore(String tokenAccess, long userId, CLMSContentScore contentScore,
                                   int score, int progress, long lastaccess, final boolean isLastContent) {
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .registerTypeAdapter(CLMSContentScore.class, new ContentScoreDeserializer())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(END_POINT)
                .setConverter(new GsonConverter(gson))
                .build();

        testHarnessService = restAdapter.create(TestHarnessService.class);
        testHarnessService.updateContentScore(contentScore.getClassId(), userId,
                contentScore.getUnitId(), contentScore.getLessonId(), contentScore.getId(),
                score, progress, lastaccess, new Callback<CLMSContentScoreList>() {
                    @Override
                    public void success(CLMSContentScoreList clmsContentScoreList, Response response) {
                        if(isLastContent) {
                            ELTApplication.getInstance().getWebModel().setIsSynced(true);
                            ELTApplication.getInstance().getWebModel().notifyObservers();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (error.getMessage() != null)
                            Log.e("putContentScore()", error.getMessage());
                    }
                }
        );
    }

    @JavascriptInterface
    public boolean isPhone() {
        return activity.getResources().getBoolean(R.bool.isPhone);
    }
}