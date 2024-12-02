package com.example.excel.dto;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;

class UserDTOTest {

    @Test
    void builder_ShouldCreateUserDTO() {
        // Arrange
        Long id = 1L;
        String fullName = "John Doe";
        String email = "john@example.com";
        LocalDate joinDate = LocalDate.of(2023, 1, 15);
        BigDecimal accountBalance = new BigDecimal("1000.50");

        // Act
        UserDTO userDTO = UserDTO.builder()
            .id(id)
            .fullName(fullName)
            .email(email)
            .joinDate(joinDate)
            .accountBalance(accountBalance)
            .build();

        // Assert
        assertThat(userDTO)
            .hasFieldOrPropertyWithValue("id", id)
            .hasFieldOrPropertyWithValue("fullName", fullName)
            .hasFieldOrPropertyWithValue("email", email)
            .hasFieldOrPropertyWithValue("joinDate", joinDate)
            .hasFieldOrPropertyWithValue("accountBalance", accountBalance);
    }

    @Test
    void value_ShouldProvideImmutability() {
        // Arrange & Act
        UserDTO userDTO1 = UserDTO.builder()
            .id(1L)
            .fullName("John Doe")
            .email("john@example.com")
            .joinDate(LocalDate.of(2023, 1, 15))
            .accountBalance(new BigDecimal("1000.50"))
            .build();

        UserDTO userDTO2 = UserDTO.builder()
            .id(1L)
            .fullName("John Doe")
            .email("john@example.com")
            .joinDate(LocalDate.of(2023, 1, 15))
            .accountBalance(new BigDecimal("1000.50"))
            .build();

        // Assert
        assertThat(userDTO1)
            .isEqualTo(userDTO2)
            .hasSameHashCodeAs(userDTO2);
    }
} 