package com.example.exportfile.controller;

import com.example.exportfile.entity.Employee;
import com.example.exportfile.entity.FileFormat;
import com.example.exportfile.repository.FileExportRepository;
import com.example.exportfile.service.FileExportService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/generate-export")


public class FileExportController {

    private final FileExportService fileExportService;
    @GetMapping("/format/{format}")
    public ResponseEntity<byte[]> exportEmployeeData(
            @PathVariable FileFormat format,
            HttpServletResponse response) throws IOException {

        byte[] reportData = fileExportService.generateEmployeeReport(format);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = dateFormat.format(new Date());

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
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        String filename = "export_" + timestamp + "." + fileExtension;
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.getOutputStream().write(reportData);

        return null;
    }
    @PostMapping
    public ResponseEntity<Employee> saveEmployee(@RequestBody Employee employee){
        return new ResponseEntity<>(fileExportService.saveEmployee(employee), HttpStatus.CREATED);
    }
}

