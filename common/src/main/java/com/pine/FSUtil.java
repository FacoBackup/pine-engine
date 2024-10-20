package com.pine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class FSUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FSUtil.class);

    public static byte[] loadResource(String path) {
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            try (InputStream inputStream = classloader.getResourceAsStream(path)) {
                if (inputStream != null) {
                    return inputStream.readAllBytes();
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error while reading resource {}", e.getMessage());
        }
        throw new RuntimeException("No resource found for " + path);
    }

    public static String getNameFromPath(String path) {
        File file = new File(path);
        String fileName = file.getName();
        int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot == -1) {
            return fileName;
        }
        return fileName.substring(0, lastIndexOfDot);
    }

    public static boolean write(Object obj, String path) {
        try {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
                out.writeObject(obj);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("Could not write file {}", path, e);
            return false;
        }
    }

    public static Object read(String path) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            return in.readObject();
        } catch (Exception e) {
            LOGGER.error("Could not read file {}", path, e);
            return null;
        }
    }

    public static Object readSilent(String path) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            return in.readObject();
        } catch (Exception e) {
            return null;
        }
    }
}
