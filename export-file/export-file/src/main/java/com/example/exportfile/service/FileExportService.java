package com.example.exportfile.service;

import com.example.exportfile.entity.Employee;
import com.example.exportfile.entity.FileFormat;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

public interface FileExportService {
    public byte[] generateEmployeeReport(List<Employee> employees, FileFormat format) throws IOException;

    Employee saveEmployee(Employee employee);
}
