package ch.uzh.ifi.hase.soprafs24.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyConfig {

    @Value("${API_KEY_ANDRI}")
    private String apikey;

    public String getKey() {
        return apikey;
    }

    public void setAndri(String andri) {
        this.apikey = andri;
    }
}

