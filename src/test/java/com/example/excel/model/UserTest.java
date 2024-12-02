package com.example.excel.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Arrange
        User user = new User();
        Long id = 1L;
        String fullName = "John Doe";
        String email = "john@example.com";
        LocalDate joinDate = LocalDate.of(2023, 1, 15);
        BigDecimal accountBalance = new BigDecimal("1000.50");

        // Act
        user.setId(id);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setJoinDate(joinDate);
        user.setAccountBalance(accountBalance);

        // Assert
        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getFullName()).isEqualTo(fullName);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getJoinDate()).isEqualTo(joinDate);
        assertThat(user.getAccountBalance()).isEqualTo(accountBalance);
    }

    @Test
    void jpaAnnotations_ShouldBePresent() throws Exception {
        // Act & Assert
        assertThat(User.class.isAnnotationPresent(jakarta.persistence.Entity.class)).isTrue();
        assertThat(User.class.isAnnotationPresent(jakarta.persistence.Table.class)).isTrue();

        // Check field annotations
        assertThat(User.class.getDeclaredField("id").isAnnotationPresent(jakarta.persistence.Id.class)).isTrue();
        assertThat(User.class.getDeclaredField("id").isAnnotationPresent(jakarta.persistence.GeneratedValue.class)).isTrue();

        assertThat(User.class.getDeclaredField("fullName").isAnnotationPresent(jakarta.persistence.Column.class)).isTrue();
        assertThat(User.class.getDeclaredField("email").isAnnotationPresent(jakarta.persistence.Column.class)).isTrue();
        assertThat(User.class.getDeclaredField("joinDate").isAnnotationPresent(jakarta.persistence.Column.class)).isTrue();
        assertThat(User.class.getDeclaredField("accountBalance").isAnnotationPresent(jakarta.persistence.Column.class)).isTrue();
    }
} 