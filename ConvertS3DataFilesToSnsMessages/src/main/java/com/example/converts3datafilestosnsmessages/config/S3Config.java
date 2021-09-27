package com.example.converts3datafilestosnsmessages.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class S3Config {

    private Region region = Region.US_EAST_1;

    @Bean
    public S3Client s3Client(){
        return S3Client.builder().region(region)
                .endpointOverride(URI.create("http://localhost:4566"))
                .credentialsProvider(LocalStackCredentials::new)
                // .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
    }

}
