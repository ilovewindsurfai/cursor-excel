package com.example.excel.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {
    String header() default "";
    int order() default 0;
    String dateFormat() default "yyyy-MM-dd";
    String numberFormat() default "#,##0.00";
    ColumnType type() default ColumnType.TEXT;
}

