package com.example.ApachePOIExample.service;


import com.example.ApachePOIExample.model.Ad;
import com.example.ApachePOIExample.model.Campaign;
import com.example.ApachePOIExample.model.ErrorMessage;
import com.example.ApachePOIExample.model.HandleExcel;


import com.github.pjfanning.xlsx.StreamingReader;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExcelHandlingService {
    private Workbook workbookError;
    private boolean isError = false;
    private Sheet errorSheet;
    private File inputFile;
    private String PATH_FILE = "src/main/resources/excelFiles/";
    public boolean handleFileHasSizeLessThan5MB(MultipartFile file, String fileName){
        try {
            FileInputStream fileInputStream = (FileInputStream) file.getInputStream();
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            HashMap<String, Class> listSheet = new HashMap<>();
            listSheet.put("Campaign", Campaign.class);
            listSheet.put("Ad", Ad.class);
            listSheet.put("ErrorMessage", ErrorMessage.class);
            for(int i = 0 ; i < workbook.getNumberOfSheets(); i++){
                Sheet sheet = workbook.getSheetAt(i);
                Class cls =listSheet.get(sheet.getSheetName());
                HandleExcel handleExcel = new HandleExcel(sheet);
                handleExcel.handleData(cls);
                if (!handleExcel.getActivateList().isEmpty()){
                    insertToDb(handleExcel.getActivateList(), cls);
                }else {
                    if(isError == false){
                        setError(true);
                        workbookError = new XSSFWorkbook();
                    }
                    errorSheet = workbookError.createSheet(sheet.getSheetName());
                    Class clss = listSheet.get("ErrorMessage");
                    int lastId = insertToDb(handleExcel.getErrorMessageList(), clss);
                    List<ErrorMessage> errorMessageList = readDataFromDB(lastId);
                    writeToFileExcel(errorSheet, errorMessageList);

                }
            }
            if(this.isError == true){
                inputFile = new File(PATH_FILE + fileName +" error.xlsx");
                inputFile.getParentFile().mkdirs();
                FileOutputStream outputStream = new FileOutputStream(inputFile);
                workbookError.write(outputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return this.isError;
    }
    public void writeToFileExcel(Sheet sheetName, List<ErrorMessage> listErrorMesssage){
        // dong khoi dau nay
        Row row ;
        Cell cell;
        row = sheetName.createRow(0);
        Field [] fields = ErrorMessage.class.getDeclaredFields();
        for (int i = 1; i< fields.length; i++){
            cell = row.createCell(i - 1, CellType.STRING);
            cell.setCellValue(Arrays.stream(fields[i].getName().split("_")).collect(Collectors.joining(" ")));
        }
        int rowNumber = 0;
        for(ErrorMessage errorMessage : listErrorMesssage){
            rowNumber ++;
            row = sheetName.createRow(rowNumber);
            for (int i = 1; i< fields.length; i++){
                if(fields[i].getName().equals("row_number")){
                    cell = row.createCell(i-1, CellType.NUMERIC);
                    cell.setCellValue((int) errorMessage.getRow_number());

                }else if(fields[i].getName().equals("sheet_name")){
                    cell = row.createCell(i-1, CellType.STRING);
                    cell.setCellValue( errorMessage.getSheet_name());
                }else if(fields[i].getName().equals("header_name")){
                    cell = row.createCell(i-1, CellType.STRING);
                    cell.setCellValue( errorMessage.getHeader_name());
                }else{
                    cell = row.createCell(i-1, CellType.STRING);
                    cell.setCellValue( errorMessage.getError_message());
                }
            }
        }
    }

    public int insertToDb(ArrayList<?> arr, Class<?> cls){
        int lastId  = 0;
        try {
            Reader reader = Resources.getResourceAsReader("SqlMapConfig.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            SqlSession session = sqlSessionFactory.openSession();

                if (session.selectOne("ErrorMessage.selectMaxId")!=null){
                    lastId = session.selectOne("ErrorMessage.selectMaxId");
                }

            arr.forEach((element) -> {
                session.insert(cls.getSimpleName()+".insert"+cls.getSimpleName(), element);
                session.commit();
            });
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lastId;
    }

    public List<ErrorMessage> readDataFromDB(int lastId){
        List<ErrorMessage> errorMessageList = new ArrayList<>();
        try {
            Reader reader = Resources.getResourceAsReader("SqlMapConfig.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            SqlSession session = sqlSessionFactory.openSession();
             errorMessageList = session.selectList("ErrorMessage.selectErrorMessage",lastId);
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return errorMessageList;
    }
    public boolean handleFileHasSizeMoreThan5MB(MultipartFile file, String fileName){
        try {
            FileInputStream inputStream = (FileInputStream) file.getInputStream();
            Workbook workbook = StreamingReader.builder()
                    .rowCacheSize(100)
                    .bufferSize(4096)
                    .open(inputStream);
            HashMap<String, Class> listSheet = new HashMap<>();
            listSheet.put("Campaign", Campaign.class);
            listSheet.put("Ad", Ad.class);
            listSheet.put("ErrorMessage", ErrorMessage.class);
            for(Sheet sheet : workbook){
                Class cls =listSheet.get(sheet.getSheetName());
                HandleExcel handleExcel = new HandleExcel(sheet);
                handleExcel.handleData(cls);
                if (!handleExcel.getActivateList().isEmpty()){
                    insertToDb(handleExcel.getActivateList(), cls);
                }else {
                    setError(true);
                    Class clss = listSheet.get("ErrorMessage");
                    insertToDb(handleExcel.getErrorMessageList(), clss);
                }
            }
            if(this.isError == true){
                inputFile = new File(PATH_FILE + fileName +" error.xlsx");
                inputFile.getParentFile().mkdirs();
                FileOutputStream outputStream = new FileOutputStream(inputFile);
                workbookError = new XSSFWorkbook();
                errorSheet = workbookError.createSheet("Error");
                List<ErrorMessage> errorMessageList = readDataFromDB(0);
                writeToFileExcel(errorSheet, errorMessageList);
                workbookError.write(outputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return this.isError;
            }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }
}
