package com.example.converts3datafilestosnsmessages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

import javax.annotation.PostConstruct;


@Service
public class SnsService {

    @Autowired
    private SnsClient snsClient;

    // comes out something like arn:aws:sns:us-east-1:000000000000:cmClicksTopic-prod
    // TODO: paste the topicArn of the production in here
    private String topicArn;

    public void pushMessageToSns(String message){
        publishToTopic(snsClient,message,topicArn);
    }

    @PostConstruct
    public void createSNSTopic() {
        CreateTopicResponse result = null;
        try {
            CreateTopicRequest request = CreateTopicRequest.builder()
                    .name("cmClicksTopic-prod")
                    .build();

            result = snsClient.createTopic(request);
            System.out.println("Arn of the topic is:- "+result.topicArn());
            topicArn = result.topicArn();
        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public void publishToTopic(SnsClient snsClient, String message, String topicArn) {

        try {
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .topicArn(topicArn)
                    .build();

            PublishResponse result = snsClient.publish(request);
            if(!result.sdkHttpResponse().isSuccessful()){
                System.out.println(result.messageId() + " Message sent. Status is " + result.sdkHttpResponse().statusCode());
            }
        } catch (SnsException e) {
            e.printStackTrace();
        }
    }

}
