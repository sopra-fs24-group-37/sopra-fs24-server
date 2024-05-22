package ch.uzh.ifi.hase.soprafs24.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "api.key")
public class ApiKeyConfig {

    private String andri;

    public String getKey() {
        return andri;
    }

    public void setAndri(String andri) {
        this.andri = andri;
    }
}

