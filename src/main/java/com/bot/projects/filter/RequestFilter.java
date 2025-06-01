package com.bot.projects.filter;

import com.bot.projects.db.utils.DatabaseConfiguration;
import com.bot.projects.model.CurrentSession;
import com.bot.projects.model.UserDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Component
public class RequestFilter implements Filter {
    @Autowired
    CurrentSession currentSession;
    @Autowired
    DatabaseConfiguration databaseConfiguration;
    @Autowired
    ObjectMapper objectMapper;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            Object database = ((HttpServletRequest) servletRequest).getHeader("database");
            if(database == null || database.toString().isEmpty()) {
                throw new Exception("Invalid company code found. Please contact to admin.");
            }

            var headerUserDetail = ((HttpServletRequest) servletRequest).getHeader("Session");
            if(headerUserDetail == null || headerUserDetail.isEmpty()) {
                throw new Exception("Invalid token found. Please contact to admin.");
            }

            var currentSessionDetail = objectMapper.readValue(headerUserDetail, CurrentSession.class);
            if (currentSessionDetail == null)
                throw new Exception("Invalid token found. Please contact to admin.");

            parseConnectionString(database.toString());
            MapCurrentSession(currentSessionDetail);
            currentSessionDetail.setLocalConnectionString(database.toString());

        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unauthorized access. Please try with valid token.");
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void MapCurrentSession(CurrentSession session) {
        currentSession.setUserId(session.getUserId());
        currentSession.setCompanyId(session.getCompanyId());
        currentSession.setCulture(session.getCulture());
        currentSession.setEmail(session.getEmail());
        currentSession.setCompanyName(session.getCompanyName());
        currentSession.setDesignationId(session.getDesignationId());
        currentSession.setEmployeeCodePrefix(session.getEmployeeCodePrefix());
        currentSession.setEmployeeCodePrefix(session.getEmployeeCodePrefix());
        currentSession.setAuthorization(session.getAuthorization());
        currentSession.setCompanyId(session.getCompanyId());
        currentSession.setOrganizationId(session.getOrganizationId());
        currentSession.setReportingManagerId(session.getReportingManagerId());
        currentSession.setManagerEmail(session.getManagerEmail());
        currentSession.setRoleId(session.getRoleId());
        currentSession.setMobile(session.getMobile());
        currentSession.setFullName(session.getFullName());
        currentSession.setManagerName(session.getManagerName());
        currentSession.setFinancialStartYear(session.getFinancialStartYear());
    }

    public void parseConnectionString(String connectionString) {
        String[] parts = connectionString.split(";");
        for (String part : parts) {
            if (part.trim().isEmpty()) continue;
            String[] keyValue = part.split("=", 2);
            if (keyValue.length != 2) continue;

            String key = keyValue[0].trim().toLowerCase();
            String value = keyValue[1].trim();

            switch (key) {
                case "server": databaseConfiguration.setServer(value); break;
                case "port": databaseConfiguration.setPort(value); break;
                case "database": databaseConfiguration.setDatabase(value); break;
                case "user id": databaseConfiguration.setUserId(value); break;
                case "password": databaseConfiguration.setPassword(value); break;
                case "connection timeout": databaseConfiguration.setConnectionTimeout(parseInt(value)); break;
                case "connection lifetime": databaseConfiguration.setConnectionLifetime(parseInt(value)); break;
                case "min pool size": databaseConfiguration.setMinPoolSize(parseInt(value)); break;
                case "max pool size": databaseConfiguration.setMaxPoolSize(parseInt(value)); break;
                case "pooling": databaseConfiguration.setPooling(Boolean.parseBoolean(value)); break;
            }
        }

        databaseConfiguration.setSchema("jdbc");
        databaseConfiguration.setDatabaseName("mysql");
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
