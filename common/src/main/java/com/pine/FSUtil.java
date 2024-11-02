package com.pine;

import com.google.gson.Gson;
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

    public static boolean writeBinary(Object obj, String path) {
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

    public static Object readBinary(String path) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            return in.readObject();
        } catch (Exception e) {
            LOGGER.error("Could not read file {}", path, e);
            return null;
        }
    }

    public static boolean writeJson(Object obj, String path) {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(path)) {
            gson.toJson(obj, writer);
            return true;
        } catch (IOException e) {
            LOGGER.error("Could not write file {}", path, e);
            return false;
        }
    }

    public static <T> T readJson(String path, Class<T> classType) {
        if(!new File(path).exists()) {
            return null;
        }
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(path)) {
            return gson.fromJson(reader, classType);
        } catch (Exception e) {
            LOGGER.error("Could not read file {}", path, e);
            return null;
        }
    }

    public static <T> T readJsonSilent(String path, Class<T> classType) {
        if(!new File(path).exists()) {
            return null;
        }
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(path)) {
            return gson.fromJson(reader, classType);
        } catch (Exception e) {
            return null;
        }
    }

    public static void delete(String pathToFile) {
        new File(pathToFile).delete();
    }
}
