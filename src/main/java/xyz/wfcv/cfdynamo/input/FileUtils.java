package xyz.wfcv.cfdynamo.input;

import java.io.File;
import java.io.IOException;
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
            targetFilePaths = new ArrayList<>();
            targetFilePaths.add(path);
        }
        return targetFilePaths;
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
