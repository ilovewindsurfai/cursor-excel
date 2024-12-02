package com.example.excel.dto;

import com.example.excel.annotation.ExcelColumn;
import com.example.excel.annotation.ColumnType;
import lombok.Builder;
import lombok.Value;
import java.time.LocalDate;
import java.math.BigDecimal;

@Value
@Builder
public class UserDTO {
    @ExcelColumn(header = "ID", order = 1)
    Long id;

    @ExcelColumn(header = "Full Name", order = 2)
    String fullName;

    @ExcelColumn(header = "Email Address", order = 3)
    String email;

    @ExcelColumn(
        header = "Join Date", 
        order = 4, 
        type = ColumnType.DATE,
        dateFormat = "dd/MM/yyyy"
    )
    LocalDate joinDate;

    @ExcelColumn(
        header = "Account Balance", 
        order = 5, 
        type = ColumnType.NUMBER,
        numberFormat = "$#,##0.00"
    )
    BigDecimal accountBalance;
} 