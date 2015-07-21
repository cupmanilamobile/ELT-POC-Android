package org.cambridge.eltpoc.util;

import android.content.Context;
import android.os.AsyncTask;

import org.cambridge.eltpoc.ELTApplication;
import org.cambridge.eltpoc.model.CLMSClass;
import org.cambridge.eltpoc.model.CLMSClassList;
import org.cambridge.eltpoc.model.CLMSContentScore;
import org.cambridge.eltpoc.model.CLMSCourse;
import org.cambridge.eltpoc.model.CLMSCourseList;
import org.cambridge.eltpoc.model.CLMSLessonScore;
import org.cambridge.eltpoc.model.CLMSUnitScore;
import org.cambridge.eltpoc.model.CLMSUser;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by etorres on 6/22/15.
 */
public class RealmTransactionUtils {

    public interface OnSaveListener {
        void onSaved();
    }

    public static ArrayList<CLMSUser> getAllUsers(Context context) {
        Realm realm = Realm.getInstance(context);
        RealmResults<CLMSUser> result = realm.where(CLMSUser.class).findAll();
        ArrayList<CLMSUser> users = new ArrayList<>();
        for (CLMSUser user : result)
            users.add(user);
        return users;
    }

    public static ArrayList<CLMSCourse> getAllCourses(Context context) {
        Realm realm = Realm.getInstance(context);
        CLMSUser user = SharedPreferencesUtils.getLoggedInUser(context);
        RealmResults<CLMSCourse> result = realm.where(CLMSCourse.class).contains("uniqueId",
                user.getUsername()).findAll();
        ArrayList<CLMSCourse> courses = new ArrayList<>();
        for (CLMSCourse course : result)
            courses.add(course);
        return courses;
    }

    public static ArrayList<CLMSClass> getClassesByCourseId(Context context, int id) {
        Realm realm = Realm.getInstance(context);
        CLMSUser user = SharedPreferencesUtils.getLoggedInUser(context);
        RealmResults<CLMSClass> result = realm.where(CLMSClass.class).equalTo("courseId", id).contains("uniqueId",
                user.getUsername()).findAll();
        ArrayList<CLMSClass> classes = new ArrayList<>();
        for (CLMSClass cClass : result)
            classes.add(cClass);
        return classes;
    }

    public static ArrayList<CLMSUnitScore> getUnitScores(Context context, int classId) {
        Realm realm = Realm.getInstance(context);
        CLMSUser user = SharedPreferencesUtils.getLoggedInUser(context);
        RealmResults<CLMSUnitScore> result = realm.where(CLMSUnitScore.class).equalTo("classId", classId).contains("uniqueId",
                user.getUsername()).findAll();
        ArrayList<CLMSUnitScore> unitScores = new ArrayList<>();
        for (CLMSUnitScore unitScore : result)
           unitScores.add(unitScore);
        return unitScores;
    }

    public static ArrayList<CLMSLessonScore> getLessonScores(Context context, int classId, int unitId) {
        Realm realm = Realm.getInstance(context);
        CLMSUser user = SharedPreferencesUtils.getLoggedInUser(context);
        RealmResults<CLMSLessonScore> result = realm.where(CLMSLessonScore.class)
                .equalTo("classId", classId).equalTo("unitId", unitId).contains("uniqueId",
                        user.getUsername()).findAll();
        ArrayList<CLMSLessonScore> lessonScores = new ArrayList<>();
        for (CLMSLessonScore lessonScore : result)
            lessonScores.add(lessonScore);
        return lessonScores;
    }

    public static ArrayList<CLMSContentScore> getContentScores(Context context, int classId, int unitId, int lessonId) {
        Realm realm = Realm.getInstance(context);
        CLMSUser user = SharedPreferencesUtils.getLoggedInUser(context);
        RealmResults<CLMSContentScore> result = realm.where(CLMSContentScore.class)
                .equalTo("classId", classId).equalTo("unitId", unitId).equalTo("lessonId", lessonId)
                .contains("uniqueId",
                        user.getUsername()).findAll();
        ArrayList<CLMSContentScore> contentScores = new ArrayList<>();
        for (CLMSContentScore contentScore : result) {
            contentScores.add(contentScore);
        }
        return contentScores;
    }

