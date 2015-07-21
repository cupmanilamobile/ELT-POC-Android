package org.cambridge.eltpoc.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import org.cambridge.eltpoc.Constants;
import org.cambridge.eltpoc.ELTApplication;
import org.cambridge.eltpoc.LoginActivity;
import org.cambridge.eltpoc.R;
import org.cambridge.eltpoc.connections.DownloadAsync;
import org.cambridge.eltpoc.javascript.CLMSJavaScriptInterface;
import org.cambridge.eltpoc.model.CLMSContentScore;
import org.cambridge.eltpoc.model.CLMSLessonScore;
import org.cambridge.eltpoc.model.CLMSModel;
import org.cambridge.eltpoc.model.CLMSUnitScore;
import org.cambridge.eltpoc.model.CLMSUser;
import org.cambridge.eltpoc.observers.CLMSContentScoreListObserver;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.CopyOnWriteArrayList;

import io.realm.Realm;

/**
 * Created by etorres on 7/20/15.
 */
public class WebServiceHelper {
    public static void signOutUser(Activity activity) {
        SharedPreferencesUtils.updateLoggedInUser(activity, "", "", "", 0);
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

    public static void syncContents(Context context, boolean clearData) {
        if (clearData)
            SharedPreferencesUtils.clearContentSync(context);
        ELTApplication.getInstance().getWebModel().setIsSynced(true);
        ELTApplication.getInstance().getWebModel().notifyObservers();
    }

    public static void notifyContentScores(Context context, final int classId, int type,
                                           CopyOnWriteArrayList<CLMSUnitScore> unitScores,
                                           CopyOnWriteArrayList<CLMSLessonScore> lessonScores,
                                           CopyOnWriteArrayList<CLMSContentScore> contentScores) {
        final CLMSContentScoreListObserver contentScoreListObserver =
                ELTApplication.getInstance().getContentScoreListObserver();
        switch (type) {
            case Constants.UNIT_TYPE:
                contentScoreListObserver.setUnitsRetrieved(true);
                break;
            case Constants.LESSON_TYPE:
                contentScoreListObserver.setLessonsRetrieved(true);
                break;
            case Constants.CONTENT_TYPE:
                contentScoreListObserver.setContentsRetrieved(true);
                break;
        }
        if (contentScoreListObserver.allDetailsRetrieved()) {
            RealmTransactionUtils.OnSaveListener onSaveListener =
                    new RealmTransactionUtils.OnSaveListener() {
                        @Override
                        public void onSaved() {
                            contentScoreListObserver.setClassId(classId);
                            contentScoreListObserver.notifyObservers();
                        }
                    };
            RealmTransactionUtils.RealmSaveAsync realmSaveAsync =
                    new RealmTransactionUtils.RealmSaveAsync(context, onSaveListener);
            realmSaveAsync.setUnitScores(unitScores);
            realmSaveAsync.setLessonScores(lessonScores);
            realmSaveAsync.setContentScores(contentScores);
            realmSaveAsync.execute();
        }
    }

    public static void deleteContent(final int courseId, final int classId,
                                     final int unitId, final int lessonId, final int contentId,
                                     final Context context, final CLMSModel webModel) {
        final CLMSContentScore contentScore = RealmTransactionUtils.getContentScore(context, classId,
                unitId, lessonId, contentId);
        DialogUtils.createOptionDialog(context, "DELETE", "Do you want to delete "
                        + contentScore.getContentName() + " ?", "Ok", "Cancel",
                new DialogUtils.OnOptionSelectedListener() {
                    @Override
                    public void onOptionSelected() {
                        File file = new File(contentScore.getDownloadedFile());
                        file.delete();
                        Realm realm = io.realm.Realm.getInstance(context);
                        realm.beginTransaction();
                        contentScore.setDownloadedFile("");
                        realm.copyToRealmOrUpdate(contentScore);
                        realm.commitTransaction();
                        DialogUtils.createDialog(context, "Deleted", contentScore.getContentName() +
                                " has been deleted.");
                        webModel.setCourseId(courseId);
                        webModel.setContentScore(RealmTransactionUtils.cloneContentScore(contentScore));
                        webModel.setWebOperation(CLMSModel.WEB_OPERATION.DELETED);
                        webModel.notifyObservers();
                    }
                });
    }

    public static ArrayList<CLMSContentScore> getSyncContentScores(Context context) {
        ArrayList<CLMSContentScore> contentScores = new ArrayList<>();
        ArrayList<String> uniqueIds = SharedPreferencesUtils.getContentSync(context);
        for (String uniqueId : uniqueIds) {
            String[] split = uniqueId.split("-");
            CLMSContentScore contentScore = RealmTransactionUtils.getContentScore(
                    context, Integer.parseInt(split[1]), Integer.parseInt(split[2]),
                    Integer.parseInt(split[3]), Integer.parseInt(split[4]));
            if (contentScore != null && contentScore.getCalcProgress() == 0)
                contentScores.add(contentScore);
        }
        return contentScores;
    }

    public static void saveUser(Context context, CLMSUser user, String displayName, long id) {
        user.setDisplayName(displayName);
        user.setId(id);
        RealmTransactionUtils.saveUser(context, user);
        ELTApplication.getInstance().setCurrentUser(user);
        SharedPreferencesUtils.updateLoggedInUser(context, user.getUsername(),
                user.getPassword(), user.getDisplayName(), user.getId());
    }

    public static CLMSLessonScore createDummyLesson(int classId, int unitId, int lessonId) {
        CLMSLessonScore score = new CLMSLessonScore();
        score.setUnitId(unitId);
        score.setCalcProgress(0);
        score.setClassId(classId);
        score.setId(0);
        score.setLessonName("CONTENTS");
        score.setUniqueId(ELTApplication.getInstance().getCurrentUser().getUsername() + "-" +
                classId + "-" + unitId + "-" + lessonId + "-" + score.getId());
        return score;
    }

    public static void downloadContent(Context context, CLMSModel webModel, String url,
                                       int courseId, int classId, int unitId, int lessonId,
                                       int contentId) {
        //SAMPLE: "http://content-poc.cambridgelms.org/touchstone2/p/sites/default
        // /files/html_content_zip/UN_UVM_OWB_1B_LS_U06_E10_HTML5_GMV01.zip
        CLMSContentScore contentScore = RealmTransactionUtils.getContentScore(context, classId,
                unitId, lessonId, contentId);
        String[] str = url.split("/");
        DownloadAsync downloadAsync = new DownloadAsync(context, contentScore, url,
                context.getFilesDir().getAbsolutePath(), str[str.length - 1], webModel, courseId);
        downloadAsync.execute();
    }

    public static void showLoadingScreen(CLMSModel webModel, boolean isLoading) {
        webModel.setWebOperation(isLoading ? CLMSModel.WEB_OPERATION.LOADING :
                CLMSModel.WEB_OPERATION.NONE);
        webModel.notifyObservers();
    }

    public static void updateContents(Context context,
                                      final String className, final int courseId, final int classId) {
        ELTApplication instance = ELTApplication.getInstance();
        instance.getLinkModel().setClassName(className);
        instance.getContentScoreListObserver().setCourseId(courseId);
        instance.getContentScoreListObserver().setClassId(classId);
        instance.getContentScoreListObserver().setUrl(
                Misc.hasInternetConnection(context) ? Constants.LESSON_ALL_CONTENT_URL :
                        Constants.LESSON_DOWNLOADED_URL);
    }

    public static void updateContentScores(Context context, CLMSJavaScriptInterface
            javascriptInterface) {
        ArrayList<CLMSContentScore> contentScores = WebServiceHelper.getSyncContentScores(context);
        CLMSUser user = ELTApplication.getInstance().getCurrentUser();
        int count = 0;
        ELTApplication.getInstance().getWebModel().setSyncMessage(
                context.getString(R.string.sync_message));
        if (contentScores.size() == 0) {
            ELTApplication.getInstance().getWebModel().setSyncMessage(
                    context.getString(R.string.sync_nothing));
            WebServiceHelper.syncContents(context, false);
        }
        for (CLMSContentScore contentScore : contentScores) {
            javascriptInterface.updateContentScore(user.getAccessToken(), user.getId(),
                    contentScore, 100, 100, Calendar.getInstance().getTimeInMillis(),
                    count == contentScores.size() - 1);
            ++count;
        }
    }
}