package com.example.excel.mapper;

import com.example.excel.dto.UserDTO;
import com.example.excel.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User user);
} 