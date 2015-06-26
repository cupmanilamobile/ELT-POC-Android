package org.cambridge.eltpoc.observers;

import org.cambridge.eltpoc.model.CLMSCourseList;
import org.cambridge.eltpoc.model.CLMSModel;

/**
 * Created by etorres on 6/25/15.
 */
public class CLMSCourseListObserver extends CLMSModel {
    private CLMSCourseList courseList;

    public CLMSCourseList getCourseList() {
        return courseList;
    }

    public void setCourseList(CLMSCourseList courseList) {
        this.courseList = courseList;
    }
}
