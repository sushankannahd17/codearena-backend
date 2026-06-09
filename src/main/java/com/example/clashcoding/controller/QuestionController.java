package com.example.clashcoding.controller;

import com.example.clashcoding.config.ExceptionConfig;
import com.example.clashcoding.dto.QuestionDTO;
import com.example.clashcoding.dto.TestCasesDTO;
import com.example.clashcoding.models.TestCase;
import com.example.clashcoding.service.QuestionService;
import com.example.clashcoding.utils.JwtUtils;
import com.example.clashcoding.utils.ResponseBody;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questions")
public class QuestionController {
    @Autowired
    private QuestionService service;
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/create")
    public ResponseEntity<?> createQuestion(@RequestBody QuestionDTO dto, HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();

            String email = null;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("jwt".equalsIgnoreCase(cookie.getName())) {
                        String token = cookie.getValue();

                        email = jwtUtils.extractEmail(token);
                        break;
                    }
                }
            }

            dto.setEmail(email);

            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseBody("success", "Question created. ID: " + service.createQuestion(dto)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseBody("error", e.getMessage()));
        }
    }

    @PatchMapping("/{questionID}/testcases")
    public ResponseEntity<?> modifyTestCases(@PathVariable Long questionID, @RequestBody List<TestCasesDTO> dto, HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();

            String email = null;
            for (Cookie cookie : cookies) {
                if ("jwt".equalsIgnoreCase(cookie.getName())) {
                    String token = cookie.getValue();

                    email = jwtUtils.extractEmail(token);
                    break;
                }
            }

            if (email == null) {
                throw new ExceptionConfig("User is not authenticated");
            }

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseBody("success", service.modifyTestCases(questionID, dto, email)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseBody("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{questionID}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long questionID, HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();

            String email = null;
            for (Cookie cookie : cookies) {
                if ("jwt".equalsIgnoreCase(cookie.getName())) {
                    String token = cookie.getValue();

                    email = jwtUtils.extractEmail(token);
                    break;
                }
            }

            if (email == null) {
                throw new ExceptionConfig("User is not authenticated");
            }

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseBody("success", service.deleteQuestion(questionID)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseBody("error", e.getMessage()));
        }
    }
}
