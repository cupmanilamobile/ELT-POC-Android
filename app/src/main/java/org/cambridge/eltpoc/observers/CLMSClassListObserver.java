package org.cambridge.eltpoc.observers;

import org.cambridge.eltpoc.model.CLMSModel;

/**
 * Created by etorres on 6/25/15.
 */
public class CLMSClassListObserver extends CLMSModel {
    private boolean isCoursesRetrieved = false;
    private boolean isClassesRetrieved = false;

    public boolean isCoursesRetrieved() {
        return isCoursesRetrieved;
    }

    public void setIsCoursesRetrieved(boolean isCoursesRetrieved) {
        this.isCoursesRetrieved = isCoursesRetrieved;
    }

    public boolean isClassesRetrieved() {
        return isClassesRetrieved;
    }

    public void setIsClassesRetrieved(boolean isClassesRetrieved) {
        this.isClassesRetrieved = isClassesRetrieved;
    }
}
