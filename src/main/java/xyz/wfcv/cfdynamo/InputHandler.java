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

    private InputHandler() { }

    public InputHandler(String path, Boolean isDir) throws IllegalArgumentException, IOException {
        var file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException(
                    String.format("No %s exists at path: %s\n", (isDir ? "directory" : "file"), path));
        }

        if (isDir) {
            if (!file.isDirectory()) {
                throw new IllegalArgumentException("Input supplied with -d flag, but is not a directory: " + path);
            }
            parentDirectory = path;
            targetFilePaths = listFilesInDirectory(path);
        } else {
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
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        }
    }
}
