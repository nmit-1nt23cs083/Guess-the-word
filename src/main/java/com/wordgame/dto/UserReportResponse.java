package com.wordgame.dto;

import java.util.List;

public class UserReportResponse {
    private String username;
    private List<UserReportRow> rows;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<UserReportRow> getRows() {
        return rows;
    }

    public void setRows(List<UserReportRow> rows) {
        this.rows = rows;
    }
}
