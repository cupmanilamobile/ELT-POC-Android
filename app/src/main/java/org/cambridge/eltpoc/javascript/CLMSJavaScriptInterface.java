package org.cambridge.eltpoc.javascript;

import android.app.Activity;
import android.app.ProgressDialog;
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

import org.cambridge.eltpoc.R;
import org.cambridge.eltpoc.api.ClassDeserializer;
import org.cambridge.eltpoc.api.CourseDeserializer;
import org.cambridge.eltpoc.api.TestHarnessService;
import org.cambridge.eltpoc.customviews.CustomVideoView;
import org.cambridge.eltpoc.download.DownloadReceiver;
import org.cambridge.eltpoc.download.DownloadService;
import org.cambridge.eltpoc.model.CLMSClass;
import org.cambridge.eltpoc.model.CLMSClassList;
import org.cambridge.eltpoc.model.CLMSContentScore;
import org.cambridge.eltpoc.model.CLMSCourse;
import org.cambridge.eltpoc.model.CLMSCourseList;
import org.cambridge.eltpoc.model.CLMSUnitLessonScore;
import org.cambridge.eltpoc.model.CLMSUser;
import org.cambridge.eltpoc.util.RealmTransactionUtils;

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


    public CLMSJavaScriptInterface(Activity activity) {
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
        RealmTransactionUtils.saveUser(activity, user);
        saveCourseList(user.getAccessToken());
//        saveClassList(user.getAccessToken());
//        getUnitScore(user.getAccessToken(), 111597, 108116);
//        getLessonScore(user.getAccessToken(), 111597, 108116, 240);
        getContentScore(user.getAccessToken(), 111597, 108116, 240, 241);
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
                // Save course lists here!
                RealmTransactionUtils.saveCourseList(activity, clmsCourseList);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("getClassList()", error.getMessage());
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
            public void success(CLMSClassList clmsCourseList, Response response) {
                // Save class lists here!
                System.out.println("test");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("getCourseList()", error.getMessage());
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
                System.out.println("test");
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
                System.out.println("test");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("getUnitScore()", error.getMessage());
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
                System.out.println("test");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("getCourseList()", error.getMessage());
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
}