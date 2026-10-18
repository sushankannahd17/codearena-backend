package com.example.codearena.controller;

import com.example.codearena.security.AuthenticatedUserService;
import com.example.codearena.service.LeaderboardService;
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
@RequestMapping("/api/leaderboard")
public class LeaderboardController {
    private final LeaderboardService service;
    private final AuthenticatedUserService authenticatedUserService;

    LeaderboardController(AuthenticatedUserService authenticatedUserService, LeaderboardService leaderboardService) {
        this.authenticatedUserService = authenticatedUserService;
        this.service = leaderboardService;
    }

    @GetMapping("/{contestID}")
    public ResponseEntity<?> getLeaderBoard(@PathVariable Long contestID, HttpServletRequest request) {
        try {
            Long userID = authenticatedUserService.getCurrentUserId();

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseBody("success", service.getLeaderBoard(userID, contestID)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseBody("error", e.getMessage()));
        }
    }

    @GetMapping("/report/{contestID}")
    public ResponseEntity<?> downloadReport(@PathVariable Long contestID, HttpServletRequest request) {
        try {
            Long userID = authenticatedUserService.getCurrentUserId();

            byte[] excel = service.generateReport(contestID, userID);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

            headers.setContentDisposition(ContentDisposition.attachment().filename("Leaderboard.xlsx").build());

            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(excel);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseBody("error", e.getMessage()));
        }
    }
}
