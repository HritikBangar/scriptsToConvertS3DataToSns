//package com.example.converts3datafilestosnsmessages;
//
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//
//@Component
//public class StartScript implements ApplicationRunner {
//
//    private  final Integer lastRequiredDate=1;
//    private  final Integer hoursPartitions=24;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//
//        long start=System.currentTimeMillis();
//
//        List<CompletableFuture<Void>> executionResults=new ArrayList<>();
//        for(int i=1;i<=lastRequiredDate;i++){
//            String folderNameWithDate=convertToDateString(i);
//            for(int j=0;j<hoursPartitions;j++){
//                String folderNameWithDateAndHour=folderNameWithDate+"/"+convertToDateString(j);
//                S3Utilities s3Utilities=new S3Utilities();
//                executionResults.add(s3Utilities.startConversion(folderNameWithDateAndHour));
//            }
//        }
//
//        CompletableFuture.allOf(executionResults.toArray(new CompletableFuture[executionResults.size()])).join();
//
//        System.out.println("everything fine no exception");
//
//        long end=System.currentTimeMillis();
//        String returnLine="Time taken for Completion :-"+(end-start)+"milli-seconds";
//        System.out.println(returnLine);
//    }
//
//    public  String convertToDateString(Integer date){
//        String stringDate=date.toString();
//        if(stringDate.length()==2){
//            return stringDate;
//        }
//        return ("0"+stringDate);
//    }
//
//
//
//}
