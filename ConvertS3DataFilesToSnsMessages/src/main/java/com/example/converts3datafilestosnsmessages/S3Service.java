package com.example.converts3datafilestosnsmessages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;

    private Set<Long> groupIds=populateGroupIds();

    @Autowired
    private SnsService snsService;

    @Async
    public CompletableFuture<Void> startConversion(String folderKey) {
        readCompleteS3BucketAndUploadData("durin-events",folderKey);
        return CompletableFuture.completedFuture(null);
    }

    public  String[] getS3ObjectContent(String bucketName, String objectName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .build();
        ResponseInputStream<GetObjectResponse> responseInputStream=s3Client.getObject(getObjectRequest);
        BufferedReader br = new BufferedReader(new InputStreamReader(responseInputStream));
        StringBuilder sb = new StringBuilder();
        String line = "";
        try {
            line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (line != null) {
            sb.append(line);
            try {
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                line="";
            }
        }
        String fileAsString = sb.toString();
        fileAsString=fileAsString.replace("}{","};;;{");
        String[] responses=fileAsString.split(";;;");
        return responses;
    }

    public void readCompleteS3BucketAndUploadData(String bucketName, String folderKey) {
        ListObjectsRequest listObjectsRequest= ListObjectsRequest.builder()
                .bucket(bucketName)
                .prefix(folderKey+"/")
                .build();
        ListObjectsResponse listObjectsResponse;
        try{
             listObjectsResponse=s3Client.listObjects(listObjectsRequest);
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
        List<S3Object> s3ObjectList=listObjectsResponse.contents();
        if(s3ObjectList.size()<1) return;
        for(int i=0;i<s3ObjectList.size();i++){
            String[] temp;
            try {
                temp = getS3ObjectContent(bucketName,s3ObjectList.get(i).key());
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            for(int j=0;j<temp.length;j++){
                if(temp[i].length()==0) continue;
                Long groupId=extractGroupId(temp[i]);
                if(groupIds.contains(groupId)){
                    snsService.pushMessageToSns(temp[i]);
                }
            }
        }
    }

    public  Set<Long> populateGroupIds() {
        Set<Long> records;
        try{
            records = new HashSet<>();
            try (BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/src/main/resources/groupids.csv"))) {
                String line;
                int count=0;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    if(count!=0) records.add(Long.parseLong(values[1]));
                    count++;
                }
            }
        } catch (Exception e){
            throw new RuntimeException("Unable to load groupIds from Csv files present in local system");
        }
        return records;
    }

    public Long extractGroupId(String jsonString){
        int index=jsonString.indexOf("\"group\":{\"id\":");
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=index+14; i<jsonString.length() && jsonString.charAt(i)!=',';i++){
            stringBuilder.append(jsonString.charAt(i));
        }
        Long groupId=Long.parseLong(stringBuilder.toString());
        return groupId;
    }

}
