package com.example.exportfile.service.impl;

import com.example.exportfile.entity.Employee;
import com.example.exportfile.entity.FileFormat;
import com.example.exportfile.repository.FileExportRepository;
import com.example.exportfile.service.FileExportService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
@AllArgsConstructor
@Service
@Slf4j
public class FileExportServiceImpl implements FileExportService {
    private final FileExportRepository fileExportRepository;
    @Override
    public byte[] generateEmployeeReport(List<Employee> employees, FileFormat fileFormat) throws IOException {
        log.info("Inside generateEmployeeReport");
        switch (fileFormat) {
            case EXCEL:
                return generateExcelReport(employees);
            case CSV:
                return generateCsvReport(employees);
            case HTML:
                return generateHtmlReport(employees);
            case PDF:
                return generatePdfReport(employees);
            default:
                throw new IllegalArgumentException("Unsupported file format");
        }
    }
    private byte[] generateExcelReport(List<Employee> employees) throws IOException {
        log.info("Inside generateExcelReport");

        Workbook workbook = new XSSFWorkbook();
        //creating Sheet with headerRow
        Sheet sheet = workbook.createSheet("Employee Data");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Age");
        headerRow.createCell(3).setCellValue("Gender");


        int rowNum = 1;
        for (Employee employee : employees) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(employee.getId());
            row.createCell(1).setCellValue(employee.getName());
            row.createCell(2).setCellValue(employee.getAge());
            row.createCell(3).setCellValue(employee.getGender());
        }


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        outputStream.close();
        workbook.close();


        return outputStream.toByteArray();
    }

    private byte[] generateCsvReport(List<Employee> employees) throws IOException {
        log.info("Inside generateCsvReport");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(outputStream), CSVFormat.EXCEL)) {
            // Defining the CSV header
            csvPrinter.printRecord("ID", "Name", "Age", "Gender");

            for (Employee employee : employees) {
                csvPrinter.printRecord(
                        employee.getId(),
                        employee.getName(),
                        employee.getAge(),
                        employee.getGender()
                );
            }
            csvPrinter.flush();
        }

        return outputStream.toByteArray();

    }
    private byte[] generateHtmlReport(List<Employee> employees) throws IOException {
        log.info("Inside generateHtmlReport");

        String htmlContent = generateHTMLContent(employees);

        // Converting the HTML content to a byte array
        return htmlContent.getBytes("UTF-8");
    }
    private String generateHTMLContent(List<Employee> employees) {
        // Generate HTML content programmatically
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html>");
        htmlBuilder.append("<head><title>Employee Report</title></head>");
        htmlBuilder.append("<body>");
        htmlBuilder.append("<h1>Employee Report</h1>");

        for (Employee employee : employees) {
            htmlBuilder.append("<p>Name: " + employee.getName() + "</p>");
            htmlBuilder.append("<p>Age: " + employee.getAge() + "</p>");
            htmlBuilder.append("<p>Department: " + employee.getGender() + "</p>");
            htmlBuilder.append("<hr>");
        }

        htmlBuilder.append("</body>");
        htmlBuilder.append("</html>");

        return htmlBuilder.toString();
    }

    private byte[] generatePdfReport(List<Employee> employees) throws IOException {
        log.info("Inside generatePdfReport");

        byte[] pdfContent = generatePdfContent(employees);

        return pdfContent;

    }
    private byte[] generatePdfContent(List<Employee> employees) throws IOException {
        log.info("Inside generatePdfContent");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            for (Employee employee : employees) {
                document.add(new Paragraph("Name: " + employee.getName()));
                document.add(new Paragraph("Age: " + employee.getAge()));
                document.add(new Paragraph("Gender: " + employee.getGender()));
                document.add(new Paragraph("------------------------------"));
            }
        } catch (  DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

        return outputStream.toByteArray();
    }




@Override
    public Employee saveEmployee(Employee employee) {
        log.info("Inside saveEmployee");
        return fileExportRepository.save(employee);
    }
}
