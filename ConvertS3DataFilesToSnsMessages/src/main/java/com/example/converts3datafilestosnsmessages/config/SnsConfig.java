package com.example.converts3datafilestosnsmessages.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

import java.net.URI;

@Configuration
public class SnsConfig {

    private final Region region = Region.US_EAST_1;

    @Bean
    public SnsClient snsClient(){
        return SnsClient.builder()
                .region(region)
                .endpointOverride(URI.create("http://localhost:4566"))
                // .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .credentialsProvider(LocalStackCredentials::new)
                .build();
    }

}
