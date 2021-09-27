package com.example.converts3datafilestosnsmessages.controller;

import com.example.converts3datafilestosnsmessages.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class StartConversionController {

    // TODO: change these values based on what duration of data you want
    private  final Integer lastRequiredDate=1;
    private  final Integer hoursPartitions=24;

    @Autowired
    private S3Service s3Service;

    @GetMapping(value = "/start-s3-to-sns-conversion", produces = "application/json")
    public ResponseEntity<String> startS3ToSnsConversion(){

        long start=System.currentTimeMillis();
        List<CompletableFuture<Void>> executionResults=new ArrayList<>();
        for(int i=1;i<=lastRequiredDate;i++){
            String folderNameWithDate=convertToDateString(i);
            for(int j=0;j<hoursPartitions;j++){
                String folderNameWithDateAndHour=folderNameWithDate+"/"+convertToDateString(j);
                CompletableFuture<Void> temp= s3Service.startConversion(folderNameWithDateAndHour);
                executionResults.add(temp);
            }
        }
        CompletableFuture.allOf(executionResults.toArray(new CompletableFuture[executionResults.size()])).join();
        System.out.println("everything fine no exception");

        long end=System.currentTimeMillis();
        String returnLine="Time taken for Completion :-"+(end-start)+"milli-seconds";

        return ResponseEntity.ok(returnLine);

    }

    public  String convertToDateString(Integer date){
        String stringDate=date.toString();
        if(stringDate.length()==2){
            return stringDate;
        }
        return ("0"+stringDate);
    }

}
