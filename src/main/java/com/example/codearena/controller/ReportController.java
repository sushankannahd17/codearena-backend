package com.example.codearena.controller;

import com.example.codearena.config.ExceptionConfig;
import com.example.codearena.models.Contest;
import com.example.codearena.service.ReportService;
import com.example.codearena.utils.ID;
import com.example.codearena.utils.ResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @Autowired
    private ID ID;
    @Autowired
    private ReportService service;

    @GetMapping("/contestsOver")
    public ResponseEntity<?> fetchCompletedContests(HttpServletRequest request) {
        try {
            Long id = ID.getId(request);

            if (id == null) {
                throw new ExceptionConfig("User is not authenticated");
            }

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseBody("success", service.displayCompletedContests(id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseBody("error", e.getMessage()));
        }
    }

    @GetMapping("/export/{contestID}")
    public ResponseEntity<?> downloadReport(@PathVariable long contestID, HttpServletRequest request) {
        try {
            Long id = ID.getId(request);

            if (id == null) {
                throw new ExceptionConfig("User is not authenticated");
            }

            byte[] doc= service.generateReport(id, contestID);
            HttpHeaders headers=new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
            headers.setContentDisposition(ContentDisposition.attachment().filename("Report.docx").build());

            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(doc);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseBody("error", e.getMessage()));
        }
    }
}
