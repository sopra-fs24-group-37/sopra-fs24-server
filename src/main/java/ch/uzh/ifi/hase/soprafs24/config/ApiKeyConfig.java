package ch.uzh.ifi.hase.soprafs24.config;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ApiKeyConfig {

    private final String[] apiKeys = {
            "S65zj1IttPxsMT5yrzWsgLnC6PZT6XfSVxN9a5FX2U4",
            "vzUYuzlG1QUpgAi-uyHM0Rdm9uEwmf6YCbUwHS6TVXI",
            "1omf5cvm2NVeab3a_bxi8Rwd4AqBT7ewGtnnMBNgQjQ",
            "Ri4Er_Nr9GAMk_QTpErZvHuVESEVmM7RNKnymwEcNZM"
    };
    private static final int INTERVAL_MINUTES = 15;

    public String getCurrentApiKey() {
        long currentTimeMillis = Instant.now().toEpochMilli();
        int index = (int) ((currentTimeMillis / (INTERVAL_MINUTES * 60 * 1000)) % apiKeys.length);
        return apiKeys[index];
    }
}

