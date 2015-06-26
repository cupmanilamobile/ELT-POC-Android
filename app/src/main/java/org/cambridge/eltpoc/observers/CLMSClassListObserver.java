package org.cambridge.eltpoc.observers;

import org.cambridge.eltpoc.model.CLMSClassList;
import org.cambridge.eltpoc.model.CLMSModel;

/**
 * Created by etorres on 6/25/15.
 */
public class CLMSClassListObserver extends CLMSModel {
    private CLMSClassList classList;

    public CLMSClassList getClassList() {
        return classList;
    }

    public void setClassList(CLMSClassList classList) {
        this.classList = classList;
    }
}
