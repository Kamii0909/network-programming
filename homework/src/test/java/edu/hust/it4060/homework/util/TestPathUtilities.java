package edu.hust.it4060.homework.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TestPathUtilities {
    private TestPathUtilities() {}

    // The JVM should be launch from ../homework
    private static final String WORKING_DIRECTORY = System.getProperty("user.dir");
    
    public static Path getSourceFolderPath(Package packaze) {
        return Paths.get(WORKING_DIRECTORY,
            "src/test/java",
            packaze.getName().replace(".", "/"));
    }
}
