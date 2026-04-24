package com.nisa.itsm.category.repository;

import com.nisa.itsm.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
