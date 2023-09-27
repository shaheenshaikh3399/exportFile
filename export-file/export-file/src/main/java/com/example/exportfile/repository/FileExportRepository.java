package com.example.exportfile.repository;

import com.example.exportfile.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileExportRepository extends JpaRepository<Employee, Integer> {
}
