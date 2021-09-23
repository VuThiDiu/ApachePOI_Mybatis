package com.example.ApachePOIExample.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
public class UploadFileController {
    @PostMapping("/uploadFile")
    public ResponseEntity<String> fileUpload(@RequestParam("file")MultipartFile file){
        System.out.println("this is inside ");
        if (file.isEmpty()){
            return new ResponseEntity<String>("Cant upload the file. Plz check file. ", HttpStatus.EXPECTATION_FAILED);
        }
        else if (file.getSize() > 1000000){
            return new ResponseEntity<String>("File is too long. Plz choose the file has size max is 10MB. ", HttpStatus.OK);
        }else if (file.getName().length() > 250){
            return new ResponseEntity<String>("File name is too long. Plz rename the file before upload. ", HttpStatus.OK);
        }else if(!file.getOriginalFilename().endsWith(".xlsx")){
            return new ResponseEntity<String>("You must upload file extension has  .xlsx", HttpStatus.EXPECTATION_FAILED);
        }
        return new ResponseEntity<String>("Upload file successfully ", HttpStatus.OK);
    }
}
