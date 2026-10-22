package com.coopera2075.cooperanki.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.coopera2075.cooperanki.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    @Query("SELECT department FROM Department department ORDER BY department.name")
    List<Department> findAllOrderByName();
}
