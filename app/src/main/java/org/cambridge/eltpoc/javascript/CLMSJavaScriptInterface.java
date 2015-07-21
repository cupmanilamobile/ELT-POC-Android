package org.cambridge.eltpoc.javascript;

import android.app.Activity;
import android.content.ContentValues;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.cambridge.eltpoc.Constants;
import org.cambridge.eltpoc.ELTApplication;
import org.cambridge.eltpoc.R;
import org.cambridge.eltpoc.api.ClassDeserializer;
import org.cambridge.eltpoc.api.ContentScoreDeserializer;
import org.cambridge.eltpoc.api.ContentURLDeserializer;
import org.cambridge.eltpoc.api.CourseDeserializer;
import org.cambridge.eltpoc.api.LessonScoreDeserializer;
import org.cambridge.eltpoc.api.TestHarnessService;
import org.cambridge.eltpoc.api.UnitScoreDeserializer;
import org.cambridge.eltpoc.api.UserDeserializer;
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
import org.cambridge.eltpoc.util.RealmServiceHelper;
import org.cambridge.eltpoc.util.RealmTransactionUtils;
import org.cambridge.eltpoc.util.SharedPreferencesUtils;
import org.cambridge.eltpoc.util.WebServiceHelper;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.CopyOnWriteArrayList;

import io.realm.RealmObject;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by mbaltazar on 6/10/15.
 */
public class CLMSJavaScriptInterface {
    private Activity activity;
    private TestHarnessService testHarnessService;
    private CLMSModel webModel;
    private CopyOnWriteArrayList<CLMSUnitScore> unitScores = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<CLMSLessonScore> lessonScores = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<CLMSContentScore> contentScores = new CopyOnWriteArrayList<>();
    private ELTApplication instance = ELTApplication.getInstance();

    private interface OnLoggedInListener {
        void onLoggedIn();
    }

    public CLMSJavaScriptInterface(Activity activity, CLMSModel webModel) {
        this.activity = activity;
        this.webModel = webModel;
    }

    @JavascriptInterface
    public void saveAuthenticationLogin(String jsonAuth, String username, String password,
                                        OnLoggedInListener onLoggedInListener) {
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
        instance.setCurrentUser(user);
        updateUser(user.getAccessToken(), user);

        if (onLoggedInListener == null) {
            ArrayList<CLMSCourse> courses = RealmTransactionUtils.getAllCourses(activity);
            if (courses.size() == 0) {
                saveCourseList(user.getAccessToken());
                saveClassList(user.getAccessToken());
            }
        } else
            onLoggedInListener.onLoggedIn();
    }

    @JavascriptInterface
    public void updateUser(String authentication, final CLMSUser user) {
        RestAdapter restAdapter = createAdapter(CLMSUser.class, new UserDeserializer());
        testHarnessService = restAdapter.create(TestHarnessService.class);
        testHarnessService.getAboutInfo(RealmServiceHelper.createBearerToken(authentication),
                new Callback<CLMSUser>() {
                    @Override
                    public void success(CLMSUser clmsUser, Response response) {
                        WebServiceHelper.saveUser(activity, user, clmsUser.getDisplayName(),
                                clmsUser.getId());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (error.getMessage() != null)
                            Log.e("getUser()", error.getMessage());
                    }
                });
    }

    @JavascriptInterface
    public void saveCourseList(final String authentication) {
        RestAdapter restAdapter = createAdapter(CLMSCourse.class, new CourseDeserializer());
        testHarnessService = restAdapter.create(TestHarnessService.class);
        testHarnessService.getCoursesList(RealmServiceHelper.createBearerToken(authentication),
                new Callback<CLMSCourseList>() {
                    @Override
                    public void success(CLMSCourseList clmsCourseList, Response response) {
                        RealmTransactionUtils.SaveCourseAsync saveCourseAsync = new
                                RealmTransactionUtils.SaveCourseAsync(activity, clmsCourseList);
                        saveCourseAsync.execute();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (error.getMessage() != null) {
                            Log.e("getCourseList()", error.getMessage());
                            OnLoggedInListener onLoggedInListener = new OnLoggedInListener() {
                                @Override
                                public void onLoggedIn() {
                                    saveCourseList(instance.getCurrentUser().getAccessToken());
                                }
                            };
                            requireLog(error.getMessage(), onLoggedInListener);
                        }
                    }
                });
    }

