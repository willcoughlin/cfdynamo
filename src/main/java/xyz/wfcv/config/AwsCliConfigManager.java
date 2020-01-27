package xyz.wfcv.config;

import org.ini4j.*;

public class AwsCliConfigManager implements ConfigManager {

    private static AwsCliConfigManager INSTANCE;

    private AwsCliConfigManager(){ }

    public AwsCliConfigManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AwsCliConfigManager();
        }
        return INSTANCE;
    }

    public String getValue(String propName) {
        return null;
    }
}
