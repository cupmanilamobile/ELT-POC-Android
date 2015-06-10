package org.cambridge.eltpoc.util;

import java.io.File;

/**
 * Created by mbaltazar on 6/10/15.
 */
public class Misc {
    public static File getFileDirectory(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }
}
