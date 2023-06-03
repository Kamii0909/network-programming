package edu.hust.it4060.homework.util;

import java.nio.file.Path;
import java.nio.file.Paths;


public class PathUtilities {
    // The JVM should be launch from ../homework
    private static final String WORKING_DIRECTORY = System.getProperty("user.dir");
    
    public static Path getSourceFolderPath(Package packaze) {
        return Paths.get(WORKING_DIRECTORY,
            "/homework/src/main/java",
            packaze.getName().replace(".", "/"));
    }
    
    private PathUtilities() {
    }
}
