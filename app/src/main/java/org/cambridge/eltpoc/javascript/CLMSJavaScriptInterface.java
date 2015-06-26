package org.cambridge.eltpoc.javascript;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.cambridge.eltpoc.ELTApplication;
import org.cambridge.eltpoc.LoginActivity;
import org.cambridge.eltpoc.R;
import org.cambridge.eltpoc.api.ClassDeserializer;
import org.cambridge.eltpoc.api.CourseDeserializer;
import org.cambridge.eltpoc.api.TestHarnessService;
import org.cambridge.eltpoc.connections.HTTPConnectionPost;
import org.cambridge.eltpoc.customviews.CustomVideoView;
import org.cambridge.eltpoc.download.DownloadReceiver;
import org.cambridge.eltpoc.download.DownloadService;
import org.cambridge.eltpoc.model.CLMSClass;
import org.cambridge.eltpoc.model.CLMSClassList;
import org.cambridge.eltpoc.model.CLMSContentScore;
import org.cambridge.eltpoc.model.CLMSCourse;
import org.cambridge.eltpoc.model.CLMSCourseList;
import org.cambridge.eltpoc.model.CLMSModel;
import org.cambridge.eltpoc.model.CLMSUnitLessonScore;
import org.cambridge.eltpoc.model.CLMSUser;
import org.cambridge.eltpoc.util.RealmTransactionUtils;
import org.cambridge.eltpoc.util.SharedPreferencesUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.MalformedURLException;
import java.net.URL;

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
        }).create()
                ;
        CLMSUser user = gson.fromJson(jsonAuth, CLMSUser.class);
        user.setUsername(username);
        user.setPassword(password);

        ELTApplication.getInstance().setCurrentUser(user);

        RealmTransactionUtils.saveUser(activity, user);
        saveCourseList(user.getAccessToken());
        saveClassList(user.getAccessToken());
