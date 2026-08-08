package com.example.codearena.controller;

import com.example.codearena.service.ReportService;
import com.example.codearena.utils.ID;
import com.example.codearena.utils.ResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
        Long id = ID.getId(request);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseBody("success", service.displayCompletedContests(id)));
    }
}
