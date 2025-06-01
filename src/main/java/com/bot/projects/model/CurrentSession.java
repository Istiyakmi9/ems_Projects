package com.bot.projects.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Date;
import java.util.TimeZone;

@Component
@RequestScope
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentSession {
    @JsonProperty("UserId")
    long UserId;
    @JsonProperty("EmployeeCodePrefix")
    String EmployeeCodePrefix;
    @JsonProperty("EmployeeCodeLength")
    int EmployeeCodeLength;
    @JsonProperty("Authorization")
    String Authorization;
    @JsonProperty("CompanyId")
    int CompanyId;
    @JsonProperty("CompanyName")
    String CompanyName;
    @JsonProperty("DesignationId")
    int DesignationId;
    @JsonProperty("OrganizationId")
    int OrganizationId;
    @JsonProperty("ReportingManagerId")
    long ReportingManagerId;
    @JsonProperty("Culture")
    String Culture;
    @JsonProperty("ManagerEmail")
    String ManagerEmail;
    @JsonProperty("RoleId")
    int RoleId;
    @JsonProperty("Email")
    String Email;
    @JsonProperty("Mobile")
    String Mobile;
    @JsonProperty("FullName")
    String FullName;
    @JsonProperty("ManagerName")
    String ManagerName;
    @JsonProperty("TimeZoneName")
    String TimeZoneName;
    @JsonProperty("FinancialStartYear")
    int FinancialStartYear;
    @JsonProperty("CompanyCode")
    String CompanyCode;
    @JsonProperty("LocalConnectionString")
    String LocalConnectionString;
    @JsonProperty("TimeZoneNow")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    Date TimeZoneNow;
    @JsonProperty("TimeZone")
    TimeZone TimeZone;
}
