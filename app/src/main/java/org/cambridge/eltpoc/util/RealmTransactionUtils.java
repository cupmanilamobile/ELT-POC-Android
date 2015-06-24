package org.cambridge.eltpoc.util;

import android.content.Context;

import org.cambridge.eltpoc.model.CLMSClass;
import org.cambridge.eltpoc.model.CLMSContentScore;
import org.cambridge.eltpoc.model.CLMSCourse;
import org.cambridge.eltpoc.model.CLMSCourseList;
import org.cambridge.eltpoc.model.CLMSUnitLessonScore;
import org.cambridge.eltpoc.model.CLMSUser;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by etorres on 6/22/15.
 */
public class RealmTransactionUtils {

    public static ArrayList<CLMSUser> getAllUsers(Context context) {
        Realm realm = Realm.getInstance(context);
        RealmResults<CLMSUser> result = realm.where(CLMSUser.class).findAll();
        ArrayList<CLMSUser> users = new ArrayList<>();
        for (CLMSUser user : result)
            users.add(user);
        return users;
    }

    public static void saveUser(Context context, CLMSUser user) {
        Realm realm = Realm.getInstance(context);
        RealmResults<CLMSUser> result = realm.where(CLMSUser.class)
                .equalTo("username", user.getUsername()).findAll();
        if (result.size() == 0) {
            realm.beginTransaction();
            CLMSUser realmUser = realm.createObject(CLMSUser.class);
            realmUser.setPassword(user.getPassword());
            realmUser.setUsername(user.getUsername());
            realmUser.setAccessToken(user.getAccessToken());
            realmUser.setDisplayName(user.getDisplayName());
            realmUser.setExpiresIn(user.getExpiresIn());
            realmUser.setId(user.getId());
            realmUser.setKind(user.getKind());
            realmUser.setRefreshToken(user.getRefreshToken());
            realmUser.setScope(user.getScope());
            realmUser.setTokenType(user.getTokenType());
            realm.commitTransaction();
        }
    }

    public static void saveCourseList(Context context, CLMSCourseList courseList) {
        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();
        CLMSCourseList realmCourseList = realm.createObject(CLMSCourseList.class);
        realmCourseList.setKind(courseList.getKind());
        for (CLMSCourse course : courseList.getCourseLists())
            saveCourse(context, course, false);
        realm.commitTransaction();
    }

    public static void saveCourse(Context context, CLMSCourse course, boolean beginTransaction) {
        Realm realm = Realm.getInstance(context);
        RealmResults<CLMSCourse> result = realm.where(CLMSCourse.class)
                .equalTo("nid", course.getNid()).findAll();
        if (result.size() == 0) {
            if (beginTransaction)
                realm.beginTransaction();
            CLMSCourse realmCourse = realm.createObject(CLMSCourse.class);
            realmCourse.setName(course.getName());
            realmCourse.setNid(course.getNid());
            realmCourse.setProductLogo(course.getProductLogo());
            realmCourse.setProductName(course.getProductName());
            if (course.getClasses() != null)
                for (CLMSClass cClass : course.getClasses())
                    saveClass(context, cClass, false);
            if (beginTransaction)
                realm.commitTransaction();
        }
    }

    public static void saveClass(Context context, CLMSClass cClass, boolean beginTransaction) {
        Realm realm = Realm.getInstance(context);
        RealmResults<CLMSClass> result = realm.where(CLMSClass.class)
                .equalTo("id", cClass.getId()).findAll();
        if (result.size() == 0) {
            if (beginTransaction)
                realm.beginTransaction();
            CLMSClass realmClass = realm.createObject(CLMSClass.class);
            realmClass.setClassName(cClass.getClassName());
            realmClass.setClassRole(cClass.getClassRole());
            realmClass.setCourseId(cClass.getCourseId());
            realmClass.setId(cClass.getId());
            realmClass.setTYPE(cClass.getTYPE());
            if (cClass.getLessonScore() != null)
                saveScore(context, cClass.getLessonScore(), false);
            if (cClass.getUnitScore() != null)
                saveScore(context, cClass.getUnitScore(), false);
            if (beginTransaction)
                realm.commitTransaction();
        }
    }

    public static void saveScore(Context context, CLMSUnitLessonScore unitScore, boolean beginTransaction) {
        Realm realm = Realm.getInstance(context);
        RealmResults<CLMSUnitLessonScore> result = realm.where(CLMSUnitLessonScore.class)
                .equalTo("id", unitScore.getId()).findAll();
        if (result.size() == 0) {
            if (beginTransaction)
                realm.beginTransaction();
            CLMSUnitLessonScore realmUnitScore = realm.createObject(CLMSUnitLessonScore.class);
            realmUnitScore.setContentName(unitScore.getContentName());
            realmUnitScore.setKind(unitScore.getKind());
            realmUnitScore.setCalcProgress(unitScore.getCalcProgress());
            realmUnitScore.setId(unitScore.getId());
            if (beginTransaction)
                realm.commitTransaction();
        }
    }

    public static void saveContentScore(Context context, CLMSContentScore contentScore, boolean beginTransaction) {
        Realm realm = Realm.getInstance(context);
        RealmResults<CLMSContentScore> result = realm.where(CLMSContentScore.class)
                .equalTo("id", contentScore.getId()).findAll();
        if (result.size() == 0) {
            if (beginTransaction)
                realm.beginTransaction();
            CLMSContentScore realmContentScore = realm.createObject(CLMSContentScore.class);
            realmContentScore.setKind(contentScore.getKind());
            realmContentScore.setId(contentScore.getId());
            realmContentScore.setCalcProgress(contentScore.getCalcProgress());
            realmContentScore.setContentName(contentScore.getContentName());
            realmContentScore.setTimeAccessed(contentScore.getTimeAccessed());
            if (beginTransaction)
                realm.commitTransaction();
        }
    }
}
