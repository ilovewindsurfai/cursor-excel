package com.example.excel.service;

import com.example.excel.annotation.ExcelColumn;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicInteger;

public class ApachePoiExcelGenerator {
    private static final int STREAMING_WINDOW_SIZE = 100;
    
    public <T> void generateExcel(List<T> data, Class<T> clazz, String filePath) throws Exception {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data cannot be empty");
        }

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(STREAMING_WINDOW_SIZE)) {
            workbook.setCompressTempFiles(true);
            Sheet sheet = workbook.createSheet("Sheet1");

            List<Field> fields = getAnnotatedFields(clazz);
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            createHeaderRow(workbook, headerRow, fields);

            // Create data rows using streaming
            createDataRows(sheet, data.stream(), fields);

            // Write to file
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            } finally {
                workbook.dispose(); // Clean up temporary files
            }
        }
    }

    private List<Field> getAnnotatedFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ExcelColumn.class))
                .sorted(Comparator.comparingInt(field -> 
                    field.getAnnotation(ExcelColumn.class).order()))
                .collect(Collectors.toList());
    }

    private void createHeaderRow(Workbook workbook, Row headerRow, List<Field> fields) {
        CellStyle headerStyle = createHeaderStyle(workbook);
        
        for (int i = 0; i < fields.size(); i++) {
            Cell cell = headerRow.createCell(i);
            ExcelColumn annotation = fields.get(i).getAnnotation(ExcelColumn.class);
            cell.setCellValue(annotation.header());
            cell.setCellStyle(headerStyle);
        }
    }

    private <T> void createDataRows(Sheet sheet, java.util.stream.Stream<T> dataStream, List<Field> fields) {
        final AtomicInteger rowNum = new AtomicInteger(1);
        
        dataStream.forEach(item -> {
            try {
                Row row = sheet.createRow(rowNum.getAndIncrement());
                for (int j = 0; j < fields.size(); j++) {
                    Cell cell = row.createCell(j);
                    Field field = fields.get(j);
                    field.setAccessible(true);
                    
                    Object value = field.get(item);
                    setCellValue(cell, value, field);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error creating row", e);
            }
        });
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private void setCellValue(Cell cell, Object value, Field field) {
        if (value == null) {
            cell.setCellValue("");
            return;
        }

        ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
        CellStyle style = createCellStyle(cell.getSheet().getWorkbook(), annotation);
        cell.setCellStyle(style);

        switch (annotation.type()) {
            case DATE:
                if (value instanceof java.time.LocalDate) {
                    cell.setCellValue(java.sql.Date.valueOf((java.time.LocalDate) value));
                } else if (value instanceof Date) {
                    cell.setCellValue((Date) value);
                } else {
                    cell.setCellValue(value.toString());
                }
                break;
                
            case NUMBER:
                if (value instanceof Number) {
                    double numericValue = ((Number) value).doubleValue();
                    cell.setCellValue(numericValue);
                } else {
                    try {
                        double numericValue = Double.parseDouble(value.toString());
                        cell.setCellValue(numericValue);
                    } catch (NumberFormatException e) {
                        // If parsing fails, set as string
                        cell.setCellValue(value.toString());
                    }
                }
                break;
                
            case TEXT:
            default:
                cell.setCellValue(value.toString());
                break;
        }
    }

    private CellStyle createCellStyle(Workbook workbook, ExcelColumn annotation) {
        CellStyle style = workbook.createCellStyle();
        
        switch (annotation.type()) {
            case DATE:
                CreationHelper createHelper = workbook.getCreationHelper();
                style.setDataFormat(
                    createHelper.createDataFormat().getFormat(annotation.dateFormat())
                );
                break;
                
            case NUMBER:
                DataFormat format = workbook.createDataFormat();
                style.setDataFormat(format.getFormat(annotation.numberFormat()));
                break;
                
            case TEXT:
                // Default text formatting
                break;
        }
        
        return style;
    }
} 