    public static CLMSContentScore getContentScore(Context context, int classId, int unitId,
                                                   int lessonId, int contentId) {
        Realm realm = Realm.getInstance(context);
        CLMSUser user = SharedPreferencesUtils.getLoggedInUser(context);
        RealmResults<CLMSContentScore> result = realm.where(CLMSContentScore.class)
                .equalTo("classId", classId).equalTo("unitId", unitId).equalTo("lessonId", lessonId)
                .equalTo("id", contentId).contains("uniqueId", user.getUsername()).findAll();
        if(result != null && result.size() > 0)
            return result.get(0);
        return null;
    }

    public static void updateContentScore(Context context, CLMSContentScore contentScore) {
        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(contentScore);
        realm.commitTransaction();
    }

    public static void updateContentScoreUrl(Context context, CLMSContentScore contentScore, String url) {
        Realm realm = Realm.getInstance(context);
        CLMSContentScore toEdit = realm.where(CLMSContentScore.class)
                .equalTo("uniqueId", contentScore.getUniqueId()).findFirst();
        realm.beginTransaction();
        toEdit.setDownloadedFile(url);
        realm.commitTransaction();
    }

    public static void updateContentScoreProgress(Context context, CLMSContentScore contentScore, int progress) {
        Realm realm = Realm.getInstance(context);
        CLMSContentScore toEdit = realm.where(CLMSContentScore.class)
                .equalTo("uniqueId", contentScore.getUniqueId()).findFirst();
        realm.beginTransaction();
        toEdit.setCalcProgress(contentScore.getCalcProgress());
        realm.commitTransaction();
    }

    public static void updateLessonScore(Context context, CLMSLessonScore lessonScore) {
        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(lessonScore);
        realm.commitTransaction();
    }

    public static void updateUnitScore(Context context, CLMSUnitScore unitScore) {
        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(unitScore);
        realm.commitTransaction();
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
        for (CLMSCourse course : courseList.getCourseLists()) {
            course.setUniqueId(ELTApplication.getInstance().getCurrentUser().getUsername() +
                    course.getNid());
            saveCourse(context, course, false);
        }
        realm.commitTransaction();
    }