//        getUnitScore(user.getAccessToken(), 111597, 108116);
//        getLessonScore(user.getAccessToken(), 111597, 108116, 240);
//        getContentScore(user.getAccessToken(), 111597, 108116, 240, 241);
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
            }

            @Override
            public void failure(RetrofitError error) {
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
                for (CLMSClass clmsClass : clmsClassList.getClassLists())
                    RealmTransactionUtils.saveClass(activity, clmsClass, true);
                ELTApplication.getInstance().getClassListObserver().setClassList(clmsClassList);
                ELTApplication.getInstance().getClassListObserver().notifyObservers();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("getClassList()", error.getMessage());
            }
        });
    }

    @JavascriptInterface
    public void getUnitScore(String tokenAccess, int classId, int userId) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(END_POINT)
                .setConverter(new GsonConverter(defaultGson))
                .build();

        testHarnessService = restAdapter.create(TestHarnessService.class);
        testHarnessService.getUnitScore("Bearer " + tokenAccess, classId, userId, new Callback<CLMSUnitLessonScore>() {
            @Override
            public void success(CLMSUnitLessonScore clmsUnitScore, Response response) {
                RealmTransactionUtils.saveScore(activity, clmsUnitScore, true);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("getUnitScore()", error.getMessage());
            }
        });
    }

    @JavascriptInterface
    public void getLessonScore(String tokenAccess, int classId, int userId, int unitId) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(END_POINT)
                .setConverter(new GsonConverter(defaultGson))
                .build();

        testHarnessService = restAdapter.create(TestHarnessService.class);
        testHarnessService.getLessonScore("Bearer " + tokenAccess, classId, userId, unitId, new Callback<CLMSUnitLessonScore>() {
            @Override
            public void success(CLMSUnitLessonScore clmsUnitLessonScore, Response response) {
                RealmTransactionUtils.saveScore(activity, clmsUnitLessonScore, true);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("getLessonScore()", error.getMessage());
            }
        });
    }

    @JavascriptInterface
    public void getContentScore(String tokenAccess, int classId, int userId, int unitId, int lessonId) {
        RestAdapter restAdapter =  new RestAdapter.Builder()
                .setEndpoint(END_POINT)
                .setConverter(new GsonConverter(defaultGson))
                .build();

        testHarnessService = restAdapter.create(TestHarnessService.class);
        testHarnessService.getContentScore("Bearer " + tokenAccess, classId, userId, unitId, lessonId, new Callback<CLMSContentScore>() {
            @Override
            public void success(CLMSContentScore clmsContentScore, Response response) {
                RealmTransactionUtils.saveContentScore(activity, clmsContentScore, true);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("getContentScore()", error.getMessage());
            }
        });
    }

    @JavascriptInterface
    public void downloadContent() {
        ProgressDialog mProgressDialog = new ProgressDialog(this.activity);
        mProgressDialog.setMessage("Message");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();

        Intent intent = new Intent(this.activity, DownloadService.class);
        intent.putExtra(DownloadService.DOWNLOAD_URL, "http://content-poc.cambridgelms.org/touchstone2/p/sites/default/files/html_content_zip/UN_UVM_OWB_1B_LS_U06_E10_HTML5_GMV01.zip");
        intent.putExtra(DownloadService.DOWNLOAD_RECEIVER, new DownloadReceiver(new Handler(), mProgressDialog));
        intent.putExtra(DownloadService.DOWNLOAD_OUTPUT_DIR, activity.getFilesDir().getAbsolutePath());
        this.activity.startService(intent);
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
                    System.out.println("RESPONSE: " + response);
                    object = (JSONObject) new JSONTokener(response).nextValue();
                    if (object.getString("access_token") != null)
                        saveAuthenticationLogin(response, user, password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SharedPreferencesUtils.updateLoggedInUser(activity, user, password);
                if (webModel != null) {
                    webModel.setHasError(isFailed);
                    webModel.notifyObservers();
                }
            }
        });
        post.execute();
    }

    @JavascriptInterface
    public void showVideo() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final CustomVideoView videoView = (CustomVideoView) (activity.findViewById(R.id.video_player));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        videoView.setVisibility(View.VISIBLE);
                        mediaController = new MediaController(videoView.getContext(), false);
                        final int videoSize = activity.getResources().getDimensionPixelSize(R.dimen.video_size);
                        videoView.setDimensions(videoSize, videoSize);
                        videoView.getHolder().setFixedSize(videoSize, videoSize);
                        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                // set correct height
                                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
                                params.height = mp.getVideoHeight();
                                videoView.setLayoutParams(params);

                                videoView.setMediaController(mediaController);
                                mediaController.show(0);

                                FrameLayout f = (FrameLayout) mediaController.getParent();
                                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                lp.addRule(RelativeLayout.ALIGN_BOTTOM, videoView.getId());
                                lp.addRule(RelativeLayout.ALIGN_LEFT, videoView.getId());
                                lp.width = videoSize;

                                ((LinearLayout) f.getParent()).removeView(f);
                                ((RelativeLayout) videoView.getParent()).addView(f, lp);

                                mediaController.setAnchorView(videoView);
                            }
                        });

                    }
                }, 600);
                String UrlPath = "android.resource://" + activity.getPackageName() + "/" + R.raw.rickroll;
                videoView.setVideoURI(Uri.parse(UrlPath));
            }
        });
    }

    @JavascriptInterface
    public void hideVideo() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final CustomVideoView videoView = (CustomVideoView) (activity.findViewById(R.id.video_player));
                videoView.stopPlayback();
                if (mediaController != null) {
                    FrameLayout f = (FrameLayout) mediaController.getParent();
                    ((RelativeLayout) f.getParent()).removeView(f);
                }//
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        videoView.setVisibility(View.GONE);
                    }
                }, 100);
            }
        });
    }

    @JavascriptInterface
    public void signOutUser() {
        SharedPreferencesUtils.updateLoggedInUser(activity, "", "");
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

    @JavascriptInterface
    public void print() {
        System.out.println("THIS IS A TEST");
    }

    @JavascriptInterface
    public void showClass() {
        String CLASS_URL = "file:///android_asset/www/new_html/content.html";
        ELTApplication.getInstance().getLinkModel().setWebLink(CLASS_URL);
        ELTApplication.getInstance().getLinkModel().notifyObservers();
    }

    @JavascriptInterface
    public void updateClassName(String className) {
        ELTApplication.getInstance().getLinkModel().setClassName(className);
    }
}