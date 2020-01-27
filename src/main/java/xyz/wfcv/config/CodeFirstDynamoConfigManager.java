package xyz.wfcv.config;

import java.io.File;

public class CodeFirstDynamoConfigManager implements ConfigManager {

    private CodeFirstDynamoConfigManager INSTANCE;

    private CodeFirstDynamoConfigManager() { }

    public CodeFirstDynamoConfigManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CodeFirstDynamoConfigManager();
        }
        return INSTANCE;
    }

    public String getValue(String propName) {
        return null;
    }

    private static String getUserDataDirectory() {
        return System.getProperty("user.home") + File.separator + ".codefirstdynamo" + File.separator;
    }
}
