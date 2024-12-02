package com.example.excel.service;

import com.example.excel.dto.UserDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApachePoiExcelGeneratorTest {

    private ApachePoiExcelGenerator excelGenerator;
    private List<UserDTO> users;

    @BeforeEach
    void setUp() {
        excelGenerator = new ApachePoiExcelGenerator();
        users = Arrays.asList(
            UserDTO.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john@example.com")
                .joinDate(LocalDate.of(2023, 1, 15))
                .accountBalance(new BigDecimal("1000.50"))
                .build(),
            UserDTO.builder()
                .id(2L)
                .fullName("Jane Smith")
                .email("jane@example.com")
                .joinDate(LocalDate.of(2023, 2, 20))
                .accountBalance(new BigDecimal("2500.75"))
                .build()
        );
    }

    @Test
    void generateExcel_WithValidData_ShouldCreateFile(@TempDir Path tempDir) throws Exception {
        // Arrange
        String fileName = tempDir.resolve("test.xlsx").toString();

        // Act
        excelGenerator.generateExcel(users, UserDTO.class, fileName);

        // Assert
        File excelFile = new File(fileName);
        assertTrue(excelFile.exists());
        assertTrue(excelFile.length() > 0);

        // Verify file contents
        try (Workbook workbook = new XSSFWorkbook(excelFile)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // Verify headers
            Row headerRow = sheet.getRow(0);
            assertEquals("ID", headerRow.getCell(0).getStringCellValue());
            assertEquals("Full Name", headerRow.getCell(1).getStringCellValue());
            assertEquals("Email Address", headerRow.getCell(2).getStringCellValue());
            assertEquals("Join Date", headerRow.getCell(3).getStringCellValue());
            assertEquals("Account Balance", headerRow.getCell(4).getStringCellValue());

            // Verify first data row
            Row firstDataRow = sheet.getRow(1);
            assertEquals(1.0, firstDataRow.getCell(0).getNumericCellValue());
            assertEquals("John Doe", firstDataRow.getCell(1).getStringCellValue());
            assertEquals("john@example.com", firstDataRow.getCell(2).getStringCellValue());
            assertEquals("15/01/2023", firstDataRow.getCell(3).getStringCellValue());
            assertEquals("$1,000.50", firstDataRow.getCell(4).getStringCellValue());
        }
    }

    @Test
    void generateExcel_WithEmptyList_ShouldThrowException() {
        // Arrange
        List<UserDTO> emptyList = List.of();

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            excelGenerator.generateExcel(emptyList, UserDTO.class, "test.xlsx")
        );
        assertEquals("Data cannot be empty", exception.getMessage());
    }

    @Test
    void generateExcel_WithNullList_ShouldThrowException() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            excelGenerator.generateExcel(null, UserDTO.class, "test.xlsx")
        );
        assertEquals("Data cannot be empty", exception.getMessage());
    }

    @Test
    void generateExcel_HeadersShouldBeStyled(@TempDir Path tempDir) throws Exception {
        // Arrange
        String fileName = tempDir.resolve("test.xlsx").toString();

        // Act
        excelGenerator.generateExcel(users, UserDTO.class, fileName);

        // Assert
        try (Workbook workbook = new XSSFWorkbook(new File(fileName))) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            Cell headerCell = headerRow.getCell(0);
            
            CellStyle headerStyle = headerCell.getCellStyle();
            Font headerFont = workbook.getFontAt(headerStyle.getFontIndex());
            
            assertTrue(headerFont.getBold());
            assertEquals(IndexedColors.GREY_25_PERCENT.getIndex(), 
                headerStyle.getFillForegroundColor());
            assertEquals(FillPatternType.SOLID_FOREGROUND, 
                headerStyle.getFillPattern());
        }
    }

    @Test
    void generateExcel_ShouldApplyCorrectFormatting(@TempDir Path tempDir) throws Exception {
        // Arrange
        String fileName = tempDir.resolve("test.xlsx").toString();

        // Act
        excelGenerator.generateExcel(users, UserDTO.class, fileName);

        // Assert
        try (Workbook workbook = new XSSFWorkbook(new File(fileName))) {
            Sheet sheet = workbook.getSheetAt(0);
            Row firstDataRow = sheet.getRow(1);
            
            // Verify date formatting
            Cell dateCell = firstDataRow.getCell(3);
            assertEquals("dd/MM/yyyy", 
                dateCell.getCellStyle().getDataFormat());
            assertEquals("15/01/2023", 
                dateCell.getStringCellValue());

            // Verify number formatting
            Cell numberCell = firstDataRow.getCell(4);
            assertEquals("$#,##0.00", 
                numberCell.getCellStyle().getDataFormat());
            assertEquals("$1,000.50", 
                numberCell.getStringCellValue());
            assertEquals(1000.50, 
                numberCell.getNumericCellValue(), 0.001);
        }
    }
} 