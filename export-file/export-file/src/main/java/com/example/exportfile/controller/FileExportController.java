package com.example.exportfile.controller;

import com.example.exportfile.entity.Employee;
import com.example.exportfile.entity.FileFormat;
import com.example.exportfile.repository.FileExportRepository;
import com.example.exportfile.service.FileExportService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/export")


public class FileExportController {

    private final FileExportRepository fileExportRepository;
    private final FileExportService fileExportService;
    @GetMapping
    public ResponseEntity<byte[]> exportEmployeeData(
            @RequestParam FileFormat format,
            @RequestParam(required = false) Integer id) throws IOException {

        List<Employee> employees;

        if (id != null) {
            // Retrieving a single employee by ID
            Employee employee = fileExportRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
            employees = Collections.singletonList(employee);
        } else {
            // Retrieving all employees
            employees = fileExportRepository.findAll();
        }
        byte[] reportData = fileExportService.generateEmployeeReport(employees, format);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = dateFormat.format(new Date());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        String fileExtension;
        switch (format) {
            case EXCEL:
                fileExtension = "xlsx";
                break;
            case CSV:
                fileExtension = "csv";
                break;
            case HTML:
                fileExtension = "html";
                break;
            case PDF:
                fileExtension = "pdf";
                break;
            default:
                throw new IllegalArgumentException("Unsupported file format");
        }
        String filename = "export_" + timestamp + "." + fileExtension;
        headers.setContentDispositionFormData("attachment", filename);


        return new ResponseEntity<>(reportData, headers, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<Employee> saveEmployee(@RequestBody Employee employee){
        return new ResponseEntity<>(fileExportService.saveEmployee(employee), HttpStatus.CREATED);
    }
}

