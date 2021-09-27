package com.example.converts3datafilestosnsmessages.config;

import software.amazon.awssdk.auth.credentials.AwsCredentials;

public class LocalStackCredentials implements AwsCredentials {

  public String accessKeyId() {
    return "test";
  }

  public String secretAccessKey() {
    return "test";
  }
}
