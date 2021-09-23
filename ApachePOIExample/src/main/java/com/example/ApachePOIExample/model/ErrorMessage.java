package com.example.ApachePOIExample.model;

public class ErrorMessage {
    private long id;
    private String sheet_name;
    private String header_name;
    private int row_number;
    private String error_message;

    public ErrorMessage() {
    }


    public ErrorMessage(String sheet_name, String header_name, int row_number, String error_message) {
        this.sheet_name = sheet_name;
        this.header_name = header_name;
        this.row_number = row_number;
        this.error_message = error_message;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSheet_name() {
        return sheet_name;
    }

    public void setSheet_name(String sheet_name) {
        this.sheet_name = sheet_name;
    }

    public String getHeader_name() {
        return header_name;
    }

    public void setHeader_name(String header_name) {
        this.header_name = header_name;
    }

    public int getRow_number() {
        return row_number;
    }

    public void setRow_number(int row_number) {
        this.row_number = row_number;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }
}
