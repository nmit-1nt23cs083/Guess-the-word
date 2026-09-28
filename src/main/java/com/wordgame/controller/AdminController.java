package com.wordgame.controller;

import com.wordgame.dto.ReportResponse;
import com.wordgame.dto.UserReportResponse;
import com.wordgame.service.AdminReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminReportService adminReportService;

    public AdminController(AdminReportService adminReportService) {
        this.adminReportService = adminReportService;
    }

    /** GET /api/admin/report?date=2026-09-26 (date optional, defaults to today) */
    @GetMapping("/report")
    public ResponseEntity<ReportResponse> getReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate target = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(adminReportService.getDailyReport(target));
    }

    /** GET /api/admin/user-report?username=JaneDoe&date=2026-09-26 (date optional: omit for all dates) */
    @GetMapping("/user-report")
    public ResponseEntity<UserReportResponse> getUserReport(
            @RequestParam String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(adminReportService.getUserReport(username.trim(), date));
    }
}
