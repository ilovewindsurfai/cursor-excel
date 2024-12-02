package com.example.excel.annotation;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import static org.assertj.core.api.Assertions.assertThat;

class ExcelColumnTest {

    @ExcelColumn(header = "Test Header", order = 1)
    private String testField;

    @Test
    void testExcelColumnAnnotation() throws NoSuchFieldException {
        // Arrange
        Field field = ExcelColumnTest.class.getDeclaredField("testField");
        
        // Act
        ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);

        // Assert
        assertThat(annotation).isNotNull();
        assertThat(annotation.header()).isEqualTo("Test Header");
        assertThat(annotation.order()).isEqualTo(1);
    }

    @Test
    void testExcelColumnDefaultValues() {
        // Arrange
        class TestClass {
            @ExcelColumn
            private String field;
        }

        // Act
        ExcelColumn annotation = TestClass.class.getDeclaredFields()[0]
            .getAnnotation(ExcelColumn.class);

        // Assert
        assertThat(annotation.header()).isEmpty();
        assertThat(annotation.order()).isZero();
    }
} 