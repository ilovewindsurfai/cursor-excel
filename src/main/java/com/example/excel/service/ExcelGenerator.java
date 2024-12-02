package com.example.excel.service;

import com.example.excel.annotation.ExcelColumn;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ExcelGenerator {
    private static final ThreadLocal<Field> currentField = new ThreadLocal<>();
    
    public <T> void generateExcel(List<T> data, Class<T> clazz, String filePath) throws Exception {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data cannot be empty");
        }

        try (FileOutputStream outputStream = new FileOutputStream(filePath);
             Workbook workbook = new Workbook(outputStream, "ExcelGenerator", "1.0")) {
            
            Worksheet worksheet = workbook.newWorksheet("Sheet1");

            List<Field> fields = getAnnotatedFields(clazz);
            
            // Create header row
            createHeaderRow(worksheet, fields);

            // Create data rows
            createDataRows(worksheet, data, fields);

            // Auto-size columns
            for (int i = 0; i < fields.size(); i++) {
                worksheet.width(i, 20); // Set column width to 20 characters
            }
        }
    }

    private List<Field> getAnnotatedFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ExcelColumn.class)) {
                fields.add(field);
            }
        }
        return fields.stream()
                .sorted(Comparator.comparingInt(field -> 
                    field.getAnnotation(ExcelColumn.class).order()))
                .collect(Collectors.toList());
    }

    private void createHeaderRow(Worksheet worksheet, List<Field> fields) throws IOException {
        for (int i = 0; i < fields.size(); i++) {
            ExcelColumn annotation = fields.get(i).getAnnotation(ExcelColumn.class);
            worksheet.value(0, i, annotation.header());
            worksheet.style(0, i).bold().fillColor("D3D3D3").set();
        }
    }

    private <T> void createDataRows(Worksheet worksheet, List<T> data, List<Field> fields) throws Exception {
        for (int i = 0; i < data.size(); i++) {
            T item = data.get(i);
            
            for (int j = 0; j < fields.size(); j++) {
                Field field = fields.get(j);
                field.setAccessible(true);
                
                currentField.set(field);
                Object value = field.get(item);
                setCellValue(worksheet, i + 1, j, value);
                currentField.remove();
            }
        }
    }

    private void setCellValue(Worksheet worksheet, int row, int col, Object value) throws IOException {
        if (value == null) {
            worksheet.value(row, col, "");
            return;
        }

        if (value instanceof Number) {
            worksheet.value(row, col, ((Number) value).doubleValue());
        } else {
            worksheet.value(row, col, value.toString());
        }
    }
} 