    @JavascriptInterface
    public void saveClassList(final String authentication) {
        RestAdapter restAdapter = createAdapter(CLMSClass.class, new ClassDeserializer());
        testHarnessService = restAdapter.create(TestHarnessService.class);
        testHarnessService.getClassesList(RealmServiceHelper.createBearerToken(authentication),
                new Callback<CLMSClassList>() {
                    @Override
                    public void success(CLMSClassList clmsClassList, Response response) {
                        RealmTransactionUtils.SaveClassAsync saveClassAsync = new
                                RealmTransactionUtils.SaveClassAsync(activity, clmsClassList);
                        saveClassAsync.execute();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (error.getMessage() != null) {
                            Log.e("getClassList()", error.getMessage());
                            OnLoggedInListener onLoggedInListener = new OnLoggedInListener() {
                                @Override
                                public void onLoggedIn() {
                                    saveClassList(instance.getCurrentUser().getAccessToken());
                                }
                            };
                            requireLog(error.getMessage(), onLoggedInListener);
                        }
                    }
                });
    }

    @JavascriptInterface
    public void saveUnitScoreList(final String tokenAccess, final int classId, final long userId) {
        unitScores.clear();
        lessonScores.clear();
        contentScores.clear();
        RestAdapter restAdapter = createAdapter(CLMSUnitScore.class, new UnitScoreDeserializer());
        testHarnessService = restAdapter.create(TestHarnessService.class);
        testHarnessService.getUnitScoreList(RealmServiceHelper.createBearerToken(tokenAccess),
                classId, userId, new Callback<CLMSUnitScoreList>() {
                    @Override
                    public void success(CLMSUnitScoreList clmsUnitScoreList, Response response) {
                        if (clmsUnitScoreList != null && clmsUnitScoreList.getUnitScoreList() != null) {
                            int count = 0;
                            for (CLMSUnitScore score : clmsUnitScoreList.getUnitScoreList()) {
                                score.setUniqueId(instance.getCurrentUser().getUsername()
                                        + "-" + classId + "-" + score.getId());
                                score.setClassId(classId);
                                unitScores.add(score);
                                saveLessonScoreList(tokenAccess, classId, userId, score.getId(),
                                        count == clmsUnitScoreList.getUnitScoreList().size() - 1);
                                if (clmsUnitScoreList.getUnitScoreList().size() - 1 == count)
                                    WebServiceHelper.notifyContentScores(activity, classId,
                                            Constants.UNIT_TYPE, unitScores, lessonScores,
                                            contentScores);
                                ++count;
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (error.getMessage() != null) {
                            Log.e("getUnitScore()", error.getMessage());
                            OnLoggedInListener onLoggedInListener = new OnLoggedInListener() {
                                @Override
                                public void onLoggedIn() {
                                    CLMSUser user = instance.getCurrentUser();
                                    saveUnitScoreList(user.getAccessToken(), classId, userId);
                                }
                            };
                            requireLog(error.getMessage(), onLoggedInListener);
                        }
                    }
                });
    }

    @JavascriptInterface
    public void saveLessonScoreList(final String tokenAccess, final int classId, final long userId,
                                    final int unitId, final boolean isLastContent) {
        RestAdapter restAdapter = createAdapter(CLMSLessonScore.class, new LessonScoreDeserializer());
        testHarnessService = restAdapter.create(TestHarnessService.class);
        testHarnessService.getLessonScoreList(RealmServiceHelper.createBearerToken(tokenAccess),
                classId, userId, unitId, new Callback<CLMSLessonScoreList>() {
                    @Override
                    public void success(CLMSLessonScoreList clmsLessonScoreList, Response response) {
                        if (clmsLessonScoreList.getLessonScoreList() != null) {
                            for (CLMSLessonScore score : clmsLessonScoreList.getLessonScoreList()) {
                                score.setUnitId(unitId);
                                score.setClassId(classId);
                                score.setUniqueId(instance.getCurrentUser().getUsername()
                                        + "-" + unitId + "-" + classId + "-" + score.getId());
                                lessonScores.add(score);
                                saveContentScoreList(tokenAccess, classId, userId, unitId,
                                        score.getId(), isLastContent);
                            }
                        }
                        saveContentScoreList(tokenAccess, classId, userId, unitId, 0, isLastContent);
                        if (isLastContent)
                            WebServiceHelper.notifyContentScores(activity, classId,
                                    Constants.LESSON_TYPE, unitScores, lessonScores, contentScores);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (error.getMessage() != null) {
                            Log.e("getLessonScore()", error.getMessage());
                            OnLoggedInListener onLoggedInListener = new OnLoggedInListener() {
                                @Override
                                public void onLoggedIn() {
                                    saveLessonScoreList(instance.getCurrentUser().getAccessToken(),
                                            classId, userId, unitId, isLastContent);
                                }
                            };
                            requireLog(error.getMessage(), onLoggedInListener);
                        }
                        if (isLastContent)
                            WebServiceHelper.notifyContentScores(activity, classId,
                                    Constants.LESSON_TYPE, unitScores, lessonScores, contentScores);
                    }
                });
    }

    @JavascriptInterface
    public void saveContentScoreList(final String tokenAccess, final int classId, final long userId,
                                     final int unitId, final int lessonId, final boolean isLastContent) {
        RestAdapter restAdapter = createAdapter(CLMSContentScore.class, new ContentScoreDeserializer());
        testHarnessService = restAdapter.create(TestHarnessService.class);
        testHarnessService.getContentScoreList(RealmServiceHelper.createBearerToken(tokenAccess),
                classId, userId, unitId, lessonId, new Callback<CLMSContentScoreList>() {
                    @Override
                    public void success(CLMSContentScoreList clmsContentScoreList, Response response) {
                        if (clmsContentScoreList.getContentScoreList() != null) {
                            for (CLMSContentScore score : clmsContentScoreList.getContentScoreList()) {
                                score.setClassId(classId);
                                score.setUnitId(unitId);
                                score.setLessonId(lessonId);
                                score.setUniqueId(instance.getCurrentUser().getUsername() + "-" +
                                        classId + "-" + unitId + "-" + lessonId + "-" + score.getId());
                                contentScores.add(score);
                            }
                        }
                        if (lessonId == 0 && clmsContentScoreList.getContentScoreList() != null &&
                                clmsContentScoreList.getContentScoreList().size() > 0)
                            lessonScores.add(WebServiceHelper.createDummyLesson(classId, unitId,
                                    lessonId));
                        if (isLastContent)
                            WebServiceHelper.notifyContentScores(activity, classId,
                                    Constants.CONTENT_TYPE, unitScores, lessonScores, contentScores);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (error.getMessage() != null) {
                            Log.e("getContentScore()", error.getMessage());
                            OnLoggedInListener onLoggedInListener = new OnLoggedInListener() {
                                @Override
                                public void onLoggedIn() {
                                    CLMSUser user = instance.getCurrentUser();
                                    saveContentScoreList(user.getAccessToken(), classId, userId,
                                            unitId, lessonId, isLastContent);
                                }
                            };
                            requireLog(error.getMessage(), onLoggedInListener);
                        }
                        if (isLastContent)
                            WebServiceHelper.notifyContentScores(activity, classId,
                                    Constants.CONTENT_TYPE, unitScores, lessonScores, contentScores);
                    }
                });
    }

    @JavascriptInterface
    public void downloadContent(final int courseId, final int classId,
                                final int unitId, final int lessonId, final int contentId) {
        String authentication = instance.getCurrentUser().getAccessToken();
        final CLMSContentScore contentScore = RealmTransactionUtils.getContentScore(activity, classId,
                unitId, lessonId, contentId);
        if (contentScore != null && contentScore.getDownloadedFile() != null &&
                !contentScore.getDownloadedFile().equalsIgnoreCase("")) {
            instance.getLinkModel().setWebLink("file:///" +
                    contentScore.getDownloadedFile() + "/index.html");
            instance.getLinkModel().setContentName(contentScore.getContentName());
            instance.getLinkModel().notifyObservers();
            SharedPreferencesUtils.addContentSync(activity, contentScore.getUniqueId());
        } else {
            showLoadingScreen(true);
            RestAdapter restAdapter = createAdapter(CLMSContentURL.class, new ContentURLDeserializer());
            testHarnessService = restAdapter.create(TestHarnessService.class);
            testHarnessService.getContentUrl(RealmServiceHelper.createBearerToken(authentication),
                    courseId, unitId, lessonId, contentId, new Callback<CLMSContentURL>() {
                        @Override
                        public void success(CLMSContentURL clmsContentURL, Response response) {
                            showLoadingScreen(false);
                            WebServiceHelper.downloadContent(activity, webModel,
                                    clmsContentURL.getUrl(), courseId, classId, unitId, lessonId,
                                    contentId);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            showLoadingScreen(false);
                            if (error.getMessage() != null) {
                                Log.e("getContentUrl()", error.getMessage());
                                OnLoggedInListener onLoggedInListener = new OnLoggedInListener() {
                                    @Override
                                    public void onLoggedIn() {
                                        downloadContent(courseId, classId, unitId, lessonId, contentId);
                                    }
                                };
                                requireLog(error.getMessage(), onLoggedInListener);
                                if (!error.getMessage().contains("Unauthorized"))
                                    DialogUtils.createDialog(activity, "ERROR", "The content " +
                                            "does not exist!");
                            }
                        }
                    });
        }
    }

    public void authenticateLogin(final String user, final String password,
                                  final OnLoggedInListener onLoggedInListener) throws MalformedURLException {
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
                            saveAuthenticationLogin(response, user, password, onLoggedInListener);
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

                if (webModel != null && onLoggedInListener == null) {
                    webModel.setHasError(isFailed);
                    webModel.notifyObservers();
                }
            }
        });
        post.execute();
    }

    @JavascriptInterface
    public void print(String num) {
        System.out.println("THIS IS A TEST: " + num);
    }

    @JavascriptInterface
    public void updateContents(final String className, final int courseId, final int classId) {
        WebServiceHelper.updateContents(activity, className, courseId, classId);
        ArrayList<CLMSUnitScore> unitScores = RealmTransactionUtils.getUnitScores(activity, classId);
        if (unitScores.size() == 0 && Misc.hasInternetConnection(activity)) {
            showLoadingScreen(true);
            CLMSUser user = instance.getCurrentUser();
            saveUnitScoreList(user.getAccessToken(), classId, user.getId());
        } else {
            showLoadingScreen(true);
            instance.getContentScoreListObserver().notifyObservers();
        }
    }

    @JavascriptInterface
    public boolean hasInternetConnection() {
        return Misc.hasInternetConnection(activity);
    }

    @JavascriptInterface
    public void updateContentScores() {
        ArrayList<CLMSContentScore> contentScores = WebServiceHelper.getSyncContentScores(activity);
        CLMSUser user = instance.getCurrentUser();
        int count = 0;
        instance.getWebModel().setSyncMessage(
                activity.getString(R.string.sync_message));
        if (contentScores.size() == 0) {
            instance.getWebModel().setSyncMessage(
                    activity.getString(R.string.sync_nothing));
            WebServiceHelper.syncContents(activity, false);
        }
        for (CLMSContentScore contentScore : contentScores) {
            updateContentScore(user.getAccessToken(), user.getId(), contentScore, 100, 100,
                    Calendar.getInstance().getTimeInMillis(),
                    count == contentScores.size() - 1);
            ++count;
        }
    }

    @JavascriptInterface
    public void updateContentScore(final String tokenAccess, final long userId,
                                   final CLMSContentScore contentScore, final int score,
                                   final int progress, final long lastaccess,
                                   final boolean isLastContent) {
        RestAdapter restAdapter = createAdapter(CLMSContentScore.class, new ContentScoreDeserializer());
        testHarnessService = restAdapter.create(TestHarnessService.class);
        testHarnessService.updateContentScore(RealmServiceHelper.createBearerToken(tokenAccess),
                contentScore.getClassId(), userId, contentScore.getUnitId(),
                contentScore.getLessonId(), contentScore.getId(), score, progress, lastaccess,
                new Callback<CLMSContentScoreList>() {
                    @Override
                    public void success(CLMSContentScoreList clmsContentScoreList, Response response) {
                        if (clmsContentScoreList != null) {
                            RealmTransactionUtils.updateContentScoreProgress(activity, contentScore, 100);
                            instance.getWebModel().setSyncMessage(
                                    instance.getWebModel().getSyncMessage()
                                            .concat("\n" + contentScore.getContentName()));
                        } else
                            instance.getWebModel().setSyncMessage(
                                    activity.getString(R.string.sync_problem));
                        WebServiceHelper.syncContents(activity, isLastContent);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (error.getMessage() != null)
                            Log.e("putContentScore()", error.getMessage());
                        RealmTransactionUtils.updateContentScoreProgress(activity, contentScore, 100);
                        instance.getWebModel().setSyncMessage(
                                instance.getWebModel().getSyncMessage()
                                        .concat("\n" + contentScore.getContentName()));
                        OnLoggedInListener onLoggedInListener = new OnLoggedInListener() {
                            @Override
                            public void onLoggedIn() {
                                CLMSUser user = instance.getCurrentUser();
                                updateContentScore(user.getAccessToken(), userId, contentScore,
                                        score, progress, lastaccess, isLastContent);
                            }
                        };
                        requireLog(error.getMessage(), onLoggedInListener);
                        if (isLastContent)
                            WebServiceHelper.syncContents(activity, true);
                    }
                }
        );
    }

    @JavascriptInterface
    public boolean isPhone() {
        return activity.getResources().getBoolean(R.bool.isPhone);
    }

    @JavascriptInterface
    public void showLoadingScreen(boolean isLoading) {
        WebServiceHelper.showLoadingScreen(webModel, isLoading);
    }

    @JavascriptInterface
    public void deleteContent(final int courseId, final int classId,
                              final int unitId, final int lessonId, final int contentId) {
        WebServiceHelper.deleteContent(courseId, classId, unitId, lessonId, contentId, activity,
                webModel);
    }

    private RestAdapter createAdapter(Type type, Object object) {
        return RealmServiceHelper.createAdapter(type, object, Constants.END_POINT);
    }

    private void requireLog(String errorMessage, final OnLoggedInListener onLoggedInListener) {
        if (errorMessage.contains("Unauthorized")) {
            DialogUtils.OnOptionSelectedListener onOptionSelectedListener =
                    new DialogUtils.OnOptionSelectedListener() {
                        @Override
                        public void onOptionSelected() {
                            CLMSUser user = SharedPreferencesUtils.getLoggedInUser(activity);
                            try {
                                authenticateLogin(user.getUsername(), user.getPassword(), onLoggedInListener);
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        }
                    };
            DialogUtils.createDialog(activity, "Unauthorized", "Your access token has expired.\n" +
                    "Please login again.", onOptionSelectedListener);
        }
    }
}