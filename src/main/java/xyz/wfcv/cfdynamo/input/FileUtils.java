package xyz.wfcv.cfdynamo.input;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Methods for file input validation and handling
 */
public class FileUtils {
    private FileUtils() { }

    /**
     * Makes sure that given input is a class file or directory that contains one or more
     * class files.
     * @param path
     * @param isDir
     */
    public static List<String> validateAndGetAbsolutePaths(String path, Boolean isDir) {
        List<String> targetFilePaths = new ArrayList<String>();
        var file = new File(path);
        // if nothing exists there
        if (!file.exists()) {
            throw new IllegalArgumentException(
                    String.format("No %s exists at path: %s\n", (isDir ? "directory" : "file"), path));
        }
        if (isDir) {
            // if directory flag set but not a directory
            if (!file.isDirectory()) {
                throw new IllegalArgumentException("Input supplied with -d flag, but is not a directory: " + path);
            }
            targetFilePaths = listFilesInDirectory(path);
            // make sure there are class files in the directory
            if (targetFilePaths.size() == 0) {
                throw new IllegalArgumentException("No .class files in directory: " + path);
            }
        } else {  // its a single file
            if (!path.endsWith(".class")) {
                throw new IllegalArgumentException("Input is not a class file: " + path);
            }
            var absolutePath = new File(path).getAbsolutePath();
            targetFilePaths.add(absolutePath);
        }
        return targetFilePaths;
    }

    /**
     * Given a list of absolute file paths, loads classes using URLClassloader.
     * @param absolutePaths List of file paths
     * @return List of Class literals
     * @throws IllegalArgumentException
     */
    public static List<Class> loadClassesFromFiles(List<String> absolutePaths) throws IllegalArgumentException {
        if (absolutePaths.isEmpty()) return null;
        try {
            var classes = new ArrayList<Class>();
            var parentDir = new File(absolutePaths.get(0)).getParentFile();
            var classLoader = new URLClassLoader(new URL[]{parentDir.toURI().toURL()});
            for (var path : absolutePaths) {
                var className = new ClassParser(path).parse().getClassName();
                classes.add(classLoader.loadClass(className));
            }
            return classes;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Given a directory, gets all files within
     * @param path Path to directory
     * @return List of absolute file paths
     * @throws IOException
     */
    private static List<String> listFilesInDirectory(String path) {
        try (var paths = Files.list(Paths.get(path))) {
            return paths
                    .filter(it -> Files.isRegularFile(it) && it.toString().endsWith(".class"))
                    .map(it -> it.toAbsolutePath().toString())
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            return new ArrayList<>();
        }
    }
}
