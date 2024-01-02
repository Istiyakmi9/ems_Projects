package com.bot.projects.entity;

import com.bot.projects.db.annotations.Column;
import com.bot.projects.db.annotations.Id;
import com.bot.projects.db.annotations.Table;
import com.bot.projects.db.annotations.Transient;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Table(name = "project")
@Data
public class Projects {
    @Id()
    @Column(name = "ProjectId")
    @JsonProperty(value = "ProjectId")
    int projectId;
    @Column(name = "ProjectName")
    @JsonProperty(value = "ProjectName")
    String projectName;
    @Column(name = "ProjectDescription")
    @JsonProperty("ProjectDescription")
    String projectDescription;
    @Column(name = "ProjectStartedOn")
    @JsonProperty("ProjectStartedOn")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    Date projectStartedOn;
    @Column(name = "ProjectEndedOn")
    @JsonProperty("ProjectEndedOn")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    Date projectEndedOn;
    @Column(name = "IsClientProject")
    @JsonProperty("IsClientProject")
    boolean isClientProject;
    @Column(name = "ClientId")
    @JsonProperty("ClientId")
    long clientId;
    @Column(name = "HomePageUrl")
    @JsonProperty("HomePageUrl")
    String homePageUrl;
    @Column(name = "DocumentPath")
    @JsonProperty("DocumentPath")
    String documentPath;
    @Column(name = "PageIndexDetail")
    @JsonProperty("PageIndexDetail")
    String pageIndexDetail;
    @Column(name = "KeywordDetail")
    @JsonProperty("KeywordDetail")
    String keywordDetail;
    @Column(name = "DocumentationDetail")
    @JsonProperty("DocumentationDetail")
    String documentationDetail;
    @Column(name = "CompanyId")
    @JsonProperty("CompanyId")
    int companyId;
    @Column(name = "CEOId")
    @JsonProperty("CEOId")
    Long cEOId;
    @Column(name = "CFOId")
    @JsonProperty("CFOId")
    Long cFOId;
    @Column(name = "CTOId")
    @JsonProperty("CTOId")
    Long cTOId;
    @Column(name = "COOId")
    @JsonProperty("COOId")
    Long cOOId;
    @Column(name = "CanCFOAccess")
    @JsonProperty("CanCFOAccess")
    Boolean canCFOAccess;
    @Column(name = "CanCEOAccess")
    @JsonProperty("CanCEOAccess")
    Boolean canCEOAccess;
    @Column(name = "CanCTOAccess")
    @JsonProperty("CanCTOAccess")
    Boolean canCTOAccess;
    @Column(name = "CanCOOAccess")
    @JsonProperty("CanCOOAccess")
    Boolean canCOOAccess;
    @Column(name = "CreatedBy")
    @JsonProperty("CreatedBy")
    long createdBy;
    @Column(name = "UpdatedBy")
    @JsonProperty("UpdatedBy")
    long updatedBy;
    @Column(name = "CreatedOn")
    @JsonProperty("CreatedOn")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    Date createdOn;
    @Column(name = "UpdatedOn")
    @JsonProperty("UpdatedOn")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    Date updatedOn;
    @JsonProperty("ProjectManagerId")
    @Column(name = "ProjectManagerId")
    long projectManagerId;
    @Transient
    @JsonProperty("CTC")
    BigDecimal cTC;
    @Transient
    @JsonProperty("EmployeeId")
    long employeeId;
    @Transient
    @JsonProperty("DesignationId")
    int designationId;
    @Transient
    @JsonProperty("FullName")
    String fullName;
    @Transient
    @JsonProperty("Email")
    String email;
    @Transient
    @JsonProperty("IsActive")
    boolean isActive;
    @Transient
    @JsonProperty("AssignedOn")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    Date assignedOn;
    @Transient
    @JsonProperty("LastDateOnProject")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    Date lastDateOnProject;
    @Transient
    @JsonProperty("DesignationName")
    String designationName;
    @Transient
    @JsonProperty("Team")
    String team;
    @Transient
    @JsonProperty("MemberType")
    int memberType;
    @Transient
    @JsonProperty("TeamMembers")
    List<ProjectMembers> teamMembers;
}