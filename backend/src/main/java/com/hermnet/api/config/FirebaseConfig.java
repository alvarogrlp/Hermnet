package com.hermnet.api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.service.account.path:}")
    private String serviceAccountPath;

    @Value("${firebase.service.account.json:}")
    private String serviceAccountJson;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            GoogleCredentials credentials;

            if (serviceAccountJson != null && !serviceAccountJson.isEmpty()) {
                // Load from JSON string (e.g. environment variable)
                credentials = GoogleCredentials.fromStream(
                        new ByteArrayInputStream(serviceAccountJson.getBytes(StandardCharsets.UTF_8)));
            } else if (serviceAccountPath != null && !serviceAccountPath.isEmpty()) {
                // Load from file path
                credentials = GoogleCredentials.fromStream(new FileInputStream(serviceAccountPath));
            } else {
                // Fallback to Application Default Credentials (ADC)
                credentials = GoogleCredentials.getApplicationDefault();
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();

            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }
}
