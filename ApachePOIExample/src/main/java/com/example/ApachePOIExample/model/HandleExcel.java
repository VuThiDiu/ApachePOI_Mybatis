package com.example.ApachePOIExample.model;
import org.apache.poi.ss.usermodel.*;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class HandleExcel<T> {
    private ArrayList<ErrorMessage> errorMessageList;
    private ArrayList<T> activateList;
    private boolean isError;
    private Sheet sheet;
    private int currentRow;
    private HashMap<Integer, String> header = new HashMap<>();
    private HashMap<String, Integer> mapHeader = new HashMap<>();
    private int numberCell;
    private boolean initHandle = true;
    public HandleExcel(Sheet sheet) {
        this.activateList = new ArrayList<>();
        this.sheet = sheet;
        this.isError = false;
        this.currentRow = 0;
    }


    public HashMap<Integer, String> getHeader() {
        return header;
    }

    public void setHeader(HashMap<Integer, String> header) {
        this.header = header;
    }

    public int getNumberCell() {
        return numberCell;
    }

    public void setNumberCell(int numberCell) {
        this.numberCell = numberCell;
    }


    public int handleData(Class<T> cls){
        for (Row  row : sheet){
            int currentCell = 1;
            try {
            this.setCurrentRow(this.getCurrentRow() + 1);
            Object obj = cls.newInstance();
            if(this.initHandle == true){
                for(Cell cell: row){
                    header.put(currentCell, cell.getStringCellValue());
                    mapHeader.put(cell.getStringCellValue(), currentCell);
                    currentCell ++;
                }
                this.initHandle = false;
                continue;
            }
            for (Cell cell : row){
                Field fieldObject = cls.getDeclaredField(Arrays.stream(header.get(currentCell).toLowerCase().split(" ")).collect(Collectors.joining("_")));
                String mess;
                if(obj instanceof  Campaign ){
                    mess = readAndValidateDataForCampaign(obj, fieldObject, cell, row);

                }else {
                    mess = readAndValidateDataForAd(obj, fieldObject, cell);
                }
                if (!mess.equals("Done")){
                    if(this.isError() == false){
                        this.setError(true);
                        this.setErrorMessageList(new ArrayList<>());
                        this.activateList.clear();
                    }
                    errorMessageList.add(new ErrorMessage(sheet.getSheetName(), this.header.get(currentCell), this.getCurrentRow(), mess ));
                }
                currentCell ++;
            }
                if(this.isError() == false) {
                    activateList.add((T) obj);
                }
            } catch (InstantiationException e) {

                e.printStackTrace();
                return 0;
            } catch (IllegalAccessException e) {

                e.printStackTrace();
                return 0;
            } catch (NoSuchFieldException e) {

                e.printStackTrace();
                return 0;
            }
        }
        return 1;
    }
    public String readAndValidateDataForCampaign(Object obj,  Field field, Cell cell,  Row row){
        field.setAccessible(true);
        if (field.getName().equals("campaign_id")){
            if(cell.getCellType() !=  CellType.NUMERIC){
                return "Không phải định dạng số" ;
            } else if (String.valueOf((long)cell.getNumericCellValue()).length() > 10){
                return "Không được vượt quá 10 ký tự"  ;
            }
            else {
                if (this.isError() == false) {
                    try {
                        field.set(obj, (long) cell.getNumericCellValue());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (field.getName().equals("campaign_name")){
            if (cell.getCellType() != CellType.STRING){
                return "Không phai kiểu chuỗi";
            }else if(cell.getStringCellValue().length() > 25){
                return "Không được quá 25 ký tự";
            }else {
                if(this.isError == false){
                    try {
                        field.set(obj, cell.getStringCellValue());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else if (field.getName().equals("campaign_status")){
            if (cell.getCellType() != CellType.STRING){
                return "Không phai kiểu chuỗi";
            }else if(!cell.getStringCellValue().toLowerCase().equals("active") && !cell.getStringCellValue().toLowerCase().equals("paused") &&  !cell.getStringCellValue().toLowerCase().equals("removed") ){
                return "Không chứa một trong các giá trị : Active, Paused, Removed";
            }else {
                if(this.isError == false){
                    try {
                        field.set(obj, cell.getStringCellValue());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (field.getName().equals("start_date") || field.getName().equals("end_date")) {
            if(cell.getCellType() != CellType.NUMERIC){
                return "Không đúng định dạng YYYY-MM-DD";
            }
            else if (!cell.getCellStyle().getDataFormatString().equals("yyyy\\-mm\\-dd")){
                return "Không đúng định dạng YYYY-MM-DD";
            }
            else if(cell.getLocalDateTimeCellValue().compareTo(LocalDateTime.now()) > 0){
                return "Không được phép là ngày quá khứ";
            }else if (field.getName().equals("end_date")){
                int cellOfStartDate =  this.mapHeader.get("Start Date") - 1;
                Cell cell1 = row.getCell(cellOfStartDate);
                if(cell1.getCellType() == CellType.NUMERIC){
                    if (cell.getLocalDateTimeCellValue().compareTo(cell1.getLocalDateTimeCellValue()) <= 0){
                        return "Không được phép trước  ngày Start Date";
                    }
                }

            }
            else {
            }
            if (this.isError() == false) {
                try {
                    field.set(obj, cell.getDateCellValue());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        if( field.getName().equals("budget")){
            if (cell.getCellType() != CellType.NUMERIC) {
                return "Không phải định dạng số";
            } else if (cell.getNumericCellValue() > 100000000) {
                return "Không được vượt quá 100000000";
            } else {
                if (this.isError() == false) {
                    try {
                        field.set(obj, (float) cell.getNumericCellValue());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return "Done";
    }

    // validate data for Ad
    public String readAndValidateDataForAd(Object obj,  Field field, Cell cell){
        field.setAccessible(true);
        if (field.getName().equals("ad_id")){
            if(cell.getCellType() !=  CellType.NUMERIC){
                return "Không phải định dạng số";
            } else if (String.valueOf((long)cell.getNumericCellValue()).length() > 10){
                return "Không được vượt quá 10 ký tự";
            }
            else {
                if (this.isError() == false) {
                    try {
                        field.set(obj, (long) cell.getNumericCellValue());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        if (field.getName().equals("ad_name")){
            if (cell.getCellType() != CellType.STRING){
                return "Không phai kiểu chuỗi";
            }else if(cell.getStringCellValue().length() > 25){
                return "Không được quá 25 ký tự";
            }else {
                if(this.isError == false){
                    try {
                        field.set(obj, cell.getStringCellValue());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (field.getName().equals("ad_status")){
            if (cell.getCellType() != CellType.STRING){
                return "Không phai kiểu chuỗi";
            }else if(!cell.getStringCellValue().toLowerCase().equals("active") && !cell.getStringCellValue().toLowerCase().equals("paused") &&  !cell.getStringCellValue().toLowerCase().equals("removed") ){
                return "Không chứa một trong các giá trị : Active, Paused, Removed";
            }else {
                if(this.isError == false){
                    try {
                        field.set(obj, cell.getStringCellValue());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (field.getName().equals("ad_type")) {
            if (cell.getCellType() != CellType.STRING){
                return "Không phai kiểu chuỗi";
            }else if(!cell.getStringCellValue().toLowerCase().equals("search") && !cell.getStringCellValue().toLowerCase().equals("display") &&  !cell.getStringCellValue().toLowerCase().equals("video") ){
                return "Không chứa một trong các giá trị : Search, Display, Video";
            }else {
                if(this.isError == false){
                    try {
                        field.set(obj, cell.getStringCellValue());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if(field.getName().equals("bid_modifier")) {
            if (cell.getCellType() != CellType.NUMERIC) {
                return "Không phải định dạng số";
            } else if (cell.getNumericCellValue() > 5000) {
                return "Không được vượt quá 5000";
            } else {
                if (this.isError() == false) {
                    try {
                        field.set(obj, (float) cell.getNumericCellValue());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return "Done";
    }


    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public ArrayList<ErrorMessage> getErrorMessageList() {
        return errorMessageList;
    }

    public void setErrorMessageList(ArrayList<ErrorMessage> errorMessageList) {
        this.errorMessageList = errorMessageList;
    }
    public int getCurrentRow() {
        return currentRow;
    }

    public void setCurrentRow(int currentRow) {
        this.currentRow = currentRow;
    }

    public ArrayList<T> getActivateList() {
        return activateList;
    }

    public void setActivateList(ArrayList<T> activateList) {
        this.activateList = activateList;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }
}
