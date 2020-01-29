package xyz.wfcv.cfdynamo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Validates input and provides methods to access absolute paths of all target files.
 */
public class InputHandler {
    private List<String> targetFilePaths;
    private String parentDirectory;

    /**
     * Constructor. Instantiates input handler which performs validation on file type and existence.
     * @param path File or directory path
     * @param isDir
     * @throws IllegalArgumentException if input is not class file or doesn't exist
     * @throws IOException
     */
    public InputHandler(String path, Boolean isDir) throws IllegalArgumentException, IOException {
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
            parentDirectory = path;
            targetFilePaths = listFilesInDirectory(path);
            // make sure there are class files in the directory
            if (targetFilePaths.size() == 0) {
                throw new IllegalArgumentException("No .class files in directory: " + path);
            }
        } else {  // its a single file
            if (!path.endsWith(".class")) {
                throw new IllegalArgumentException("Input is not a class file: " + path);
            }
            parentDirectory = file.getAbsoluteFile().getParent();
            targetFilePaths = new ArrayList<>();
            targetFilePaths.add(path);
        }
    }

    public List<String> getTargetFilePaths() {
        return targetFilePaths;
    }

    public String getParentDirectory() {
        return parentDirectory;
    }

    /**
     * Given a directory, gets all files within
     * @param path Path to directory
     * @return List of absolute file paths
     * @throws IOException
     */
    private static List<String> listFilesInDirectory(String path) throws IOException {
        try (var paths = Files.list(Paths.get(path))) {
            return paths
                    .filter(it -> Files.isRegularFile(it) && it.endsWith(".class"))
                    .map(it -> it.toAbsolutePath().toString())
                    .collect(Collectors.toList());
        }
    }
}
