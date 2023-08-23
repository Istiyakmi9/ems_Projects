package com.bot.projects.filter;

import com.bot.projects.db.utils.DatabaseConfiguration;
import com.bot.projects.model.CurrentSession;
import com.bot.projects.model.UserDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class RequestFilter implements Filter {
    @Autowired
    CurrentSession userDetail;
    @Autowired
    DatabaseConfiguration databaseConfiguration;
    @Autowired
    ObjectMapper objectMapper;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            Object headerUserDetail = ((HttpServletRequest) servletRequest).getHeader("userDetail");
            if(headerUserDetail == null || headerUserDetail.toString().isEmpty()) {
                throw new Exception("Invalid token found. Please contact to admin.");
            }

            Object database = ((HttpServletRequest) servletRequest).getHeader("database");
            if(database == null || database.toString().isEmpty()) {
                throw new Exception("Invalid company code found. Please contact to admin.");
            }

            var userData = objectMapper.readValue(headerUserDetail.toString(), UserDetail.class);
            userDetail.setUserDetail(userData);
            if (userDetail.getUserDetail() == null)
                throw new Exception("Invalid token found. Please contact to admin.");

            if (userDetail.getUserDetail().getOrganizationId() <= 0
                    || userDetail.getUserDetail().getCompanyId() <= 0)
                throw new Exception("Invalid Organization id or Company id. Please contact to admin.");

            userDetail.getUserDetail().setUserId(userData.getUserId());

            var dbResult = objectMapper.readValue(database.toString(), DatabaseConfiguration.class);
            if(dbResult == null) {
                throw new Exception("Invalid company code found. Please contact to admin.");
            } else {
                databaseConfiguration.setSchema(dbResult.getSchema());
                databaseConfiguration.setDatabaseName(dbResult.getDatabaseName());
                databaseConfiguration.setServer(dbResult.getServer());
                databaseConfiguration.setPort(dbResult.getPort());
                databaseConfiguration.setDatabase(dbResult.getDatabase());
                databaseConfiguration.setUserId(dbResult.getUserId());
                databaseConfiguration.setPassword(dbResult.getPassword());
                databaseConfiguration.setConnectionTimeout(dbResult.getConnectionTimeout());
                databaseConfiguration.setConnectionLifetime(dbResult.getConnectionLifetime());
                databaseConfiguration.setPooling(dbResult.getPooling());
                databaseConfiguration.setMinPoolSize(dbResult.getMinPoolSize());
                databaseConfiguration.setMaxPoolSize(dbResult.getMaxPoolSize());
            }

        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Your session got expired");
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unauthorized access. Please try with valid token.");
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
