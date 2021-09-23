package com.example.ApachePOIExample.controller;


import com.example.ApachePOIExample.service.ExcelHandlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;


@RestController
@RequestMapping("/handleFile")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ExcelHandlingController {
    private String PATH_FILE = "src/main/resources/excelFiles/";;
    ExcelHandlingService excelHandlingService;
    @PostMapping(value = "/less-than-5MB", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<String> handleFileExcel(@RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName) throws IOException {
        excelHandlingService = new ExcelHandlingService();
        boolean isError =  excelHandlingService.handleFileHasSizeLessThan5MB(file, fileName);
        if(!isError){
            return new ResponseEntity<String>("Success" , HttpStatus.OK);
        }else{
            return new ResponseEntity<String>(excelHandlingService.getInputFile().getName(),  HttpStatus.EXPECTATION_FAILED);
        }
        }
    @PostMapping("/more-than-5MB")
    public ResponseEntity<String> handleFileExcelHasSizeMoreThan5MB( @RequestParam("file") MultipartFile file , @RequestParam("fileName") String fileName) throws FileNotFoundException {
        excelHandlingService = new ExcelHandlingService();
        boolean isError = excelHandlingService.handleFileHasSizeMoreThan5MB(file, fileName);
        if(!isError){
            return new ResponseEntity<String>("Success" , HttpStatus.OK);
        }else{
            return new ResponseEntity<String>(excelHandlingService.getInputFile().getName(),  HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> donwloadFile(@PathVariable("fileName") String fileName) throws IOException{
        String pathFile = PATH_FILE+ fileName;
        File file = new File(pathFile);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        System.out.println(file.getName());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .body(resource);
    }
    // something else
}
