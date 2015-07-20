package org.cambridge.eltpoc;

/**
 * Created by etorres on 6/29/15.
 */
public class Constants {
    //HTML URL's
    public static final String LEARNING_URL = "file:///android_asset/www/new_html/index.html";
    public static final String TEACHING_URL = "file:///android_asset/www/new_html/index_teaching.html";
    public static final String LESSON_DOWNLOADED_URL = "file:///android_asset/www/new_html/content_downloaded.html";
    public static final String LESSON_ALL_CONTENT_URL = "file:///android_asset/www/new_html/content.html";
    public static final String NO_ENROLLED_URL = "file:///android_asset/www/new_html/no_enrolled.html";
    public static final String END_POINT = "http://content-poc-api.cambridgelms.org";

    //WEB LEVEL
    public static final int HOME_LEVEL = 0;
    public static final int UNIT_LEVEL = 1;
    public static final int CONTENT_LEVEL = 2;

    //NAVIGATION DRAWER ITEMS
    public static final int LEARNING = 1;
    public static final int TEACHING = 2;
    public static final int SIGN_OUT = 3;

    public static final String TYPE_BOTH = "BOTH";
    public static final String TYPE_LEARNING = "LEARNING";
    public static final String TYPE_TEACHING = "TEACHING";

    public static final String ROLE_STUDENT = "student";
    public static final String ROLE_TEACHER = "teacher";

    //JSON IDENTIFIERS
    public static final String CLASS_COUNT = "Count";
    public static final String COURSE_NAME = "Name";
    public static final String COURSE_ID = "CourseId";
    public static final String COURSE_IMAGE = "Image";

    public static final String TYPE = "Type";
    public static final String CLASSES = "Classes";
    public static final String CLASS_SIZE = "ClassSize";

    public static final String CLASS_NAME = "ClassName";
    public static final String CLASS_ID = "ClassId";

    public static final String LESSON_NAME = "LessonName";
    public static final String LESSON_ID = "LessonId";
    public static final String LESSON_UNIQUE_ID = "LessonUniqueId";
    public static final String LESSONS = "Lessons";
    public static final String LESSON_SIZE = "LessonSize";
    public static final String LESSON_PROGRESS = "LessonProgress";

    public static final String CONTENT_NAME = "ContentName";
    public static final String CONTENT_ID = "ContentId";
    public static final String CONTENTS = "Contents";
    public static final String CONTENT_SIZE = "ContentSize";
    public static final String CONTENT_PROGRESS = "ContentProgress";
    public static final String CONTENT_DOWNLOADED = "ContentDownloaded";
    public static final String CONTENT_UNIQUE_ID = "ContentUniqueId";

    public static final String UNIT_COUNT = "Count";
    public static final String UNIT_NAME = "Name";
    public static final String UNIT_ID = "UnitId";
    public static final String UNIT_PROGRESS = "UnitProgress";

    public static final int UNIT_TYPE = 0;
    public static final int LESSON_TYPE = 1;
    public static final int CONTENT_TYPE = 2;

    public static final String JS_INTERFACE = "JSInterface";
}
