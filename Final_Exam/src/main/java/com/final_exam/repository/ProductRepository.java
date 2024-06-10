package com.final_exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.final_exam.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    // 추가적인 쿼리 메소드를 정의할 수 있습니다.
}