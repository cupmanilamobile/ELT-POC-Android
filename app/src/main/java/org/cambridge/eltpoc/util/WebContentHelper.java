package org.cambridge.eltpoc.util;

import android.content.Context;
import android.view.View;
import android.webkit.WebView;

import org.cambridge.eltpoc.Constants;
import org.cambridge.eltpoc.ELTApplication;
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
    public static ArrayList<Integer> updateCourseContent(Context context, WebView webView, boolean isLearning) {
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
                    }
                    else if (cCLass.getClassRole().equalsIgnoreCase(Constants.ROLE_STUDENT))
                        ++studentCount;
                    JSONObject classObj = new JSONObject();
                    classObj.put(Constants.CLASS_NAME, cCLass.getClassName());
                    classObj.put(Constants.CLASS_ID, cCLass.getId());
                    classArray.put(classObj);
                }
                if (studentCount > 0 && teachCount > 0) {
                    type = Constants.TYPE_BOTH;
                    learningCount++;
                    teachingCount++;
                }
                else if (studentCount > 0) {
                    type = Constants.TYPE_LEARNING;
                    learningCount++;
                }
                else if (teachCount > 0) {
                    type = Constants.TYPE_TEACHING;
                    teachingCount++;
                }
                obj.put(Constants.TYPE, type);
                obj.put(Constants.CLASSES, classArray);
                obj.put(Constants.CLASS_SIZE, classArray.length());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (classes.size() > 0) {
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

    public static void updateUnitContent(Context context, ArrayList<CLMSUnitScore> unitScores,
                                         WebView webView, int courseId, boolean isDownloaded) {
        JSONObject obj;
        JSONArray unitScoreArray = new JSONArray();
        JSONArray lessonScoreArray;
        JSONArray contentScoreArray;
        int classId = 0;
        ArrayList<CLMSContentScore> contentList = new ArrayList<>();
        if(unitScores != null) {
            for (CLMSUnitScore unitScore : unitScores) {
                obj = new JSONObject();
                classId = unitScore.getClassId();
                lessonScoreArray = new JSONArray();
                ArrayList<CLMSLessonScore> lessonScores = RealmTransactionUtils.getLessonScores(context,
                        unitScore.getClassId(), unitScore.getId());
                try {
                    obj.put(Constants.UNIT_NAME, unitScore.getUnitName());
                    obj.put(Constants.UNIT_ID, unitScore.getId());
                    obj.put(Constants.UNIT_PROGRESS, unitScore.getCalcProgress());
                    for(CLMSLessonScore lessonScore : lessonScores) {
                        JSONObject lessonObj = new JSONObject();
                        lessonObj.put(Constants.LESSON_NAME, lessonScore.getLessonName());
                        lessonObj.put(Constants.LESSON_ID, lessonScore.getId());
                        lessonObj.put(Constants.LESSON_UNIQUE_ID, lessonScore.getUniqueId());
                        lessonObj.put(Constants.LESSON_PROGRESS, lessonScore.getCalcProgress());
                        ArrayList<CLMSContentScore> contentScores = RealmTransactionUtils.getContentScores(context,
                                unitScore.getClassId(), unitScore.getId(), lessonScore.getId());
                        contentScoreArray = new JSONArray();
                        for(CLMSContentScore contentScore : contentScores) {
                            JSONObject contentObj = new JSONObject();
                            contentObj.put(Constants.CONTENT_NAME, contentScore.getContentName());
                            contentObj.put(Constants.CONTENT_ID, contentScore.getId());
                            contentObj.put(Constants.CONTENT_PROGRESS, contentScore.getCalcProgress());
                            contentObj.put(Constants.CONTENT_DOWNLOADED,
                                    !contentScore.getDownloadedFile().isEmpty());
                            if((isDownloaded && !contentScore.getDownloadedFile().isEmpty()) || !isDownloaded) {
                                contentScoreArray.put(contentObj);
                                contentList.add(contentScore);
                            }
                        }
                        if((isDownloaded && contentScoreArray.length() > 0) || !isDownloaded) {
                            lessonObj.put(Constants.CONTENT_SIZE, contentScoreArray.length());
                            lessonObj.put(Constants.CONTENTS, contentScoreArray);
                            lessonScoreArray.put(lessonObj);
                        }
                    }
                    if((isDownloaded && lessonScoreArray.length() > 0) || !isDownloaded) {
                        obj.put(Constants.LESSON_SIZE, lessonScoreArray.length());
                        obj.put(Constants.LESSONS, lessonScoreArray);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if((isDownloaded && lessonScoreArray.length() > 0) || !isDownloaded)
                    unitScoreArray.put(obj);
            }
            webView.loadUrl("javascript:addUnit('" + unitScoreArray.length() + "', "
                    + unitScoreArray + ", "+ courseId + ", " + classId + ")");
            ELTApplication.getInstance().getContentScoreListObserver().setScoreList(contentList);
        }
    }

    public static void updateTabVisibility(int learningCount, int teachingCount, boolean isInitial,
                                           View learningLayout, View teachingLayout, WebView webView,
                                           NavigationDrawerAdapter drawerAdapter) {
        learningLayout.setVisibility(View.VISIBLE);
//        if(learningCount == 0 && !isInitial)
//            webView.loadUrl(Constants.NO_ENROLLED_URL);
        if(teachingCount > 0)
            teachingLayout.setVisibility(View.VISIBLE);
        else
            teachingLayout.setVisibility(View.GONE);
        if(!isInitial)
            drawerAdapter.removeTeachingTab(teachingCount == 0);
    }

    public static void updateTabVisibility(boolean isDownloadedEnabled,
                                           View learningLayout, View teachingLayout) {
        if(isDownloadedEnabled)
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
            case Constants.LESSON_DONWLOADED_URL:
                webLevel = Constants.LESSON_LEVEL;
                break;
            case Constants.VIDEO_URL:
                webLevel = Constants.VIDEO_LEVEL;
                break;
            default:
                webLevel = Constants.HOME_LEVEL;
        }
        return webLevel;
    }
}
