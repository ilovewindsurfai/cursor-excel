package com.example.excel.repository;

import com.example.excel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.stream.Stream;
import java.time.LocalDate;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = """
            SELECT id, full_name, email, join_date, account_balance 
            FROM users 
            ORDER BY id
            """, nativeQuery = true)
    Stream<User> streamAllBy();

    @Query(value = """
            SELECT id, full_name, email, join_date, account_balance 
            FROM users 
            WHERE join_date >= :startDate 
            AND join_date <= :endDate 
            ORDER BY join_date, id
            """, nativeQuery = true)
    Stream<User> streamByJoinDateBetween(LocalDate startDate, LocalDate endDate);
} 