package com.bot.projects.filter;

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
    ObjectMapper objectMapper;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String headerToken = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if(headerToken == null || !headerToken.startsWith("Bearer")) {
            // throw new RuntimeException("Invalid toke.");
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        headerToken = headerToken.substring(7);
        try {
            String secret = "SchoolInMind_secret_key_is__bottomhalf@mi9_01";
            byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            SecretKey key = Keys.hmacShaKeyFor(keyBytes);

            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(headerToken)
                    .getBody();

            String sid = claims.get("sid", String.class);
            String user = claims.get("JBot", String.class);
            var userData = objectMapper.readValue(user, UserDetail.class);
            userDetail.setUserDetail(userData);
            var roleName = claims.get("role", String.class);
            switch (roleName) {
                case "Admin":
                    userDetail.getUserDetail().setRoleId(1);
                    break;
                case "Employee":
                    userDetail.getUserDetail().setRoleId(2);
                    break;
                case "Candidate":
                    userDetail.getUserDetail().setRoleId(3);
                    break;
                case "Client":
                    userDetail.getUserDetail().setRoleId(4);
                    break;
                default:
                    userDetail.getUserDetail().setRoleId(5);
                    break;
            }

            if (userDetail.getUserDetail() == null)
                throw new Exception("Invalid token found. Please contact to admin.");

            if (userDetail.getUserDetail().getOrganizationId() <= 0
                    || userDetail.getUserDetail().getCompanyId() <= 0)
                throw new Exception("Invalid Organization id or Company id. Please contact to admin.");

            if (sid == null)
                throw new Exception("Invalid employee id used. Please contact to admin.");

            userDetail.getUserDetail().setFullName(userDetail.getUserDetail().getFirstName() + " " +
                                                    userDetail.getUserDetail().getLastName());
            userDetail.getUserDetail().setUserId(Long.parseLong(sid));

        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Your session got expired");
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unauthorized access. Please try with valid token.");
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
