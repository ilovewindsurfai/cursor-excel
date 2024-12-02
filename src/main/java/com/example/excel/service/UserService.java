package com.example.excel.service;

import com.example.excel.dto.UserDTO;
import com.example.excel.mapper.UserMapper;
import com.example.excel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ApachePoiExcelGenerator excelGenerator;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public void exportUsersToExcel(String filePath) throws Exception {
        try (var userStream = userRepository.streamAllBy()) {
            var userDTOs = userStream
                .map(userMapper::toDTO)
                .toList();
            excelGenerator.generateExcel(userDTOs, UserDTO.class, filePath);
        }
    }
} 