    public static void saveCourse(Context context, CLMSCourse course, boolean beginTransaction) {
        Realm realm = Realm.getInstance(context);
        RealmResults<CLMSCourse> result = realm.where(CLMSCourse.class)
                .equalTo("uniqueId", course.getUniqueId()).findAll();
        if (result.size() == 0) {
            if (beginTransaction)
                realm.beginTransaction();
            CLMSCourse realmCourse = realm.createObject(CLMSCourse.class);
            realmCourse.setName(course.getName());
            realmCourse.setNid(course.getNid());
            realmCourse.setProductLogo(course.getProductLogo());
            realmCourse.setProductName(course.getProductName());
            realmCourse.setUniqueId(course.getUniqueId());
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
                .equalTo("uniqueId", cClass.getUniqueId()).findAll();
        if (result.size() == 0) {
            if (beginTransaction)
                realm.beginTransaction();
            CLMSClass realmClass = realm.createObject(CLMSClass.class);
            realmClass.setClassName(cClass.getClassName());
            realmClass.setClassRole(cClass.getClassRole());
            realmClass.setCourseId(cClass.getCourseId());
            realmClass.setId(cClass.getId());
            realmClass.setTYPE(cClass.getTYPE());
            realmClass.setUniqueId(cClass.getUniqueId());
            if (cClass.getLessonScore() != null)
                saveLessonScore(context, cClass.getLessonScore(), false);
            if (cClass.getUnitScore() != null)
                saveUnitScore(context, cClass.getUnitScore(), false);
            if (beginTransaction)
                realm.commitTransaction();
        }
    }

    public static void saveUnitScore(Context context, CLMSUnitScore unitScore, boolean beginTransaction) {
        Realm realm = Realm.getInstance(context);
        RealmResults<CLMSUnitScore> result = realm.where(CLMSUnitScore.class)
                .equalTo("uniqueId", unitScore.getUniqueId()).findAll();
        if (result.size() == 0) {
            if (beginTransaction)
                realm.beginTransaction();
            CLMSUnitScore realmUnitScore = realm.createObject(CLMSUnitScore.class);
            realmUnitScore.setUnitName(unitScore.getUnitName());
            if(unitScore.getKind() == null)
                unitScore.setKind("");
            realmUnitScore.setKind(unitScore.getKind());
            realmUnitScore.setCalcProgress(unitScore.getCalcProgress());
            realmUnitScore.setId(unitScore.getId());
            realmUnitScore.setUniqueId(unitScore.getUniqueId());
            realmUnitScore.setClassId(unitScore.getClassId());
            if (beginTransaction)
                realm.commitTransaction();
        }
    }

    public static void saveLessonScore(Context context, CLMSLessonScore lessonScore, boolean beginTransaction) {
        Realm realm = Realm.getInstance(context);
        RealmResults<CLMSLessonScore> result = realm.where(CLMSLessonScore.class)
                .equalTo("uniqueId", lessonScore.getUniqueId()).findAll();
        if (result.size() == 0) {
            if (beginTransaction)
                realm.beginTransaction();
            CLMSLessonScore realmLessonScore = realm.createObject(CLMSLessonScore.class);
            realmLessonScore.setLessonName(lessonScore.getLessonName());
            if(lessonScore.getKind() == null)
                lessonScore.setKind("");
            realmLessonScore.setKind(lessonScore.getKind());
            realmLessonScore.setCalcProgress(lessonScore.getCalcProgress());
            realmLessonScore.setId(lessonScore.getId());
            realmLessonScore.setUniqueId(lessonScore.getUniqueId());
            realmLessonScore.setClassId(lessonScore.getClassId());
            realmLessonScore.setUnitId(lessonScore.getUnitId());
            if (beginTransaction)
                realm.commitTransaction();
        }
    }

    public static void saveContentScore(Context context, CLMSContentScore contentScore, boolean beginTransaction) {
        Realm realm = Realm.getInstance(context);
        RealmResults<CLMSContentScore> result = realm.where(CLMSContentScore.class)
                .equalTo("uniqueId", contentScore.getUniqueId()).findAll();
        if (result.size() == 0) {
            if (beginTransaction)
                realm.beginTransaction();
            CLMSContentScore realmContentScore = realm.createObject(CLMSContentScore.class);
            if(contentScore.getKind() == null)
                contentScore.setKind("");
            realmContentScore.setKind(contentScore.getKind());
            realmContentScore.setId(contentScore.getId());
            realmContentScore.setCalcProgress(contentScore.getCalcProgress());
            realmContentScore.setContentName(contentScore.getContentName());
            realmContentScore.setTimeAccessed(contentScore.getTimeAccessed());
            realmContentScore.setUniqueId(contentScore.getUniqueId());
            realmContentScore.setClassId(contentScore.getClassId());
            realmContentScore.setLessonId(contentScore.getLessonId());
            realmContentScore.setUnitId(contentScore.getUnitId());
            if (beginTransaction)
                realm.commitTransaction();
        }
    }

    public static CLMSContentScore cloneContentScore(CLMSContentScore contentScore) {
        CLMSContentScore clonedContentScore = new CLMSContentScore();
        clonedContentScore.setCalcProgress(contentScore.getCalcProgress());
        clonedContentScore.setId(contentScore.getId());
        clonedContentScore.setDownloadedFile(contentScore.getDownloadedFile());
        clonedContentScore.setClassId(contentScore.getClassId());
        clonedContentScore.setContentName(contentScore.getContentName());
        clonedContentScore.setLessonId(contentScore.getLessonId());
        clonedContentScore.setTimeAccessed(contentScore.getTimeAccessed());
        clonedContentScore.setUniqueId(contentScore.getUniqueId());
        clonedContentScore.setUnitId(contentScore.getUnitId());
        return clonedContentScore;
    }

    public static class RealmSaveAsync extends AsyncTask<Object, Object, Object> {
        private CopyOnWriteArrayList<CLMSUnitScore> unitScores = new CopyOnWriteArrayList<>();
        private CopyOnWriteArrayList<CLMSLessonScore> lessonScores = new CopyOnWriteArrayList<>();
        private CopyOnWriteArrayList<CLMSContentScore> contentScores = new CopyOnWriteArrayList<>();
        private Context context;
        private OnSaveListener onSaveListener;

        public RealmSaveAsync(Context context, OnSaveListener onSaveListener) {
            this.context = context;
            this.onSaveListener = onSaveListener;
        }

        public void addUnitScore(CLMSUnitScore unitScore) {
            unitScores.add(unitScore);
        }

        public void addLessonScore(CLMSLessonScore lessonScore) {
            lessonScores.add(lessonScore);
        }

        public void addContentScore(CLMSContentScore contentScore) {
            contentScores.add(contentScore);
        }

        public CopyOnWriteArrayList<CLMSUnitScore> getUnitScores() {
            return unitScores;
        }

        public void setUnitScores(CopyOnWriteArrayList<CLMSUnitScore> unitScores) {
            this.unitScores = unitScores;
        }

        public CopyOnWriteArrayList<CLMSLessonScore> getLessonScores() {
            return lessonScores;
        }

        public void setLessonScores(CopyOnWriteArrayList<CLMSLessonScore> lessonScores) {
            this.lessonScores = lessonScores;
        }

        public CopyOnWriteArrayList<CLMSContentScore> getContentScores() {
            return contentScores;
        }

        public void setContentScores(CopyOnWriteArrayList<CLMSContentScore> contentScores) {
            this.contentScores = contentScores;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... params) {
            for(CLMSUnitScore unitScore : unitScores)
                saveUnitScore(context, unitScore, true);
            for(CLMSLessonScore lessonScore : lessonScores)
                saveLessonScore(context, lessonScore, true);
            for(CLMSContentScore contentScore : contentScores)
                saveContentScore(context, contentScore, true);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(onSaveListener != null)
                onSaveListener.onSaved();
        }
    }

    public static class SaveCourseAsync extends AsyncTask<Object, Object, Object> {
        private CLMSCourseList courseList;
        private Context context;

        public SaveCourseAsync(Context context, CLMSCourseList courseList) {
            this.courseList = courseList;
            this.context = context;
        }

        @Override
        protected Object doInBackground(Object... params) {
            RealmTransactionUtils.saveCourseList(context, courseList);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            ELTApplication.getInstance().getClassListObserver().setIsCoursesRetrieved(true);
            ELTApplication.getInstance().getClassListObserver().notifyObservers();
        }
    }

    public static class SaveClassAsync extends AsyncTask<Object, Object, Object> {
        private CLMSClassList classList;
        private Context context;

        public SaveClassAsync(Context context, CLMSClassList classList) {
            this.classList = classList;
            this.context = context;
        }

        @Override
        protected Object doInBackground(Object... params) {
            for (CLMSClass clmsClass : classList.getClassLists()) {
                clmsClass.setUniqueId(ELTApplication.getInstance().getCurrentUser().getUsername() +
                        clmsClass.getId());
                RealmTransactionUtils.saveClass(context, clmsClass, true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            ELTApplication.getInstance().getClassListObserver().setIsClassesRetrieved(true);
            ELTApplication.getInstance().getClassListObserver().notifyObservers();
        }
    }
}
