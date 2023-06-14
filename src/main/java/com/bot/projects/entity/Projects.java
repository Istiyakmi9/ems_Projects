package com.bot.projects.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "project")
@Data
@NoArgsConstructor
public class Projects {
    @Id
    @Column(name = "ProjectId")
    @JsonProperty("ProjectId")
    int projectId;
    @Column(name = "ProjectName")
    @JsonProperty("ProjectName")
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
    @Transient
    @JsonProperty("CTC")
    float cTC;
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
    @JsonProperty("ProjectManagerId")
    long projectManagerId;
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