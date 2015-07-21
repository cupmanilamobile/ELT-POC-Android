package org.cambridge.eltpoc.util;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.webkit.WebView;

import org.cambridge.eltpoc.Constants;
import org.cambridge.eltpoc.adapters.NavigationDrawerAdapter;
import org.cambridge.eltpoc.model.CLMSClass;
import org.cambridge.eltpoc.model.CLMSContentScore;
import org.cambridge.eltpoc.model.CLMSCourse;
import org.cambridge.eltpoc.model.CLMSLessonScore;
import org.cambridge.eltpoc.model.CLMSUnitScore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by etorres on 7/6/15.
 */
public class WebContentHelper {
    public static ArrayList<Integer> updateCourseContent(Context context, WebView webView,
                                                         boolean isLearning, boolean hasConnection) {
        JSONObject obj;
        JSONArray courseArray = new JSONArray();
        JSONArray classArray;
        ArrayList<CLMSCourse> courses = RealmTransactionUtils.getAllCourses(context);
        ArrayList<Integer> contentCount = new ArrayList<>();
        int learningCount = 0;
        int teachingCount = 0;
        for (CLMSCourse course : courses) {
            ArrayList<CLMSClass> classes = RealmTransactionUtils.getClassesByCourseId(context, course.getNid());
            classArray = new JSONArray();
            obj = new JSONObject();
            String type = Constants.TYPE_BOTH;
            try {
                obj.put(Constants.CLASS_COUNT, classes.size());
                obj.put(Constants.COURSE_NAME, course.getName());
                obj.put(Constants.COURSE_ID, course.getNid());
                obj.put(Constants.COURSE_IMAGE, course.getProductLogo());
                int teachCount = 0;
                int studentCount = 0;
                for (CLMSClass cCLass : classes) {
                    if (cCLass.getClassRole().equalsIgnoreCase(Constants.ROLE_TEACHER)) {
                        ++teachCount;
                    } else if (cCLass.getClassRole().equalsIgnoreCase(Constants.ROLE_STUDENT))
                        ++studentCount;
                    JSONObject classObj = new JSONObject();
                    classObj.put(Constants.CLASS_NAME, cCLass.getClassName());
                    classObj.put(Constants.CLASS_ID, cCLass.getId());
                    if (hasConnection || (!hasConnection && checkClassHasContent(context, cCLass.getId())))
                        classArray.put(classObj);
                }
                if (studentCount > 0 && teachCount > 0) {
                    type = Constants.TYPE_BOTH;
                    learningCount++;
                    teachingCount++;
                } else if (studentCount > 0) {
                    type = Constants.TYPE_LEARNING;
                    learningCount++;
                } else if (teachCount > 0) {
                    type = Constants.TYPE_TEACHING;
                    teachingCount++;
                }
                obj.put(Constants.TYPE, type);
                obj.put(Constants.CLASSES, classArray);
                obj.put(Constants.CLASS_SIZE, classArray.length());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (classArray.length() > 0) {
                if (isLearning) {
                    if (type.equalsIgnoreCase(Constants.TYPE_BOTH) ||
                            type.equalsIgnoreCase(Constants.TYPE_LEARNING))
                        courseArray.put(obj);
                } else {
                    if (type.equalsIgnoreCase(Constants.TYPE_BOTH) ||
                            type.equalsIgnoreCase(Constants.TYPE_TEACHING))
                        courseArray.put(obj);
                }
            }
        }
        contentCount.add(learningCount);
        contentCount.add(teachingCount);
        if (isLearning)
            webView.loadUrl("javascript:addLearningCourse('" + courseArray.length() + "', " + courseArray + ")");
        else
            webView.loadUrl("javascript:addTeachingCourse('" + courseArray.length() + "', " + courseArray + ")");
        return contentCount;
    }

    public static void updateUnitContent(Context context, WebView webView, int courseId,
                                         int classId, boolean isDownloaded) {
        if (!Misc.hasInternetConnection(context))
            isDownloaded = true;
        new UpdateContentAsync(context, webView, courseId, classId, isDownloaded).execute();
    }

    private static String formatProgress(double progress, int length) {
        if (length == 0)
            return "0%";
        return Math.round(progress) + "%";
    }

    private static boolean checkClassHasContent(Context context, int classId) {
        ArrayList<CLMSUnitScore> unitScores = RealmTransactionUtils.getUnitScores(context, classId);
        boolean hasContent = false;
        for (CLMSUnitScore unitScore : unitScores) {
            ArrayList<CLMSLessonScore> lessonScores = RealmTransactionUtils.getLessonScores(context,
                    classId, unitScore.getId());
            for (CLMSLessonScore lessonScore : lessonScores) {
                ArrayList<CLMSContentScore> contentScores = RealmTransactionUtils.getContentScores(
                        context, classId, unitScore.getId(), lessonScore.getId());
                for (CLMSContentScore contentScore : contentScores) {
                    if (!contentScore.getDownloadedFile().equalsIgnoreCase("")) {
                        hasContent = true;
                        break;
                    }
                    if (hasContent)
                        break;
                }
                if (hasContent)
                    break;
            }
            if (hasContent)
                break;
        }
        return hasContent;
    }

    public static void updateTabVisibility(int learningCount, int teachingCount, boolean isInitial,
                                           View learningLayout, View teachingLayout, WebView webView,
                                           NavigationDrawerAdapter drawerAdapter) {
        learningLayout.setVisibility(View.VISIBLE);
//        if(learningCount == 0 && !isInitial)
//            webView.loadUrl(Constants.NO_ENROLLED_URL);
        if (teachingCount > 0)
            teachingLayout.setVisibility(View.VISIBLE);
        else
            teachingLayout.setVisibility(View.GONE);
        drawerAdapter.removeTeachingTab(teachingCount == 0);
        drawerAdapter.notifyDataSetChanged();
    }

    public static void updateTabVisibility(boolean isDownloadedEnabled,
                                           View learningLayout, View teachingLayout) {
        if (isDownloadedEnabled)
            learningLayout.setVisibility(View.VISIBLE);
        else
            learningLayout.setVisibility(View.GONE);
        teachingLayout.setVisibility(View.VISIBLE);
    }

    public static int updateWebLevel(String url) {
        int webLevel;
        switch (url) {
            case Constants.LEARNING_URL:
            case Constants.TEACHING_URL:
                webLevel = Constants.HOME_LEVEL;
                break;
            case Constants.LESSON_ALL_CONTENT_URL:
            case Constants.LESSON_DOWNLOADED_URL:
                webLevel = Constants.UNIT_LEVEL;
                break;
            default:
                webLevel = Constants.HOME_LEVEL;
        }
        return webLevel;
    }

    public static void refreshContents(Context context, WebView webView,
                                       CLMSContentScore contentScore, int courseId) {
        if (webView.getUrl().equalsIgnoreCase(Constants.LESSON_ALL_CONTENT_URL)) {
            String refreshUrl = "javascript:refreshContents(" +
                    !contentScore.getDownloadedFile().equalsIgnoreCase("") + ", "
                    + courseId + ", " + contentScore.getClassId() + ", " + contentScore.getUnitId() + ", " +
                    contentScore.getLessonId() + ", " + contentScore.getId() + ", " + "'" +
                    contentScore.getUniqueId() + "')";
            webView.loadUrl(refreshUrl);
        } else {
            boolean noUnit = false;
            boolean noLesson = false;
            String lessonUniquedId = "";
            int totalContentCount = 0;
            ArrayList<CLMSLessonScore> lessonScores = RealmTransactionUtils.getLessonScores(context,
                    contentScore.getClassId(), contentScore.getUnitId());
            for (CLMSLessonScore lessonScore : lessonScores) {
                int tempCount = 0;
                ArrayList<CLMSContentScore> contentScores =
                        RealmTransactionUtils.getContentScores(context,
                                contentScore.getClassId(), contentScore.getUnitId(), lessonScore.getId());
                for (CLMSContentScore score : contentScores) {
                    if (!score.getDownloadedFile().equalsIgnoreCase("")) {
                        ++totalContentCount;
                        ++tempCount;
                    }
                }
                if (lessonScore.getId() == contentScore.getLessonId()) {
                    if (tempCount == 0) {
                        noLesson = true;
                        lessonUniquedId = lessonScore.getUniqueId();
                    }
                }
            }
            if (totalContentCount == 0)
                noUnit = true;
            if (noUnit)
                webView.loadUrl("javascript:removeUnit(" + "'" +
                        contentScore.getUnitId() + "')");
            if (noLesson)
                webView.loadUrl("javascript:removeLesson(" + "'" +
                        lessonUniquedId + "')");
            webView.loadUrl("javascript:removeContent(" + "'" +
                    contentScore.getUniqueId() + "')");
        }
    }

    private static class UpdateContentAsync extends AsyncTask<Object, Object, Object> {
        private Context context;
        private WebView webView;
        private int courseId;
        private int classId;
        private boolean isDownloaded;
        private JSONArray unitScoreArray = new JSONArray();

        public UpdateContentAsync(Context context, WebView webView, int courseId,
                                  int classId, boolean isDownloaded) {
            this.context = context;
            this.webView = webView;
            this.courseId = courseId;
            this.classId = classId;
            this.isDownloaded = isDownloaded;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            JSONObject obj;
            JSONArray lessonScoreArray;
            JSONArray contentScoreArray;
            ArrayList<CLMSContentScore> contentList = new ArrayList<>();
            ArrayList<CLMSUnitScore> unitScores = RealmTransactionUtils.getUnitScores(context, classId);
            if (unitScores != null) {
                for (CLMSUnitScore unitScore : unitScores) {
                    float unitProgress = 0;
                    obj = new JSONObject();
                    classId = unitScore.getClassId();
                    lessonScoreArray = new JSONArray();
                    ArrayList<CLMSLessonScore> lessonScores = RealmTransactionUtils.getLessonScores(context,
                            unitScore.getClassId(), unitScore.getId());
                    try {
                        obj.put(Constants.UNIT_NAME, unitScore.getUnitName());
                        obj.put(Constants.UNIT_ID, unitScore.getId());
                        obj.put(Constants.UNIT_PROGRESS, unitScore.getCalcProgress());
                        for (CLMSLessonScore lessonScore : lessonScores) {
                            JSONObject lessonObj = new JSONObject();
                            lessonObj.put(Constants.LESSON_NAME, lessonScore.getLessonName());
                            lessonObj.put(Constants.LESSON_ID, lessonScore.getId());
                            lessonObj.put(Constants.LESSON_UNIQUE_ID, lessonScore.getUniqueId());
                            ArrayList<CLMSContentScore> contentScores = RealmTransactionUtils.getContentScores(context,
                                    unitScore.getClassId(), unitScore.getId(), lessonScore.getId());
                            contentScoreArray = new JSONArray();
                            float progress = 0;
                            for (CLMSContentScore contentScore : contentScores) {
                                JSONObject contentObj = new JSONObject();
                                contentObj.put(Constants.CONTENT_NAME, contentScore.getContentName());
                                contentObj.put(Constants.CONTENT_ID, contentScore.getId());
                                contentObj.put(Constants.CONTENT_PROGRESS, formatProgress(
                                        contentScore.getCalcProgress(), 1));
                                contentObj.put(Constants.CONTENT_DOWNLOADED,
                                        !contentScore.getDownloadedFile().equalsIgnoreCase(""));
                                contentObj.put(Constants.CONTENT_UNIQUE_ID, contentScore.getUniqueId());
                                if ((isDownloaded &&
                                        !contentScore.getDownloadedFile().equalsIgnoreCase("")) || !isDownloaded) {
                                    contentScoreArray.put(contentObj);
                                    contentList.add(contentScore);
                                    progress += contentScore.getCalcProgress();
                                }
                            }
                            if ((isDownloaded && contentScoreArray.length() > 0) || !isDownloaded) {
                                lessonObj.put(Constants.CONTENT_SIZE, contentScoreArray.length());
                                lessonObj.put(Constants.CONTENTS, contentScoreArray);
                                lessonScoreArray.put(lessonObj);
                            }
                            lessonObj.put(Constants.LESSON_PROGRESS, formatProgress(
                                    progress / contentScoreArray.length(), contentScoreArray.length()));
                            if (contentScoreArray.length() > 0)
                                unitProgress += progress / contentScoreArray.length();
                        }
                        if ((isDownloaded && lessonScoreArray.length() > 0) || !isDownloaded) {
                            obj.put(Constants.LESSON_SIZE, lessonScoreArray.length());
                            obj.put(Constants.LESSONS, lessonScoreArray);
                        }
                        obj.put(Constants.UNIT_PROGRESS, formatProgress(unitProgress,
                                lessonScoreArray.length()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if ((isDownloaded && lessonScoreArray.length() > 0) || !isDownloaded)
                        unitScoreArray.put(obj);
                }
            }
        }

        @Override
        protected Object doInBackground(Object... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl("javascript:addUnit('" + unitScoreArray.length() + "', "
                            + unitScoreArray + ", " + courseId + ", " + classId + ")");
                }
            });
        }
    }
}