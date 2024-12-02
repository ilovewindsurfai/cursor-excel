package com.example.excel.model;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column
    private String email;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(name = "account_balance")
    private BigDecimal accountBalance;
} 