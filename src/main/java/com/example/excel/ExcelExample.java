package com.example.excel;

import com.example.excel.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ExcelExample {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ExcelExample.class, args);
        UserService userService = context.getBean(UserService.class);
        
        try {
            userService.exportUsersToExcel("users.xlsx");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 