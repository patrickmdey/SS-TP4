package main.java.ar.edu.itba.ss.utils;

import java.io.File;

public class FileReader {
    public static File getFile(String path) {
        try {
            return new File("src/main.java.ar.edu.itba.ss.files/" + path);
        } catch (NullPointerException e) {
            throw new RuntimeException(e);
        }
    }
}
