package org.cambridge.eltpoc.util;

import android.content.Context;
import android.view.View;
import android.webkit.WebView;

import org.cambridge.eltpoc.Constants;
import org.cambridge.eltpoc.model.CLMSClass;
import org.cambridge.eltpoc.model.CLMSCourse;
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
                    if (cCLass.getClassRole().equalsIgnoreCase(Constants.ROLE_TEACHER))
                        ++teachCount;
                    else if (cCLass.getClassRole().equalsIgnoreCase(Constants.ROLE_STUDENT))
                        ++studentCount;
                    JSONObject classObj = new JSONObject();
                    classObj.put(Constants.CLASS_NAME, cCLass.getClassName());
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

    public static void updateTabVisibility(int learningCount, int teachingCount, boolean isInitial,
                                           View learningLayout, View teachingLayout, WebView webView) {
        learningLayout.setVisibility(View.VISIBLE);
        if(learningCount == 0 && !isInitial)
            webView.loadUrl(Constants.NO_ENROLLED_URL);
        if(teachingCount > 0)
            teachingLayout.setVisibility(View.VISIBLE);
        else
            teachingLayout.setVisibility(View.GONE);
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
